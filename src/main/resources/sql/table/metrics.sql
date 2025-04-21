CREATE TABLE IF NOT EXISTS metrics
(
    id         SERIAL PRIMARY KEY,
    server_id  INTEGER   NOT NULL,
    project_id INTEGER   NOT NULL,
    name       TEXT      NOT NULL,
    type       TEXT      NOT NULL,
    value      TEXT      NOT NULL,
    timestamp  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (server_id) REFERENCES servers (id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
)
