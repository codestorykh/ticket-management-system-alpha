CREATE SEQUENCE IF NOT EXISTS api_routes_id_seq start with 1;

CREATE TABLE IF NOT EXISTS api_routes (
    id BIGINT PRIMARY KEY DEFAULT nextval('api_routes_id_seq'),
    uri VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    method VARCHAR(10) NOT NULL,
    description VARCHAR(255),
    group_code VARCHAR(50),
    rate_limit INT DEFAULT 0,
    rate_limit_duration INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50)
);