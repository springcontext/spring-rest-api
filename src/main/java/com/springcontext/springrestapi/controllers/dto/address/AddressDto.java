package com.springcontext.springrestapi.controllers.dto.address;

import lombok.Data;

@Data
public class AddressDto {

    private String street;

    private Integer streetNumber;

    private String zipCode;

    private String city;

    private String country;
}