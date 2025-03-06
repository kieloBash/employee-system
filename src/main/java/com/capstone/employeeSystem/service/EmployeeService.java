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

    /**
     * Creates a new employee record in the repository.
     *
     * This method first checks if the provided employee object is valid (not null).
     * If the employee object is null, an IllegalArgumentException is thrown. If the
     * employee object is valid, it is saved to the employee repository.
     * If any other runtime exceptions occur during the saving process, a RuntimeException
     * with a custom message is thrown to indicate the failure.
     *
     * @param employee The employee object containing the details to be saved.
     * @return The saved employee object, after being persisted in the repository.
     * @throws IllegalArgumentException If the provided employee object is null.
     * @throws RuntimeException If an error occurs while saving the employee to the repository.
     */
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

    /**
     * Retrieves a list of employees based on optional filters and grouping parameters.
     *
     * This method allows retrieving employees with a filter applied to their name and
     * optionally grouping them by a specified attribute. If the `groupBy` parameter is
     * provided and valid, employees will be grouped accordingly. If no grouping is required,
     * employees can be filtered by name or returned in their entirety if no filter is provided.
     *
     * @param nameFilter An optional filter for employee names. If provided, only employees
     *                   whose names match the filter will be returned.
     * @param groupBy An optional parameter specifying the attribute by which to group the employees.
     *                If provided, the list will be grouped according to the valid attribute.
     * @return A list of employees that match the given filter and/or grouping criteria.
     * @throws InvalidGroupByException If the provided `groupBy` parameter is invalid.
     * @throws RuntimeException If an error occurs while retrieving the list of employees.
     */


    public Page<Employee> getListOfEmployees(String nameFilter, String groupBy, Pageable pageable) {
        try {
            Page<Employee> employeePage;

            // If grouping is specified, validate and get the grouped list.
            if (!groupBy.isEmpty()) {
                if (!isValidGroupBy(groupBy)) {
                    throw new InvalidGroupByException("Invalid Group By Parameter");
                }

                // Handle the grouping logic (e.g., grouped data should also support pagination)
                employeePage = getGroupedListEmployees(nameFilter, groupBy, pageable);
            } else {
                // If no grouping is specified, apply name filter if provided and return paginated result.
                if (!nameFilter.isEmpty()) {
                    employeePage = this.employeeRepository.findEmployeesByFilters(nameFilter, pageable);
                } else {
                    employeePage = this.employeeRepository.findAll(pageable);
                }
            }

            return employeePage;
        } catch (RuntimeException e) {
            throw new RuntimeException("An error occurred while retrieving employees: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a list of employees grouped by a specified attribute (either department or age).
     *
     * This method filters employees based on the provided `nameFilter` and then groups the
     * filtered employees by the specified attribute (either "department" or "age").
     * The grouping is done using Java Streams and the resulting list contains all the employees,
     * but grouped according to the chosen attribute.
     *
     * @param nameFilter The filter to apply to employee names. Only employees whose names match
     *                   this filter will be included in the result.
     * @param groupBy The attribute by which to group the employees. It should be either "department"
     *                or "age".
     * @return A list of employees grouped by the specified attribute (either department or age).
     * @throws IllegalArgumentException If the `groupBy` parameter is neither "department" nor "age".
     */
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

    /**
     * Updates an existing employee's details based on the provided employee ID.
     *
     * This method retrieves an employee from the repository by their employee ID.
     * If the employee is found, the provided update details (such as salary, department,
     * name, and date of birth) are applied to the employee, and the updated employee is
     * saved back to the repository. If the employee with the given ID is not found,
     * an `EmployeeNotFoundException` is thrown.
     *
     * @param updateEmployee The employee object containing the new details to update.
     * @param employeeId The ID of the employee to be updated.
     * @return The updated employee object after being persisted in the repository.
     * @throws EmployeeNotFoundException If no employee is found with the provided ID.
     */
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

    /**
     * Deletes an employee based on the provided employee ID.
     *
     * This method attempts to find an employee by their employee ID. If the employee is
     * found, the employee is deleted from the repository. If no employee with the provided
     * ID is found, an `EmployeeNotFoundException` is thrown.
     *
     * @param employeeId The ID of the employee to be deleted.
     * @return A boolean value indicating the success of the deletion. Returns `true`
     *         if the employee is successfully deleted.
     * @throws EmployeeNotFoundException If no employee is found with the provided ID.
     */
    public Boolean deletedEmployee(String employeeId){
        Optional<Employee> foundEmployee = this.employeeRepository.findByEmployeeId(employeeId);

        if(foundEmployee.isEmpty()){
            throw new EmployeeNotFoundException("Employee not found!");
        }

        Employee toDeleteEmployee = foundEmployee.get();
        this.employeeRepository.delete(toDeleteEmployee);
        return true;
    }

    /**
     * Calculates the average salary of all employees.
     *
     * This method retrieves all employees from the repository and computes the average
     * salary. If there are no employees in the repository, it returns 0 to avoid division
     * by zero.
     *
     * @return The average salary of all employees. Returns 0 if the employee list is empty.
     */
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

    /**
     * Calculates the average age of all employees.
     *
     * This method retrieves all employees from the repository and calculates their average
     * age. The age is calculated based on the date of birth of each employee. If there are no
     * employees in the repository, it returns 0 to avoid division by zero.
     *
     * @return The average age of all employees. Returns 0 if the employee list is empty.
     */
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

    /**
     * Calculates the age of an employee based on their date of birth.
     *
     * This method calculates an employee's age by comparing their date of birth to the
     * current year. The calculation is performed by subtracting the birth year from the
     * current year.
     *
     * @param dateOfBirth The employee's date of birth.
     * @return The calculated age of the employee.
     */
    private int calculateAge(Date dateOfBirth) {
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateOfBirth);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int birthYear = birthDate.get(Calendar.YEAR);

        return currentYear - birthYear;
    }

}
