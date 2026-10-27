package by.vsu.service;

import by.vsu.dao.CategoryDao;
import by.vsu.dao.EventTypeDao;
import by.vsu.domain.EventType;
import by.vsu.dto.EventTypeDto;
import by.vsu.util.TransactionTemplate;

import java.util.List;

public class EventTypeService {
    private final TransactionTemplate tt;
    private final EventTypeDao eventTypeDao;
    private final CategoryDao categoryDao;

    public EventTypeService(TransactionTemplate tt, EventTypeDao eventTypeDao, CategoryDao categoryDao) {
        this.tt = tt;
        this.eventTypeDao = eventTypeDao;
        this.categoryDao = categoryDao;
    }

    private void validate(EventTypeDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidateException("Название типа не может быть пустым");
        }
        if (dto.getName().length() > 255) {
            throw new ValidateException("Название слишком длинное");
        }
        if (dto.getCategoryId() == null) {
            throw new ValidateException("ID категории не может быть пустым");
        }
    }

    private EventType toDomain(EventTypeDto dto) {
        EventType eventType = new EventType();
        eventType.setId(dto.getId());
        eventType.setName(dto.getName());
        eventType.setCategoryId(dto.getCategoryId());
        return eventType;
    }

    private EventTypeDto toResponse(EventType eventType) {
        EventTypeDto dto = new EventTypeDto();
        dto.setId(eventType.getId());
        dto.setName(eventType.getName());
        dto.setCategoryId(eventType.getCategoryId());
        return dto;
    }

    public void create(EventTypeDto dto) {
        validate(dto);
        tt.execute(conn -> {
            categoryDao.findCategoryById(dto.getCategoryId(), conn)
                    .orElseThrow(() -> new ServiceException("Категория не найдена: " + dto.getCategoryId()));
            eventTypeDao.save(toDomain(dto), conn);
            return null;
        });
    }

    public boolean update(EventTypeDto dto) {
        validate(dto);
        if (dto.getId() == null) {
            throw new ServiceException("ID не может быть пустым");
        }
        return tt.execute(conn -> {
            eventTypeDao.findEventTypeById(dto.getId(), conn)
                    .orElseThrow(() -> new ServiceException("Тип события не найден: " + dto.getId()));
            categoryDao.findCategoryById(dto.getCategoryId(), conn)
                    .orElseThrow(() -> new ServiceException("Категория не найдена: " + dto.getCategoryId()));
            return eventTypeDao.update(toDomain(dto), conn);
        });
    }

    public boolean delete(Long id) {
        if (id == null) {
            throw new ServiceException("ID не может быть пустым");
        }
        return tt.execute(conn -> {
            eventTypeDao.findEventTypeById(id, conn)
                    .orElseThrow(() -> new ServiceException("Не удалось найти тип: " + id));
            return eventTypeDao.delete(id, conn);
        });
    }

    public EventTypeDto findById(Long id) {
        if (id == null) {
            throw new ValidateException("ID не может быть пустым");
        }
        return tt.execute(conn -> eventTypeDao.findEventTypeById(id, conn)
                .map(this::toResponse)
                .orElseThrow(() -> new ServiceException("Тип события не найден: " + id)));
    }

    public List<EventTypeDto> findAll() {
        return tt.execute(conn ->
                eventTypeDao.findAll(conn).stream().map(this::toResponse).toList());
    }

    public List<EventTypeDto> findByCategoryId(Long categoryId) {
        if (categoryId == null) {
            throw new ServiceException("ID категории не может быть пустым");
        }
        return tt.execute(conn -> {
            categoryDao.findCategoryById(categoryId, conn)
                    .orElseThrow(() -> new ServiceException("Категория не найдена: " + categoryId));
            return eventTypeDao.findEventTypeByCategoryId(categoryId, conn)
                    .stream().map(this::toResponse).toList();
        });
    }
}