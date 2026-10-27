package by.vsu.dao;

import by.vsu.domain.Event;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface EventDao {
    void save (Event event, Connection conn);
    boolean update (Event event, Connection conn);
    boolean delete (Long id, Connection conn);
    Optional<Event> findEventById (Long id, Connection conn);
    List<Event> findEventByEventTypeId(Long eventTypeId, Connection conn);
    List<Event> findAll(Connection conn);
}
