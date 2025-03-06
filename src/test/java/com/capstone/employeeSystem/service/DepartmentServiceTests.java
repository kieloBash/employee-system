package com.capstone.employeeSystem.service;

import com.capstone.employeeSystem.exceptions.DepartmentNotFoundException;
import com.capstone.employeeSystem.model.Department;
import com.capstone.employeeSystem.repository.DepartmentRepository;
import com.capstone.employeeSystem.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DepartmentServiceTests {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private DepartmentService departmentService;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllDepartments() {
        Department department1 = new Department();
        Department department2 = new Department();
        List<Department> departments = Arrays.asList(department1, department2);

        when(departmentRepository.findAll()).thenReturn(departments);

        List<Department> result = departmentService.getAllDepartments();

        assertEquals(2, result.size());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    public void createDepartment() {
        Department newDepartment = new Department();
        when(departmentRepository.save(newDepartment)).thenReturn(newDepartment);

        Department result = departmentService.createDepartment(newDepartment);

        assertNotNull(result);
        verify(departmentRepository, times(1)).save(newDepartment);
    }

    @Test
    public void deleteDepartment() {
        Department department = new Department();
        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));

        departmentService.deleteDepartment(1);

        verify(departmentRepository, times(1)).delete(department);
    }

    @Test
    public void updateDepartment() {
        Department updatedDepartment = new Department();
        updatedDepartment.setId(1);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(updatedDepartment));
        when(departmentRepository.save(updatedDepartment)).thenReturn(updatedDepartment);

        Department result = departmentService.updateDepartment(updatedDepartment);

        assertNotNull(result);
        verify(departmentRepository, times(1)).save(updatedDepartment);
    }
    
}
