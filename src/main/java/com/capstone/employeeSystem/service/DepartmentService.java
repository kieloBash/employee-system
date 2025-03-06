package com.capstone.employeeSystem.service;

import com.capstone.employeeSystem.exceptions.DepartmentNotFoundException;
import com.capstone.employeeSystem.model.Department;
import com.capstone.employeeSystem.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private MessageSource messageSource;

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
            String errorMessage = messageSource.getMessage("error.department.delete.notfound",
                    new Object[]{departmentId}, Locale.getDefault());
            throw new DepartmentNotFoundException(errorMessage);
        }

        Department toDeleteDepartment = foundDepartment.get();

        this.departmentRepository.delete(toDeleteDepartment);
    }

    public Department getDepartmentById(Integer departmentId){
        Optional<Department> foundDepartment = this.departmentRepository.findById(departmentId);

        if(foundDepartment.isEmpty()){
            String errorMessage = messageSource.getMessage("error.department.get.notfound",
                    new Object[]{departmentId}, Locale.getDefault());
            throw new DepartmentNotFoundException(errorMessage);
        }

        return foundDepartment.get();
    }

    public Department updateDepartment(Department updatedDepartment){
        Optional<Department> foundDepartment = this.departmentRepository.findById(updatedDepartment.getId());

        if(foundDepartment.isEmpty()){
            String errorMessage = messageSource.getMessage("error.department.update.notfound",
                    new Object[]{updatedDepartment.getId()}, Locale.getDefault());
            throw new DepartmentNotFoundException(errorMessage);
        }

        Department savedUpdatedDepartment = this.departmentRepository.save(updatedDepartment);

        return savedUpdatedDepartment;
    }
}
