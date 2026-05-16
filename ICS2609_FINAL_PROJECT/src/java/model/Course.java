package model;

public class Course {
    private String c_id;
    private String teacher_id;
    private String title;
    private String description;
    private String status;
    private String created_at;

    public Course() {}

    public String getC_id() { return c_id; }
    public void setC_id(String c_id) { this.c_id = c_id; }

    public String getTeacher_id() { return teacher_id; }
    public void setTeacher_id(String teacher_id) { this.teacher_id = teacher_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
    public List<User> getEnrolledStudents() { return enrolledStudents; }
    public void setEnrolledStudents(List<User> students) { this.enrolledStudents = students; }
    public void addStudent(User student) { this.enrolledStudents.add(student); }

    public List<Module> getModules() { return modules; }
    public void setModules(List<Module> modules) { this.modules = modules; }
    public void addModule(Module module) { this.modules.add(module); }

    public List<Assignment> getAssignments() { return assignments; }
    public void setAssignments(List<Assignment> assignments) { this.assignments = assignments; }
    public void addAssignment(Assignment assignment) { this.assignments.add(assignment); }
}