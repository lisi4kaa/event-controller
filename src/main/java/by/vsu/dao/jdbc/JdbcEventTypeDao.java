package by.vsu.dao.jdbc;

import by.vsu.dao.DaoException;
import by.vsu.dao.EventTypeDao;
import by.vsu.domain.EventType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcEventTypeDao implements EventTypeDao {
    private EventType mapRow(ResultSet rs) throws SQLException {
        EventType type = new EventType();
        type.setId(rs.getLong("id"));
        type.setName(rs.getString("name"));
        type.setCategoryId(rs.getLong("category_id"));
        return type;
    }

    @Override
    public void save(EventType eventType, Connection conn) {
        String sql = "INSERT INTO event_type (name, category_id)" + "VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, eventType.getName());
            ps.setLong(2, eventType.getCategoryId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    eventType.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось сохранить тип события : " + eventType.toString(), e);
        }
    }

    @Override
    public boolean update(EventType eventType, Connection conn) {
        String sql = "UPDATE event_type SET name = ?, category_id = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventType.getName());
            ps.setLong(2, eventType.getCategoryId());
            ps.setLong(3, eventType.getId());
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось обновить тип события : " + eventType.toString(), e);
        }
    }

    @Override
    public boolean delete(Long id, Connection conn) {
        String sql = "DELETE FROM event_type WHERE id =?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось удалить тип события по id : " + id, e);
        }
    }

    @Override
    public Optional<EventType> findEventTypeById(Long id, Connection conn) {
        String sql = "SELECT * FROM event_type WHERE id = ?";
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
            throw new DaoException("Не удалось найти тип события по id : " + id, e);
        }
    }

    @Override
    public List<EventType> findEventTypeByCategoryId(Long categoryId, Connection conn) {
        String sql = "SELECT * FROM event_type WHERE category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, categoryId);
            List<EventType> types = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    types.add(mapRow(rs));
                }
            }
            return types;
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти тип события по categoryId : " + categoryId, e);
        }
    }

    @Override
    public List<EventType> findAll(Connection conn) {
        String sql = "SELECT * FROM event_type";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<EventType> eventTypes = new ArrayList<>();
                while (rs.next()) {
                    eventTypes.add(mapRow(rs));
                }
                return eventTypes;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти типы событий : ", e);
        }
    }
}
