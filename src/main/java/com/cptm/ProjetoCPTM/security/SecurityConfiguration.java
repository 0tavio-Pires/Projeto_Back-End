package com.cptm.ProjetoCPTM.security;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import java.security.SecureRandom;
import java.util.*;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {
    @Bean @Profile("!desktop") UserDetailsService users(@Value("${rail.security.admin-password:}") String admin,
            @Value("${rail.security.operator-password:}") String operator,@Value("${rail.security.viewer-password:}") String viewer,
            @Value("${rail.security.require-password:false}") boolean requirePassword) {
        if(admin.isBlank()) {
            if(requirePassword) throw new IllegalStateException("RAIL_ADMIN_PASSWORD é obrigatório em produção.");
            byte[] entropy=new byte[24]; new SecureRandom().nextBytes(entropy); admin=Base64.getUrlEncoder().withoutPadding().encodeToString(entropy);
            LoggerFactory.getLogger(getClass()).warn("Acesso local temporário: usuário admin / senha {}. Configure RAIL_ADMIN_PASSWORD para persistir a credencial.",admin);
        }
        var encoder=new BCryptPasswordEncoder(); var users=new ArrayList<UserDetails>();
        for(var entry:Map.of("admin",admin,"operator",operator,"viewer",viewer).entrySet()) if(!entry.getValue().isBlank()) {
            if(entry.getValue().length()<12 || entry.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72)
                throw new IllegalStateException("Senhas configuradas devem ter pelo menos 12 caracteres e no máximo 72 bytes UTF-8.");
            users.add(User.withUsername(entry.getKey()).password("{bcrypt}"+encoder.encode(entry.getValue())).roles(entry.getKey().toUpperCase(Locale.ROOT)).build());
        }
        return new InMemoryUserDetailsManager(users);
    }
    @Bean @Profile("desktop") UserDetailsService desktopUser() {
        return new InMemoryUserDetailsManager(User.withUsername("local").password("{noop}"+UUID.randomUUID()).roles("ADMIN").build());
    }
    @Bean SecurityFilterChain filterChain(HttpSecurity http,
            @Value("${rail.desktop.enabled:false}") boolean desktop,
            @Value("${rail.desktop.token:}") String desktopToken) throws Exception {
        if(desktop) http.addFilterBefore(new DesktopAccessFilter(desktopToken),org.springframework.security.web.csrf.CsrfFilter.class);
        http.authorizeHttpRequests(auth->auth
                .requestMatchers("/","/index.html","/favicon.svg","/assets/**","/auth/csrf","/login").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN").anyRequest().authenticated())
            .formLogin(form->form.loginPage("/").loginProcessingUrl("/login")
                .successHandler((request,response,authentication)->response.setStatus(204))
                .failureHandler((request,response,exception)->{ response.setStatus(401); response.setContentType("application/json"); response.getWriter().write("{\"detail\":\"Usuário ou senha inválidos.\"}"); }))
            .logout(out->out.logoutUrl("/logout").logoutSuccessHandler((request,response,auth)->response.setStatus(204)))
            .exceptionHandling(errors->errors.accessDeniedHandler((request,response,exception)->{
                // Write directly: sendError would dispatch /error without the desktop request's authentication.
                response.setStatus(403); response.setContentType("application/problem+json"); response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"status\":403,\"detail\":\"Operação não permitida ou token CSRF inválido.\"}");
            }).authenticationEntryPoint((request,response,exception)->{
                response.setStatus(401); response.setContentType("application/json"); response.getWriter().write("{\"detail\":\"Entre na sua conta para continuar.\"}");
            }))
            .headers(headers->headers.contentSecurityPolicy(csp->csp.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; connect-src 'self'; object-src 'none'; base-uri 'self'; frame-ancestors 'none'; form-action 'self'")));
        return http.build();
    }
    @RestController public static class CsrfController {
        @GetMapping("/auth/csrf") public Object csrf(CsrfToken token) { return Map.of("token",token.getToken(),"header",token.getHeaderName()); }
    }
}
