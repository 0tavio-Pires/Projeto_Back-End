package com.cptm.ProjetoCPTM.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

/** Private desktop transport: a new random capability is created by the launcher at every start. */
public final class DesktopAccessFilter extends OncePerRequestFilter {
    public static final String HEADER="X-Ferrovia-Desktop";
    private final byte[] expected;
    public DesktopAccessFilter(String token) {
        if(token==null || !token.matches("[a-f0-9]{64}")) throw new IllegalStateException("O perfil desktop exige uma credencial temporária de 256 bits fornecida pelo aplicativo.");
        expected=token.getBytes(StandardCharsets.UTF_8);
    }
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
        String supplied=request.getHeader(HEADER);
        String origin=request.getHeader("Origin");
        String localOrigin="http://127.0.0.1:"+request.getLocalPort();
        boolean allowed="127.0.0.1".equals(request.getRemoteAddr()) && supplied!=null
                && MessageDigest.isEqual(expected,supplied.getBytes(StandardCharsets.UTF_8))
                && (origin==null || localOrigin.equals(origin));
        if(!allowed) {
            response.setStatus(403); response.setContentType("application/problem+json"); response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\":403,\"detail\":\"Este serviço é privado do aplicativo Ferrovia.\"}"); return;
        }
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                "local",null,List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        chain.doFilter(request,response);
    }
}
