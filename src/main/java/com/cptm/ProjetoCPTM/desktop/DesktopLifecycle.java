package com.cptm.ProjetoCPTM.desktop;

import com.cptm.ProjetoCPTM.application.Commands.ClockInput;
import com.cptm.ProjetoCPTM.application.NetworkService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@Profile("desktop")
public class DesktopLifecycle {
    private final ConfigurableApplicationContext context;
    private final NetworkService network;
    private final boolean watchParent;
    private final AtomicBoolean closing=new AtomicBoolean();
    public DesktopLifecycle(ConfigurableApplicationContext context,NetworkService network,
            @Value("${rail.desktop.watch-parent:true}") boolean watchParent) {
        this.context=context; this.network=network; this.watchParent=watchParent;
    }
    @EventListener(ApplicationReadyEvent.class) public void ready() {
        int port=context.getEnvironment().getRequiredProperty("local.server.port",Integer.class);
        System.out.println("FERROVIA_DESKTOP_READY:"+port);
        System.out.flush();
        if(watchParent) Thread.ofPlatform().daemon(true).name("desktop-parent-watch").start(()->{
            try { while(System.in.read()!=-1) { /* The pipe stays open while the launcher lives. */ } }
            catch(IOException ignored) { /* Broken pipe is equivalent to launcher termination. */ }
            close();
        });
    }
    @PostMapping("/desktop/shutdown") @PreAuthorize("hasRole('ADMIN')")
    public Map<String,Boolean> shutdown() {
        Thread.ofPlatform().daemon(true).name("desktop-shutdown").start(()->{
            try { Thread.sleep(250); } catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
            close();
        });
        return Map.of("closing",true);
    }
    private void close() {
        if(!closing.compareAndSet(false,true)) return;
        try { network.clock(new ClockInput(false,network.export().timeScale),"desktop"); }
        catch(RuntimeException ex) { LoggerFactory.getLogger(getClass()).error("Unable to persist desktop pause; last committed state retained",ex); }
        finally { context.close(); }
    }
}
