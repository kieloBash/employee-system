package com.capstone.employeeSystem.service;

import com.capstone.employeeSystem.model.Employee;
import com.capstone.employeeSystem.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.text.html.Option;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

////    @Test
//    public void createEmployeeTest(){
//        Employee mockEmployee = new Employee("1",
//                "DEVELOPER",
//                20000d,
//                "Kielo",
//                new Date());
//
//        // Given
//        when(employeeRepository.save(mockEmployee)).thenReturn(mockEmployee);
//
//        // When
//        Employee result = this.employeeService.createEmployee(mockEmployee);
//
//        // Assert
//        assertEquals("DEVELOPER",result.getDepartment(),"Department should be 'DEVELOPER'");
//
//        // Verify
//        verify(employeeRepository,times(1)).save(mockEmployee);
//
//
//    }
//
////    @Test
//    public void updateEmployeeTest(){
//        String employee_id = "1";
//
//        Employee mockEmployee = new Employee(employee_id,
//                "DEVELOPER",
//                20000d,
//                "Kielo",
//                new Date());
//
//        String updatedDepartment = "MID DEVELOPER";
//        Employee updatedMockEmployee = new Employee(employee_id,
//                updatedDepartment,
//                20000d,"Kielo",new Date());
//
//
//        // Given
//        when(employeeRepository.findByEmployeeId(employee_id))
//                .thenReturn(Optional.of(mockEmployee));
//        when(employeeRepository.save(mockEmployee))
//                .thenReturn(updatedMockEmployee);
//
//
//        // When
//        Employee result = this.employeeService.updateEmployee(updatedMockEmployee,employee_id);
//
//        // Assert
//        assertEquals(updatedDepartment,result.getDepartment(), "The department should be updated to 'MID DEVELOPER'");
//
//        // Verify
//        verify(employeeRepository,times(1)).findByEmployeeId(employee_id);
//    }
//
////    @Test
//    public void deleteEmployeeTest() {
//        String employeeId = "1";
//        Employee mockEmployee = new Employee(employeeId,
//                "DEVELOPER",
//                20000d,
//                "Kielo",
//                new Date());
//
//        // Given
//        when(employeeRepository.findByEmployeeId(employeeId))
//                .thenReturn(Optional.of(mockEmployee));
//
//        // Stubbing the void method with doNothing (because it doesn't return a value)
//        doNothing().when(employeeRepository).delete(mockEmployee);
//
//        // When
//        this.employeeService.deletedEmployee(employeeId);
//
//        // Assert
//        verify(employeeRepository, times(1)).findByEmployeeId(employeeId);
//        verify(employeeRepository, times(1)).delete(mockEmployee);  // Verify delete was called
//    }
//
////    @Test
//    public void searchByNameEmployeesTest(){
//        Employee mockEmployee1 = new Employee("1",
//                "DEVELOPER",
//                20000d,
//                "Kielo",
//                new Date());
//
//        Employee mockEmployee2 = new Employee("2",
//                "DEVELOPER",
//                2000d,
//                "Bash",
//                new Date());
//
//        Employee mockEmployee3 = new Employee("3",
//                "DEVELOPER",
//                2000d,
//                "Mercado",
//                new Date());
//
//        ArrayList<Employee> mockedList = new ArrayList<>();
//        mockedList.add(mockEmployee1);
//        mockedList.add(mockEmployee2);
//        mockedList.add(mockEmployee3);
//
//        // Given
//        String searchedName = "Kielo";
//        when(employeeService.getListOfEmployees(searchedName,""))
//                .thenReturn(List.of(mockEmployee1));
//        when(employeeRepository.findEmployeesByFilters(searchedName))
//                .thenReturn(List.of(mockEmployee1));
//
//        // When
//        List<Employee> result = this.employeeService
//                .getListOfEmployees(searchedName,"");
//
//        // Assert
//        assertEquals(1,result.size(),"Size should be 1");
//    }

}
