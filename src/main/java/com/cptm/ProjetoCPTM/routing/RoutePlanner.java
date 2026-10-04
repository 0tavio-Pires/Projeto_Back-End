package com.cptm.ProjetoCPTM.routing;

import com.cptm.ProjetoCPTM.domain.*;
import java.util.*;
import static com.cptm.ProjetoCPTM.domain.Network.*;

/** Dijkstra on directed platform/track nodes; passenger integrations are never edges. */
public final class RoutePlanner {
    private record Cost(double seconds, int changes) implements Comparable<Cost> {
        public int compareTo(Cost other) { int c=Double.compare(seconds,other.seconds); return c!=0?c:Integer.compare(changes,other.changes); }
    }
    private record Candidate(String node, Cost cost) {}
    public Route plan(Network s, Train train, String targetLine, String destination) {
        Topology.require(s.lines,targetLine,"Linha");
        if(train.nodeId==null) throw DomainException.conflict("Planeje a transferência quando o trem estiver em uma plataforma.");
        if(!Topology.compatible(s,train.profileId,targetLine)) throw new DomainException(409,"INCOMPATIBLE_TRAIN","Bitola, energia, sinalização ou tecnologia incompatível com o destino.");
        if(destination!=null && !Topology.require(s.nodes,destination,"Destino").lineId().equals(targetLine))
            throw DomainException.invalid("A plataforma de destino deve pertencer à linha selecionada.");
        if(!s.lines.get(targetLine).enabled()) throw DomainException.conflict("Linha de destino desativada.");
        Map<String,Cost> distances=new HashMap<>(); Map<String,Edge> previous=new HashMap<>();
        PriorityQueue<Candidate> queue=new PriorityQueue<>(Comparator.comparing(Candidate::cost).thenComparing(Candidate::node));
        Cost zero=new Cost(0,0); distances.put(train.nodeId,zero); queue.add(new Candidate(train.nodeId,zero));
        Set<String> busyNodes=Topology.occupiedNodes(s,train.id), busyResources=Topology.occupiedResources(s,train.id);
        String end=null;
        while(!queue.isEmpty()) {
            Candidate current=queue.remove();
            if(current.cost().compareTo(distances.get(current.node()))!=0) continue;
            Node node=s.nodes.get(current.node());
            if(node.lineId().equals(targetLine) && (destination==null || destination.equals(node.id()))) { end=node.id(); break; }
            for(Edge edge:Topology.outgoing(s,node.id())) {
                if(Topology.unavailable(s,train,edge)!=null) continue;
                double travel=edge.lengthMeters()/(Topology.speed(s,train,edge)/3.6)+8;
                double wait=busyNodes.contains(edge.to()) || busyResources.contains(edge.resourceId()) ? 30 : 0;
                int change=s.nodes.get(edge.from()).lineId().equals(s.nodes.get(edge.to()).lineId())?0:1;
                Cost next=new Cost(current.cost().seconds()+travel+wait,current.cost().changes()+change);
                Cost old=distances.get(edge.to());
                if(old==null || next.compareTo(old)<0) { distances.put(edge.to(),next); previous.put(edge.to(),edge); queue.add(new Candidate(edge.to(),next)); }
            }
        }
        if(end==null) throw new DomainException(409,"NO_PHYSICAL_ROUTE","Não há caminho físico verificado e disponível para esta composição. Integrações de passageiros não permitem passagem de trens.");
        LinkedList<String> edges=new LinkedList<>(); String cursor=end;
        while(!cursor.equals(train.nodeId)) { Edge e=previous.get(cursor); edges.addFirst(e.id()); cursor=e.from(); }
        List<String> steps=new ArrayList<>(); steps.add(stationLabel(s,train.nodeId));
        for(String edge:edges) steps.add(stationLabel(s,s.edges.get(edge).to()));
        return new Route(train.id,targetLine,end,List.copyOf(edges),Math.round(distances.get(end).seconds()*10)/10.0,
                distances.get(end).changes(),s.version,List.copyOf(steps));
    }
    private String stationLabel(Network s,String nodeId) {
        Node n=s.nodes.get(nodeId); return s.stations.get(n.stationId()).name()+" · "+s.lines.get(n.lineId()).name()+" · "+n.direction();
    }
}

