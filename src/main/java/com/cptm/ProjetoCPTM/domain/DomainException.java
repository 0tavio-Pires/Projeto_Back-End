package com.cptm.ProjetoCPTM.domain;

public class DomainException extends RuntimeException {
    private final int status;
    private final String code;
    public DomainException(int status, String code, String message) {
        super(message); this.status = status; this.code = code;
    }
    public int status() { return status; }
    public String code() { return code; }
    public static DomainException invalid(String message) { return new DomainException(400, "INVALID_INPUT", message); }
    public static DomainException missing(String entity) { return new DomainException(404, "NOT_FOUND", entity + " não encontrado."); }
    public static DomainException conflict(String message) { return new DomainException(409, "CONFLICT", message); }
}

