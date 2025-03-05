package com.capstone.employeeSystem.controller;

import com.capstone.employeeSystem.dto.ResponseDTO;
import com.capstone.employeeSystem.exceptions.DepartmentNotFoundException;
import com.capstone.employeeSystem.model.Department;
import com.capstone.employeeSystem.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<?>> getAllDepartments(){
        try{
            List<Department> departmentList = this.departmentService.getAllDepartments();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDTO<List<Department>>(
                            "Successfully fetched list of departments",
                            HttpStatus.OK,
                            departmentList
                    ));
        }catch (DepartmentNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(e.getMessage(),HttpStatus.BAD_REQUEST,null));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<?>> createDepartment(@Valid @RequestBody Department department){
        try{
            Department savedDepartment = this.departmentService.createDepartment(department);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDTO<Department>(
                            "Successfully created department",
                            HttpStatus.OK,
                            savedDepartment
                    ));
        }catch (DepartmentNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(e.getMessage(),HttpStatus.BAD_REQUEST,null));
        }
    }


    @DeleteMapping("/{departmentId}")
    public ResponseEntity<ResponseDTO<?>> deleteDepartment(@PathVariable(name = "departmentId") Integer departmentId) {
        try {
            // Try deleting the department
            this.departmentService.deleteDepartment(departmentId);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDTO<>(
                            "Successfully deleted department",
                            HttpStatus.OK,
                            null
                    ));
        } catch (DepartmentNotFoundException e) {
            // Department not found, handle as bad request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.BAD_REQUEST, null));
        } catch (DataIntegrityViolationException e) {
            // Handle the case where there are still employees in the department
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(
                            "Department cannot be deleted because it has existing employees.",
                            HttpStatus.BAD_REQUEST,
                            null
                    ));
        } catch (Exception e) {
            // Handle any other unexpected exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(
                            "An unexpected error occurred. Please try again later.",
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            null
                    ));
        }
    }


    @PutMapping()
    public ResponseEntity<ResponseDTO<?>> updateDepartment(@Valid @RequestBody Department department) {
        try {
            // Try updating the department
            Department updatedDepartment = this.departmentService.updateDepartment(department);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDTO<Department>(
                            "Successfully updated department",
                            HttpStatus.OK,
                            updatedDepartment
                    ));
        } catch (DepartmentNotFoundException e) {
            // Department not found, handle as bad request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.BAD_REQUEST, null));
        } catch (Exception e) {
            // Handle any other unexpected exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(
                            "An unexpected error occurred. Please try again later.",
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            null
                    ));
        }
    }

}
