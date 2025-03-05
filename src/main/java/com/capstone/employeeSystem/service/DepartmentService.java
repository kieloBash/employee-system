package com.capstone.employeeSystem.service;

import com.capstone.employeeSystem.exceptions.DepartmentNotFoundException;
import com.capstone.employeeSystem.model.Department;
import com.capstone.employeeSystem.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartments(){
        List<Department> departmentList = this.departmentRepository.findAll();

        return departmentList;
    }

    public Department createDepartment(Department newDepartment){


        Department savedDepartment = this.departmentRepository.save(newDepartment);

        return savedDepartment;
    }

    public void deleteDepartment(Integer departmentId){
        Optional<Department> foundDepartment = this.departmentRepository.findById(departmentId);

        if(foundDepartment.isEmpty()){
            throw new DepartmentNotFoundException("DELETE: department with that ID is unavailable.");
        }

        //TODO: check if a user is in that department

        Department toDeleteDepartment = foundDepartment.get();

        this.departmentRepository.delete(toDeleteDepartment);
    }

    public Department getDepartmentById(Integer departmentId){
        Optional<Department> foundDepartment = this.departmentRepository.findById(departmentId);

        if(foundDepartment.isEmpty()){
            throw new DepartmentNotFoundException("GET by ID: department with that ID is unavailable.");
        }

        return foundDepartment.get();
    }

    public Department updateDepartment(Department updatedDepartment){
        Optional<Department> foundDepartment = this.departmentRepository.findById(updatedDepartment.getId());

        if(foundDepartment.isEmpty()){
            throw new DepartmentNotFoundException("UPDATE: department with that ID is unavailable.");
        }

        Department savedUpdatedDepartment = this.departmentRepository.save(updatedDepartment);

        return savedUpdatedDepartment;
    }
}
