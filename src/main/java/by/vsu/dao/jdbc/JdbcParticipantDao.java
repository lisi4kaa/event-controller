package by.vsu.dao.jdbc;

import by.vsu.dao.DaoException;
import by.vsu.dao.ParticipantDao;
import by.vsu.domain.Participant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcParticipantDao implements ParticipantDao {
    private Participant mapRow(ResultSet rs) throws SQLException {
        Participant participant = new Participant();
        participant.setId(rs.getLong("id"));
        participant.setPersonId(rs.getLong("person_id"));
        participant.setEventId(rs.getLong("event_id"));
        participant.setRoleId(rs.getLong("role_id"));
        return participant;
    }

    @Override
    public void save(Participant participant, Connection conn) {
        String sql = "INSERT INTO participant (person_id, event_id, role_id)" + "VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, participant.getPersonId());
            ps.setLong(2, participant.getEventId());
            ps.setLong(3, participant.getRoleId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    participant.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось сохранить участника : " + participant.toString(), e);
        }
    }

    @Override
    public boolean update(Participant participant, Connection conn) {
        String sql = "UPDATE participant SET person_id = ?, event_id = ?, role_id = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, participant.getPersonId());
            ps.setLong(2, participant.getEventId());
            ps.setLong(3, participant.getRoleId());
            ps.setLong(4, participant.getId());
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось обновить участника  : " + participant.toString(), e);
        }
    }

    @Override
    public boolean delete(Long id, Connection conn) {
        String sql = "DELETE FROM participant WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось удалить участника по id : " + id, e);
        }
    }

    @Override
    public Optional<Participant> findParticipantById(Long id, Connection conn) {
        String sql = "SELECT * FROM participant WHERE id = ?";
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
            throw new DaoException("Не удалось найти участника по id : " + id, e);
        }
    }

    @Override
    public List<Participant> findParticipantByPersonId(Long personId, Connection conn) {
        String sql = "SELECT * FROM participant WHERE person_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, personId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Participant> participants = new ArrayList<>();
                while (rs.next()) {
                    participants.add(mapRow(rs));
                }
                return participants;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти участников по personId : " + personId, e);
        }
    }

    @Override
    public List<Participant> findParticipantByEventId(Long eventId, Connection conn) {
        String sql = "SELECT * FROM participant WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Participant> participants = new ArrayList<>();
                while (rs.next()) {
                    participants.add(mapRow(rs));
                }
                return participants;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти участника по eventId : " + eventId, e);
        }
    }


    @Override
    public List<Participant> findParticipantByRoleId(Long roleId, Connection conn) {
        String sql = "SELECT * FROM participant WHERE role_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Participant> participants = new ArrayList<>();
                while (rs.next()) {
                    participants.add(mapRow(rs));
                }
                return participants;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти участников по roleId : " + roleId, e);
        }
    }

    @Override
    public List<Participant> findAll(Connection conn) {
        String sql = "SELECT * FROM participant";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Participant> participants = new ArrayList<>();
                while (rs.next()) {
                    participants.add(mapRow(rs));
                }
                return participants;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти участников : ", e);
        }
    }

    @Override
    public boolean deleteByEventId(Long eventId, Connection conn) {
        String sql = "DELETE FROM participant WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось удалить участника по event_id : " + eventId, e);
        }
    }
}
