-- Database 1: Apache Derby (Authentication)
CREATE TABLE USERS (
    username VARCHAR(50) PRIMARY KEY 
        CONSTRAINT user_format_check CHECK (username LIKE '%@%.%'),
    
    password VARCHAR(255) NOT NULL,
    
    role VARCHAR(10) NOT NULL 
        CONSTRAINT role_check CHECK (role IN ('Admin', 'Guest')),
    
    createdDate DATE NOT NULL
);

-- 15 Admins
INSERT INTO USERS (username, password, role, createdDate) VALUES
('sys.admin1@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-01'),
('sys.admin2@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-01'),
('sys.admin3@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-01'),
('prof.turing@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-05'),
('prof.hopper@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-05'),
('prof.lovelace@school.edu','ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-05'),
('prof.einstein@school.edu','ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-06'),
('prof.curie@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-06'),
('prof.newton@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-06'),
('prof.tesla@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-07'),
('prof.bohr@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-07'),
('prof.feynman@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-07'),
('prof.hawking@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-08'),
('prof.sagan@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-08'),
('prof.tyson@school.edu', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Admin', '2026-01-08');

-- 35 Guests (Students)
INSERT INTO USERS (username, password, role, createdDate) VALUES
('student01@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-10'),
('student02@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-10'),
('student03@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-10'),
('student04@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-11'),
('student05@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-11'),
('student06@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-11'),
('student07@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-12'),
('student08@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-12'),
('student09@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-12'),
('student10@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-13'),
('student11@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-13'),
('student12@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-13'),
('student13@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-14'),
('student14@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-14'),
('student15@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-14'),
('student16@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-15'),
('student17@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-15'),
('student18@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-15'),
('student19@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-16'),
('student20@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-16'),
('student21@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-16'),
('student22@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-17'),
('student23@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-17'),
('student24@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-17'),
('student25@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-18'),
('student26@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-18'),
('student27@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-18'),
('student28@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-19'),
('student29@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-19'),
('student30@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-19'),
('student31@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-20'),
('student32@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-20'),
('student33@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-20'),
('student34@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-21'),
('student35@mail.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Guest', '2026-01-21');

