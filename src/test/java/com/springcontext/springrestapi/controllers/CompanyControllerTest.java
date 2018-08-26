package com.springcontext.springrestapi.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springcontext.springrestapi.controllers.dto.address.AddressDto;
import com.springcontext.springrestapi.controllers.dto.company.CompanyRequestDto;
import com.springcontext.springrestapi.controllers.dto.company.CompanyResponseDto;
import com.springcontext.springrestapi.controllers.dto.person.PersonResponseDto;
import com.springcontext.springrestapi.entities.Address;
import com.springcontext.springrestapi.entities.Company;
import com.springcontext.springrestapi.entities.Person;
import com.springcontext.springrestapi.services.CompanyService;
import com.springcontext.springrestapi.services.PersonService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@EnableWebMvc
@SpringBootTest
public class CompanyControllerTest {

    /* Person test values */

    private static final Long PERSON_ID = 1L;
    private static final String FIRSTNAME = "John";
    private static final String LASTNAME = "Doe";

    private static final Long PERSON_ID_2 = 2L;
    private static final String FIRSTNAME_2 = "John_2";
    private static final String LASTNAME_2 = "Doe_2";

    /* Address test values */

    private static final String COUNTRY = "Canada";
    private static final String CITY = "Vancouver";
    private static final String ZIP_CODE = "A0B 1C2";
    private static final String STREET = "Main street";
    private static final int STREET_NUMBER = 123456789;

    private static final String COUNTRY_2 = "Canada";
    private static final String CITY_2 = "Burnaby";
    private static final String ZIP_CODE_2 = "A0B 1C3";
    private static final String STREET_2 = "Main street_2";
    private static final int STREET_NUMBER_2 = 234567891;

    /* Company test values */

    private static final Long COMPANY_ID = 1L;
    private static final String COMPANY_NAME = "Spring-Context";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    private CompanyService companyService;

    @MockBean
    private PersonService personService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void create_should_returnValidResponseDto() throws Exception {

        Company company = new Company();

        company.setId(COMPANY_ID);
        company.setName(COMPANY_NAME);

        when(this.companyService.create(any())).thenReturn(company);

        CompanyRequestDto companyRequestDto = new CompanyRequestDto();

        companyRequestDto.setName(COMPANY_NAME);

        MvcResult mvcResult = this.mvc.perform(
                post("/company")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(companyRequestDto))
        ).andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        CompanyResponseDto companyResponseDto = this.objectMapper.readValue(response, CompanyResponseDto.class);

