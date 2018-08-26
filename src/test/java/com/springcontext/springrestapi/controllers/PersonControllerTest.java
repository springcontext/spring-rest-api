package com.springcontext.springrestapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springcontext.springrestapi.controllers.dto.address.AddressDto;
import com.springcontext.springrestapi.controllers.dto.person.PersonRequestDto;
import com.springcontext.springrestapi.controllers.dto.person.PersonResponseDto;
import com.springcontext.springrestapi.entities.Address;
import com.springcontext.springrestapi.entities.Person;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@EnableWebMvc
@SpringBootTest
public class PersonControllerTest {

    /* Person test values */

    private static final Long PERSON_ID = 1L;
    private static final String FIRSTNAME = "John";
    private static final String LASTNAME = "Doe";

    /* Address test values */

    private static final String COUNTRY = "Canada";
    private static final String CITY = "Vancouver";
    private static final String ZIP_CODE = "A0B 1C2";
    private static final String STREET = "Main street";
    private static final int STREET_NUMBER = 123456789;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    private PersonService personService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void create_should_returnValidResponseDto() throws Exception {

        Person person = new Person();

        person.setId(PERSON_ID);
        person.setFirstname(FIRSTNAME);
        person.setLastname(LASTNAME);

        when(this.personService.create(any())).thenReturn(person);

        PersonRequestDto personRequestDto = new PersonRequestDto();

        personRequestDto.setFirstname(FIRSTNAME);
        personRequestDto.setLastname(LASTNAME);

        MvcResult mvcResult = this.mvc.perform(
                post("/person")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(personRequestDto))
        ).andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        PersonResponseDto personResponseDto = this.objectMapper.readValue(response, PersonResponseDto.class);

        assertThat(personResponseDto).isNotNull();
        assertThat(personResponseDto.getFirstname()).isNotNull().isEqualTo(FIRSTNAME);
        assertThat(personResponseDto.getLastname()).isNotNull().isEqualTo(LASTNAME);
    }

    @Test
    public void create_should_notWorkWhenRequestBodyIsEmpty() throws Exception {

        this.mvc.perform(
                post("/person")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content("")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void getPerson_should_returnExistingPersonWhenIDExists() throws Exception {

        Person person = new Person();

        person.setId(PERSON_ID);
        person.setFirstname(FIRSTNAME);
        person.setLastname(LASTNAME);

        when(this.personService.getById(PERSON_ID)).thenReturn(person);

        PersonRequestDto personRequestDto = new PersonRequestDto();

        personRequestDto.setFirstname(FIRSTNAME);
        personRequestDto.setLastname(LASTNAME);

        MvcResult mvcResult = this.mvc.perform(
                get("/person/" + PERSON_ID)
        ).andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        PersonResponseDto personResponseDto = this.objectMapper.readValue(response, PersonResponseDto.class);

        assertThat(personResponseDto).isNotNull();
        assertThat(personResponseDto.getFirstname()).isNotNull().isEqualTo(FIRSTNAME);
        assertThat(personResponseDto.getLastname()).isNotNull().isEqualTo(LASTNAME);
    }

    @Test
    public void getPerson_should_returnNullWhenIDDoesNotExist() throws Exception {

        MvcResult mvcResult = this.mvc.perform(
                get("/person/" + PERSON_ID)
        ).andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotNull().isEmpty();
    }

    @Test
    public void addAddress_should_returnExistingPersonWithNewAddressWhenDataIsValid() throws Exception {

        Address address = new Address();

        address.setCountry(COUNTRY);
        address.setCity(CITY);
        address.setStreet(STREET);
        address.setStreetNumber(STREET_NUMBER);
        address.setZipCode(ZIP_CODE);

        Person person = new Person();

        person.setId(PERSON_ID);
        person.setFirstname(FIRSTNAME);
        person.setLastname(LASTNAME);
        person.addAddress(address);

        when(this.personService.addAddress(eq(PERSON_ID), any())).thenReturn(person);

        AddressDto addressDto = PersonController.convertModel(address);

        MvcResult mvcResult = this.mvc.perform(
                put("/person/" + PERSON_ID)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(addressDto))
        ).andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        PersonResponseDto personResponseDto = this.objectMapper.readValue(response, PersonResponseDto.class);

        assertThat(personResponseDto).isNotNull();
        assertThat(personResponseDto.getFirstname()).isNotNull().isEqualTo(FIRSTNAME);
        assertThat(personResponseDto.getLastname()).isNotNull().isEqualTo(LASTNAME);

        List<AddressDto> addresses = personResponseDto.getAddresses();

        assertThat(addresses).isNotNull().hasSize(1);

        AddressDto responseAddress = addresses.get(0);

        assertThat(responseAddress).isNotNull().isEqualToComparingFieldByField(addressDto);
    }

    @Test
    public void addAddress_should_returnNullWhenPersonDoesNotExist() throws Exception {

        Address address = new Address();

        address.setCountry(COUNTRY);
        address.setCity(CITY);
        address.setStreet(STREET);
        address.setStreetNumber(STREET_NUMBER);
        address.setZipCode(ZIP_CODE);

        AddressDto addressDto = PersonController.convertModel(address);

        when(this.personService.getById(PERSON_ID)).thenReturn(null);

        MvcResult mvcResult = this.mvc.perform(
                put("/person/" + PERSON_ID)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(addressDto))
        ).andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotNull().isEmpty();
    }

    @Test
    public void addAddress_should_returnNullWhenRequestBodyIsEmpty() throws Exception {

        MvcResult mvcResult = this.mvc.perform(
                put("/person/" + PERSON_ID)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void delete_should_callServiceMethodsProperly() throws Exception {

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        doNothing().when(this.personService).delete(captor.capture());

        this.mvc.perform(
                delete("/person/" + PERSON_ID)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isOk());

        assertThat(captor.getValue()).isNotNull().isEqualTo(PERSON_ID);
    }
}