package com.capstone.employeeSystem.controller;

import com.capstone.employeeSystem.model.Employee;
import com.capstone.employeeSystem.security.JwtFilter;
import com.capstone.employeeSystem.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    public ResponseEntity<Employee> createEmployee(Principal principal,
                                                   @Valid @RequestBody Employee employee){
        String username = principal.getName();
        if(username == null){
            return ResponseEntity.badRequest().build();
        }

        Employee createdEmployee = this.employeeService.createEmployee(employee);

        if(createdEmployee == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getEmployees(Principal principal,
                                                       @RequestParam(name = "groupBy", required = false, defaultValue = "")String groupByFilter,
                                                       @RequestParam(name = "name", required = false, defaultValue = "")String nameFilter){
        String username = principal.getName();
        if(username == null){
            return ResponseEntity.badRequest().build();
        }

        List<Employee> employeeList = this.employeeService.getListOfEmployees(nameFilter, groupByFilter);

        return ResponseEntity.ok(employeeList);
    }


    @PutMapping("/update/{employeeId}")
    public ResponseEntity<?> updateEmployee(Principal principal,
                                            @PathVariable(name = "employeeId") String employeeId,
                                            @Valid @RequestBody Employee updatedEmployee) {

        String username = principal.getName();

        // Assuming user authentication should be validated in Spring Security, so you may not need this check
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Update the employee
        Employee savedAfterUpdateEmployee = this.employeeService.updateEmployee(updatedEmployee, employeeId);

        if (savedAfterUpdateEmployee == null) {
            // If employee is not found or couldn't be updated
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 404 Not Found
        }

        return ResponseEntity.ok(savedAfterUpdateEmployee);  // 200 OK - Success
    }

    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<?> deleteEmployee(Principal principal,
                                            @PathVariable(name = "employeeId") String employeeId){
        String username = principal.getName();
        if(username == null){
            return ResponseEntity.badRequest().build();
        }

        Boolean hasDeleted = this.employeeService.deletedEmployee(employeeId);

        if(!hasDeleted){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/summary/calculate-avg-salary")
    public ResponseEntity<Double> calculateAverageSalary(Principal principal){
        String username = principal.getName();
        if(username == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(this.employeeService.calculateAverageSalaries());
    }

    @GetMapping("/summary/calculate-avg-age")
    public ResponseEntity<Double> calculateAverageAge(Principal principal){
        String username = principal.getName();
        if(username == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(this.employeeService.calculateAverageAge());
    }


}
