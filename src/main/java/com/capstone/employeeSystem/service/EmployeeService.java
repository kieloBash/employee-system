package com.capstone.employeeSystem.service;

import com.capstone.employeeSystem.model.Employee;
import com.capstone.employeeSystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;


import java.util.*;
import java.time.LocalDate;
import java.time.Period;

import java.time.ZoneId;
import java.util.stream.Collectors;


@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(Employee employee){
        try{
            Employee savedEmployee = this.employeeRepository.save(employee);

            return savedEmployee;

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Employee> getListOfEmployees(String nameFilter, String groupBy){
        try{
            List<Employee> employeeList;

            if(!groupBy.isEmpty()){
                return getGroupedListEmployees(nameFilter, groupBy);
            }else{
                if(!nameFilter.isEmpty()){
                    employeeList  = this.employeeRepository.findEmployeesByFilters(nameFilter);
                }else{
                    employeeList = this.employeeRepository.findAll();
                }

                return employeeList;
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Employee> getGroupedListEmployees(String nameFilter, String groupBy){

        List<Employee> employees = this.employeeRepository.findEmployeesByFilters(nameFilter);

        // List to store the grouped employees
        List<Employee> groupedEmployees = new ArrayList<>();

        // Group by department or age
        if (groupBy.equals("department")) {
            // Grouping by department
            Map<String, List<Employee>> groupedByDepartment = employees.stream()
                    .collect(Collectors.groupingBy(Employee::getDepartment)); // Group by department

            // Flatten the grouped map into a single list of employees
            groupedByDepartment.values().forEach(groupedEmployees::addAll);

        } else if (groupBy.equals("age")) {
            // Grouping by age
            Map<String, List<Employee>> groupedByAge = employees.stream()
                    .collect(Collectors.groupingBy(e -> {
                        // Calculate age dynamically based on date of birth and current year
                        int age = calculateAge(e.getDateOfBirth());
                        return String.valueOf(age); // Group by age as a string
                    }));

            // Flatten the grouped map into a single list of employees
            groupedByAge.values().forEach(groupedEmployees::addAll);

        } else {
            throw new IllegalArgumentException("Invalid groupBy value. It should be either 'department' or 'age'.");
        }

        return groupedEmployees;
    }

    public Employee updateEmployee(Employee updateEmployee, String employeeId){
        Optional<Employee> foundEmployee = this.employeeRepository.findByEmployeeId(employeeId);

        if(foundEmployee.isEmpty()){
            return null;
        }

        Employee toUpdateEmployee = foundEmployee.get();
        toUpdateEmployee.setSalary(updateEmployee.getSalary());
        toUpdateEmployee.setDepartment(updateEmployee.getDepartment());
        toUpdateEmployee.setName(updateEmployee.getName());
        toUpdateEmployee.setDateOfBirth(updateEmployee.getDateOfBirth());

        return this.employeeRepository.save(toUpdateEmployee);
    }

    public Boolean deletedEmployee(String employeeId){
        Optional<Employee> foundEmployee = this.employeeRepository.findByEmployeeId(employeeId);

        if(foundEmployee.isEmpty()){
            return false;
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
