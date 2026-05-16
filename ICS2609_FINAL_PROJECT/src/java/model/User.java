package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    // --- 1. Security & Profile Data ---
    private String email; 
    private String systemRole;
    private String appRole;   
    private String u_id;       
    private String firstName;
    private String lastName;
    private String createdAt;

    // --- 2. Relational Data (Lists belonging ONLY to this user) ---
    private List<Course> coursesTaught;
    
    private List<Course> enrolledCourses;
    private List<Grade> myGrades;
    private List<Submission> mySubmissions;

    public User() {
        this.coursesTaught = new ArrayList<>();
        this.enrolledCourses = new ArrayList<>();
        this.myGrades = new ArrayList<>();
        this.mySubmissions = new ArrayList<>();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSystemRole() { return systemRole; }
    public void setSystemRole(String systemRole) { this.systemRole = systemRole; }

    public String getAppRole() { return appRole; }
    public void setAppRole(String appRole) { this.appRole = appRole; }

    public String getU_id() { return u_id; }
    public void setU_id(String u_id) { this.u_id = u_id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public List<Course> getCoursesTaught() { return coursesTaught; }
    public void setCoursesTaught(List<Course> coursesTaught) { this.coursesTaught = coursesTaught; }
    public void addCourseTaught(Course course) { this.coursesTaught.add(course); }

    public List<Course> getEnrolledCourses() { return enrolledCourses; }
    public void setEnrolledCourses(List<Course> enrolledCourses) { this.enrolledCourses = enrolledCourses; }
    public void addEnrolledCourse(Course course) { this.enrolledCourses.add(course); }

    public List<Grade> getMyGrades() { return myGrades; }
    public void setMyGrades(List<Grade> myGrades) { this.myGrades = myGrades; }
    public void addGrade(Grade grade) { this.myGrades.add(grade); }

    public List<Submission> getMySubmissions() { return mySubmissions; }
    public void setMySubmissions(List<Submission> mySubmissions) { this.mySubmissions = mySubmissions; }
    public void addSubmission(Submission sub) { this.mySubmissions.add(sub); }
}