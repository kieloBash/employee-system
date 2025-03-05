package com.capstone.employeeSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;  // Import the Pattern annotation
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "'department' is required")
    @Size(max = 100, message = "'department' can have a maximum of 100 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "'department' name must only contain letters (no numbers or special characters allowed)")
    @Column(nullable = false, unique = true)
    private String name;

}
