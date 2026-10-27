package by.vsu.service;

import by.vsu.dao.EventTypeDao;
import by.vsu.dao.RoleDao;
import by.vsu.domain.Role;
import by.vsu.dto.RoleDto;
import by.vsu.util.TransactionTemplate;

import java.util.List;

public class RoleService {
    private final TransactionTemplate tt;
    private final RoleDao roleDao;
    private final EventTypeDao eventTypeDao;

    public RoleService(TransactionTemplate tt, RoleDao roleDao, EventTypeDao eventTypeDao) {
        this.tt = tt;
        this.roleDao = roleDao;
        this.eventTypeDao = eventTypeDao;
    }

    private void validate(RoleDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidateException("Название роли не должно быть пустым");
        }
        if (dto.getName().length() > 255) {
            throw new ValidateException("Название роли слишком длинное");
        }
        if (dto.getTypeId() == null) {
            throw new ValidateException("ID категории не может быть пустым");
        }
        if (dto.getMax() < dto.getMin()) {
            throw new ValidateException("Минимальное количество участников не может быть больше максимального");
        }
    }

    private RoleDto toResponse(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        roleDto.setTypeId(role.getTypeId());
        roleDto.setMin(role.getMin());
        roleDto.setMax(role.getMax());
        return roleDto;
    }

    private Role toDomain(RoleDto dto) {
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        role.setTypeId(dto.getTypeId());
        role.setMin(dto.getMin());
        role.setMax(dto.getMax());
        return role;
    }

    public void create(RoleDto dto) {
        validate(dto);
        tt.execute(conn -> {
            eventTypeDao.findEventTypeById(dto.getTypeId(), conn)
                    .orElseThrow(() -> new ServiceException("Тип события не найден: " + dto.getTypeId()));
            roleDao.save(toDomain(dto), conn);
            return null;
        });
    }

    public boolean update(RoleDto dto) {
        validate(dto);
        if (dto.getId() == null) {
            throw new ValidateException("ID роли не может быть пустым");
        }
        return tt.execute(conn -> {
            roleDao.findRoleById(dto.getId(), conn)
                    .orElseThrow(() -> new ServiceException("Роль не найдена"));
            eventTypeDao.findEventTypeById(dto.getTypeId(), conn)
                    .orElseThrow(() -> new ServiceException("Тип события не найден: " + dto.getTypeId()));
            return roleDao.update(toDomain(dto), conn);
        });
    }

    public boolean delete(Long id) {
        if (id == null) {
            throw new ValidateException("ID роли не может быть пустым");
        }
        return tt.execute(conn -> {
            roleDao.findRoleById(id,conn)
                    .orElseThrow(() -> new ServiceException("Роль не найдена"));
            return roleDao.delete(id, conn);
        });
    }

    public RoleDto findById(Long id) {
        if (id == null) {
            throw new ValidateException("ID роли не может быть пустым");
        }
        return tt.execute(conn -> {
            return roleDao.findRoleById(id, conn)
                    .map(this::toResponse)
                    .orElseThrow(() -> new ServiceException("Роль не найдена по id: " + id));
        });
    }

    public List<RoleDto> findAll(){
        return tt.execute(conn -> {
            return  roleDao.findAll(conn).stream().map(this::toResponse).toList();
        });
    }

    public List<RoleDto> findByTypeId(Long typeId){
        if (typeId == null) {
            throw new ValidateException("ID типа не может быть пустым");
        }
        return tt.execute(conn -> {
            return roleDao.findRoleByTypeId(typeId,conn).stream().map(this::toResponse).toList();
        });
    }
}
