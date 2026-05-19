package model;

public class Material {
    private String mat_id;
    private String module_id;
    private String title;
    private String type;
    private String url;
    private int order;
    private String created_at;

    public Material() {}

    public String getMat_id() { return mat_id; }
    public void setMat_id(String mat_id) { this.mat_id = mat_id; }

    public String getModule_id() { return module_id; }
    public void setModule_id(String module_id) { this.module_id = module_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}
