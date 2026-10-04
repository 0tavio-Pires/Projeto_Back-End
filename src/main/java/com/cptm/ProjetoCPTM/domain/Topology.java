package com.cptm.ProjetoCPTM.domain;

import java.util.*;
import static com.cptm.ProjetoCPTM.domain.Network.*;

public final class Topology {
    private Topology() {}
    public static <T> T require(Map<String,T> values, String id, String entity) {
        T value = values.get(id);
        if (value == null) throw DomainException.missing(entity + " " + id);
        return value;
    }
    public static void id(String value) {
        if (value == null || !value.matches("[A-Za-z0-9_.:-]{1,100}")) throw DomainException.invalid("Identificador inválido.");
    }
    public static void text(String value, int max, String field) {
        if (value == null || value.isBlank() || value.length() > max) throw DomainException.invalid(field + " inválido.");
    }
    public static void positive(double value, String field) {
        if (!Double.isFinite(value) || value <= 0) throw DomainException.invalid(field + " deve ser positivo.");
    }
    public static boolean compatible(Network s, String trainProfile, String lineId) {
        Line line = s.lines.get(lineId);
        Profile a = s.profiles.get(trainProfile), b = line == null ? null : s.profiles.get(line.profileId());
        return a != null && b != null && a.gaugeMm() == b.gaugeMm() && a.power().equals(b.power())
                && a.signalling().equals(b.signalling()) && a.technology().equals(b.technology());
    }
    public static List<Edge> outgoing(Network s, String node) {
        return s.edges.values().stream().filter(e -> e.from().equals(node)).sorted(Comparator.comparing(Edge::id)).toList();
    }
    /** Destination platforms are reserved for the whole traversal. */
    public static Set<String> occupiedNodes(Network s, String exceptTrain) {
        Set<String> result = new HashSet<>();
        for (Train t : s.trains.values()) if (t.active && !t.id.equals(exceptTrain)) {
            if (t.nodeId != null) result.add(t.nodeId);
            if (t.edgeId != null) result.add(s.edges.get(t.edgeId).to());
        }
        return result;
    }
    public static Set<String> occupiedResources(Network s, String exceptTrain) {
        Set<String> result = new HashSet<>();
        for (Train t : s.trains.values()) if (t.active && t.edgeId != null && !t.id.equals(exceptTrain))
            result.add(s.edges.get(t.edgeId).resourceId());
        return result;
    }
    public static String unavailable(Network s, Train train, Edge edge) {
        Node from = s.nodes.get(edge.from()), to = s.nodes.get(edge.to());
        if (!edge.enabled() || !edge.verified()) return "Via indisponível ou sem verificação.";
        if (!from.enabled() || !to.enabled()) return "Plataforma indisponível.";
        if (!s.lines.get(from.lineId()).enabled() || !s.lines.get(to.lineId()).enabled()) return "Linha desativada.";
        if (!s.stations.get(from.stationId()).enabled() || !s.stations.get(to.stationId()).enabled()) return "Estação desativada.";
        if (!compatible(s, train.profileId, to.lineId())) return "Perfil técnico incompatível.";
        for (Incident i : s.incidents.values()) if (i.open() && i.effect() == Effect.BLOCK && affects(s, i, train, edge))
            return i.label() + " (" + i.id() + ")";
        for (Incident i : s.incidents.values()) if (i.open() && i.effect() == Effect.BLOCK) {
            Set<String> dependent = com.cptm.ProjetoCPTM.incident.ImpactCalculator.operationalDependents(s, i);
            if (dependent.contains(from.lineId()) || dependent.contains(to.lineId())) return "Dependência operacional: " + i.label();
        }
        return null;
    }
    public static boolean affects(Network s, Incident i, Train train, Edge edge) {
        Node from = s.nodes.get(edge.from()), to = s.nodes.get(edge.to());
        return switch (i.targetType()) {
            case LINE -> i.targetId().equals(from.lineId()) || i.targetId().equals(to.lineId());
            case STATION -> i.targetId().equals(from.stationId()) || i.targetId().equals(to.stationId());
            case EDGE -> i.targetId().equals(edge.id());
            case TRAIN -> i.targetId().equals(train.id);
        };
    }
    public static double speed(Network s, Train train, Edge edge) {
        return s.incidents.values().stream().anyMatch(i -> i.open() && i.effect() == Effect.SLOW && (affects(s, i, train, edge)
                || com.cptm.ProjetoCPTM.incident.ImpactCalculator.operationalDependents(s,i).contains(s.nodes.get(edge.from()).lineId())
                || com.cptm.ProjetoCPTM.incident.ImpactCalculator.operationalDependents(s,i).contains(s.nodes.get(edge.to()).lineId())))
                ? Math.min(15, edge.maxSpeedKmh()) : edge.maxSpeedKmh();
    }
    public static void validate(Network s) {
        if (s == null || s.schemaVersion != 1) throw DomainException.invalid("Versão de dados incompatível.");
        if (s.lines == null || s.stations == null || s.profiles == null || s.nodes == null || s.edges == null
                || s.dependencies == null || s.trains == null || s.incidents == null || s.sources == null)
            throw DomainException.invalid("Coleções da rede são obrigatórias.");
        text(s.name,200,"Nome da rede"); text(s.notes,5000,"Notas do cenário");
        if(s.referenceDate==null || !s.referenceDate.matches("\\d{4}-\\d{2}-\\d{2}")) throw DomainException.invalid("Data de referência inválida.");
        for(Map<String,?> map:List.of(s.lines,s.stations,s.profiles,s.nodes,s.edges,s.dependencies,s.trains,s.incidents)) map.keySet().forEach(Topology::id);
        if(s.sources.size()>30 || s.sources.stream().anyMatch(v->v==null || v.length()>2000)) throw DomainException.invalid("Fontes inválidas.");
        if(s.profiles.size()>1000 || s.dependencies.size()>10000) throw DomainException.invalid("Limite de perfis ou dependências excedido.");
        if (s.lines.size() > 100 || s.stations.size() > 5000 || s.nodes.size() > 20000 || s.edges.size() > 40000
                || s.trains.size() > 2000 || s.incidents.size() > 10000) throw DomainException.invalid("Limite de tamanho da rede excedido.");
        if (!Double.isFinite(s.timeScale) || s.timeScale < 0.1 || s.timeScale > 60 || !Double.isFinite(s.elapsedSeconds) || s.elapsedSeconds < 0)
            throw DomainException.invalid("Relógio de simulação inválido.");
        s.profiles.forEach((key,p) -> {
            if (p == null || !key.equals(p.id())) throw DomainException.invalid("Perfil inconsistente.");
            id(key); text(p.name(),120,"Nome"); text(p.power(),120,"Alimentação"); text(p.signalling(),120,"Sinalização"); text(p.technology(),120,"Tecnologia");
            if (p.gaugeMm() < 0) throw DomainException.invalid("Bitola inválida.");
        });
        s.lines.forEach((key,l) -> {
            if (l == null || !key.equals(l.id())) throw DomainException.invalid("Linha inconsistente.");
            id(key); text(l.name(),120,"Nome"); text(l.operator(),120,"Operador"); require(s.profiles,l.profileId(),"Perfil");
            if (l.color() == null || !l.color().matches("#[0-9a-fA-F]{6}")) throw DomainException.invalid("Cor deve usar #RRGGBB.");
        });
        s.stations.forEach((key,v) -> {
            if (v == null || !key.equals(v.id())) throw DomainException.invalid("Estação inconsistente.");
            id(key); text(v.name(),150,"Nome");
            if (!Double.isFinite(v.x()) || !Double.isFinite(v.y()) || Math.abs(v.x()) > 10000 || Math.abs(v.y()) > 10000)
                throw DomainException.invalid("Coordenadas inválidas.");
        });
        s.nodes.forEach((key,n) -> {
            if (n == null || !key.equals(n.id())) throw DomainException.invalid("Plataforma inconsistente.");
            id(key); require(s.stations,n.stationId(),"Estação"); require(s.lines,n.lineId(),"Linha");
            if (!"A".equals(n.direction()) && !"B".equals(n.direction())) throw DomainException.invalid("Sentido inválido.");
        });
        s.edges.forEach((key,e) -> {
            if (e == null || !key.equals(e.id())) throw DomainException.invalid("Via inconsistente.");
            id(key); id(e.resourceId()); Node a=require(s.nodes,e.from(),"Plataforma"), b=require(s.nodes,e.to(),"Plataforma");
            positive(e.lengthMeters(),"Extensão"); positive(e.maxSpeedKmh(),"Velocidade");
            if (e.maxSpeedKmh() > 200 || e.lengthMeters() > 100000) throw DomainException.invalid("Limites físicos inválidos.");
            if (!a.lineId().equals(b.lineId()) && !e.connection()) throw DomainException.invalid("Ligação entre linhas deve ser operacional.");
            if (e.verified()) text(e.source(),1000,"Fonte de verificação");
            if (e.enabled() && !e.verified()) throw DomainException.invalid("Via sem verificação não pode ser habilitada.");
        });
        s.dependencies.forEach((key,d) -> {
            if (d == null || !key.equals(d.id())) throw DomainException.invalid("Dependência inconsistente.");
            id(key); require(s.lines,d.fromLine(),"Linha"); require(s.lines,d.toLine(),"Linha"); text(d.reason(),300,"Motivo");
        });
        Set<String> numbers=new HashSet<>(), nodes=new HashSet<>(), resources=new HashSet<>();
        s.trains.forEach((key,t) -> {
            if (t == null || !key.equals(t.id)) throw DomainException.invalid("Trem inconsistente.");
            id(key); text(t.number,40,"Composição"); text(t.model,120,"Modelo");
            if (!numbers.add(t.number.toUpperCase(Locale.ROOT))) throw DomainException.conflict("Número de composição duplicado.");
            require(s.profiles,t.profileId,"Perfil"); require(s.lines,t.assignedLineId,"Linha designada");
            require(s.lines,t.currentLineId,"Linha atual");
            if (!compatible(s,t.profileId,t.assignedLineId) || !compatible(s,t.profileId,t.currentLineId))
                throw DomainException.conflict("Trem incompatível com a linha.");
            if (t.capacity < 1 || t.capacity > 10000 || t.carriages < 1 || t.carriages > 30
                    || !Double.isFinite(t.progressMeters) || t.progressMeters < 0 || !Double.isFinite(t.delaySeconds) || t.delaySeconds < 0
                    || !Double.isFinite(t.dwellRemaining) || t.dwellRemaining < 0 || !Double.isFinite(t.speedKmh) || t.speedKmh < 0
                    || t.route == null || t.route.size()>40000 || t.movement == null || t.transfer == null || t.reason==null || t.reason.length()>2000) throw DomainException.invalid("Estado do trem inválido.");
            if ((t.nodeId == null) == (t.edgeId == null)) throw DomainException.invalid("Trem deve ter exatamente uma localização.");
            String locationNode;
            if (t.edgeId != null) {
                Edge e=require(s.edges,t.edgeId,"Via"); locationNode=e.to();
                if (t.progressMeters > e.lengthMeters() || !s.nodes.get(e.from()).lineId().equals(t.currentLineId))
                    throw DomainException.invalid("Posição do trem inconsistente.");
                if (!t.active) throw DomainException.conflict("Trem em trecho não pode sair de circulação.");
                if (!resources.add(e.resourceId())) throw DomainException.conflict("Via ocupada por mais de um trem.");
            } else {
                Node n=require(s.nodes,t.nodeId,"Plataforma"); locationNode=n.id();
                if (!n.lineId().equals(t.currentLineId) || t.progressMeters != 0) throw DomainException.invalid("Linha atual inconsistente.");
            }
            if (t.active && !nodes.add(locationNode)) throw DomainException.conflict("Plataforma ocupada ou reservada.");
            String cursor=locationNode;
            for (String edgeId:t.route) {
                Edge e=require(s.edges,edgeId,"Via da rota");
                if (!e.from().equals(cursor) || !compatible(s,t.profileId,s.nodes.get(e.to()).lineId()))
                    throw DomainException.invalid("Rota descontínua ou incompatível.");
                cursor=e.to();
            }
            if (t.transfer == Transfer.IN_PROGRESS) {
                require(s.lines,t.targetLineId,"Destino");
                Node destination=require(s.nodes,t.targetNodeId,"Plataforma de destino");
                if (!destination.lineId().equals(t.targetLineId) || !cursor.equals(t.targetNodeId))
                    throw DomainException.invalid("Destino da transferência inconsistente.");
            }
            else if(!t.route.isEmpty() || !t.assignedLineId.equals(t.currentLineId)) throw DomainException.invalid("Trem sem transferência deve circular na linha designada.");
        });
        s.incidents.forEach((key,i) -> {
            if (i == null || !key.equals(i.id()) || i.targetType()==null || i.effect()==null || i.severity()==null || i.status()==null || i.createdAt()==null)
                throw DomainException.invalid("Ocorrência inválida.");
            id(key); text(i.label(),120,"Rótulo"); text(i.description(),2000,"Descrição"); text(i.category(),60,"Categoria");
            text(i.createdBy(),100,"Autor");
            if((i.status()==IncidentStatus.RESOLVED)!=(i.resolvedAt()!=null)) throw DomainException.invalid("Data de resolução inconsistente.");
            switch(i.targetType()) {
                case LINE -> require(s.lines,i.targetId(),"Linha"); case STATION -> require(s.stations,i.targetId(),"Estação");
                case EDGE -> require(s.edges,i.targetId(),"Via"); case TRAIN -> require(s.trains,i.targetId(),"Trem");
            }
        });
    }
}

