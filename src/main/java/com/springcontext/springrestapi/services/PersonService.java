package com.springcontext.springrestapi.services;

import com.springcontext.springrestapi.entities.Address;
import com.springcontext.springrestapi.entities.Person;
import com.springcontext.springrestapi.repositories.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person create(Person person) {
        log.info("Adding a new person - firstname {}, lastname {}", person.getFirstname(), person.getLastname());

        return this.personRepository.save(person);
    }

    public Person getById(long id) {
        log.info("Trying to fetch Person with id {}", id);

        Optional<Person> person = this.personRepository.findById(id);

        if (person.isPresent()) {
            return person.get();
        }

        log.error("No person found with id {}", id);

        return null;
    }

    public Person addAddress(long id, Address address) {

        Person person = this.getById(id);

        log.info("Adding new address to Person with id {}", id);

        person.addAddress(address);

        return this.personRepository.save(person);
    }

    public void delete(long id) {
        log.info("Deleting Person with id {}", id);

        this.personRepository.deleteById(id);
    }

    public void removeCompany(Person person) {
        person.setCompany(null);

        this.personRepository.save(person);
    }
}