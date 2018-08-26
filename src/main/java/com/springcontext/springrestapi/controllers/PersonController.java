package com.springcontext.springrestapi.controllers;

import com.springcontext.springrestapi.controllers.dto.address.AddressDto;
import com.springcontext.springrestapi.controllers.dto.person.PersonRequestDto;
import com.springcontext.springrestapi.controllers.dto.person.PersonResponseDto;
import com.springcontext.springrestapi.entities.Address;
import com.springcontext.springrestapi.entities.Company;
import com.springcontext.springrestapi.entities.Person;
import com.springcontext.springrestapi.services.CompanyService;
import com.springcontext.springrestapi.services.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class PersonController {

    private static final String API_CALL_MESSAGE = "New API call: ";
    private static final String FAILURE_MESSAGE = "Failure: ";
    private static final String SUCCESS_MESSAGE = "Success: ";

    private final PersonService personService;

    private final CompanyService companyService;

    @Autowired
    public PersonController(PersonService personService, CompanyService companyService) {
        this.personService = personService;
        this.companyService = companyService;
    }

    @PostMapping(path = "/person", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PersonResponseDto create(@RequestBody PersonRequestDto person) {

        log.info(API_CALL_MESSAGE + "Create a person");

        if (Objects.isNull(person)) {
            log.error(FAILURE_MESSAGE + "The request body is null");
            return null;
        }

        Person createdPerson = this.personService.create(this.convertRequestDto(person));

        log.info(SUCCESS_MESSAGE + "New person created!");

        return PersonController.convertModel(createdPerson);
    }

    @GetMapping(path = "/person/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonResponseDto getPerson(@PathVariable long id) {

        log.info(API_CALL_MESSAGE + "Get a person");

        Person person = this.personService.getById(id);

        if (Objects.isNull(person)) {
            log.error(FAILURE_MESSAGE + "No person found with id {}", id);
            return null;
        }

        log.info(SUCCESS_MESSAGE + "Returning person with id {}", id);
        return PersonController.convertModel(person);
    }

    @PutMapping(
            path = "/person/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public PersonResponseDto addAddress(@PathVariable long id, @RequestBody AddressDto address) {

        log.info(API_CALL_MESSAGE + "Add a new address");

        Address addressModel = PersonController.convertRequestDto(address);

        Person person = this.personService.addAddress(id, addressModel);

        if (Objects.isNull(person)) {
            log.error(FAILURE_MESSAGE + "No person found with id {}", id);
            return null;
        }

        log.info(SUCCESS_MESSAGE + "Added new address to person with id {}", id);
        return this.convertModel(person);
    }

    @DeleteMapping(path = "/person/{id}")
    public void delete(@PathVariable long id) {
        log.info(API_CALL_MESSAGE + "Delete a person");

        this.personService.delete(id);
    }

    public Person convertRequestDto(PersonRequestDto dto) {
        Person person = new Person();

        Optional<Long> company = dto.getCompany();

        person.setFirstname(dto.getFirstname());
        person.setLastname(dto.getLastname());

        if (company.isPresent()) {
            Company existingCompany = this.companyService.getById(company.get());

            person.setCompany(existingCompany);
        }

        return person;
    }

    public static PersonResponseDto convertModel(Person person) {
        PersonResponseDto dto = new PersonResponseDto();

        Company company = person.getCompany();

        dto.setFirstname(person.getFirstname());
        dto.setLastname(person.getLastname());

        if(Objects.nonNull(person.getAddresses())) {
            dto.setAddresses(
                    person.getAddresses().stream().map(PersonController::convertModel).collect(Collectors.toList())
            );
        }

        if (Objects.nonNull(company)) {
            dto.setCompany(company.getId());
        }

        return dto;
    }

    public static AddressDto convertModel(Address address) {
        AddressDto dto = new AddressDto();

        dto.setCity(address.getCity());
        dto.setCountry(address.getCountry());
        dto.setStreet(address.getStreet());
        dto.setStreetNumber(address.getStreetNumber());
        dto.setZipCode(address.getZipCode());

        return dto;
    }

    public static Address convertRequestDto(AddressDto dto) {
        Address address = new Address();

        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setStreet(dto.getStreet());
        address.setStreetNumber(dto.getStreetNumber());
        address.setZipCode(dto.getZipCode());

        return address;
    }
}