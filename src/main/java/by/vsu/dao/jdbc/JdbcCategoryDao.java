package by.vsu.dao.jdbc;

import by.vsu.dao.CategoryDao;
import by.vsu.dao.DaoException;
import by.vsu.domain.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCategoryDao implements CategoryDao {

    @Override
    public void save(Category category, Connection conn) {
        String sql = "INSERT INTO category (name, parent_id)" + "VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, category.getName());
            if (category.getParent() != null) {
                ps.setLong(2, category.getParent().getId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    category.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось сохранить категорию : " + category.toString(), e);
        }
    }

    @Override
    public boolean update(Category category, Connection conn) {
        String sql = "UPDATE category SET name = ?, parent_id = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            if (category.getParent() != null) {
                ps.setLong(2, category.getParent().getId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }
            ps.setLong(3, category.getId());
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось обновить категорию : " + category.toString(), e);
        }
    }

    @Override
    public boolean delete(Long id, Connection conn) {
        String sql = "DELETE FROM category WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось удалить category по id : " + id, e);
        }
    }

    @Override
    public Optional<Category> findCategoryById(Long id, Connection conn) {
        String sql = "SELECT * FROM category WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getLong("id"));
                    category.setName(rs.getString("name"));
                    Long parentId = rs.getLong("parent_id");
                    if (!rs.wasNull()) {
                        Category parent = new Category();
                        parent.setId(parentId);
                        category.setParent(parent);
                    }
                    return Optional.of(category);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти категорию по id : " + id, e);
        }
    }

    @Override
    public List<Category> findAll(Connection conn) {
        String sql = "SELECT * FROM category";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Category> categories = new ArrayList<>();
                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getLong("id"));
                    category.setName(rs.getString("name"));
                    long parentId = rs.getLong("parent_id");
                    if(!rs.wasNull()){
                        Category parent = new Category();
                        parent.setId(parentId);
                        category.setParent(parent);
                    }
                    categories.add(category);
                }
                return categories;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти категории : ", e);
        }
    }
}
