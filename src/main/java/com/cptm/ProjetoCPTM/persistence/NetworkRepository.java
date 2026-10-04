package com.cptm.ProjetoCPTM.persistence;

import com.cptm.ProjetoCPTM.domain.*;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;
import java.time.Instant;
import java.util.*;

@Repository
@DependsOnDatabaseInitialization
public class NetworkRepository {
    private final JdbcTemplate jdbc;
    private final JsonMapper json;
    public NetworkRepository(JdbcTemplate jdbc, JsonMapper json) { this.jdbc=jdbc; this.json=json; }
    public Optional<Network> load() {
        return jdbc.query("SELECT payload FROM network_state WHERE id=1", (r,n)->json.readValue(r.getString(1),Network.class)).stream().findFirst();
    }
    @Transactional
    public void initialize(Network state) {
        jdbc.update("INSERT INTO network_state(id,version,payload,updated_at) VALUES(1,?,?,?)",
                state.version,json.writeValueAsString(state),state.updatedAt.toString());
    }
    @Transactional
    public void save(Network state, long expectedVersion, String actor, String action, String detail) {
        int changed=jdbc.update("UPDATE network_state SET version=?,payload=?,updated_at=? WHERE id=1 AND version=?",
                state.version,json.writeValueAsString(state),state.updatedAt.toString(),expectedVersion);
        if(changed != 1) throw DomainException.conflict("Estado alterado por outra instância. Recarregue o servidor.");
        if(action != null) jdbc.update("INSERT INTO audit_event(id,version,occurred_at,actor,action,detail) VALUES(?,?,?,?,?,?)",
                UUID.randomUUID().toString(),state.version,Instant.now().toString(),actor,action,detail);
    }
    public List<Map<String,Object>> history() {
        return jdbc.query("SELECT id,version,occurred_at,actor,action,detail FROM audit_event ORDER BY version DESC FETCH FIRST 200 ROWS ONLY",
                (r,n)->Map.of("id",r.getString(1),"version",r.getLong(2),"at",r.getString(3),"actor",r.getString(4),"action",r.getString(5),"detail",r.getString(6)));
    }
}

