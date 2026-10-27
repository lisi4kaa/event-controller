package by.vsu.domain;

public class Participant {
    private Long id;
    private Long personId;
    private Long eventId;
    private Long roleId;

    public Participant(Long id, Long personId, Long eventId, Long roleId) {
        this.id = id;
        this.personId = personId;
        this.eventId = eventId;
        this.roleId = roleId;
    }

    public Participant() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", personId=" + personId +
                ", eventId=" + eventId +
                ", roleId=" + roleId +
                '}';
    }
}
