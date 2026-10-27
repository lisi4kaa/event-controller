package by.vsu.dao.jdbc;

import by.vsu.dao.DaoException;
import by.vsu.dao.PersonDao;
import by.vsu.domain.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPersonDao implements PersonDao {

    private Person mapRow(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setId(rs.getLong("id"));
        person.setFio(rs.getString("fio"));
        return person;
    }

    @Override
    public void save(Person person, Connection conn) {
        String sql = "INSERT INTO person (fio)" + "VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, person.getFio());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    person.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось сохранить человека : " + person.toString(), e);
        }
    }

    @Override
    public boolean update(Person person, Connection conn) {
        String sql = "UPDATE person SET fio = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, person.getFio());
            ps.setLong(2, person.getId());
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось обновить человека : " + person.toString(), e);
        }
    }

    @Override
    public boolean delete(Long id, Connection conn) {
        String sql = "DELETE FROM person WHERE id =?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DaoException("Не удалось удалить человека по id : " + id, e);
        }
    }

    @Override
    public Optional<Person> findPersonById(Long id, Connection conn) {
        String sql = "SELECT * FROM person WHERE id = ?";
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
            throw new DaoException("Не удалось найти человека : ", e);
        }
    }

    @Override
    public List<Person> findAll(Connection conn) {
        String sql = "SELECT * FROM person";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Person> people = new ArrayList<>();
                while (rs.next()) {
                    people.add(mapRow(rs));
                }
                return people;
            }
        } catch (SQLException e) {
            throw new DaoException("Не удалось найти людей : ", e);
        }
    }
}
