package model;

public class Enrollment {
    private String e_id;
    private String course_id;
    private String student_id;
    private String status;
    private String enrolled_at;

    public Enrollment() {}

    public String getE_id() { return e_id; }
    public void setE_id(String e_id) { this.e_id = e_id; }

    public String getCourse_id() { return course_id; }
    public void setCourse_id(String course_id) { this.course_id = course_id; }

    public String getStudent_id() { return student_id; }
    public void setStudent_id(String student_id) { this.student_id = student_id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEnrolled_at() { return enrolled_at; }
    public void setEnrolled_at(String enrolled_at) { this.enrolled_at = enrolled_at; }
}
