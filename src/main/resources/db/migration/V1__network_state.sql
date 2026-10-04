CREATE TABLE network_state (
  id INTEGER PRIMARY KEY,
  version BIGINT NOT NULL,
  payload TEXT NOT NULL,
  updated_at VARCHAR(40) NOT NULL
);
CREATE TABLE audit_event (
  id VARCHAR(40) PRIMARY KEY,
  version BIGINT NOT NULL,
  occurred_at VARCHAR(40) NOT NULL,
  actor VARCHAR(100) NOT NULL,
  action VARCHAR(100) NOT NULL,
  detail VARCHAR(2000) NOT NULL
);
CREATE INDEX audit_event_version_idx ON audit_event(version);

