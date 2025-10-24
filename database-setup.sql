CREATE DATABASE IF NOT EXISTS codifica_guali
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;


USE codifica_guali;


CREATE USER IF NOT EXISTS 'guali_user'@'%' IDENTIFIED BY 'guali_password';
GRANT ALL PRIVILEGES ON codifica_guali.* TO 'guali_user'@'%';
FLUSH PRIVILEGES;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tracks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    grid_data TEXT NOT NULL,
    grid_width INT NOT NULL DEFAULT 5,
    grid_height INT NOT NULL DEFAULT 4,
    start_x INT NOT NULL,
    start_y INT NOT NULL,
    start_direction VARCHAR(10) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    details TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_action (action),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS game_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    track_id BIGINT,
    completed BOOLEAN NOT NULL,
    attempts INT NOT NULL,
    session_ip VARCHAR(50),
    user_agent VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (track_id) REFERENCES tracks(id) ON DELETE SET NULL,
    INDEX idx_track_id (track_id),
    INDEX idx_completed (completed),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Password: admin123 (BCrypt hash)
INSERT INTO users (username, password, email, role, active) VALUES
('admin', '$2a$10$8.VJ5VqKvCzjPvZ5qRZLB.lYuJrGvQHsf9fJKxN5kYxN0XxGxGxGG', 'admin@codificaguali.com', 'ADMIN', TRUE)
ON DUPLICATE KEY UPDATE username=username;

-- Nota: La contraseña 'admin123' está hasheada con BCrypt para mayor seguridad.

-- PISTAS DE EJEMPLO

-- Pista 1: Camino Recto Simple
INSERT INTO tracks (name, grid_data, grid_width, grid_height, start_x, start_y, start_direction, active) VALUES
('Camino Recto', 
'[[false,false,false,false,false],[false,false,false,false,false],[false,false,false,false,false],[true,true,true,true,true]]',
5, 4, 0, 3, 'RIGHT', TRUE)
ON DUPLICATE KEY UPDATE name=name;

-- Pista 2: Camino en L
INSERT INTO tracks (name, grid_data, grid_width, grid_height, start_x, start_y, start_direction, active) VALUES
('Camino en L',
'[[false,false,true,false,false],[false,false,true,false,false],[false,false,true,false,false],[true,true,true,false,false]]',
5, 4, 0, 3, 'RIGHT', TRUE)
ON DUPLICATE KEY UPDATE name=name;

-- Pista 3: Camino Cuadrado
INSERT INTO tracks (name, grid_data, grid_width, grid_height, start_x, start_y, start_direction, active) VALUES
('Camino Cuadrado',
'[[false,false,false,false,false],[false,true,true,true,false],[false,true,false,true,false],[false,true,true,true,false]]',
5, 4, 1, 3, 'RIGHT', TRUE)
ON DUPLICATE KEY UPDATE name=name;

-- Pista 4: Camino en Zigzag
INSERT INTO tracks (name, grid_data, grid_width, grid_height, start_x, start_y, start_direction, active) VALUES
('Camino Zigzag',
'[[false,false,true,true,true],[false,false,true,false,false],[false,true,true,false,false],[true,true,false,false,false]]',
5, 4, 0, 3, 'RIGHT', TRUE)
ON DUPLICATE KEY UPDATE name=name;

-- Pista 5: Camino en U
INSERT INTO tracks (name, grid_data, grid_width, grid_height, start_x, start_y, start_direction, active) VALUES
('Camino en U',
'[[false,true,false,true,false],[false,true,false,true,false],[false,true,false,true,false],[false,true,true,true,false]]',
5, 4, 1, 3, 'RIGHT', TRUE)
ON DUPLICATE KEY UPDATE name=name;


SELECT 
    'Tablas creadas correctamente' AS status,
    COUNT(*) AS total_tables
FROM information_schema.tables
WHERE table_schema = 'codifica_guali';

SELECT 
    'Usuario admin creado' AS status,
    username, 
    email, 
    active,
    created_at
FROM users
WHERE username = 'admin';

SELECT 
    'Pistas de ejemplo cargadas' AS status,
    COUNT(*) AS total_pistas
FROM tracks;

SELECT 
    id,
    name,
    CONCAT(grid_width, 'x', grid_height) AS dimensiones,
    IF(active, 'Activa', 'Inactiva') AS estado
FROM tracks
ORDER BY id;
