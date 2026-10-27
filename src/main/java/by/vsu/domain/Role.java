package by.vsu.domain;

public class Role {
    private Long id;
    private String name;
    private int min;
    private int max;
    private Long typeId;

    public Role(Long id, String name, int min, int max, Long typeId) {
        this.id = id;
        this.name = name;
        this.min = min;
        this.max = max;
        this.typeId = typeId;
    }

    public Role() {
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

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", typeId=" + typeId +
                '}';
    }
}
