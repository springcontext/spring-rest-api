package com.springcontext.springrestapi.controllers;

import com.springcontext.springrestapi.controllers.dto.company.CompanyRequestDto;
import com.springcontext.springrestapi.controllers.dto.company.CompanyResponseDto;
import com.springcontext.springrestapi.controllers.dto.person.PersonResponseDto;
import com.springcontext.springrestapi.entities.Company;
import com.springcontext.springrestapi.entities.Person;
import com.springcontext.springrestapi.services.CompanyService;
import com.springcontext.springrestapi.services.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class CompanyController {

    private static final String API_CALL_MESSAGE = "New API call: ";
    private static final String FAILURE_MESSAGE = "Failure: ";
    private static final String SUCCESS_MESSAGE = "Success: ";

    private final CompanyService companyService;

    private final PersonService personService;

    @Autowired
    public CompanyController(CompanyService companyService, PersonService personService) {
        this.companyService = companyService;
        this.personService = personService;
    }

    @PostMapping(path = "/company", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CompanyResponseDto create(@RequestBody CompanyRequestDto company) {

        log.info(API_CALL_MESSAGE + "Create a company");

        if (Objects.isNull(company)) {
            log.error(FAILURE_MESSAGE + "The request body is null");
            return null;
        }

        Company createdCompany = this.companyService.create(CompanyController.convertRequestDto(company));

        log.info(SUCCESS_MESSAGE + "New company created!");

        return CompanyController.convertModel(createdCompany);
    }

    @GetMapping(path = "/company/{id}/people", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonResponseDto> getEmployees(@PathVariable long id) {

        log.info(API_CALL_MESSAGE + "Get company employees");

        Company company = this.companyService.getById(id);

        if (Objects.isNull(company)) {
            log.error(FAILURE_MESSAGE + "No company was found with id {}", id);
            return null;
        }

        log.info(SUCCESS_MESSAGE + "Fetching all the employees!");

        return company.getEmployees().stream().map(PersonController::convertModel).collect(Collectors.toList());
    }

    @PutMapping(
            path = "/company/{company_id}/people/{person_id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public CompanyResponseDto addEmployee(
            @PathVariable("company_id") long companyId,
            @PathVariable("person_id") long personId
    ) {

        log.info(API_CALL_MESSAGE + "Add an employee in a company");

        Company company = this.companyService.addEmployee(companyId, personId);

        if (Objects.isNull(company)) {
            log.error(FAILURE_MESSAGE + "No company was found with id {} or the person does not exist", companyId);
            return null;
        }

        return CompanyController.convertModel(company);
    }

    @DeleteMapping(path = "/company/{id}")
    public void delete(@PathVariable long id) {
        log.info(API_CALL_MESSAGE + "Delete a company");

        Company company = this.companyService.getById(id);

        if (Objects.nonNull(company)) {
            company.getEmployees().forEach(employee -> {
                this.personService.removeCompany(employee);
            });
        }

        this.companyService.delete(id);
    }

    public static Company convertRequestDto(CompanyRequestDto dto) {

        Company company = new Company();

        company.setName(dto.getName());

        return company;
    }

    public static CompanyResponseDto convertModel(Company company) {

        CompanyResponseDto dto = new CompanyResponseDto();

        dto.setName(company.getName());

        List<Person> employees = company.getEmployees();

        if (Objects.nonNull(employees)) {
            dto.setEmployees(
                    employees.stream().map(PersonController::convertModel).collect(Collectors.toList())
            );
        }

        return dto;
    }
}
