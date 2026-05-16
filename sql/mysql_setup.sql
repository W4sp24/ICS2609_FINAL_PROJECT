-- Table 1: Main Course Info
CREATE TABLE COURSES (
    courseCode VARCHAR(10) PRIMARY KEY,
    courseName VARCHAR(100) NOT NULL,
    instructor VARCHAR(100) NOT NULL,
    availableSlots INT DEFAULT 50 CHECK (availableSlots >= 0 AND availableSlots <= 50),
    createdDate DATE NOT NULL,
    endDate DATE NOT NULL
);

-- Table 2: Detailed Schedule (Linked via Foreign Key)
CREATE TABLE COURSE_SCHEDULES (
    scheduleID INT AUTO_INCREMENT PRIMARY KEY,
    courseCode VARCHAR(10),
    startTime TIME NOT NULL,
    endTime TIME NOT NULL,
    daysOfWeek VARCHAR(50) NOT NULL, 
    CONSTRAINT fk_course FOREIGN KEY (courseCode) 
        REFERENCES COURSES(courseCode) 
        ON DELETE CASCADE,
    CONSTRAINT chk_days_not_empty CHECK (LENGTH(daysOfWeek) > 0)
);

CREATE TABLE ENROLLMENTS (
    enrollmentID INT AUTO_INCREMENT PRIMARY KEY,
    courseCode VARCHAR(10),
    username VARCHAR(50), -- Logical link to Derby
    enrollmentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_enroll_course FOREIGN KEY (courseCode) 
        REFERENCES COURSES(courseCode)
);