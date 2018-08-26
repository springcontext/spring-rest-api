package com.springcontext.springrestapi.services;

import com.springcontext.springrestapi.entities.Company;
import com.springcontext.springrestapi.entities.Person;
import com.springcontext.springrestapi.repositories.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class CompanyService {

    private final CompanyRepository companyRepository;

    private final PersonService personService;

    @Autowired
    public CompanyService(CompanyRepository companyRepository, PersonService personService) {
        this.companyRepository = companyRepository;
        this.personService = personService;
    }

    public Company create(Company company) {
        log.info("Adding a new company - name {}", company.getName());

        return this.companyRepository.save(company);
    }

    public Company getById(long id) {
        log.info("Trying to fetch Company with id {}", id);

        Optional<Company> company = this.companyRepository.findById(id);

        if (company.isPresent()) {
            return company.get();
        }

        log.error("No company found with id {}", id);

        return null;
    }

    public Company addEmployee(long companyId, long personId) {
        Company company = this.getById(companyId);

        if (Objects.nonNull(company)) {
            Person person = this.personService.getById(personId);

            if (Objects.nonNull(person)) {
                log.info("Adding Person {} in Company {}", personId, companyId);

                company.addEmployee(person);
                person.setCompany(company);

                this.companyRepository.save(company);
            }
        }

        return company;
    }

    public void delete(long id) {
        this.companyRepository.deleteById(id);
    }
}