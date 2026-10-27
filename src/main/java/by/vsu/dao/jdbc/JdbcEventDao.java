package by.vsu.dao.jdbc;

import by.vsu.dao.DaoException;
import by.vsu.dao.EventDao;
import by.vsu.domain.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcEventDao implements EventDao {
    private Event mapRow(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getLong("id"));
        event.setName(rs.getString("name"));
        event.setDescription(rs.getString("description"));
        event.setEventTypeId(rs.getLong("event_type_id"));
        event.setStart(rs.getObject("start", java.time.LocalDateTime.class));
        event.setFinish(rs.getObject("finish", java.time.LocalDateTime.class));
        return event;
    }

    @Override
    public void save(Event event, Connection conn) {
        String sql = "INSERT INTO event (name, description, event_type_id, start, finish)" + "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription());
            ps.setLong(3, event.getEventTypeId());
            ps.setObject(4, event.getStart());
            ps.setObject(5, event.getFinish());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    event.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось сохранить событие : " + event.toString(), e);
        }
    }

    @Override
    public boolean update(Event event, Connection conn) {
        String sql = "UPDATE event SET name = ?, description = ?, event_type_id = ?, start = ?, finish = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription());
            ps.setLong(3, event.getEventTypeId());
            ps.setObject(4, event.getStart());
            ps.setObject(5, event.getFinish());
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось обновить событие : " + event.toString(), e);
        }
    }

    @Override
    public boolean delete(Long id, Connection conn) {
        String sql = "DELETE FROM event WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось удалить событие по id : " + id, e);
        }
    }

    @Override
    public Optional<Event> findEventById(Long id, Connection conn) {
        String sql = "SELECT * FROM event WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти событие по id : " + id, e);
        }
    }

    @Override
    public List<Event> findEventByEventTypeId(Long eventTypeId, Connection conn) {
        String sql = "SELECT * FROM event WHERE event_type_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Event> events = new ArrayList<>();
                while (rs.next()) {
                    events.add(mapRow(rs));
                }
                return events;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти событие по eventTypeId : " + eventTypeId, e);
        }
    }

    @Override
    public List<Event> findAll(Connection conn) {
        String sql = "SELECT * FROM event ";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Event> events = new ArrayList<>();
                while (rs.next()) {
                    events.add(mapRow(rs));
                }
                return events;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти события : ", e);
        }
    }
}
