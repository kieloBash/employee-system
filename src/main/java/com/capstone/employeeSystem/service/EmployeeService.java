package com.capstone.employeeSystem.service;

import com.capstone.employeeSystem.model.Employee;
import com.capstone.employeeSystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<Employee> getListOfEmployees(){
        try{
            List<Employee> employeeList = this.employeeRepository.findAll();

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
}
