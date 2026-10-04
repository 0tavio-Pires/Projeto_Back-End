package com.cptm.ProjetoCPTM.api;

import com.cptm.ProjetoCPTM.application.NetworkService;
import com.cptm.ProjetoCPTM.domain.DomainException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class EventController {
    private final CopyOnWriteArrayList<SseEmitter> clients=new CopyOnWriteArrayList<>();
    private final NetworkService service;
    public EventController(NetworkService service) { this.service=service; }
    @GetMapping(value="/api/v1/events",produces="text/event-stream") public synchronized SseEmitter events() {
        if(clients.size()>=64) throw new DomainException(429,"STREAM_LIMIT","Limite de conexões simultâneas atingido.");
        SseEmitter emitter=new SseEmitter(60000L); clients.add(emitter);
        emitter.onCompletion(()->clients.remove(emitter)); emitter.onTimeout(()->{clients.remove(emitter);emitter.complete();}); emitter.onError(ex->clients.remove(emitter));
        send(emitter,service.version()); return emitter;
    }
    @Scheduled(fixedDelay=2000) public void broadcast() { long version=service.version(); clients.forEach(e->send(e,version)); }
    private void send(SseEmitter emitter,long version) {
        try { emitter.send(SseEmitter.event().name("version").data(version)); }
        catch(Exception ex) { clients.remove(emitter); emitter.complete(); }
    }
}
