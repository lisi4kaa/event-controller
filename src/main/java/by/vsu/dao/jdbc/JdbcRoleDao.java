package by.vsu.dao.jdbc;

import by.vsu.dao.DaoException;
import by.vsu.dao.RoleDao;
import by.vsu.domain.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcRoleDao implements RoleDao {
    private Role mapRow(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));
        role.setName(rs.getString("name"));
        role.setMin(rs.getInt("min"));
        role.setMax(rs.getInt("max"));
        role.setTypeId(rs.getLong("type_id"));
        return role;
    }

    @Override
    public void save(Role role, Connection conn) {
        String sql = "INSERT INTO role (name, min, max, type_id)" + "VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, role.getName());
            ps.setInt(2, role.getMin());
            ps.setInt(3, role.getMax());
            ps.setLong(4, role.getTypeId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    role.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось сохранить роль : " + role.toString(), e);
        }
    }

    @Override
    public boolean update(Role role, Connection conn) {
        String sql = "UPDATE role SET name = ?, min = ?, max = ?, type_id = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.getName());
            ps.setInt(2, role.getMin());
            ps.setInt(3, role.getMax());
            ps.setLong(4, role.getTypeId());
            ps.setLong(5,role.getId());
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось обновить роль : " + role.toString(), e);
        }
    }

    @Override
    public boolean delete(Long id, Connection conn) {
        String sql = "DELETE FROM role WHERE id =?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось удалить роль по id : " + id, e);
        }
    }

    @Override
    public Optional<Role> findRoleById(Long id, Connection conn) {
        String sql = "SELECT * FROM role WHERE id = ?";
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
    public List<Role> findAll(Connection conn) {
        String sql = "SELECT * FROM role ";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Role> roles = new ArrayList<>();
                while (rs.next()) {
                    roles.add(mapRow(rs));
                }
                return roles;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти роли : ", e);
        }
    }

    @Override
    public List<Role> findRoleByTypeId(Long typeId, Connection conn) {
        String sql = "SELECT * FROM role WHERE type_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, typeId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Role> roles = new ArrayList<>();
                while (rs.next()) {
                    roles.add(mapRow(rs));
                }
                return roles;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти роли по typeId : " + typeId, e);
        }
    }
}
