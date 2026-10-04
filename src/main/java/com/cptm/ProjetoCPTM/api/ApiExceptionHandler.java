package com.cptm.ProjetoCPTM.api;

import com.cptm.ProjetoCPTM.domain.DomainException;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.JacksonException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(DomainException.class) ResponseEntity<ProblemDetail> domain(DomainException ex) { return problem(ex.status(),ex.code(),ex.getMessage()); }
    @ExceptionHandler({HttpMessageNotReadableException.class,JacksonException.class})
    ResponseEntity<ProblemDetail> malformed(Exception ex) { return problem(400,"INVALID_JSON","JSON inválido, campo desconhecido ou tipo incompatível."); }
    @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ProblemDetail> invalid(MethodArgumentNotValidException ex) {
        String detail=ex.getBindingResult().getFieldErrors().stream().map(e->e.getField()+": "+e.getDefaultMessage()).distinct().sorted().reduce((a,b)->a+"; "+b).orElse("Entrada inválida.");
        return problem(400,"VALIDATION",detail);
    }
    @ExceptionHandler(AccessDeniedException.class) ResponseEntity<ProblemDetail> denied() { return problem(403,"FORBIDDEN","Seu perfil não permite esta operação."); }
    @ExceptionHandler(Exception.class) ResponseEntity<ProblemDetail> unexpected(Exception ex) {
        if(ex instanceof org.springframework.web.ErrorResponse response) return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        LoggerFactory.getLogger(getClass()).error("Unhandled request error",ex);
        return problem(500,"INTERNAL_ERROR","A operação não foi concluída. Consulte o registro do servidor.");
    }
    private ResponseEntity<ProblemDetail> problem(int status,String code,String detail) {
        ProblemDetail body=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(status),detail); body.setProperty("code",code);
        return ResponseEntity.status(status).body(body);
    }
}
