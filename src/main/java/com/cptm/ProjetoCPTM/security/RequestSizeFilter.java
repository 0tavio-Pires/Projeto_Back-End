package com.cptm.ProjetoCPTM.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;

/** Bounds JSON bodies even when the client uses chunked transfer encoding. */
@Component
public class RequestSizeFilter extends OncePerRequestFilter {
    private static final int MAX=5*1024*1024;
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
        if(!request.getRequestURI().startsWith("/api/") || !java.util.Set.of("POST","PUT","PATCH").contains(request.getMethod())) { chain.doFilter(request,response); return; }
        byte[] body=request.getInputStream().readNBytes(MAX+1);
        if(body.length>MAX) { response.setStatus(413); response.setContentType("application/problem+json"); response.setCharacterEncoding("UTF-8"); response.getWriter().write("{\"status\":413,\"detail\":\"Corpo da requisição excede 5 MB.\"}"); return; }
        chain.doFilter(new HttpServletRequestWrapper(request) {
            @Override public ServletInputStream getInputStream() {
                ByteArrayInputStream input=new ByteArrayInputStream(body);
                return new ServletInputStream() {
                    @Override public int read() { return input.read(); }
                    @Override public int read(byte[] b,int off,int len) { return input.read(b,off,len); }
                    @Override public boolean isFinished() { return input.available()==0; }
                    @Override public boolean isReady() { return true; }
                    @Override public void setReadListener(ReadListener listener) { throw new UnsupportedOperationException("Synchronous request body"); }
                };
            }
            @Override public BufferedReader getReader() { return new BufferedReader(new InputStreamReader(getInputStream(),StandardCharsets.UTF_8)); }
            @Override public int getContentLength() { return body.length; }
            @Override public long getContentLengthLong() { return body.length; }
        },response);
    }
}
