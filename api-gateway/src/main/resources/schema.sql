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

CREATE SEQUENCE IF NOT EXISTS request_logs_id_seq start with 1;

CREATE TABLE IF NOT EXISTS request_logs (
    id BIGINT PRIMARY KEY DEFAULT nextval('request_logs_id_seq'),
    request_id VARCHAR(50) NOT NULL,
    correlation_id VARCHAR(50),
    method VARCHAR(10) NOT NULL,
    uri VARCHAR(500) NOT NULL,
    path VARCHAR(255),
    client_ip VARCHAR(45),
    user_agent TEXT,
    request_headers TEXT,
    request_body TEXT,
    request_size BIGINT,
    response_status INTEGER,
    response_headers TEXT,
    duration_ms BIGINT,
    service_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes separately
CREATE INDEX IF NOT EXISTS idx_request_logs_request_id ON request_logs(request_id);
CREATE INDEX IF NOT EXISTS idx_request_logs_correlation_id ON request_logs(correlation_id);
CREATE INDEX IF NOT EXISTS idx_request_logs_created_at ON request_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_request_logs_method_uri ON request_logs(method, uri);
CREATE INDEX IF NOT EXISTS idx_request_logs_response_status ON request_logs(response_status);
CREATE INDEX IF NOT EXISTS idx_request_logs_duration_ms ON request_logs(duration_ms);