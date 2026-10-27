package by.vsu.service;

import by.vsu.dao.*;
import by.vsu.domain.Category;
import by.vsu.domain.Event;
import by.vsu.domain.EventType;
import by.vsu.domain.Participant;
import by.vsu.dto.EventCreateUpdateDto;
import by.vsu.dto.EventResponseDto;
import by.vsu.dto.ParticipantResponseDto;
import by.vsu.util.TransactionTemplate;

import java.sql.Connection;
import java.util.List;

public class EventService {
    private final TransactionTemplate tt;
    private final EventDao eventDao;
    private final EventTypeDao eventTypeDao;
    private final CategoryDao categoryDao;
    private final ParticipantDao participantDao;
    private final RoleDao roleDao;
    private final PersonDao personDao;

    public EventService(TransactionTemplate tt, EventDao eventDao, EventTypeDao categoryDao, CategoryDao categoryDao1, ParticipantDao participantDao, RoleDao roleDao, PersonDao personDao) {
        this.tt = tt;
        this.eventDao = eventDao;
        this.eventTypeDao = categoryDao;
        this.categoryDao = categoryDao1;
        this.participantDao = participantDao;
        this.roleDao = roleDao;
        this.personDao = personDao;
    }

    private void validate(EventCreateUpdateDto event) {
        if (event.getStart() == null) {
            throw new ValidateException("Время начала события не должно быть пустым ");
        }
        if (event.getFinish() == null) {
            throw new ValidateException("Время конца события не должно быть пустым ");
        }
        if (event.getFinish().isBefore(event.getStart())) {
            throw new ValidateException("Время конца события не должно быть раньше начала ");
        }
        if (event.getEventTypeId() == null) {
            throw new ValidateException("ID типа события не может быть пустым");
        }
        if (event.getName() == null || event.getName().isBlank()) {
            throw new ValidateException("Название события не может быть пустым");
        }
        if (event.getName().length() > 255) {
            throw new ValidateException("Название события слишком длинное");
        }
    }

    private ParticipantResponseDto toResponseParticipant(Participant participant, Connection conn) {
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

    private EventResponseDto toResponse(Event event, Connection conn) {
        EventType type = eventTypeDao.findEventTypeById(event.getEventTypeId(), conn)
                .orElseThrow(() -> new ServiceException("Тип события не найден: " + event.getEventTypeId()));
        Category category = categoryDao.findCategoryById(type.getCategoryId(), conn)
                .orElseThrow(() -> new ServiceException("Категория не найдена: " + type.getCategoryId()));
        EventResponseDto dto = new EventResponseDto();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setStart(event.getStart());
        dto.setFinish(event.getFinish());
        dto.setEventTypeId(type.getId());
        dto.setEventTypeName(type.getName());
        dto.setCategoryId(category.getId());
        dto.setCategoryName(category.getName());
        dto.setParticipants(participantDao.findParticipantByEventId(event.getId(), conn)
                .stream().map(e -> toResponseParticipant(e, conn)).toList());
        return dto;
    }

    private Event toDomain(EventCreateUpdateDto dto) {
        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setEventTypeId(dto.getEventTypeId());
        event.setDescription(dto.getDescription());
        event.setStart(dto.getStart());
        event.setFinish(dto.getFinish());
        return event;
    }

    public EventResponseDto create(EventCreateUpdateDto dto) {
        validate(dto);
        return tt.execute(conn -> {
            eventTypeDao.findEventTypeById(dto.getEventTypeId(), conn)
                    .orElseThrow(() -> new ServiceException("Тип события не найден: " + dto.getEventTypeId()));

            Event event = toDomain(dto);
            eventDao.save(event, conn);

            if (dto.getParticipants() != null) {
                for (Participant p : dto.getParticipants()) {
                    p.setId(null);
                    p.setEventId(event.getId());
                    participantDao.save(p, conn);
                }
            }
            return toResponse(event, conn);
        });
    }

    public boolean update(EventCreateUpdateDto dto) {
        validate(dto);
        if (dto.getId() == null) {
            throw new ValidateException("ID события не может быть пустым");
        }
        return tt.execute(conn -> {
            eventDao.findEventById(dto.getId(), conn)
                    .orElseThrow(() -> new ServiceException("Событие не найдено: " + dto.getId()));
            eventTypeDao.findEventTypeById(dto.getEventTypeId(), conn)
                    .orElseThrow(() -> new ServiceException("Тип события не найден: " + dto.getEventTypeId()));

            boolean updated = eventDao.update(toDomain(dto), conn);

            participantDao.deleteByEventId(dto.getId(), conn);
            if (dto.getParticipants() != null) {
                for (Participant p : dto.getParticipants()) {
                    p.setId(null);
                    p.setEventId(dto.getId());
                    participantDao.save(p, conn);
                }
            }
            return updated;
        });
    }

    public boolean delete(Long id) {
        if (id == null) {
            throw new ValidateException("ID события не может быть пустым");
        }
        return tt.execute(conn -> {
            eventDao.findEventById(id, conn)
                    .orElseThrow(() -> new ServiceException("Событие не найдено: " + id));
            participantDao.deleteByEventId(id, conn);
            return eventDao.delete(id, conn);
        });
    }

    public EventResponseDto findById(Long id) {
        if (id == null) {
            throw new ValidateException("ID события не может быть пустым");
        }
        return tt.execute(conn -> {
            Event event = eventDao.findEventById(id, conn)
                    .orElseThrow(() -> new ServiceException("Событие не найдено"));
            return toResponse(event, conn);
        });
    }

    public List<EventResponseDto> findAll() {
        return tt.execute(conn -> {
            return eventDao.findAll(conn).stream().map(e -> toResponse(e, conn)).toList();
        });
    }

}
