package com.springcontext.springrestapi.services;

import com.springcontext.springrestapi.entities.Company;
import com.springcontext.springrestapi.repositories.CompanyRepository;
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
public class CompanyServiceTest {

    /* Person test values */

    private static final Long ID = 1L;
    private static final Long NOT_FOUND_ID = 2L;

    /* Company test values */

    private static final Long COMPANY_ID = 1L;
    private static final String COMPANY_NAME = "Spring-Context";

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PersonService personService;

    private CompanyService companyService;

    @Before
    public void setUp() {
        this.companyService = new CompanyService(this.companyRepository, this.personService);
    }

    @Test
    public void create_should_saveNewCompany() {
        Company company = new Company();

        company.setId(COMPANY_ID);
        company.setName(COMPANY_NAME);

        when(this.companyRepository.save(company)).thenReturn(company);

        Company createdCompany = this.companyService.create(company);

        assertThat(createdCompany).isNotNull().isEqualToComparingFieldByField(company);
    }

    @Test
    public void getById_should_fetchExistingPerson() {
        Company company = new Company();

        company.setId(COMPANY_ID);
        company.setName(COMPANY_NAME);

        Optional<Company> createdCompany = Optional.of(company);

        when(this.companyRepository.findById(ID)).thenReturn(createdCompany);

        Company fetchedCompany = this.companyService.getById(ID);

        assertThat(fetchedCompany).isNotNull().isEqualToComparingFieldByFieldRecursively(company);
    }

    @Test
    public void getById_should_returnNullWhenIdNotFound() {
        Optional<Company> company = this.companyRepository.findById(NOT_FOUND_ID);

        assertThat(company.isPresent()).isEqualTo(false);
    }

    @Test
    public void delete_should_properlyCallRepository() {
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        doNothing().when(this.companyRepository).deleteById(captor.capture());

        this.companyService.delete(ID);

        assertThat(captor.getValue()).isNotNull().isEqualTo(ID);
    }
}
