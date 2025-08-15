-- Insert default routes for user service
INSERT INTO api_routes (uri, path, method, description, group_code, rate_limit, rate_limit_duration, status, created_by) VALUES
('http://localhost:8081', '/api/public/users/**', 'POST', 'User service public endpoints', 'USER_SERVICE', 100, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/users/**', 'GET', 'User service protected endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/users/**', 'POST', 'User service protected endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/users/**', 'PUT', 'User service protected endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/users/**', 'DELETE', 'User service protected endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/roles/**', 'GET', 'Role service endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/roles/**', 'POST', 'Role service endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/roles/**', 'PUT', 'Role service endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/roles/**', 'DELETE', 'Role service endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/groups/**', 'GET', 'Group service endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/groups/**', 'POST', 'Group service endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/groups/**', 'PUT', 'Group service endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/groups/**', 'DELETE', 'Group service endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/permissions/**', 'GET', 'Permission service endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/permissions/**', 'POST', 'Permission service endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/permissions/**', 'PUT', 'Permission service endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system'),
('http://localhost:8081', '/api/permissions/**', 'DELETE', 'Permission service endpoints', 'USER_SERVICE', 50, 60, 'ACTIVE', 'system')
ON CONFLICT DO NOTHING; 