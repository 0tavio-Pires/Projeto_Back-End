package com.cptm.ProjetoCPTM.domain;

import java.time.Instant;
import java.util.*;

/** Internal aggregate. Mutated only on a private command copy, never returned to HTTP callers. */
public final class Network {
    public int schemaVersion = 1;
    public long version;
    public String name = "São Paulo · referência histórica 2024";
    public String referenceDate = "2024-10-01";
    public String notes = "Cenário de simulação. Geometria, distâncias, velocidades e blocos são simplificados.";
    public List<String> sources = new ArrayList<>();
    public Instant updatedAt = Instant.now();
    public boolean running;
    public double timeScale = 5;
    public double elapsedSeconds;
    public Map<String, Profile> profiles = new LinkedHashMap<>();
    public Map<String, Line> lines = new LinkedHashMap<>();
    public Map<String, Station> stations = new LinkedHashMap<>();
    public Map<String, Node> nodes = new LinkedHashMap<>();
    public Map<String, Edge> edges = new LinkedHashMap<>();
    public Map<String, Dependency> dependencies = new LinkedHashMap<>();
    public Map<String, Train> trains = new LinkedHashMap<>();
    public Map<String, Incident> incidents = new LinkedHashMap<>();

    public record Profile(String id, String name, int gaugeMm, String power, String signalling, String technology) {}
    public record Line(String id, String name, String color, String operator, String profileId, boolean enabled) {}
    public record Station(String id, String name, double x, double y, boolean enabled) {}
    public record Node(String id, String stationId, String lineId, String direction, boolean enabled) {}
    public record Edge(String id, String from, String to, String resourceId, double lengthMeters,
                       double maxSpeedKmh, boolean connection, boolean verified, boolean enabled, String source) {}
    public record Dependency(String id, String fromLine, String toLine, String reason, boolean operational) {}
    public enum Movement { STOPPED, DWELLING, MOVING, WAITING, BLOCKED }
    public enum Transfer { NONE, IN_PROGRESS, COMPLETED, CANCELLED }
    public enum Severity { INFO, LOW, MEDIUM, HIGH, CRITICAL }
    public enum Target { LINE, STATION, EDGE, TRAIN }
    public enum Effect { NOTICE, SLOW, BLOCK }
    public enum IncidentStatus { OPEN, ACKNOWLEDGED, RESOLVED }
    public record Incident(String id, String label, String description, String category, Severity severity,
                           Target targetType, String targetId, Effect effect, IncidentStatus status,
                           Instant createdAt, Instant resolvedAt, String createdBy, String acknowledgedBy) {
        public boolean open() { return status != IncidentStatus.RESOLVED; }
    }
    public record Impact(String incidentId, String lineId, String label, Severity severity, String level,
                         String effect, List<String> path, String reason) {}
    public record Route(String trainId, String targetLineId, String destinationNodeId, List<String> edgeIds,
                        double estimatedSeconds, int lineChanges, long stateVersion, List<String> steps) {}

    public static final class Train {
        public String id;
        public String number;
        public String model;
        public String profileId;
        public int capacity;
        public int carriages;
        public String assignedLineId;
        public String currentLineId;
        public String nodeId;
        public String edgeId;
        public double progressMeters;
        public double speedKmh;
        public double delaySeconds;
        public double dwellRemaining = 8;
        public boolean active = true;
        public Movement movement = Movement.DWELLING;
        public Transfer transfer = Transfer.NONE;
        public String targetLineId;
        public String targetNodeId;
        public List<String> route = new ArrayList<>();
        public String reason = "";
    }
}

