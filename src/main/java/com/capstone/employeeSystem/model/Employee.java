package com.capstone.employeeSystem.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "employees")  // This ensures everything is stored in the "employees" table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  // Inheritance strategy
//@DiscriminatorColumn(name = "person_type", discriminatorType = DiscriminatorType.STRING)  // Optional, to distinguish between subtypes (if needed)
public class Employee extends Person {

    @Column(name = "employeeId", nullable = false, unique = true)
    @NotNull(message = "'employeeId' is required")
    @Size(max = 10, message = "'department' can have a maximum of 100 characters")
    private String employeeId;


    @Column(name = "department", nullable = false)
    @NotNull(message = "'department' is required")
    @Size(max = 100, message = "'department' can have a maximum of 100 characters")
    private String department;

    @Column(name = "salary")
    @Min(value = 0, message = "'salary' must be greater than or equal to 0")
    @Max(value = 1000000, message = "'salary' must be less than or equal to 1,000,000")
    private Double salary;

    public Employee(){
        super("Temp",new Date());
    }

    public Employee(String employeeId, String department, Double salary, String name, Date birthDate){
        super(name,birthDate);
        this.employeeId = employeeId;
        this.department = department;
        this.salary = salary;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }
}
