package com.springcontext.springrestapi.controllers.dto.company;

import com.springcontext.springrestapi.controllers.dto.person.PersonResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class CompanyResponseDto {

    private String name;

    private List<PersonResponseDto> employees;
}
