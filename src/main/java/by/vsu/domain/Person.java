package by.vsu.domain;

public class Person {
    private Long id;
    private String fio;

    public Person(Long id, String fio) {
        this.id = id;
        this.fio = fio;
    }

    public Person() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", fio='" + fio + '\'' +
                '}';
    }
}
