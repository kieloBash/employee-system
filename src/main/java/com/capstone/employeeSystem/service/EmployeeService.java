package com.capstone.employeeSystem.service;

import com.capstone.employeeSystem.model.Employee;
import com.capstone.employeeSystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.Period;

import java.time.ZoneId;


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

    public List<Employee> getListOfEmployees(String nameFilter){
        try{
            List<Employee> employeeList;

            if(!nameFilter.isEmpty()){
                employeeList  = this.employeeRepository.findEmployeesByFilters(nameFilter);
            }else{
                employeeList = this.employeeRepository.findAll();
            }

            return employeeList;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
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


}
