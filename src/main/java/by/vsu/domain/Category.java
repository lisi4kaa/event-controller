package by.vsu.domain;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private Long id;
    private String name;
    private Category parent;
    private List<Category> children = new ArrayList<>();

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(Long id, String name, Category parent) {
        this.id = id;
        this.name = name;
        setParent(parent);
    }

    public Category() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
        }
        this.parent = parent;
        if (parent != null && !parent.getChildren().contains(this)) {
            parent.getChildren().add(this);
        }
    }

    public List<Category> getChildren() {
        return children;
    }

    public void addChildren(Category child) {
        child.setParent(this);
        this.children.add(child);
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parent=" + parent +
                ", children=" + children +
                '}';
    }
}
