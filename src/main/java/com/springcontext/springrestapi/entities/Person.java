package com.springcontext.springrestapi.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "people")
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    private String firstname;

    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    private String lastname;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Address> addresses;

    @ManyToOne()
    @JoinColumn(nullable = true)
    private Company company;

    public void addAddress(Address address) {
        if(Objects.isNull(this.addresses)) {
            this.addresses = new ArrayList<>();
        }

        this.addresses.add(address);
    }
}