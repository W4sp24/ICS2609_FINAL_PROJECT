package model;

public class Assignment {
    private String a_id;
    private String module_id;
    private String title;
    private String instructions;
    private String due_date;
    private double max_score;
    private String created_at;

    public Assignment() {}

    public String getA_id() { return a_id; }
    public void setA_id(String a_id) { this.a_id = a_id; }

    public String getModule_id() { return module_id; }
    public void setModule_id(String module_id) { this.module_id = module_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getDue_date() { return due_date; }
    public void setDue_date(String due_date) { this.due_date = due_date; }

    public double getMax_score() { return max_score; }
    public void setMax_score(double max_score) { this.max_score = max_score; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}
