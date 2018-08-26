package com.springcontext.springrestapi.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "addresses")
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String street;

    @Column(nullable = false, columnDefinition = "INT(11) DEFAULT 0")
    private Integer streetNumber;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String zipCode;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String city;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String country;

    @ManyToMany(mappedBy = "addresses", fetch = FetchType.EAGER)
    private List<Person> people;

    public void addPerson(Person person) {
        if (Objects.isNull(this.people)) {
            this.people = new ArrayList<>();
        }

        this.people.add(person);
    }
}