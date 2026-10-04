package com.cptm.ProjetoCPTM.api;

import com.cptm.ProjetoCPTM.application.NetworkService;
import com.cptm.ProjetoCPTM.domain.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;
import java.util.*;
import static com.cptm.ProjetoCPTM.application.Commands.*;

@RestController
@RequestMapping("/api/v1")
public class NetworkController {
    private final NetworkService service;
    private final JsonMapper json;
    public NetworkController(NetworkService service,JsonMapper json) { this.service=service; this.json=json; }
    @GetMapping("/session") public Object session(Authentication auth,CsrfToken token) {
        return Map.of("username",auth.getName(),"roles",auth.getAuthorities().stream().map(Object::toString).toList(),"csrf",token.getToken(),"csrfHeader",token.getHeaderName());
    }
    @GetMapping("/network") public Object network() { return service.snapshot(); }
    @GetMapping("/history") public Object history() { return service.history(); }
    @GetMapping(value="/export",produces="application/json") public Object export() { return service.export(); }
    @PostMapping("/trains") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Object createTrain(@Valid @RequestBody TrainInput input,Authentication auth) { service.train(input,true,auth.getName()); return service.snapshot(); }
    @PutMapping("/trains/{id}") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Object updateTrain(@PathVariable String id,@Valid @RequestBody TrainInput input,Authentication auth) {
        if(!id.equals(input.id())) throw DomainException.invalid("Identificador do corpo difere da URL.");
        service.train(input,false,auth.getName()); return service.snapshot();
    }
    @DeleteMapping("/trains/{id}") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Object deleteTrain(@PathVariable String id,Authentication auth) { service.deleteTrain(id,auth.getName()); return service.snapshot(); }
    @PatchMapping("/trains/{id}/active") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Object active(@PathVariable String id,@RequestBody Active input,Authentication auth) { service.active(id,input.active(),auth.getName()); return service.snapshot(); }
    @PostMapping("/trains/{id}/route") public Object route(@PathVariable String id,@Valid @RequestBody TransferInput input) { return service.plan(id,input); }
    @PostMapping("/trains/{id}/transfer") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Object transfer(@PathVariable String id,@Valid @RequestBody TransferInput input,Authentication auth) { service.transfer(id,input,auth.getName()); return service.snapshot(); }
    @DeleteMapping("/trains/{id}/transfer") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Object cancel(@PathVariable String id,Authentication auth) { service.cancelTransfer(id,auth.getName()); return service.snapshot(); }
    @PostMapping("/incidents") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Object incident(@Valid @RequestBody IncidentInput input,Authentication auth) { service.incident(input,auth.getName()); return service.snapshot(); }
    @PostMapping("/incidents/{id}/{action:ack|resolve}") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Object incidentStatus(@PathVariable String id,@PathVariable String action,Authentication auth) { service.incidentStatus(id,action.equals("resolve"),auth.getName()); return service.snapshot(); }
    @PostMapping("/clock") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Object clock(@Valid @RequestBody ClockInput input,Authentication auth) { service.clock(input,auth.getName()); return service.snapshot(); }
    @PostMapping("/step") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Object step(@Valid @RequestBody StepInput input,Authentication auth) { service.step(input.seconds(),auth.getName()); return service.snapshot(); }
    @PostMapping("/infrastructure/{collection}/{id}") @PreAuthorize("hasRole('ADMIN')")
    public Object create(@PathVariable String collection,@PathVariable String id,@RequestBody String body,Authentication auth) { service.infrastructure(collection,id,body,true,auth.getName()); return service.snapshot(); }
    @PutMapping("/infrastructure/{collection}/{id}") @PreAuthorize("hasRole('ADMIN')")
    public Object update(@PathVariable String collection,@PathVariable String id,@RequestBody String body,Authentication auth) { service.infrastructure(collection,id,body,false,auth.getName()); return service.snapshot(); }
    @DeleteMapping("/infrastructure/{collection}/{id}") @PreAuthorize("hasRole('ADMIN')")
    public Object delete(@PathVariable String collection,@PathVariable String id,Authentication auth) { service.deleteInfrastructure(collection,id,auth.getName()); return service.snapshot(); }
    @PostMapping("/import") @PreAuthorize("hasRole('ADMIN')")
    public Object restore(@RequestBody String body,Authentication auth) { service.restore(json.readValue(body,Network.class),auth.getName()); return service.snapshot(); }
    @PostMapping("/reset") @PreAuthorize("hasRole('ADMIN')")
    public Object reset(Authentication auth) { service.reset(auth.getName()); return service.snapshot(); }
}
