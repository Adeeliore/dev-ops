CREATE DATABASE IF NOT EXISTS logs;

DROP TABLE IF EXISTS logs.docker_logs;

CREATE TABLE logs.docker_logs (
                                  timestamp     DateTime64(3, 'UTC'),
                                  service_name  String,
                                  log_level     Nullable(String),
                                  message       Nullable(String)
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (service_name, timestamp);
