-- Database 3: PostgreSQL (Audit & Activity Logs)
DROP TABLE IF EXISTS activity_logs;

CREATE TABLE activity_logs (
    log_id        SERIAL          PRIMARY KEY,
    username      VARCHAR(255)    NOT NULL,
    action        VARCHAR(100)    NOT NULL,
    ip_address    VARCHAR(45)     NOT NULL,
    role          VARCHAR(20)     NOT NULL,
    source        VARCHAR(100)    NOT NULL,
    log_timestamp TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO activity_logs (username, action, ip_address, role, source) VALUES
('sys.admin1@school.edu', 'System Setup', '127.0.0.1', 'Admin', 'Database'),
('student01@mail.com',    'User Login',   '127.0.0.1', 'Guest', 'Auth');
