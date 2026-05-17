package model;

public class Submission {
    private String s_id;
    private String assignment_id;
    private String student_id;
    private String file_url;
    private String status;
    private String submitted_at;

    public Submission() {}

    public String getS_id() { return s_id; }
    public void setS_id(String s_id) { this.s_id = s_id; }

    public String getAssignment_id() { return assignment_id; }
    public void setAssignment_id(String assignment_id) { this.assignment_id = assignment_id; }

    public String getStudent_id() { return student_id; }
    public void setStudent_id(String student_id) { this.student_id = student_id; }

    public String getFile_url() { return file_url; }
    public void setFile_url(String file_url) { this.file_url = file_url; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubmitted_at() { return submitted_at; }
    public void setSubmitted_at(String submitted_at) { this.submitted_at = submitted_at; }
}
