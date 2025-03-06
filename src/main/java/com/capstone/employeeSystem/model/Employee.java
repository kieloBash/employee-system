package com.capstone.employeeSystem.model;


import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
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

    @Transient  // This makes sure it's not persisted in the database
    private Integer departmentId;

    @Transient  // This makes sure it's not persisted in the database
    private String departmentName;


    @Valid
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departmentId")
    private Department department;

    @Column(name = "salary")
    @Min(value = 0, message = "'salary' must be greater than or equal to 0")
    @Max(value = 1000000, message = "'salary' must be less than or equal to 1,000,000")
    private Double salary;

    public Employee(){
        super("Temp",new Date());
    }

    public Employee(String employeeId, Department department, Double salary, String name, Date birthDate){
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getAge() {
        if (this.getDateOfBirth() == null) {
            return null;
        }
        LocalDate birthDate = this.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
