package by.vsu.dao;


import by.vsu.domain.Category;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    void save (Category category, Connection conn);
    boolean update (Category category, Connection conn);
    boolean delete (Long id,Connection conn);
    Optional<Category> findCategoryById(Long id, Connection conn);
    List<Category> findAll(Connection conn);
}
