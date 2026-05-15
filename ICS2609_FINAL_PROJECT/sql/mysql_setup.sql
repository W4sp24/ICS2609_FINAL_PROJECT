-- Database 2: MySQL (Couse Management)
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

INSERT INTO COURSES (courseCode, courseName, instructor, availableSlots, createdDate, endDate) VALUES
('CS101', 'Introduction to Computing', 'Dr. Alan Turing', 45, '2026-01-10', '2026-05-20'),
('ITS312', 'Database Management Systems', 'Prof. Grace Hopper', 30, '2026-01-10', '2026-05-20'),
('ETHICS2', 'Social Ethics & Professionalism', 'Dr. Jose Rizal', 50, '2026-01-12', '2026-05-22'),
('HIST1872', 'Philippine History: Cavite Mutiny', 'Prof. Teodoro Agoncillo', 25, '2026-01-12', '2026-05-22'),
('MATH402', 'Differential Equations', 'Dr. Leonhard Euler', 40, '2026-01-10', '2026-05-20'),
('AI500', 'Artificial Intelligence Algorithms', 'Dr. Yann LeCun', 20, '2026-01-15', '2026-05-25');

INSERT INTO COURSE_SCHEDULES (courseCode, startTime, endTime, daysOfWeek) VALUES
('CS101', '08:00:00', '09:30:00', 'Mon, Wed, Fri'),
('ITS312', '10:00:00', '12:00:00', 'Tue, Thu'),
('ETHICS2', '13:00:00', '14:30:00', 'Mon, Wed, Fri'),
('HIST1872', '15:00:00', '16:30:00', 'Tue, Thu'),
('MATH402', '07:30:00', '09:00:00', 'Tue, Thu'),
('AI500', '14:00:00', '17:00:00', 'Fri');

INSERT INTO ENROLLMENTS (courseCode, username) VALUES
('CS101', 'user01@mail.com'),
('CS101', 'tester_a@demo.org'),
('ITS312', 'ace_admin@corp.com'),
('ITS312', 'm.rossi@tech.net'),
('ETHICS2', 'visit.22@site.net'),
('ETHICS2', 'hello_world@js.io'),
('HIST1872', 'c.kent@daily.com'),
('HIST1872', 'd.prince@themyscira.org'),
('MATH402', 't.stark@stark.com'),
('MATH402', 'p.parker@bugle.com'),
('AI500', 'n.neo@matrix.net'),
('AI500', 's.rogers@shield.gov');