package com.cptm.ProjetoCPTM.application;

import com.cptm.ProjetoCPTM.domain.*;
import com.cptm.ProjetoCPTM.incident.ImpactCalculator;
import com.cptm.ProjetoCPTM.persistence.NetworkRepository;
import com.cptm.ProjetoCPTM.routing.RoutePlanner;
import com.cptm.ProjetoCPTM.simulation.SimulationEngine;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import static com.cptm.ProjetoCPTM.domain.Network.*;
import static com.cptm.ProjetoCPTM.application.Commands.*;

/** Single writer. A validated copy is published only after the database transaction commits. */
@Service
public class NetworkService {
    private static final Logger log=LoggerFactory.getLogger(NetworkService.class);
    private final NetworkRepository repository;
    private final JsonMapper json;
    private final SimulationEngine engine=new SimulationEngine();
    private final RoutePlanner planner=new RoutePlanner();
    private Network current;
    private String fault;
    @Value("${rail.scheduler.enabled:true}") private boolean schedulerEnabled;
    @Value("${rail.scheduler.interval-ms:1000}") private long intervalMs;
    public NetworkService(NetworkRepository repository,JsonMapper json) { this.repository=repository; this.json=json; }
    @PostConstruct public synchronized void initialize() {
        var saved=repository.load();
        current=saved.orElseGet(()->new NetworkSeed().create(json));
        Topology.validate(current);
        if(saved.isEmpty()) repository.initialize(current);
        else if(current.running) change("system","RECOVERY_PAUSE","Recuperação após reinício",s->s.running=false);
    }
    public record Snapshot(Network network, List<Impact> impacts, Map<String,Object> metrics, String fault) {}
    public synchronized Snapshot snapshot() {
        Network copy=copy(current);
        return new Snapshot(copy,ImpactCalculator.calculate(copy),metrics(copy),fault);
    }
    public synchronized Network export() { return copy(current); }
    public synchronized long version() { return current.version; }
    public List<Map<String,Object>> history() { return repository.history(); }
    private Network copy(Network source) { return json.readValue(json.writeValueAsBytes(source),Network.class); }
    private void change(String actor,String action,String detail,Consumer<Network> command) {
        Network candidate=copy(current); command.accept(candidate); commit(candidate,actor,action,detail);
    }
    private void commit(Network candidate,String actor,String action,String detail) {
        Topology.validate(candidate); candidate.version=current.version+1; candidate.updatedAt=Instant.now();
        repository.save(candidate,current.version,actor,action,detail); current=candidate;
    }
    @Scheduled(fixedDelayString="${rail.scheduler.interval-ms:1000}")
    public synchronized void tick() {
        if(!schedulerEnabled || !current.running || fault!=null) return;
        try { change("system",null,"",s->engine.advance(s,Math.min(60,s.timeScale*intervalMs/1000.0))); }
        catch(RuntimeException ex) { fault="Simulação suspensa após falha. Verifique o histórico do servidor e retome quando resolvida."; log.error("Simulation tick failed; published state preserved",ex); }
    }
    public synchronized void clock(ClockInput input,String actor) {
        change(actor,"CLOCK","Execução="+input.running()+", escala="+input.timeScale(),s->{ s.running=input.running(); s.timeScale=input.timeScale(); }); fault=null;
    }
    public synchronized void step(double seconds,String actor) {
        if(current.running && fault==null) throw DomainException.conflict("Pause a simulação antes do avanço manual.");
        change(actor,"STEP","Avanço de "+seconds+" segundos",s->engine.advance(s,seconds));
    }
    public synchronized void train(TrainInput input,boolean create,String actor) {
        change(actor,create?"TRAIN_CREATE":"TRAIN_UPDATE",input.id(),s->{
            Topology.id(input.id());
            Train previous=s.trains.get(input.id());
            if(create && previous!=null) throw DomainException.conflict("Identificador de trem já cadastrado.");
            if(!create && previous==null) throw DomainException.missing("Trem "+input.id());
            if(previous!=null && (previous.active || previous.edgeId!=null)) throw DomainException.conflict("Retire o trem de circulação antes de editar seu cadastro.");
            Train t=new Train(); t.id=input.id(); t.number=input.number().trim(); t.model=input.model().trim(); t.profileId=input.profileId();
            t.capacity=input.capacity(); t.carriages=input.carriages(); t.assignedLineId=input.lineId(); t.currentLineId=input.lineId();
            t.nodeId=input.nodeId(); t.active=input.active(); t.movement=t.active?Movement.DWELLING:Movement.STOPPED;
            if(previous!=null) t.delaySeconds=previous.delaySeconds;
            checkInsertion(s,t); s.trains.put(t.id,t);
        });
    }
    private void checkInsertion(Network s,Train t) {
        Node n=Topology.require(s.nodes,t.nodeId,"Plataforma");
        if(!n.lineId().equals(t.currentLineId)) throw DomainException.invalid("Plataforma pertence a outra linha.");
        if(t.active) {
            if(!n.enabled() || !s.stations.get(n.stationId()).enabled() || !s.lines.get(n.lineId()).enabled()) throw DomainException.conflict("Local de entrada indisponível.");
            if(Topology.occupiedNodes(s,t.id).contains(t.nodeId)) throw DomainException.conflict("Plataforma ocupada ou reservada.");
            for(Incident i:s.incidents.values()) if(i.open() && i.effect()==Effect.BLOCK &&
                    ((i.targetType()==Target.LINE && i.targetId().equals(n.lineId())) ||
                     (i.targetType()==Target.STATION && i.targetId().equals(n.stationId())) ||
                     (i.targetType()==Target.TRAIN && i.targetId().equals(t.id)) ||
                     ImpactCalculator.operationalDependents(s,i).contains(n.lineId()))) throw DomainException.conflict("Entrada bloqueada: "+i.label());
        }
    }
    public synchronized void active(String id,boolean active,String actor) {
        change(actor,"TRAIN_ACTIVE",id+"="+active,s->{
            Train t=Topology.require(s.trains,id,"Trem");
            if(t.edgeId!=null) throw DomainException.conflict("Aguarde a chegada à plataforma para alterar a circulação.");
            t.active=active;
            if(active) { checkInsertion(s,t); t.movement=Movement.DWELLING; t.dwellRemaining=8; }
            else { cancel(t); t.assignedLineId=t.currentLineId; t.movement=Movement.STOPPED; t.speedKmh=0; t.reason="Fora de circulação"; }
        });
    }
    public synchronized void deleteTrain(String id,String actor) {
        change(actor,"TRAIN_DELETE",id,s->{
            Train t=Topology.require(s.trains,id,"Trem");
            if(t.active) throw DomainException.conflict("Retire o trem de circulação antes de excluí-lo.");
            s.trains.remove(id); // Referential validation preserves incident history.
        });
    }
    public synchronized Route plan(String id,TransferInput input) {
        Train t=Topology.require(current.trains,id,"Trem");
        if(!t.active) throw DomainException.conflict("Trem fora de circulação.");
        return planner.plan(current,t,input.targetLineId(),input.destinationNodeId());
    }
    public synchronized void transfer(String id,TransferInput input,String actor) {
        if(input.expectedVersion()!=null && input.expectedVersion()!=current.version) throw DomainException.conflict("A rede mudou. Calcule a rota novamente.");
        Route route=plan(id,input);
        change(actor,"TRANSFER_START",id+" → "+input.targetLineId(),s->{
            Train t=s.trains.get(id); t.route=new ArrayList<>(route.edgeIds()); t.targetLineId=route.targetLineId(); t.targetNodeId=route.destinationNodeId();
            t.transfer=t.route.isEmpty()?Transfer.COMPLETED:Transfer.IN_PROGRESS;
            if(t.route.isEmpty()) t.assignedLineId=t.targetLineId;
        });
    }
    public synchronized void cancelTransfer(String id,String actor) {
        change(actor,"TRANSFER_CANCEL",id,s->{
            Train t=Topology.require(s.trains,id,"Trem");
            if(t.edgeId!=null) throw DomainException.conflict("Cancele a transferência quando o trem chegar à plataforma.");
            cancel(t); t.assignedLineId=t.currentLineId;
        });
    }
    private void cancel(Train t) { t.route.clear(); t.transfer=Transfer.CANCELLED; t.targetLineId=null; t.targetNodeId=null; }
    public synchronized String incident(IncidentInput input,String actor) {
        String id="INC-"+UUID.randomUUID();
        change(actor,"INCIDENT_CREATE",id+" "+input.label(),s->s.incidents.put(id,new Incident(id,input.label().trim(),input.description().trim(),input.category(),input.severity(),
                input.targetType(),input.targetId(),input.effect(),IncidentStatus.OPEN,Instant.now(),null,actor,null)));
        return id;
    }
    public synchronized void incidentStatus(String id,boolean resolve,String actor) {
        change(actor,resolve?"INCIDENT_RESOLVE":"INCIDENT_ACK",id,s->{
            Incident i=Topology.require(s.incidents,id,"Ocorrência");
            if(!i.open()) throw DomainException.conflict("Ocorrência já resolvida.");
            s.incidents.put(id,new Incident(i.id(),i.label(),i.description(),i.category(),i.severity(),i.targetType(),i.targetId(),i.effect(),
                    resolve?IncidentStatus.RESOLVED:IncidentStatus.ACKNOWLEDGED,i.createdAt(),resolve?Instant.now():null,i.createdBy(),actor));
        });
    }
    public synchronized void infrastructure(String collection,String id,String payload,boolean create,String actor) {
        if(current.running) throw DomainException.conflict("Pause a simulação para editar a infraestrutura.");
        change(actor,"INFRA_"+(create?"CREATE":"UPDATE"),collection+"/"+id,s->{
            switch(collection) {
                case "profiles" -> put(s.profiles,id,json.readValue(payload,Profile.class),create);
                case "lines" -> put(s.lines,id,json.readValue(payload,Line.class),create);
                case "stations" -> put(s.stations,id,json.readValue(payload,Station.class),create);
                case "nodes" -> {
                    Node value=json.readValue(payload,Node.class),old=s.nodes.get(id);
                    if(old!=null && value!=null && (!Objects.equals(old.stationId(),value.stationId()) || !Objects.equals(old.lineId(),value.lineId()) || !Objects.equals(old.direction(),value.direction()))
                            && (s.edges.values().stream().anyMatch(e->e.from().equals(id)||e.to().equals(id)) || s.trains.values().stream().anyMatch(t->id.equals(t.nodeId))))
                        throw DomainException.conflict("Plataforma em uso: remova as referências antes de alterar sua localização ou sentido.");
                    put(s.nodes,id,value,create);
                }
                case "edges" -> {
                    Edge value=json.readValue(payload,Edge.class),old=s.edges.get(id);
                    if(old!=null && value!=null && (!Objects.equals(old.from(),value.from()) || !Objects.equals(old.to(),value.to()) || !Objects.equals(old.resourceId(),value.resourceId()) || old.lengthMeters()!=value.lengthMeters())
                            && s.trains.values().stream().anyMatch(t->id.equals(t.edgeId)||t.route.contains(id)))
                        throw DomainException.conflict("Via em uso por composição ou transferência: sua geometria e recurso devem ser preservados.");
                    put(s.edges,id,value,create);
                }
                case "dependencies" -> put(s.dependencies,id,json.readValue(payload,Dependency.class),create);
                default -> throw DomainException.missing("Tipo de infraestrutura");
            }
        });
    }
    private <T> void put(Map<String,T> map,String id,T item,boolean create) {
        Topology.id(id);
        if(create && map.containsKey(id)) throw DomainException.conflict("Identificador já cadastrado.");
        if(!create && !map.containsKey(id)) throw DomainException.missing(id);
        map.put(id,item);
    }
    public synchronized void deleteInfrastructure(String collection,String id,String actor) {
        if(current.running) throw DomainException.conflict("Pause a simulação para editar a infraestrutura.");
        change(actor,"INFRA_DELETE",collection+"/"+id,s->{
            Map<String,?> map=switch(collection) {
                case "profiles" -> s.profiles; case "lines" -> s.lines; case "stations" -> s.stations;
                case "nodes" -> s.nodes; case "edges" -> s.edges; case "dependencies" -> s.dependencies;
                default -> throw DomainException.missing("Tipo de infraestrutura");
            };
            Topology.require(map,id,"Item"); map.remove(id);
        });
    }
    public synchronized void restore(Network candidate,String actor) {
        Topology.validate(candidate);
        candidate=copy(candidate); candidate.running=false; commit(candidate,actor,"IMPORT","Importação validada de cenário"); fault=null;
    }
    public synchronized void reset(String actor) { commit(new NetworkSeed().create(json),actor,"RESET","Restauração do cenário histórico"); fault=null; }
    private Map<String,Object> metrics(Network s) {
        Map<String,Long> byLine=new LinkedHashMap<>(); s.lines.keySet().forEach(id->byLine.put(id,0L));
        List<Double> delays=new ArrayList<>(); long active=0,moving=0,blocked=0;
        for(Train t:s.trains.values()) if(t.active) { active++; byLine.merge(t.currentLineId,1L,Long::sum); delays.add(t.delaySeconds); if(t.movement==Movement.MOVING)moving++; if(t.movement==Movement.BLOCKED || t.movement==Movement.WAITING)blocked++; }
        Collections.sort(delays); int size=delays.size();
        double mean=delays.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double median=size==0?0:size%2==1?delays.get(size/2):(delays.get(size/2-1)+delays.get(size/2))/2;
        Map<Double,Long> frequencies=new TreeMap<>(); delays.forEach(v->frequencies.merge(v,1L,Long::sum));
        long max=frequencies.values().stream().mapToLong(Long::longValue).max().orElse(0);
        List<Double> modes=max<2?List.of():frequencies.entrySet().stream().filter(e->e.getValue()==max).map(Map.Entry::getKey).toList();
        return Map.of("total",s.trains.size(),"active",active,"moving",moving,"waiting",blocked,"byLine",byLine,
                "openIncidents",s.incidents.values().stream().filter(Incident::open).count(),"meanDelay",mean,"medianDelay",median,"modeDelay",modes);
    }
}
