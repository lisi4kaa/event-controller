package by.vsu.domain;

import java.time.LocalDateTime;

public class Event {
    private Long id;
    private String name;
    private String description;
    private Long eventTypeId;
    private LocalDateTime start;
    private LocalDateTime finish;

    public Event(String name, String description, Long eventTypeId, LocalDateTime start, LocalDateTime finish, Long id) {
        this.name = name;
        this.description = description;
        this.eventTypeId = eventTypeId;
        this.start = start;
        this.finish = finish;
        this.id = id;
    }

    public Event() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getFinish() {
        return finish;
    }

    public void setFinish(LocalDateTime finish) {
        this.finish = finish;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", eventTypeId=" + eventTypeId +
                ", start=" + start +
                ", finish=" + finish +
                '}';
    }
}
