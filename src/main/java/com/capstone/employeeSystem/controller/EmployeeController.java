package com.capstone.employeeSystem.controller;

import com.capstone.employeeSystem.exceptions.EmployeeNotFoundException;
import com.capstone.employeeSystem.exceptions.InvalidGroupByException;
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

        try {
            Employee createdEmployee = employeeService.createEmployee(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getEmployees(Principal principal,
                                                       @RequestParam(name = "groupBy", required = false, defaultValue = "")String groupByFilter,
                                                       @RequestParam(name = "name", required = false, defaultValue = "")String nameFilter){
        String username = principal.getName();
        if(username == null){
            return ResponseEntity.badRequest().build();
        }

        try{
            List<Employee> employeeList = this.employeeService.getListOfEmployees(nameFilter, groupByFilter);
            return ResponseEntity.ok(employeeList);
        }catch (EmployeeNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (InvalidGroupByException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

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

        try{
            // Update the employee
            Employee savedAfterUpdateEmployee = this.employeeService.updateEmployee(updatedEmployee, employeeId);

            return ResponseEntity.ok(savedAfterUpdateEmployee);  // 200 OK - Success
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<?> deleteEmployee(Principal principal,
                                            @PathVariable(name = "employeeId") String employeeId){
        String username = principal.getName();
        if(username == null){
            return ResponseEntity.badRequest().build();
        }

        try{
            Boolean hasDeleted = this.employeeService.deletedEmployee(employeeId);

            return ResponseEntity.noContent().build();

        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
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
