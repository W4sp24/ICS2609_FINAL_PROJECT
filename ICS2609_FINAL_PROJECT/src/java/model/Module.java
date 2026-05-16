package model;

import java.util.ArrayList;
import java.util.List;

public class Module {
    private String mod_id;
    private String course_id;
    private String title;
    private String description;
    private int order; 
    private String created_at;
    private List materials;

    public Module() {
        this.materials = new ArrayList<>(); 
    }

    public String getMod_id() { return mod_id; }
    public void setMod_id(String mod_id) { this.mod_id = mod_id; }

    public String getCourse_id() { return course_id; }
    public void setCourse_id(String course_id) { this.course_id = course_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public List<Material> getMaterials() { return materials; }
    public void setMaterials(List<Material> materials) { this.materials = materials; }
    public void addMaterial(Material material) { this.materials.add(material); }
}