        assertThat(companyResponseDto).isNotNull();
        assertThat(companyResponseDto.getName()).isNotNull().isEqualTo(COMPANY_NAME);
    }

    @Test
    public void create_should_notWorkWhenRequestBodyIsEmpty() throws Exception {

        this.mvc.perform(
                post("/company")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content("")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void getEmployees_should_returnAllEmployees() throws Exception {

        Address address1 = createAddress1();
        Address address2 = createAddress2();

        Person person = createPerson(address1);
        Person person2 = createPerson2(address2);

        Company company = new Company();

        company.setId(COMPANY_ID);
        company.setName(COMPANY_NAME);
        company.setEmployees(Arrays.asList(person, person2));

        when(this.companyService.getById(COMPANY_ID)).thenReturn(company);

        CompanyRequestDto companyRequestDto = new CompanyRequestDto();

        companyRequestDto.setName(COMPANY_NAME);

        MvcResult mvcResult = this.mvc.perform(
                get("/company/" + COMPANY_ID + "/people")
        ).andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        List<PersonResponseDto> employees = this.objectMapper.readValue(
                response,
                new TypeReference<List<PersonResponseDto>>(){}
        );

        AddressDto addressDto1 = PersonController.convertModel(address1);
        AddressDto addressDto2 = PersonController.convertModel(address2);

        PersonResponseDto responseDto1 = new PersonResponseDto();
        PersonResponseDto responseDto2 = new PersonResponseDto();

        responseDto1.setCompany(COMPANY_ID);
        responseDto1.setAddresses(Arrays.asList(addressDto1));
        responseDto1.setLastname(LASTNAME);
        responseDto1.setFirstname(FIRSTNAME);

        responseDto2.setCompany(COMPANY_ID);
        responseDto2.setAddresses(Arrays.asList(addressDto2));
        responseDto2.setLastname(LASTNAME_2);
        responseDto2.setFirstname(FIRSTNAME_2);

        assertThat(employees).isNotNull().hasSize(2);

        PersonResponseDto employee1 = employees.get(0);
        PersonResponseDto employee2 = employees.get(1);

        assertThat(employee1).isNotNull();
        assertThat(employee1.getFirstname()).isNotNull().isEqualTo(FIRSTNAME);
        assertThat(employee1.getLastname()).isNotNull().isEqualTo(LASTNAME);
        assertThat(employee1.getAddresses()).isNotNull().hasSize(1);

        AddressDto fetchedAddress1 = employee1.getAddresses().get(0);

        assertThat(fetchedAddress1.getCountry()).isNotNull().isEqualTo(COUNTRY);
        assertThat(fetchedAddress1.getCity()).isNotNull().isEqualTo(CITY);
        assertThat(fetchedAddress1.getStreet()).isNotNull().isEqualTo(STREET);
        assertThat(fetchedAddress1.getStreetNumber()).isNotNull().isEqualTo(STREET_NUMBER);
        assertThat(fetchedAddress1.getZipCode()).isNotNull().isEqualTo(ZIP_CODE);

        assertThat(employee2).isNotNull();
        assertThat(employee2.getFirstname()).isNotNull().isEqualTo(FIRSTNAME_2);
        assertThat(employee2.getLastname()).isNotNull().isEqualTo(LASTNAME_2);
        assertThat(employee2.getAddresses()).isNotNull().hasSize(1);

        AddressDto fetchedAddress2 = employee2.getAddresses().get(0);

        assertThat(fetchedAddress2.getCountry()).isNotNull().isEqualTo(COUNTRY_2);
        assertThat(fetchedAddress2.getCity()).isNotNull().isEqualTo(CITY_2);
        assertThat(fetchedAddress2.getStreet()).isNotNull().isEqualTo(STREET_2);
        assertThat(fetchedAddress2.getStreetNumber()).isNotNull().isEqualTo(STREET_NUMBER_2);
        assertThat(fetchedAddress2.getZipCode()).isNotNull().isEqualTo(ZIP_CODE_2);
    }

    @Test
    public void getEmployees_should_returnNullWhenCompanyDoesNotExist() throws Exception {

        MvcResult mvcResult = this.mvc.perform(
                get("/company/" + COMPANY_ID + "/people")
        ).andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotNull().isEmpty();
    }

    @Test
    public void addEmployee_should_returnValidResponseDto() throws Exception {

        Company company = new Company();

        company.setId(COMPANY_ID);
        company.setName(COMPANY_NAME);

        Person person = new Person();

        person.setId(PERSON_ID);
        person.setFirstname(FIRSTNAME);
        person.setLastname(LASTNAME);

        company.addEmployee(person);
        person.setCompany(company);

        when(this.companyService.addEmployee(COMPANY_ID, PERSON_ID)).thenReturn(company);

        MvcResult mvcResult = this.mvc.perform(
                put("/company/" + COMPANY_ID + "/people/" + PERSON_ID)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        CompanyResponseDto companyResponseDto = this.objectMapper.readValue(response, CompanyResponseDto.class);

        assertThat(companyResponseDto).isNotNull();
        assertThat(companyResponseDto.getName()).isNotNull().isEqualTo(COMPANY_NAME);

        List<PersonResponseDto> employees = companyResponseDto.getEmployees();

        assertThat(employees).isNotNull().isNotEmpty();

        PersonResponseDto employee = employees.get(0);

        assertThat(employee).isNotNull();
        assertThat(employee.getFirstname()).isNotNull().isEqualTo(FIRSTNAME);
        assertThat(employee.getLastname()).isNotNull().isEqualTo(LASTNAME);
        assertThat(employee.getCompany()).isNotNull().isEqualTo(PERSON_ID);
    }

    @Test
    public void addEmployee_should_returnNullWhenCompanyDoesNotExist() throws Exception {

        MvcResult mvcResult = this.mvc.perform(
                put("/company/" + COMPANY_ID + "/people/" + PERSON_ID)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotNull().isEmpty();
    }

    @Test
    public void delete_should_callServiceMethodsProperly() throws Exception {

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        doNothing().when(this.companyService).delete(captor.capture());

        this.mvc.perform(
                delete("/company/" + COMPANY_ID)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isOk());

        assertThat(captor.getValue()).isNotNull().isEqualTo(COMPANY_ID);
    }

    private static Address createAddress1() {
        Address address = new Address();

        address.setCountry(COUNTRY);
        address.setCity(CITY);
        address.setStreet(STREET);
        address.setStreetNumber(STREET_NUMBER);
        address.setZipCode(ZIP_CODE);

        return address;
    }

    private static Address createAddress2() {
        Address address = new Address();

        address.setCountry(COUNTRY_2);
        address.setCity(CITY_2);
        address.setStreet(STREET_2);
        address.setStreetNumber(STREET_NUMBER_2);
        address.setZipCode(ZIP_CODE_2);

        return address;
    }

    private static Person createPerson(Address address) {
        Person person = new Person();

        person.setId(PERSON_ID);
        person.setFirstname(FIRSTNAME);
        person.setLastname(LASTNAME);
        person.addAddress(address);

        return person;
    }

    private static Person createPerson2(Address address) {
        Person person = new Person();

        person.setId(PERSON_ID_2);
        person.setFirstname(FIRSTNAME_2);
        person.setLastname(LASTNAME_2);
        person.addAddress(address);

        return person;
    }
}
