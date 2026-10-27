package by.vsu.dao;

import by.vsu.domain.Person;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface PersonDao {
    void save (Person person, Connection conn);
    boolean update (Person person, Connection conn);
    boolean delete (Long id, Connection conn);
    Optional<Person> findPersonById(Long id, Connection conn);
    List<Person> findAll(Connection conn);
}
