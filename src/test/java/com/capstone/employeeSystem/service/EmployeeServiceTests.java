package com.capstone.employeeSystem.service;

import com.capstone.employeeSystem.exceptions.DepartmentNotFoundException;
import com.capstone.employeeSystem.exceptions.EmployeeNotFoundException;
import com.capstone.employeeSystem.model.Department;
import com.capstone.employeeSystem.model.Employee;
import com.capstone.employeeSystem.repository.DepartmentRepository;
import com.capstone.employeeSystem.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.swing.text.html.Option;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

public class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetListOfEmployees_WithNameFilter() {
        // Arrange
        String nameFilter = "John";
        String groupBy = "";
        Pageable pageable = PageRequest.of(0, 10);
        Employee employee = new Employee();
        Page<Employee> expectedPage = new PageImpl<>(Collections.singletonList(employee));

        when(employeeRepository.findEmployeesByFilters(nameFilter, pageable)).thenReturn(expectedPage);

        // Act
        Page<Employee> result = employeeService.getListOfEmployees(nameFilter, groupBy, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(employeeRepository, times(1)).findEmployeesByFilters(nameFilter, pageable);
    }

    @Test
    public void testGetListOfEmployees_WithoutNameFilter() {
        // Arrange
        String nameFilter = "";
        String groupBy = "";
        Pageable pageable = PageRequest.of(0, 10);
        Employee employee = new Employee();
        Page<Employee> expectedPage = new PageImpl<>(Collections.singletonList(employee));

        when(employeeRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<Employee> result = employeeService.getListOfEmployees(nameFilter, groupBy, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testUpdateEmployee_EmployeeNotFound() {
        // Arrange
        String employeeId = "E123";
        Employee updateEmployee = new Employee();

        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.updateEmployee(updateEmployee, employeeId);
        });

        verify(employeeRepository, times(1)).findByEmployeeId(employeeId);
        verify(departmentRepository, times(0)).findByName(anyString());
        verify(employeeRepository, times(0)).save(any(Employee.class));
    }

    @Test
    public void testUpdateEmployee_DepartmentNotFound() {
        // Arrange
        String employeeId = "E123";
        Employee existingEmployee = new Employee();
        existingEmployee.setEmployeeId(employeeId);

        Employee updateEmployee = new Employee();
        updateEmployee.setDepartmentName("HR");

        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(departmentRepository.findByName("HR")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DepartmentNotFoundException.class, () -> {
            employeeService.updateEmployee(updateEmployee, employeeId);
        });

        verify(employeeRepository, times(1)).findByEmployeeId(employeeId);
        verify(departmentRepository, times(1)).findByName("HR");
        verify(employeeRepository, times(0)).save(any(Employee.class));
    }

    @Test
    public void testUpdateEmployee_Success() {
        // Arrange
        String employeeId = "E123";
        Employee existingEmployee = new Employee();
        existingEmployee.setEmployeeId(employeeId);
        existingEmployee.setSalary(50000.0);

        Employee updateEmployee = new Employee();
        updateEmployee.setSalary(60000.0);
        updateEmployee.setDepartmentName("HR");
        updateEmployee.setName("John Doe");
        updateEmployee.setDateOfBirth(new Date());

        Department department = new Department();
        department.setName("HR");

        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(departmentRepository.findByName("HR")).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(existingEmployee);

        // Act
        Employee result = employeeService.updateEmployee(updateEmployee, employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(60000.0, result.getSalary());
        assertEquals("HR", result.getDepartment().getName());
        assertEquals("John Doe", result.getName());
        assertEquals(updateEmployee.getDateOfBirth(), result.getDateOfBirth());
        verify(employeeRepository, times(1)).findByEmployeeId(employeeId);
        verify(departmentRepository, times(1)).findByName("HR");
        verify(employeeRepository, times(1)).save(existingEmployee);
    }

    @Test
    public void testCreateEmployee_Success() {
        // Arrange
        Employee employee = new Employee();
        employee.setDepartmentId(1);
        employee.setName("John Doe");
        employee.setSalary(50000.0);
        employee.setDateOfBirth(new Date());

        Department department = new Department();
        department.setId(1);
        department.setName("HR");

        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Act
        Employee result = employeeService.createEmployee(employee);

        // Assert
        assertNotNull(result);
        assertEquals("HR", result.getDepartment().getName());
        verify(departmentRepository, times(1)).findById(1);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    public void testCreateEmployee_EmployeeIsNull() {
        // Act & Assert
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.createEmployee(null);
        });

        verify(employeeRepository, times(0)).save(any(Employee.class));
        verify(departmentRepository, times(0)).findById(anyInt());
    }

    @Test
    public void testCreateEmployee_NoDepartmentId() {
        // Arrange
        Employee employee = new Employee();

        // Act & Assert
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.createEmployee(employee);
        });

        verify(employeeRepository, times(0)).save(any(Employee.class));
        verify(departmentRepository, times(0)).findById(anyInt());
    }

    @Test
    public void testCreateEmployee_DepartmentNotFound() {
        // Arrange
        Employee employee = new Employee();
        employee.setDepartmentId(1);

        when(departmentRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DepartmentNotFoundException.class, () -> {
            employeeService.createEmployee(employee);
        });

        verify(departmentRepository, times(1)).findById(1);
        verify(employeeRepository, times(0)).save(any(Employee.class));
    }

    @Test
    public void testDeletedEmployee_Success() {
        // Arrange
        String employeeId = "E123";
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);

        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        // Act
        Boolean result = employeeService.deletedEmployee(employeeId);

        // Assert
        assertTrue(result);
        verify(employeeRepository, times(1)).findByEmployeeId(employeeId);
        verify(employeeRepository, times(1)).delete(employee);
    }

}
