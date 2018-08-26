package com.springcontext.springrestapi.services;

import com.springcontext.springrestapi.entities.Address;
import com.springcontext.springrestapi.entities.Company;
import com.springcontext.springrestapi.entities.Person;
import com.springcontext.springrestapi.repositories.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class PersonServiceTest {

    /* Person test values */

    private static final Long ID = 1L;
    private static final Long NOT_FOUND_ID = 2L;
    private static final String FIRSTNAME = "John";
    private static final String LASTNAME = "Doe";

    /* Address test values */

    private static final String COUNTRY = "Canada";
    private static final String CITY = "Vancouver";
    private static final String ZIP_CODE = "A0B 1C2";
    private static final String STREET = "Main street";
    private static final int STREET_NUMBER = 123456789;

    /* Company test values */

    private static final String COMPANY_NAME = "Spring-Context";

    @Mock
    private PersonRepository personRepository;

    private PersonService personService;

    @Before
    public void setUp() {
        this.personService = new PersonService(this.personRepository);
    }

    @Test
    public void create_should_saveNewPerson() {
        Person person = createPerson();

        when(this.personRepository.save(person)).thenReturn(person);

        Person fetchedPerson = this.personService.create(person);

        assertThat(fetchedPerson).isNotNull().isEqualToComparingFieldByFieldRecursively(person);
    }

    @Test
    public void getById_should_fetchExistingPerson() {
        Person person = createPerson();

        Optional<Person> mockedPerson = Optional.of(person);

        when(this.personRepository.findById(ID)).thenReturn(mockedPerson);

        Person fetchedPerson = this.personService.getById(ID);

        assertThat(fetchedPerson).isNotNull().isEqualToComparingFieldByFieldRecursively(person);
    }

    @Test
    public void getById_should_returnNullWhenIdNotFound() {
        Optional<Person> person = this.personRepository.findById(NOT_FOUND_ID);

        assertThat(person.isPresent()).isEqualTo(false);
    }

    @Test
    public void delete_should_properlyCallRepository() {
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        doNothing().when(this.personRepository).deleteById(captor.capture());

        this.personService.delete(ID);

        assertThat(captor.getValue()).isNotNull().isEqualTo(ID);
    }

    private static Person createPerson() {

        Company company = new Company();

        company.setName(COMPANY_NAME);

        Address address = new Address();

        address.setCountry(COUNTRY);
        address.setCity(CITY);
        address.setStreet(STREET);
        address.setStreetNumber(STREET_NUMBER);
        address.setZipCode(ZIP_CODE);

        Person person = new Person();

        person.setId(ID);
        person.setFirstname(FIRSTNAME);
        person.setLastname(LASTNAME);
        person.setCompany(company);
        person.addAddress(address);

        return person;
    }
}
