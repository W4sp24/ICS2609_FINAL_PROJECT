CREATE DATABASE IF NOT EXISTS course_management_db;
USE course_management_db;

CREATE TABLE users (
    u_id        CHAR(36)        NOT NULL DEFAULT (UUID()),
    email       VARCHAR(255)    NOT NULL,
    first_name  VARCHAR(100)    NOT NULL,
    last_name   VARCHAR(100)    NOT NULL,
    role        ENUM('teacher', 'student', 'admin') NOT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (u_id),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE courses (
    c_id        CHAR(36)        NOT NULL DEFAULT (UUID()),
    teacher_id  CHAR(36)        NOT NULL,
    title       VARCHAR(255)    NOT NULL,
    description TEXT,
    status      ENUM('draft', 'published', 'archived') NOT NULL DEFAULT 'draft',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (c_id),
    CONSTRAINT fk_courses_teacher
        FOREIGN KEY (teacher_id) REFERENCES users(u_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE enrollments (
    e_id        CHAR(36)        NOT NULL DEFAULT (UUID()),
    course_id   CHAR(36)        NOT NULL,
    student_id  CHAR(36)        NOT NULL,
    status      ENUM('active', 'completed', 'dropped') NOT NULL DEFAULT 'active',
    enrolled_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (e_id),
    UNIQUE KEY uq_enrollment (course_id, student_id),
    CONSTRAINT fk_enrollments_course
        FOREIGN KEY (course_id) REFERENCES courses(c_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_enrollments_student
        FOREIGN KEY (student_id) REFERENCES users(u_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE modules (
    mod_id      CHAR(36)        NOT NULL DEFAULT (UUID()),
    course_id   CHAR(36)        NOT NULL,
    title       VARCHAR(255)    NOT NULL,
    description TEXT,
    `order`     INT UNSIGNED    NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (mod_id),
    CONSTRAINT fk_modules_course
        FOREIGN KEY (course_id) REFERENCES courses(c_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE materials (
    mat_id      CHAR(36)        NOT NULL DEFAULT (UUID()),
    module_id   CHAR(36)        NOT NULL,
    title       VARCHAR(255)    NOT NULL,
    type        ENUM('video', 'document', 'link') NOT NULL,
    url         TEXT            NOT NULL,
    `order`     INT UNSIGNED    NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (mat_id),
    CONSTRAINT fk_materials_module
        FOREIGN KEY (module_id) REFERENCES modules(mod_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE assignments (
    a_id         CHAR(36)        NOT NULL DEFAULT (UUID()),
    module_id    CHAR(36)        NOT NULL,
    title        VARCHAR(255)    NOT NULL,
    instructions TEXT            NOT NULL,
    due_date     DATETIME,
    max_score    DECIMAL(5, 2),
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (a_id),
    CONSTRAINT fk_assignments_module
        FOREIGN KEY (module_id) REFERENCES modules(mod_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE submissions (
    s_id          CHAR(36)        NOT NULL DEFAULT (UUID()),
    assignment_id CHAR(36)        NOT NULL,
    student_id    CHAR(36)        NOT NULL,
    file_url      TEXT            NOT NULL,
    status        ENUM('draft', 'submitted', 'graded') NOT NULL DEFAULT 'draft',
    submitted_at  DATETIME,

    PRIMARY KEY (s_id),
    UNIQUE KEY uq_submission (assignment_id, student_id),
    CONSTRAINT fk_submissions_assignment
        FOREIGN KEY (assignment_id) REFERENCES assignments(a_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_submissions_student
        FOREIGN KEY (student_id) REFERENCES users(u_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE grades (
    g_id          CHAR(36)        NOT NULL DEFAULT (UUID()),
    submission_id CHAR(36)        NOT NULL,
    graded_by     CHAR(36)        NOT NULL,
    score         DECIMAL(5, 2)   NOT NULL,
    feedback      TEXT,
    graded_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (g_id),
    UNIQUE KEY uq_grade_per_submission (submission_id),
    CONSTRAINT fk_grades_submission
        FOREIGN KEY (submission_id) REFERENCES submissions(s_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_grades_teacher
        FOREIGN KEY (graded_by) REFERENCES users(u_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- 1. USERS
-- Admins (3)
INSERT INTO users (u_id, email, first_name, last_name, role) VALUES
('e8100000-0000-4000-8000-000000000001', 'sys.admin1@school.edu', 'System', 'Admin One', 'admin'),
('e8100000-0000-4000-8000-000000000002', 'sys.admin2@school.edu', 'System', 'Admin Two', 'admin'),
('e8100000-0000-4000-8000-000000000003', 'sys.admin3@school.edu', 'System', 'Admin Three', 'admin');

-- Teachers (12)
INSERT INTO users (u_id, email, first_name, last_name, role) VALUES
('e8100000-0000-4000-8000-000000000004', 'prof.turing@school.edu', 'Alan', 'Turing', 'teacher'),
('e8100000-0000-4000-8000-000000000005', 'prof.hopper@school.edu', 'Grace', 'Hopper', 'teacher'),
('e8100000-0000-4000-8000-000000000006', 'prof.lovelace@school.edu', 'Ada', 'Lovelace', 'teacher'),
('e8100000-0000-4000-8000-000000000007', 'prof.einstein@school.edu', 'Albert', 'Einstein', 'teacher'),
('e8100000-0000-4000-8000-000000000008', 'prof.curie@school.edu', 'Marie', 'Curie', 'teacher'),
('e8100000-0000-4000-8000-000000000009', 'prof.newton@school.edu', 'Isaac', 'Newton', 'teacher'),
('e8100000-0000-4000-8000-000000000010', 'prof.tesla@school.edu', 'Nikola', 'Tesla', 'teacher'),
('e8100000-0000-4000-8000-000000000011', 'prof.bohr@school.edu', 'Niels', 'Bohr', 'teacher'),
('e8100000-0000-4000-8000-000000000012', 'prof.feynman@school.edu', 'Richard', 'Feynman', 'teacher'),
('e8100000-0000-4000-8000-000000000013', 'prof.hawking@school.edu', 'Stephen', 'Hawking', 'teacher'),
('e8100000-0000-4000-8000-000000000014', 'prof.sagan@school.edu', 'Carl', 'Sagan', 'teacher'),
('e8100000-0000-4000-8000-000000000015', 'prof.tyson@school.edu', 'Neil', 'Tyson', 'teacher');

-- Students (35)
INSERT INTO users (u_id, email, first_name, last_name, role) VALUES
('e8100000-0000-4000-8000-000000000016', 'student01@mail.com', 'John', 'Doe', 'student'),
('e8100000-0000-4000-8000-000000000017', 'student02@mail.com', 'Jane', 'Smith', 'student'),
('e8100000-0000-4000-8000-000000000018', 'student03@mail.com', 'Alice', 'Johnson', 'student'),
('e8100000-0000-4000-8000-000000000019', 'student04@mail.com', 'Bob', 'Williams', 'student'),
('e8100000-0000-4000-8000-000000000020', 'student05@mail.com', 'Charlie', 'Brown', 'student'),
('e8100000-0000-4000-8000-000000000021', 'student06@mail.com', 'Diana', 'Prince', 'student'),
('e8100000-0000-4000-8000-000000000022', 'student07@mail.com', 'Evan', 'Wright', 'student'),
('e8100000-0000-4000-8000-000000000023', 'student08@mail.com', 'Fiona', 'Gallagher', 'student'),
('e8100000-0000-4000-8000-000000000024', 'student09@mail.com', 'George', 'Miller', 'student'),
('e8100000-0000-4000-8000-000000000025', 'student10@mail.com', 'Hannah', 'Abbott', 'student'),
('e8100000-0000-4000-8000-000000000026', 'student11@mail.com', 'Ian', 'Malcolm', 'student'),
('e8100000-0000-4000-8000-000000000027', 'student12@mail.com', 'Julia', 'Roberts', 'student'),
('e8100000-0000-4000-8000-000000000028', 'student13@mail.com', 'Kevin', 'Hart', 'student'),
('e8100000-0000-4000-8000-000000000029', 'student14@mail.com', 'Liam', 'Neeson', 'student'),
('e8100000-0000-4000-8000-000000000030', 'student15@mail.com', 'Mia', 'Wallace', 'student'),
('e8100000-0000-4000-8000-000000000031', 'student16@mail.com', 'Noah', 'Centineo', 'student'),
('e8100000-0000-4000-8000-000000000032', 'student17@mail.com', 'Olivia', 'Pope', 'student'),
('e8100000-0000-4000-8000-000000000033', 'student18@mail.com', 'Peter', 'Parker', 'student'),
('e8100000-0000-4000-8000-000000000034', 'student19@mail.com', 'Quinn', 'Fabray', 'student'),
('e8100000-0000-4000-8000-000000000035', 'student20@mail.com', 'Rachel', 'Green', 'student'),
('e8100000-0000-4000-8000-000000000036', 'student21@mail.com', 'Sam', 'Winchester', 'student'),
('e8100000-0000-4000-8000-000000000037', 'student22@mail.com', 'Tina', 'Belcher', 'student'),
('e8100000-0000-4000-8000-000000000038', 'student23@mail.com', 'Ursula', 'Buffay', 'student'),
('e8100000-0000-4000-8000-000000000039', 'student24@mail.com', 'Victor', 'Stone', 'student'),
('e8100000-0000-4000-8000-000000000040', 'student25@mail.com', 'Wanda', 'Maximoff', 'student'),
('e8100000-0000-4000-8000-000000000041', 'student26@mail.com', 'Xander', 'Harris', 'student'),
('e8100000-0000-4000-8000-000000000042', 'student27@mail.com', 'Yvonne', 'Strahovski', 'student'),
('e8100000-0000-4000-8000-000000000043', 'student28@mail.com', 'Zack', 'Morris', 'student'),
('e8100000-0000-4000-8000-000000000044', 'student29@mail.com', 'Arthur', 'Dent', 'student'),
('e8100000-0000-4000-8000-000000000045', 'student30@mail.com', 'Bella', 'Swan', 'student'),
('e8100000-0000-4000-8000-000000000046', 'student31@mail.com', 'Clark', 'Kent', 'student'),
('e8100000-0000-4000-8000-000000000047', 'student32@mail.com', 'Daisy', 'Johnson', 'student'),
('e8100000-0000-4000-8000-000000000048', 'student33@mail.com', 'Ethan', 'Hunt', 'student'),
('e8100000-0000-4000-8000-000000000049', 'student34@mail.com', 'Fox', 'Mulder', 'student'),
('e8100000-0000-4000-8000-000000000050', 'student35@mail.com', 'Gemma', 'Teller', 'student');


-- 2. COURSES
INSERT INTO courses (c_id, teacher_id, title, description, status) VALUES
('c8100000-0000-4000-8000-000000000001', 'e8100000-0000-4000-8000-000000000004', 'Intro to Computer Science', 'Learn the basics of algorithms.', 'published'),
('c8100000-0000-4000-8000-000000000002', 'e8100000-0000-4000-8000-000000000005', 'Database Systems', 'Relational models and SQL.', 'published'),
('c8100000-0000-4000-8000-000000000003', 'e8100000-0000-4000-8000-000000000007', 'Physics 101', 'Classical mechanics and thermodynamics.', 'draft');


-- 3. ENROLLMENTS
INSERT INTO enrollments (e_id, course_id, student_id, status) VALUES
('n8100000-0000-4000-8000-000000000001', 'c8100000-0000-4000-8000-000000000001', 'e8100000-0000-4000-8000-000000000016', 'active'),
('n8100000-0000-4000-8000-000000000002', 'c8100000-0000-4000-8000-000000000001', 'e8100000-0000-4000-8000-000000000017', 'active'),
('n8100000-0000-4000-8000-000000000003', 'c8100000-0000-4000-8000-000000000002', 'e8100000-0000-4000-8000-000000000018', 'active'),
('n8100000-0000-4000-8000-000000000004', 'c8100000-0000-4000-8000-000000000002', 'e8100000-0000-4000-8000-000000000016', 'active');


-- 4. MODULES
INSERT INTO modules (mod_id, course_id, title, description, `order`) VALUES
('m8100000-0000-4000-8000-000000000001', 'c8100000-0000-4000-8000-000000000001', 'Week 1: Basics', 'Variables and loops', 1),
('m8100000-0000-4000-8000-000000000002', 'c8100000-0000-4000-8000-000000000002', 'Week 1: E-R Diagrams', 'Entity relationship basics', 1);


-- 5. MATERIALS
INSERT INTO materials (mat_id, module_id, title, type, url, `order`) VALUES
('t8100000-0000-4000-8000-000000000001', 'm8100000-0000-4000-8000-000000000001', 'Intro Video', 'video', 'https://video.host/cs101-w1', 1),
('t8100000-0000-4000-8000-000000000002', 'm8100000-0000-4000-8000-000000000002', 'ER Diagram PDF', 'document', 'https://docs.host/er-diagrams.pdf', 1);


-- 6. ASSIGNMENTS
INSERT INTO assignments (a_id, module_id, title, instructions, due_date, max_score) VALUES
('a8100000-0000-4000-8000-000000000001', 'm8100000-0000-4000-8000-000000000001', 'Hello World Lab', 'Write a hello world script.', '2026-05-30 23:59:59', 100.00),
('a8100000-0000-4000-8000-000000000002', 'm8100000-0000-4000-8000-000000000002', 'Design a Schema', 'Draw an ER diagram for a library.', '2026-06-05 23:59:59', 100.00);


-- 7. SUBMISSIONS
INSERT INTO submissions (s_id, assignment_id, student_id, file_url, status, submitted_at) VALUES
('s8100000-0000-4000-8000-000000000001', 'a8100000-0000-4000-8000-000000000001', 'e8100000-0000-4000-8000-000000000016', 'https://files.host/john_doe_hw1.py', 'graded', '2026-05-28 14:00:00'),
('s8100000-0000-4000-8000-000000000002', 'a8100000-0000-4000-8000-000000000002', 'e8100000-0000-4000-8000-000000000018', 'https://files.host/alice_schema.pdf', 'submitted', '2026-06-01 09:30:00');


-- 8. GRADES
INSERT INTO grades (g_id, submission_id, graded_by, score, feedback) VALUES
('g8100000-0000-4000-8000-000000000001', 's8100000-0000-4000-8000-000000000001', 'e8100000-0000-4000-8000-000000000004', 95.50, 'Great job on the loop structure!');