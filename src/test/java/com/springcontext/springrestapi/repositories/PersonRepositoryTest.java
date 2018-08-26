package com.springcontext.springrestapi.repositories;

import com.springcontext.springrestapi.entities.Address;
import com.springcontext.springrestapi.entities.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PersonRepositoryTest {

    /* Address constants */

    private static final String COUNTRY = "Canada";
    private static final String CITY = "Vancouver";
    private static final String ZIP_CODE = "A0A 0A0";
    private static final String STREET = "Main street";
    private static final Integer STREET_NUMBER = 1234;

    /* Person constants */

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void save_should_createNewPerson() {

        this.createPersonAndAddress();

        List<Person> persons = this.personRepository.findAll();

        assertThat(persons).isNotNull().hasSize(1);

        Person createdPerson = persons.get(0);

        List<Address> createdAddresses = createdPerson.getAddresses();

        assertThat(createdAddresses).isNotNull().hasSize(1);

        Address address = createdAddresses.get(0);

        assertThat(address.getCity()).isNotNull().isNotEmpty().isEqualTo(CITY);
        assertThat(address.getCountry()).isNotNull().isNotEmpty().isEqualTo(COUNTRY);
        assertThat(address.getId()).isNotNull().isGreaterThan(0);
        assertThat(address.getStreet()).isNotNull().isNotEmpty().isEqualTo(STREET);
        assertThat(address.getZipCode()).isNotNull().isNotEmpty().isEqualTo(ZIP_CODE);
        assertThat(address.getStreetNumber()).isNotNull().isEqualTo(STREET_NUMBER);
        assertThat(address.getPeople()).isNotNull().hasSize(1);

        assertThat(createdPerson).isNotNull();

        assertThat(createdPerson.getId()).isNotNull().isGreaterThan(0);
        assertThat(createdPerson.getFirstname()).isNotNull().isNotEmpty().isEqualTo(FIRST_NAME);
        assertThat(createdPerson.getLastname()).isNotNull().isNotEmpty().isEqualTo(LAST_NAME);
        assertThat(createdPerson.getAddresses()).isNotNull().hasSize(1);
    }

    @Test
    public void deleteById_should_deleteExistingCompany() {

        this.createPersonAndAddress();

        List<Person> people = this.personRepository.findAll();

        assertThat(people).isNotNull().hasSize(1);

        Person person = people.get(0);

        this.personRepository.deleteById(person.getId());

        people = this.personRepository.findAll();

        assertThat(people).isNotNull().isEmpty();
    }

    private void createPersonAndAddress() {

        this.personRepository.deleteAll();

        List<Person> persons = this.personRepository.findAll();

        assertThat(persons).isNotNull().isEmpty();

        Address address = new Address();
        Person person = new Person();

        address.setCountry(COUNTRY);
        address.setCity(CITY);
        address.setZipCode(ZIP_CODE);
        address.setStreet(STREET);
        address.setStreetNumber(STREET_NUMBER);

        person.setFirstname(FIRST_NAME);
        person.setLastname(LAST_NAME);

        address.addPerson(person);
        person.addAddress(address);

        this.personRepository.save(person);
    }
}
