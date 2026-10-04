package com.cptm.ProjetoCPTM.domain;

import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.json.JsonMapper;
import java.text.Normalizer;
import java.util.*;
import static com.cptm.ProjetoCPTM.domain.Network.*;

/** Historical service topology; infrastructure dimensions and train profiles are simulation assumptions. */
public final class NetworkSeed {
    public static final String MAP = "https://www.metro.sp.gov.br/wp-content/uploads/2025/02/mapaderede.pdf";
    public static final String SERVICE = "https://cptm.sp.gov.br/noticias/Pages/CPTM-lan%C3%A7a-o-Servi%C3%A7o-710-com-melhorias-na-mobilidade-dos-passageiros-das-linhas-7-Rubi-e-10-Turquesa.aspx";
    public record Spec(String referenceDate, List<List<String>> lines, Map<String,double[]> anchors) {}
    public Network create(JsonMapper json) {
        Spec spec;
        try (var in = new ClassPathResource("network/sp-2024.json").getInputStream()) { spec=json.readValue(in,Spec.class); }
        catch (java.io.IOException ex) { throw new IllegalStateException("Cenário de referência ausente",ex); }
        Network s=new Network(); s.referenceDate=spec.referenceDate(); s.sources=List.of(MAP,SERVICE);
        s.notes="Referência histórica de serviços em 2024. Geometria, tempos, vias direcionais, plataformas, bitolas e perfis de compatibilidade são simplificados para simulação. A ligação 710 representa a circulação documentada entre as linhas 7 e 10; sua posição no grafo é uma abstração em Brás. Integrações de passageiros não autorizam transferência de trens. Não é um cadastro técnico homologado nem a rede atual.";
        for(Profile p:List.of(new Profile("M16","Metrô 1/2/3 · referência",1600,"750V_TERCEIRO_TRILHO","METRO_123","METRO"),
                new Profile("M4","Linha 4 · referência",1435,"1500V_CATENARIA","CBTC_L4","METRO"),
                new Profile("M5","Linha 5 · referência",1435,"1500V_CATENARIA","CBTC_L5","METRO"),
                new Profile("CPTM","Ferrovia · referência simplificada",1600,"3000V_CATENARIA","CPTM_REFERENCIA","FERROVIA"),
                new Profile("MONO15","Monotrilho · referência",0,"750V_MONOTRILHO","CBTC_L15","MONOTRILHO_15"))) s.profiles.put(p.id(),p);
        for(List<String> row:spec.lines()) {
            String line=row.get(0); s.lines.put(line,new Line(line,row.get(1),row.get(2),row.get(3),row.get(4),true));
            String[] names=row.get(5).split("\\|");
            for(int i=0;i<names.length;i++) {
                String station=slug(names[i]); double[] xy=coordinate(names,i,spec.anchors());
                s.stations.putIfAbsent(station,new Station(station,names[i],xy[0],xy[1],true));
                for(String direction:List.of("A","B")) {
                    String id=node(line,names[i],direction); s.nodes.put(id,new Node(id,station,line,direction,true));
                }
                if(i>0) {
                    edge(s,line+"-"+i+"-A",node(line,names[i-1],"A"),node(line,names[i],"A"),800+(i%4)*200,60);
                    edge(s,line+"-"+i+"-B",node(line,names[i],"B"),node(line,names[i-1],"B"),800+(i%4)*200,60);
                }
            }
            edge(s,line+"-RETURN-END",node(line,names[names.length-1],"A"),node(line,names[names.length-1],"B"),40,10);
            edge(s,line+"-RETURN-START",node(line,names[0],"B"),node(line,names[0],"A"),40,10);
            for(int i=1;i<=2;i++) {
                Train t=new Train(); t.id=line+"-T"+i; t.number=String.format("%02d%02d",Integer.parseInt(line.substring(1)),i);
                t.model="Composição de referência"; t.profileId=row.get(4); t.capacity=1200; t.carriages=6;
                t.assignedLineId=line; t.currentLineId=line;
                int index=i==1?(line.equals("L7")?names.length-2:Math.min(2,names.length-1)):names.length/2;
                t.nodeId=node(line,names[index],i==1?"A":"B"); s.trains.put(t.id,t);
            }
        }
        s.edges.put("710-IDA",new Edge("710-IDA",node("L7","Brás","A"),node("L10","Brás","A"),"CONEXAO-710",200,20,true,true,true,SERVICE));
        s.edges.put("710-VOLTA",new Edge("710-VOLTA",node("L10","Brás","B"),node("L7","Brás","B"),"CONEXAO-710",200,20,true,true,true,SERVICE));
        for(Station station:s.stations.values()) {
            List<String> lines=s.nodes.values().stream().filter(n->n.stationId().equals(station.id())).map(Node::lineId).distinct().toList();
            for(String a:lines) for(String b:lines) if(!a.equals(b)) dependency(s,a,b,"Integração de passageiros em "+station.name());
        }
        dependency(s,"L2","L4","Integração de passageiros Consolação / Paulista");
        dependency(s,"L4","L2","Integração de passageiros Paulista / Consolação");
        Topology.validate(s); return s;
    }
    private static void dependency(Network s,String a,String b,String reason) {
        String id="INT-"+a+"-"+b; s.dependencies.putIfAbsent(id,new Dependency(id,a,b,reason,false));
    }
    private static void edge(Network s,String id,String a,String b,double length,double speed) {
        s.edges.put(id,new Edge(id,a,b,id,length,speed,false,true,true,MAP+" · segmento e medidas de simulação"));
    }
    public static String slug(String value) { return Normalizer.normalize(value,Normalizer.Form.NFD).replaceAll("\\p{M}","").toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+","-").replaceAll("-$",""); }
    public static String node(String line,String station,String direction) { return line+":"+slug(station)+":"+direction; }
    private static double[] coordinate(String[] names,int i,Map<String,double[]> anchors) {
        if(anchors.containsKey(names[i])) return anchors.get(names[i]);
        int before=i,after=i;
        while(before>0 && !anchors.containsKey(names[before])) before--;
        while(after<names.length-1 && !anchors.containsKey(names[after])) after++;
        double[] a=anchors.getOrDefault(names[before],new double[]{200,200}),b=anchors.getOrDefault(names[after],new double[]{1600,1000});
        double f=after==before?0:(double)(i-before)/(after-before); return new double[]{a[0]+(b[0]-a[0])*f,a[1]+(b[1]-a[1])*f};
    }
}
