package com.springcontext.springrestapi.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "companies")
@Data
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String name;

    @OneToMany
    private List<Person> employees;

    public void addEmployee(Person employee) {
        if (Objects.isNull(this.employees)) {
            this.employees = new ArrayList<>();
        }

        this.employees.add(employee);
    }
}