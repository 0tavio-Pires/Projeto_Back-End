package com.cptm.ProjetoCPTM.simulation;

import com.cptm.ProjetoCPTM.domain.*;
import com.cptm.ProjetoCPTM.routing.RoutePlanner;
import java.util.*;
import static com.cptm.ProjetoCPTM.domain.Network.*;

public final class SimulationEngine {
    private final RoutePlanner planner=new RoutePlanner();
    public void advance(Network s, double seconds) {
        if(!Double.isFinite(seconds) || seconds<=0 || seconds>60) throw DomainException.invalid("Avanço deve estar entre 0 e 60 segundos.");
        double remaining=seconds;
        while(remaining>0.000001) { double dt=Math.min(1,remaining); step(s,dt); remaining-=dt; }
    }
    private void step(Network s,double dt) {
        // All departures use the same initial occupancy; arrival and departure cannot race.
        Set<String> nodes=Topology.occupiedNodes(s,null), resources=Topology.occupiedResources(s,null);
        List<Train> ordered=new ArrayList<>(s.trains.values()); ordered.sort(Comparator.comparing(t->t.id));
        if(!ordered.isEmpty()) Collections.rotate(ordered,-(int)(Math.floor(s.elapsedSeconds)%ordered.size()));
        for(Train t:ordered) {
            t.speedKmh=0;
            if(!t.active) { t.movement=Movement.STOPPED; continue; }
            if(t.edgeId!=null) {
                Edge e=s.edges.get(t.edgeId);
                String unavailable=Topology.unavailable(s,t,e);
                if(unavailable!=null) { wait(t,Movement.BLOCKED,unavailable,dt); continue; }
                t.speedKmh=Topology.speed(s,t,e); t.movement=Movement.MOVING; t.reason="";
                t.progressMeters=Math.min(e.lengthMeters(),t.progressMeters+t.speedKmh/3.6*dt);
                if(t.progressMeters>=e.lengthMeters()) {
                    t.nodeId=e.to(); t.currentLineId=s.nodes.get(e.to()).lineId(); t.edgeId=null; t.progressMeters=0; t.speedKmh=0;
                    t.dwellRemaining=8; t.movement=Movement.DWELLING;
                    if(t.transfer==Transfer.IN_PROGRESS && t.route.isEmpty() && t.nodeId.equals(t.targetNodeId)) {
                        t.assignedLineId=t.targetLineId; t.transfer=Transfer.COMPLETED;
                    }
                }
                continue;
            }
            if(t.dwellRemaining>0) { t.dwellRemaining=Math.max(0,t.dwellRemaining-dt); t.movement=Movement.DWELLING; t.reason="Parada na plataforma"; continue; }
            Edge next=null;
            if(t.transfer==Transfer.IN_PROGRESS) {
                if(t.route.isEmpty() && t.nodeId.equals(t.targetNodeId)) { t.transfer=Transfer.COMPLETED; t.assignedLineId=t.targetLineId; }
                else {
                    next=t.route.isEmpty()?null:s.edges.get(t.route.getFirst());
                    if(next==null || Topology.unavailable(s,t,next)!=null) {
                        try { t.route=new ArrayList<>(planner.plan(s,t,t.targetLineId,t.targetNodeId).edgeIds()); next=t.route.isEmpty()?null:s.edges.get(t.route.getFirst()); }
                        catch(DomainException ex) { wait(t,Movement.BLOCKED,ex.getMessage(),dt); continue; }
                    }
                }
            }
            if(next==null) next=Topology.outgoing(s,t.nodeId).stream()
                    .filter(e->!e.connection() && s.nodes.get(e.to()).lineId().equals(t.currentLineId)).findFirst().orElse(null);
            if(next==null) { wait(t,Movement.BLOCKED,"Nenhuma via de saída cadastrada.",dt); continue; }
            String unavailable=Topology.unavailable(s,t,next);
            if(unavailable!=null) { wait(t,Movement.BLOCKED,unavailable,dt); continue; }
            if(nodes.contains(next.to()) || resources.contains(next.resourceId())) { wait(t,Movement.WAITING,"Aguardando liberação da via ou plataforma.",dt); continue; }
            nodes.add(next.to()); resources.add(next.resourceId());
            t.edgeId=next.id(); t.nodeId=null; t.progressMeters=0; t.speedKmh=Topology.speed(s,t,next); t.movement=Movement.MOVING; t.reason="";
            if(t.transfer==Transfer.IN_PROGRESS && !t.route.isEmpty()) t.route.removeFirst();
        }
        s.elapsedSeconds+=dt;
    }
    private void wait(Train t,Movement movement,String reason,double dt) {
        t.movement=movement; t.reason=reason; t.delaySeconds+=dt;
    }
}

