package com.capstone.employeeSystem.service;

import com.capstone.employeeSystem.exceptions.DepartmentNotFoundException;
import com.capstone.employeeSystem.exceptions.EmployeeNotFoundException;
import com.capstone.employeeSystem.exceptions.InvalidGroupByException;
import com.capstone.employeeSystem.model.Department;
import com.capstone.employeeSystem.model.Employee;
import com.capstone.employeeSystem.repository.DepartmentRepository;
import com.capstone.employeeSystem.repository.EmployeeRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;


import java.util.*;
import java.time.LocalDate;
import java.time.Period;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    public Map<String, List<Employee>>  groupByEmployees(String groupBy) {
        if (!isValidGroupBy(groupBy)) {
            throw new InvalidGroupByException("Invalid Group By Parameter");
        }

        List<Employee> employeeList = this.employeeRepository.findAll();

        if (groupBy.equals("department")) {
            Map<String, List<Employee>> employeesByDepartment = employeeList.stream()
                    .collect(Collectors.groupingBy(employee -> employee.getDepartment().getName()));

            return employeesByDepartment;
        }else{
            Map<String, List<Employee>> employeesByAge = employeeList.stream()
                    .collect(Collectors.groupingBy(employee -> employee.getAge().toString()));

            return employeesByAge;
        }
    }

    public Employee createEmployee(Employee employee) {
        if (employee == null) {
            throw new EmployeeNotFoundException("Employee data is invalid");
        }

        if(employee.getDepartmentId() == null){
            throw new EmployeeNotFoundException("No department Id specified");
        }

        Optional<Department> foundDepartment = this.departmentRepository.findById(employee.getDepartmentId());

        if(foundDepartment.isEmpty()){
            throw new DepartmentNotFoundException("CREATE: No department with that ID");
        }

        employee.setDepartment(foundDepartment.get());

        return this.employeeRepository.save(employee);
    }

    private boolean isValidGroupBy(String groupBy) {
        // Example validation logic (this can be customized)
        return groupBy.equals("department") || groupBy.equals("age");
    }

    public Page<Employee> getListOfEmployees(String nameFilter, String groupBy, Pageable pageable) {
        try {
            Page<Employee> employeePage;

            // If grouping is specified, validate and get the grouped list.
            if (!nameFilter.isEmpty()) {
                employeePage = this.employeeRepository.findEmployeesByFilters(nameFilter, pageable);
            } else {
                employeePage = this.employeeRepository.findAll(pageable);
            }

            return employeePage;
        } catch (RuntimeException e) {
            throw new RuntimeException("An error occurred while retrieving employees: " + e.getMessage(), e);
        }
    }

    private Page<Employee> getGroupedListEmployees(String nameFilter, String groupBy, Pageable pageable) {
        // Fetch employees with pagination
        Page<Employee> employeePage = this.employeeRepository.findEmployeesByFilters(nameFilter, pageable);

        // List to store the grouped employees
        List<Employee> groupedEmployees = new ArrayList<>();

        // Group by department
        if (groupBy.equals("department")) {
            // Grouping by department
            Map<Integer, List<Employee>> groupedByDepartment = employeePage.getContent().stream()
                    .collect(Collectors.groupingBy(employee -> employee.getDepartment().getId())); // Group by department

            // Flatten the grouped map into a single list of employees
            groupedByDepartment.values().forEach(groupedEmployees::addAll);

        } else if (groupBy.equals("age")) {
            // Grouping by age
            Map<String, List<Employee>> groupedByAge = employeePage.getContent().stream()
                    .collect(Collectors.groupingBy(e -> {
                        // Calculate age dynamically based on date of birth and current year
                        int age = calculateAge(e.getDateOfBirth());
                        return String.valueOf(age); // Group by age as a string
                    }));

            // Flatten the grouped map into a single list of employees
            groupedByAge.values().forEach(groupedEmployees::addAll);

        } else {
            // Throw an exception if the groupBy value is invalid
            throw new IllegalArgumentException("Invalid groupBy value. It should be either 'department' or 'age'.");
        }

        // Convert the grouped employees back to a Page, applying pagination on the grouped result
        int start = Math.min((int) pageable.getOffset(), groupedEmployees.size());
        int end = Math.min((start + pageable.getPageSize()), groupedEmployees.size());
        List<Employee> paginatedEmployees = groupedEmployees.subList(start, end);

        return new PageImpl<>(paginatedEmployees, pageable, groupedEmployees.size());
    }

    public Employee updateEmployee(Employee updateEmployee, String employeeId){
        Optional<Employee> foundEmployee = this.employeeRepository.findByEmployeeId(employeeId);

        if(foundEmployee.isEmpty()){
            throw new EmployeeNotFoundException("Employee not found!");
        }

        Employee toUpdateEmployee = foundEmployee.get();
        toUpdateEmployee.setSalary(updateEmployee.getSalary());

        Optional<Department> foundDepartment = departmentRepository.findByName(updateEmployee.getDepartmentName());

        if(foundDepartment.isEmpty()){
            // TODO: me
            throw new DepartmentNotFoundException("Department not found");
        }

        toUpdateEmployee.setDepartment(foundDepartment.get());
        toUpdateEmployee.setName(updateEmployee.getName());
        toUpdateEmployee.setDateOfBirth(updateEmployee.getDateOfBirth());

        Employee savedEmployee = this.employeeRepository.save(toUpdateEmployee);
        return savedEmployee;
    }

    public Boolean deletedEmployee(String employeeId){
        Optional<Employee> foundEmployee = this.employeeRepository.findByEmployeeId(employeeId);

        if(foundEmployee.isEmpty()){
            throw new EmployeeNotFoundException("Employee not found!");
        }

        Employee toDeleteEmployee = foundEmployee.get();
        this.employeeRepository.delete(toDeleteEmployee);
        return true;
    }

    public Double calculateAverageSalaries(){
        List<Employee> employeeList = this.employeeRepository.findAll();

        if (employeeList.isEmpty()) {
            return 0d;  // Return 0 if the list is empty (avoids division by zero)
        }

        double output = 0d;

        for(Employee e : employeeList){
            output+=e.getSalary();
        }

        return output / employeeList.size();
    }

    public Double calculateAverageAge() {
        List<Employee> employeeList = this.employeeRepository.findAll();

        if (employeeList.isEmpty()) {
            return 0d;  // Return 0 if the list is empty (avoids division by zero)
        }

        double totalAge = 0d;

        // Get the current date to calculate age
        LocalDate currentDate = LocalDate.now();

        for (Employee e : employeeList) {
            // Convert java.util.Date to LocalDate
            if (e.getDateOfBirth() != null) {
                LocalDate dob = e.getDateOfBirth().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                // Calculate the age of the employee
                int age = Period.between(dob, currentDate).getYears();
                totalAge += age;
            }
        }

        // Calculate the average age
        return Math.floor(totalAge / employeeList.size());
    }

    private int calculateAge(Date dateOfBirth) {
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateOfBirth);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int birthYear = birthDate.get(Calendar.YEAR);

        return currentYear - birthYear;
    }

}
