package by.vsu.service;

import by.vsu.dao.PersonDao;
import by.vsu.domain.Person;
import by.vsu.dto.PersonDto;
import by.vsu.util.TransactionTemplate;

import java.util.List;

public class PersonService {
    private final TransactionTemplate tt;
    private final PersonDao personDao;

    public PersonService(TransactionTemplate tt, PersonDao personDao) {
        this.tt = tt;
        this.personDao = personDao;
    }

    private void validate(PersonDto dto){
        if(dto.getFio() == null || dto.getFio().isBlank()){
            throw new ValidateException("ФИО не должно быть пустым");
        }
        if(dto.getFio().length() > 255){
            throw new ValidateException("ФИО слишком длинное");
        }
    }

    private Person toDomain(PersonDto dto){
        Person person = new Person();
        person.setId(dto.getId());
        person.setFio(dto.getFio());
        return person;
    }

    private PersonDto toResponse(Person person){
        PersonDto dto = new PersonDto();
        dto.setId(person.getId());
        dto.setFio(person.getFio());
        return dto;
    }

    public void create(PersonDto dto){
        validate(dto);
        tt.execute(conn ->{
            personDao.save(toDomain(dto),conn);
            return null;
        });
    }

    public boolean update(PersonDto dto){
        validate(dto);
        if(dto.getId() == null ){
            throw new ServiceException("ID не может быть пустым");
        }
        return tt.execute(conn ->{
            personDao.findPersonById(dto.getId(), conn)
                    .orElseThrow(() -> new ServiceException("Человек не найден"));
            return personDao.update(toDomain(dto),conn);
        });
    }

    public boolean delete(Long id){
        if(id == null ){
            throw new ServiceException("ID не может быть пустым");
        }
        return tt.execute(conn ->{
            personDao.findPersonById(id,conn)
                    .orElseThrow(() -> new ServiceException("Человек не найден"));
            return personDao.delete(id,conn);
        });
    }

    public PersonDto findById(Long id){
        if(id == null ){
            throw new ServiceException("ID не может быть пустым");
        }
        return tt.execute(conn ->{
            return personDao.findPersonById(id, conn)
                    .map(this::toResponse)
                    .orElseThrow(() -> new ServiceException("Человек не найден: " + id));
        });
    }

    public List<PersonDto> findAll(){
        return tt.execute(conn ->{
            List<Person> persons = personDao.findAll(conn);
            return persons.stream().map(this::toResponse).toList();
        });
    }
}
