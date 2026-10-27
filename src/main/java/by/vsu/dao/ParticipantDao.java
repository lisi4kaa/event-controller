package by.vsu.dao;

import by.vsu.domain.Participant;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface ParticipantDao {
    void save (Participant participant, Connection conn);
    boolean update (Participant participant, Connection conn);
    boolean delete (Long id, Connection conn);
    Optional<Participant> findParticipantById(Long id, Connection conn);
    List<Participant> findParticipantByPersonId (Long personId, Connection conn);
    List<Participant> findParticipantByEventId (Long eventId, Connection conn);
    List<Participant> findParticipantByRoleId (Long roleId, Connection conn);
    List<Participant> findAll(Connection conn);
    boolean deleteByEventId(Long eventId, Connection conn);
}
