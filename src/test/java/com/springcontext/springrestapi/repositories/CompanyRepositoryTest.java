package com.springcontext.springrestapi.repositories;

import com.springcontext.springrestapi.entities.Company;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CompanyRepositoryTest {

    private static final String COMPANY_NAME = "Spring-Context";

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void save_should_createNewCompanyerson() {

        this.createCompany();

        List<Company> companies = this.companyRepository.findAll();

        assertThat(companies).isNotNull().hasSize(1);

        Company company = companies.get(0);

        assertThat(company).isNotNull();
        assertThat(company.getId()).isNotNull().isGreaterThan(0);
        assertThat(company.getName()).isNotNull().isNotEmpty().isEqualTo(COMPANY_NAME);
    }

    @Test
    public void deleteById_should_deleteExistingCompany() {

        this.createCompany();

        List<Company> companies = this.companyRepository.findAll();

        assertThat(companies).isNotNull().hasSize(1);

        Company company = companies.get(0);

        this.companyRepository.deleteById(company.getId());

        companies = this.companyRepository.findAll();

        assertThat(companies).isNotNull().isEmpty();
    }

    private void createCompany() {

        this.companyRepository.deleteAll();

        List<Company> companies = this.companyRepository.findAll();

        assertThat(companies).isNotNull().isEmpty();

        Company company = new Company();

        company.setName(COMPANY_NAME);

        this.companyRepository.save(company);
    }
}
