package com.cptm.ProjetoCPTM;

import com.cptm.ProjetoCPTM.application.*;
import com.cptm.ProjetoCPTM.domain.*;
import com.cptm.ProjetoCPTM.persistence.NetworkRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static com.cptm.ProjetoCPTM.domain.Network.*;
import static com.cptm.ProjetoCPTM.application.Commands.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:api;DB_CLOSE_DELAY=-1","rail.scheduler.enabled=false","rail.security.admin-password=test-admin-password","rail.security.operator-password=test-operator-password","rail.security.viewer-password=test-viewer-password"})
@AutoConfigureMockMvc
class NetworkApiTest {
    @Autowired MockMvc mvc;
    @Autowired NetworkService service;
    @Autowired NetworkRepository repository;
    @Autowired JsonMapper json;
    @BeforeEach void reset() { service.reset("test"); }
    TrainInput input(String id,boolean active) { return new TrainInput(id,id,"Teste","M16",1200,6,"L1","L1:TUCURUVI:A",active); }
    @Test void anonymousCannotReadNetwork() throws Exception { mvc.perform(get("/api/v1/network")).andExpect(status().isUnauthorized()); }
    @Test void viewerReadsSnapshotAndRealCounters() throws Exception { mvc.perform(get("/api/v1/network").with(user("viewer").roles("VIEWER"))).andExpect(status().isOk()).andExpect(jsonPath("$.metrics.active").value(26)).andExpect(jsonPath("$.metrics.byLine.L7").value(2)); }
    @Test void viewerCannotMutateEvenWithCsrf() throws Exception { mvc.perform(post("/api/v1/clock").with(user("viewer").roles("VIEWER")).with(csrf()).contentType("application/json").content("{\"running\":true,\"timeScale\":1}")).andExpect(status().isForbidden()); }
    @Test void csrfRequiredForOperatorCommands() throws Exception { mvc.perform(post("/api/v1/clock").with(user("operator").roles("OPERATOR")).contentType("application/json").content("{\"running\":true,\"timeScale\":1}")).andExpect(status().isForbidden()); }
    @Test void operatorCannotEditInfrastructure() throws Exception { mvc.perform(delete("/api/v1/infrastructure/lines/L1").with(user("operator").roles("OPERATOR")).with(csrf())).andExpect(status().isForbidden()); }
    @Test void invalidCredentialsAreRejected() throws Exception { mvc.perform(post("/login").with(csrf()).param("username","admin").param("password","wrong")).andExpect(status().isUnauthorized()); }
    @Test void validCredentialsCreateSession() throws Exception { mvc.perform(post("/login").with(csrf()).param("username","admin").param("password","test-admin-password")).andExpect(status().isNoContent()); }
    @Test void createTrainAndRejectDuplicateWithoutChangingPublishedVersion() throws Exception {
        String body=json.writeValueAsString(input("TEST",true));
        mvc.perform(post("/api/v1/trains").with(user("operator").roles("OPERATOR")).with(csrf()).contentType("application/json").content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.metrics.active").value(27));
        long version=service.version();mvc.perform(post("/api/v1/trains").with(user("operator").roles("OPERATOR")).with(csrf()).contentType("application/json").content(body)).andExpect(status().isConflict());assertThat(service.version()).isEqualTo(version);
    }
    @Test void unknownFieldsCannotOverpostLocation() throws Exception { String body=json.writeValueAsString(input("TEST",true)).replace("\"capacity\":1200","\"edgeId\":\"L1-1-A\",\"capacity\":1200");mvc.perform(post("/api/v1/trains").with(user("operator").roles("OPERATOR")).with(csrf()).contentType("application/json").content(body)).andExpect(status().isBadRequest()); }
    @Test void nullInputAndInvalidNumbersAreRejected() throws Exception { for(String body:List.of("null","{}","{\"running\":true,\"timeScale\":61}"))mvc.perform(post("/api/v1/clock").with(user("admin").roles("ADMIN")).with(csrf()).contentType("application/json").content(body)).andExpect(status().isBadRequest()); }
    @Test void occupiedPlatformCannotBeOverwritten() { TrainInput i=input("TEST",true);i=new TrainInput(i.id(),i.number(),i.model(),i.profileId(),i.capacity(),i.carriages(),i.lineId(),service.export().trains.get("L1-T1").nodeId,true);TrainInput candidate=i;assertThatThrownBy(()->service.train(candidate,true,"test")).isInstanceOf(DomainException.class);assertThat(service.snapshot().metrics().get("active")).isEqualTo(26L); }
    @Test void stationChangesDoNotChangeTrainRegistry() { Station station=service.export().stations.get("TUCURUVI");service.infrastructure("stations",station.id(),json.writeValueAsString(new Station(station.id(),station.name(),station.x(),station.y(),false)),false,"admin");assertThat(service.export().stations.get(station.id()).enabled()).isFalse();assertThat(service.export().trains).hasSize(26); }
    @Test void incidentLifecyclePersistsAndPreservesOtherAlerts() { String a=service.incident(new IncidentInput("Energia","Teste","Energia",Severity.HIGH,Target.LINE,"L1",Effect.BLOCK),"test");String b=service.incident(new IncidentInput("Via","Teste","Via",Severity.HIGH,Target.LINE,"L1",Effect.BLOCK),"test");service.incidentStatus(a,false,"operator");assertThat(service.export().incidents.get(a).status()).isEqualTo(IncidentStatus.ACKNOWLEDGED);service.incidentStatus(a,true,"operator");assertThat(service.snapshot().impacts()).allMatch(v->v.incidentId().equals(b));assertThat(repository.load().orElseThrow().incidents.get(a).resolvedAt()).isNotNull(); }
    @Test void staleTransferIsRejectedAndFreshPlanExecutes() { var input=new TransferInput("L10",null,null);var route=service.plan("L7-T1",input);service.step(1,"test");assertThatThrownBy(()->service.transfer("L7-T1",new TransferInput("L10",route.destinationNodeId(),route.stateVersion()),"test")).isInstanceOf(DomainException.class).hasMessageContaining("mudou");var fresh=service.plan("L7-T1",input);service.transfer("L7-T1",new TransferInput("L10",fresh.destinationNodeId(),fresh.stateVersion()),"test");assertThat(service.export().trains.get("L7-T1").transfer).isEqualTo(Transfer.IN_PROGRESS); }
    @Test void snapshotsCannotMutateLiveState() { service.export().trains.clear();service.snapshot().network().lines.clear();assertThat(service.export().trains).hasSize(26);assertThat(service.export().lines).hasSize(13); }
    @Test void recoveryLoadsPersistedPositionAndPausesClock() { service.step(30,"test");var before=service.export();service.clock(new ClockInput(true,5),"test");NetworkService restarted=new NetworkService(repository,json);restarted.initialize();assertThat(restarted.export().running).isFalse();assertThat(restarted.export().elapsedSeconds).isEqualTo(before.elapsedSeconds);assertThat(restarted.export().trains.get("L1-T1").edgeId).isEqualTo(before.trains.get("L1-T1").edgeId); service.initialize(); }
    @Test void invalidImportIsAtomic() { long version=service.version(); assertThatThrownBy(()->service.restore(null,"test")).isInstanceOf(DomainException.class);Network invalid=service.export();invalid.trains.get("L1-T1").nodeId="MISSING";assertThatThrownBy(()->service.restore(invalid,"test")).isInstanceOf(DomainException.class);assertThat(service.version()).isEqualTo(version);assertThat(repository.load().orElseThrow().version).isEqualTo(version); }
    @Test void importClearsRunningFlagAndUsesServerVersion() { Network imported=service.export();imported.running=true;imported.version=999999;long previous=service.version();service.restore(imported,"test");assertThat(service.export().running).isFalse();assertThat(service.version()).isEqualTo(previous+1); }
    @Test void referencedInfrastructureCannotBeDeleted() { assertThatThrownBy(()->service.deleteInfrastructure("stations","LUZ","test")).isInstanceOf(DomainException.class);assertThat(service.export().stations).containsKey("LUZ"); }
    @Test void infrastructureEditsRequirePause() { service.clock(new ClockInput(true,5),"test");assertThatThrownBy(()->service.deleteInfrastructure("dependencies","INT-L1-L2","test")).isInstanceOf(DomainException.class).hasMessageContaining("Pause"); }
    @Test void occupiedEdgeCannotBeRewired() { service.step(10,"test");Network state=service.export();Edge edge=state.edges.get(state.trains.get("L1-T1").edgeId);Edge changed=new Edge(edge.id(),edge.from(),edge.to(),"DIFFERENT",edge.lengthMeters(),edge.maxSpeedKmh(),edge.connection(),true,true,edge.source());assertThatThrownBy(()->service.infrastructure("edges",edge.id(),json.writeValueAsString(changed),false,"test")).isInstanceOf(DomainException.class); }
    @Test void concurrentCommandsAreSerializedWithoutLostTrains() throws Exception { try(var pool=Executors.newVirtualThreadPerTaskExecutor()){List<Future<?>> jobs=new ArrayList<>();for(int i=0;i<12;i++){String id="NEW-"+i;jobs.add(pool.submit(()->service.train(input(id,false),true,"test")));}for(Future<?> job:jobs)job.get(30,TimeUnit.SECONDS);}assertThat(service.export().trains).hasSize(38);assertThat(repository.load().orElseThrow().trains).hasSize(38); }
    @Test void zeroActiveTrainsHaveDefinedStatistics() { Network state=service.export();state.trains.values().forEach(t->t.active=false);service.restore(state,"test");var metrics=service.snapshot().metrics();assertThat(metrics.get("active")).isEqualTo(0L);assertThat(metrics.get("meanDelay")).isEqualTo(0.0);assertThat(metrics.get("medianDelay")).isEqualTo(0.0); }
    @Test void oversizedJsonIsRejected() throws Exception { mvc.perform(post("/api/v1/import").with(user("admin").roles("ADMIN")).with(csrf()).contentType("application/json").content(" ".repeat(5*1024*1024+1))).andExpect(status().isPayloadTooLarge()); }
    @Test void auditContainsActorAndAction() { service.train(input("AUDIT",false),true,"operator");assertThat(service.history().getFirst()).containsEntry("actor","operator").containsEntry("action","TRAIN_CREATE").containsEntry("detail","AUDIT"); }
}

