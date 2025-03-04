package com.capstone.employeeSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.util.Date;

@MappedSuperclass
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    @NotNull(message = "'name' is required")
    private String name;

    @Column(name = "dateOfBirth", nullable = false)
    @NotNull(message = "'dateOfBirth' is required")
    @Past(message = "'dateOfBirth' must be in the past")
    private Date dateOfBirth;

    public Person(String name, Date birthDate) {
        setName(name);
        setDateOfBirth(birthDate);
    }

    // getters and setters

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
