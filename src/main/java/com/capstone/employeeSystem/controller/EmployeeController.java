package com.capstone.employeeSystem.controller;

import com.capstone.employeeSystem.dto.ResponseDTO;
import com.capstone.employeeSystem.exceptions.DepartmentNotFoundException;
import com.capstone.employeeSystem.exceptions.EmployeeNotFoundException;
import com.capstone.employeeSystem.exceptions.InvalidGroupByException;
import com.capstone.employeeSystem.model.Employee;
import com.capstone.employeeSystem.security.JwtFilter;
import com.capstone.employeeSystem.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<?>> createEmployee(Principal principal,
                                                      @Valid @RequestBody Employee employee){
        String username = principal.getName();
        if(username == null){
            return ResponseEntity.badRequest().build();
        }

        try {
            Employee createdEmployee = employeeService.createEmployee(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseDTO<Employee>("Successfully created new employee",HttpStatus.CREATED,createdEmployee)
            );
        } catch (EmployeeNotFoundException | DepartmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ResponseDTO<>(e.getMessage(),HttpStatus.BAD_REQUEST,null)
                    );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            new ResponseDTO<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR,null)
                    );
        }
    }

    @GetMapping
    public ResponseEntity<Page<Employee>> getEmployees(Principal principal,
                                                       @RequestParam(name = "groupBy", required = false, defaultValue = "")String groupByFilter,
                                                       @RequestParam(name = "name", required = false, defaultValue = "")String nameFilter,
                                                       @RequestParam(name = "page", required = false, defaultValue = "0")int page,
                                                       @RequestParam(name = "size", required = false, defaultValue = "5")int size){
        String username = principal.getName();
        if(username == null){
            return ResponseEntity.badRequest().build();
        }

        try{
            Pageable pageable = PageRequest.of(page,size);

            Page<Employee> employeeList = this.employeeService.getListOfEmployees(nameFilter, groupByFilter,pageable);
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

    @GetMapping("/group")
    public ResponseEntity<ResponseDTO<?>> getGroupByEmployees(@RequestParam(name = "by")String groupBy){
        try{
            Map<String, List<Employee>> grouped = this.employeeService.groupByEmployees(groupBy);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDTO<>(
                            "Successfully grouped employees",
                            HttpStatus.OK,
                            grouped
                    ));
        } catch (InvalidGroupByException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ResponseDTO<>(
                                    e.getMessage(),
                                    HttpStatus.BAD_REQUEST,
                                    null
                            )
                    );
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            new ResponseDTO<>(
                                    e.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR,
                                    null
                            )
                    );
        }
    }


}
