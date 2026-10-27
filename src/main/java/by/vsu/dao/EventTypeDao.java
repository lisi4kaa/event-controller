package by.vsu.dao;

import by.vsu.domain.EventType;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface EventTypeDao {
    void save (EventType eventType, Connection conn);
    boolean update (EventType eventType, Connection conn);
    boolean delete (Long id, Connection conn);
    Optional<EventType> findEventTypeById (Long id, Connection conn);
    List<EventType> findEventTypeByCategoryId (Long categoryId, Connection conn);
    List<EventType> findAll(Connection conn);
}
