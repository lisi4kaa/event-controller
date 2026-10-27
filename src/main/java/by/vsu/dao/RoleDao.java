package by.vsu.dao;

import by.vsu.domain.Role;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface RoleDao {
    void save (Role role, Connection conn);
    boolean update (Role role, Connection conn);
    boolean delete (Long id, Connection conn);
    Optional<Role> findRoleById (Long id, Connection conn);
    List<Role> findAll(Connection conn);
    List<Role> findRoleByTypeId (Long typeId, Connection conn);
}
