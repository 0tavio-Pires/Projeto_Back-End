package com.cptm.ProjetoCPTM;

import com.cptm.ProjetoCPTM.domain.*;
import com.cptm.ProjetoCPTM.incident.ImpactCalculator;
import com.cptm.ProjetoCPTM.routing.RoutePlanner;
import com.cptm.ProjetoCPTM.simulation.SimulationEngine;
import org.junit.jupiter.api.*;
import tools.jackson.databind.json.JsonMapper;
import java.time.Instant;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static com.cptm.ProjetoCPTM.domain.Network.*;

class NetworkDomainTest {
    Network s;
    final SimulationEngine engine=new SimulationEngine();
    @BeforeEach void setup() { s=new NetworkSeed().create(JsonMapper.builder().build()); }
    Incident issue(String id,Target target,String value,Effect effect) { return new Incident(id,id,"Descrição","Operação",Severity.HIGH,target,value,effect,IncidentStatus.OPEN,Instant.now(),null,"test",null); }
    @Test void historicalSeedIsValidAndContainsIdentifiableTrains() { Topology.validate(s); assertThat(s.lines).hasSize(13);assertThat(s.trains).hasSize(26);assertThat(s.stations.size()).isGreaterThan(170);assertThat(s.running).isFalse(); }
    @Test void physical710RouteExistsAndCompletes() {
        Train t=s.trains.get("L7-T1"); var route=new RoutePlanner().plan(s,t,"L10",null);
        assertThat(route.edgeIds()).contains("710-IDA"); assertThat(route.lineChanges()).isEqualTo(1);
        t.route=new ArrayList<>(route.edgeIds()); t.transfer=Transfer.IN_PROGRESS;t.targetLineId="L10";t.targetNodeId=route.destinationNodeId();
        for(int i=0;i<8;i++) {engine.advance(s,60);Topology.validate(s);}
        assertThat(t.transfer).isEqualTo(Transfer.COMPLETED);assertThat(t.currentLineId).isEqualTo("L10");assertThat(t.assignedLineId).isEqualTo("L10");
    }
    @Test void passengerIntegrationNeverBecomesTrainRoute() { assertThatThrownBy(()->new RoutePlanner().plan(s,s.trains.get("L1-T1"),"L2",null)).isInstanceOf(DomainException.class).hasMessageContaining("caminho físico"); }
    @Test void incompatibleTrainIsRejectedBeforeRouting() { assertThatThrownBy(()->new RoutePlanner().plan(s,s.trains.get("L1-T1"),"L4",null)).isInstanceOf(DomainException.class).hasMessageContaining("incompatível"); }
    @Test void closedPhysicalConnectionRemovesRoute() { Edge e=s.edges.get("710-IDA");s.edges.put(e.id(),new Edge(e.id(),e.from(),e.to(),e.resourceId(),e.lengthMeters(),e.maxSpeedKmh(),true,true,false,e.source()));assertThatThrownBy(()->new RoutePlanner().plan(s,s.trains.get("L7-T1"),"L10",null)).isInstanceOf(DomainException.class); }
    @Test void blockOnLineStopsMovementUntilEveryBlockingIncidentIsResolved() {
        Train t=s.trains.get("L1-T1");String start=t.nodeId;s.incidents.put("I1",issue("I1",Target.LINE,"L1",Effect.BLOCK));s.incidents.put("I2",issue("I2",Target.LINE,"L1",Effect.BLOCK));
        engine.advance(s,30);assertThat(t.nodeId).isEqualTo(start);assertThat(t.movement).isEqualTo(Movement.BLOCKED);
        s.incidents.remove("I1");engine.advance(s,10);assertThat(t.nodeId).isEqualTo(start);
        s.incidents.remove("I2");engine.advance(s,20);assertThat(t.edgeId).isNotNull();assertThat(t.delaySeconds).isPositive();
    }
    @Test void closedStationStopsApproachAndKeepsPosition() { Train t=s.trains.get("L1-T1");engine.advance(s,10);String edge=t.edgeId;double position=t.progressMeters;String station=s.nodes.get(s.edges.get(edge).to()).stationId();Station old=s.stations.get(station);s.stations.put(station,new Station(old.id(),old.name(),old.x(),old.y(),false));engine.advance(s,10);assertThat(t.progressMeters).isEqualTo(position);assertThat(t.movement).isEqualTo(Movement.BLOCKED); }
    @Test void incidentPropagationTerminatesOnCyclesAndPreservesOrigin() { s.incidents.put("I",issue("I",Target.LINE,"L1",Effect.BLOCK));var impacts=ImpactCalculator.calculate(s);assertThat(impacts).hasSize(13);assertThat(impacts.stream().filter(i->i.lineId().equals("L1")).findFirst().orElseThrow().level()).isEqualTo("DIRECT");assertThat(impacts.stream().filter(i->i.lineId().equals("L2")).findFirst().orElseThrow().level()).isEqualTo("POTENTIAL");assertThat(Topology.unavailable(s,s.trains.get("L2-T1"),Topology.outgoing(s,s.trains.get("L2-T1").nodeId).getFirst())).isNull(); }
    @Test void operationalDependenciesPropagateBlockAndSlow() { s.dependencies.put("OP",new Dependency("OP","L1","L2","Energia compartilhada",true));Train t=s.trains.get("L2-T1");Edge e=Topology.outgoing(s,t.nodeId).getFirst();s.incidents.put("I",issue("I",Target.LINE,"L1",Effect.BLOCK));assertThat(Topology.unavailable(s,t,e)).contains("Dependência");s.incidents.put("I",issue("I",Target.LINE,"L1",Effect.SLOW));assertThat(Topology.speed(s,t,e)).isEqualTo(15);assertThat(ImpactCalculator.calculate(s).stream().filter(v->v.lineId().equals("L2")).findFirst().orElseThrow().level()).isEqualTo("OPERATIONAL"); }
    @Test void cannotOverwriteOccupiedPlatform() { Train a=s.trains.get("L1-T1"),b=s.trains.get("L1-T2");b.nodeId=a.nodeId;assertThatThrownBy(()->Topology.validate(s)).isInstanceOf(DomainException.class).hasMessageContaining("ocupada"); }
    @Test void sharedTrackResourceIsReservedInBothDirections() { Train a=s.trains.get("L1-T1"),b=s.trains.get("L1-T2");Edge forward=Topology.outgoing(s,a.nodeId).getFirst();Node destination=s.nodes.get(forward.to());b.nodeId="L1:"+destination.stationId()+":B";Edge reverse=Topology.outgoing(s,b.nodeId).getFirst();s.edges.put(reverse.id(),new Edge(reverse.id(),reverse.from(),reverse.to(),forward.resourceId(),reverse.lengthMeters(),reverse.maxSpeedKmh(),false,true,true,reverse.source()));a.dwellRemaining=0;b.dwellRemaining=0;engine.advance(s,1);Topology.validate(s);assertThat(List.of(a,b).stream().filter(t->t.edgeId!=null).count()).isEqualTo(1);assertThat(List.of(a,b).stream().filter(t->t.movement==Movement.WAITING).count()).isEqualTo(1); }
    @Test void longSimulationPreservesOccupancyAndAllIdentifiers() { for(int i=0;i<120;i++){engine.advance(s,60);Topology.validate(s);}assertThat(s.trains).hasSize(26);assertThat(s.elapsedSeconds).isEqualTo(7200);assertThat(s.trains.values()).allMatch(t->t.nodeId!=null||t.edgeId!=null); }
    @Test void trainNumbersAreUniqueCaseInsensitive() { s.trains.get("L1-T2").number=s.trains.get("L1-T1").number;assertThatThrownBy(()->Topology.validate(s)).isInstanceOf(DomainException.class); }
    @Test void nonFiniteCoordinatesAndNullDirectionAreRejected() { Station old=s.stations.values().iterator().next();s.stations.put(old.id(),new Station(old.id(),old.name(),Double.NaN,0,true));assertThatThrownBy(()->Topology.validate(s)).isInstanceOf(DomainException.class);s.stations.put(old.id(),old);Node node=s.nodes.values().iterator().next();s.nodes.put(node.id(),new Node(node.id(),node.stationId(),node.lineId(),null,true));assertThatThrownBy(()->Topology.validate(s)).isInstanceOf(DomainException.class); }
    @Test void unverifiedConnectionCannotBeEnabled() { Edge e=s.edges.get("710-IDA");s.edges.put(e.id(),new Edge(e.id(),e.from(),e.to(),e.resourceId(),200,20,true,false,true,""));assertThatThrownBy(()->Topology.validate(s)).isInstanceOf(DomainException.class); }
    @Test void invalidTimeStepDoesNotAdvanceClock() { for(double value:new double[]{0,-1,61,Double.NaN,Double.POSITIVE_INFINITY})assertThatThrownBy(()->engine.advance(s,value)).isInstanceOf(DomainException.class);assertThat(s.elapsedSeconds).isZero(); }
}
