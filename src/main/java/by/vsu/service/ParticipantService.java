package by.vsu.service;

import by.vsu.dao.EventDao;
import by.vsu.dao.ParticipantDao;
import by.vsu.dao.PersonDao;
import by.vsu.dao.RoleDao;
import by.vsu.domain.Participant;
import by.vsu.dto.ParticipantCreateUpdateDto;
import by.vsu.dto.ParticipantResponseDto;
import by.vsu.util.TransactionTemplate;

import java.sql.Connection;
import java.util.List;

public class ParticipantService {
    private final TransactionTemplate tt;
    private final ParticipantDao participantDao;
    private final RoleDao roleDao;
    private final PersonDao personDao;
    private final EventDao eventDao;

    public ParticipantService(TransactionTemplate tt, ParticipantDao participantDao, RoleDao roleDao, PersonDao personDao, EventDao eventDao) {
        this.tt = tt;
        this.participantDao = participantDao;
        this.roleDao = roleDao;
        this.personDao = personDao;
        this.eventDao = eventDao;
    }

    private void validate(ParticipantCreateUpdateDto dto) {
        if (dto.getEventId() == null) {
            throw new ValidateException("ID события не должно быть пустым");
        }
        if (dto.getPersonId() == null) {
            throw new ValidateException("ID человека не должно быть пустым");
        }
        if (dto.getRoleId() == null) {
            throw new ValidateException("ID роли не должно быть пустым");
        }
    }

    private Participant toDomain(ParticipantCreateUpdateDto dto) {
        Participant participant = new Participant();
        participant.setId(dto.getId());
        participant.setRoleId(dto.getRoleId());
        participant.setEventId(dto.getEventId());
        participant.setPersonId(dto.getPersonId());
        return participant;
    }

    public ParticipantResponseDto toResponse(Participant participant, Connection conn) {
        ParticipantResponseDto dto = new ParticipantResponseDto();
        dto.setId(participant.getId());
        dto.setRoleId(participant.getRoleId());
        dto.setPersonId(participant.getPersonId());
        dto.setRoleName(roleDao.findRoleById(participant.getRoleId(), conn)
                .orElseThrow(() -> new ServiceException("Роль не найдена: " + participant.getRoleId()))
                .getName());
        dto.setPersonFio(personDao.findPersonById(participant.getPersonId(), conn)
                .orElseThrow(() -> new ServiceException("Человек не найден: " + participant.getPersonId()))
                .getFio());
        return dto;
    }

    public void create(ParticipantCreateUpdateDto dto) {
        validate(dto);
        tt.execute(conn -> {

            personDao.findPersonById(dto.getPersonId(), conn)
                    .orElseThrow(() -> new ServiceException("Человек не найден: " + dto.getPersonId()));
            roleDao.findRoleById(dto.getRoleId(), conn)
                    .orElseThrow(() -> new ServiceException("Роль не найдена: " + dto.getRoleId()));
            eventDao.findEventById(dto.getEventId(), conn)
                    .orElseThrow(() -> new ServiceException("Событие не найдено: " + dto.getEventId()));
            participantDao.save(toDomain(dto), conn);
            return null;
        });
    }

    public boolean update(ParticipantCreateUpdateDto dto) {
        validate(dto);
        if (dto.getId() == null) {
            throw new ValidateException("ID не должен быть пустым");
        }
        return tt.execute(conn -> {
            participantDao.findParticipantById(dto.getId(), conn)
                    .orElseThrow(() -> new ServiceException("Участник не найден: " + dto.getId()));
            personDao.findPersonById(dto.getPersonId(), conn)
                    .orElseThrow(() -> new ServiceException("Человек не найден: " + dto.getPersonId()));
            roleDao.findRoleById(dto.getRoleId(), conn)
                    .orElseThrow(() -> new ServiceException("Роль не найдена: " + dto.getRoleId()));
            eventDao.findEventById(dto.getEventId(), conn)
                    .orElseThrow(() -> new ServiceException("Событие не найдено: " + dto.getEventId()));
            return participantDao.update(toDomain(dto), conn);
        });
    }

    public boolean delete(Long id) {
        if (id == null) {
            throw new ValidateException("ID не должен быть пустым");
        }
        return tt.execute(conn -> {
            participantDao.findParticipantById(id, conn)
                    .orElseThrow(() -> new ServiceException("Участник не найден"));
            return participantDao.delete(id, conn);
        });
    }

    public ParticipantResponseDto findById(Long id) {
        if (id == null) {
            throw new ValidateException("ID участника не должен быть пустым");
        }
        return tt.execute(conn -> {
            return participantDao.findParticipantById(id, conn)
                    .map(e -> toResponse(e, conn))
                    .orElseThrow(() -> new ServiceException("Участник не найден: " + id));
        });
    }

    public List<ParticipantResponseDto> findByRoleId(Long roleId) {
        if (roleId == null) {
            throw new ValidateException("ID роли не должен быть пустым");
        }
        return tt.execute(conn ->
                participantDao.findParticipantByRoleId(roleId, conn).stream().map(p -> toResponse(p, conn)).toList());
    }

    public List<ParticipantResponseDto> findByEventId(Long eventId) {
        if (eventId == null) {
            throw new ValidateException("ID события не должен быть пустым");
        }
        return tt.execute(conn -> {
            return participantDao.findParticipantByEventId(eventId, conn).stream().map(e -> toResponse(e,conn)).toList();
        });
    }

    public List<ParticipantResponseDto> findByPersonId(Long personId) {
        if (personId == null) {
            throw new ValidateException("ID человека не должен быть пустым");
        }
        return tt.execute(conn ->
                participantDao.findParticipantByPersonId(personId, conn).stream().map(p -> toResponse(p, conn)).toList());
    }
}
