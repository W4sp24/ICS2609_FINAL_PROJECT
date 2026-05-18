package model;

public class Grade {
    private String g_id;
    private String submission_id;
    private String graded_by;
    private double score;
    private String feedback;
    private String graded_at;

    public Grade() {}

    public String getG_id() { return g_id; }
    public void setG_id(String g_id) { this.g_id = g_id; }

    public String getSubmission_id() { return submission_id; }
    public void setSubmission_id(String submission_id) { this.submission_id = submission_id; }

    public String getGraded_by() { return graded_by; }
    public void setGraded_by(String graded_by) { this.graded_by = graded_by; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getGraded_at() { return graded_at; }
    public void setGraded_at(String graded_at) { this.graded_at = graded_at; }
}
