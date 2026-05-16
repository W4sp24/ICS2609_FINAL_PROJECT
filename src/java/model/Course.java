package model;

import java.sql.Timestamp;

/**
 * Model representing a Course record.
 * Stored in MySQL (DBMS 2) as the main business data.
 *
 * SQL Table Schema (MySQL):
 * --------------------------------------------------
 * CREATE TABLE courses (
 *     course_id    INT AUTO_INCREMENT PRIMARY KEY,
 *     course_name  VARCHAR(200) NOT NULL,
 *     instructor   VARCHAR(150) NOT NULL,
 *     schedule     VARCHAR(100),
 *     created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 * );
 * --------------------------------------------------
 */
public class Course {

    private int courseId;
    private String courseName;
    private String instructor;
    private String schedule;
    private Timestamp createdDate;

    public Course() {}

    public Course(int courseId, String courseName, String instructor,
                  String schedule, Timestamp createdDate) {
        this.courseId   = courseId;
        this.courseName = courseName;
        this.instructor = instructor;
        this.schedule   = schedule;
        this.createdDate = createdDate;
    }

    public int getCourseId()                           { return courseId; }
    public void setCourseId(int courseId)              { this.courseId = courseId; }

    public String getCourseName()                      { return courseName; }
    public void setCourseName(String courseName)       { this.courseName = courseName; }

    public String getInstructor()                      { return instructor; }
    public void setInstructor(String instructor)       { this.instructor = instructor; }

    public String getSchedule()                        { return schedule; }
    public void setSchedule(String schedule)           { this.schedule = schedule; }

    public Timestamp getCreatedDate()                  { return createdDate; }
    public void setCreatedDate(Timestamp createdDate)  { this.createdDate = createdDate; }

    @Override
    public String toString() {
        return "Course{courseId=" + courseId + ", courseName=" + courseName
                + ", instructor=" + instructor + "}";
    }
}
