-- Database 3: PostgreSQL (Audit & Activity Logs)
DROP TABLE IF EXISTS ACTIVITY_LOGS;

CREATE TABLE ACTIVITY_LOGS (
    logID SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    activity VARCHAR(100) NOT NULL, 
    activityTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sourceModule VARCHAR(50) NOT NULL
);

-- Sample Data (Matching the @gmail.com format)
INSERT INTO ACTIVITY_LOGS (username, activity, sourceModule) VALUES 
('admin1@gmail.com', 'System Setup', 'Database'),
('guest1@gmail.com', 'User Login', 'Auth');