package com.cptm.ProjetoCPTM.incident;

import com.cptm.ProjetoCPTM.domain.Network;
import java.util.*;
import static com.cptm.ProjetoCPTM.domain.Network.*;

public final class ImpactCalculator {
    private ImpactCalculator() {}
    public static Set<String> originLines(Network s, Incident i) {
        Set<String> lines=new TreeSet<>();
        switch(i.targetType()) {
            case LINE -> lines.add(i.targetId());
            case STATION -> s.nodes.values().stream().filter(n->n.stationId().equals(i.targetId())).forEach(n->lines.add(n.lineId()));
            case EDGE -> { Edge e=s.edges.get(i.targetId()); if(e!=null) { lines.add(s.nodes.get(e.from()).lineId()); lines.add(s.nodes.get(e.to()).lineId()); } }
            case TRAIN -> { Train t=s.trains.get(i.targetId()); if(t!=null) lines.add(t.currentLineId); }
        }
        return lines;
    }
    private record Visit(String line, List<String> path, boolean confirmed, String reason) {}
    public static List<Impact> calculate(Network s) {
        List<Impact> result=new ArrayList<>();
        for(Incident i:s.incidents.values()) if(i.open()) {
            Map<String,Visit> visited=new LinkedHashMap<>();
            ArrayDeque<Visit> queue=new ArrayDeque<>();
            for(String line:originLines(s,i)) queue.add(new Visit(line,List.of(line),true,"Origem da ocorrência"));
            while(!queue.isEmpty()) {
                Visit v=queue.remove();
                Visit previous=visited.get(v.line());
                if(previous!=null && (previous.confirmed() || !v.confirmed())) continue;
                visited.put(v.line(),v);
                for(Dependency d:s.dependencies.values()) if(d.fromLine().equals(v.line()) && !v.path().contains(d.toLine())) {
                    List<String> path=new ArrayList<>(v.path()); path.add(d.toLine());
                    queue.add(new Visit(d.toLine(),List.copyOf(path),v.confirmed() && d.operational(),d.reason()));
                }
            }
            visited.values().forEach(v->result.add(new Impact(i.id(),v.line(),i.label(),i.severity(),
                    v.path().size()==1 ? "DIRECT" : v.confirmed() ? "OPERATIONAL" : "POTENTIAL",
                    v.confirmed()?i.effect().name():"RISK",v.path(),v.reason())));
        }
        result.sort(Comparator.comparing(Impact::severity).reversed().thenComparing(Impact::incidentId).thenComparing(Impact::lineId));
        return List.copyOf(result);
    }
    public static Set<String> operationalDependents(Network s, Incident i) {
        Set<String> origins=originLines(s,i), visited=new HashSet<>(origins);
        ArrayDeque<String> queue=new ArrayDeque<>(origins);
        while(!queue.isEmpty()) {
            String line=queue.remove();
            for(Dependency d:s.dependencies.values()) if(d.operational() && d.fromLine().equals(line) && visited.add(d.toLine())) queue.add(d.toLine());
        }
        visited.removeAll(origins);
        return visited;
    }
}

