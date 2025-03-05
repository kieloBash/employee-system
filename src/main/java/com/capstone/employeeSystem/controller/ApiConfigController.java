package com.capstone.employeeSystem.controller;

import com.capstone.employeeSystem.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ApiConfigController {

    private final String API_VER = "/api/v1";
    private final String EMPLOYEES_API = API_VER + "/employees";
    private final String DEPARTMENT_API = API_VER + "/departments";

    @GetMapping
    public ResponseEntity<ResponseDTO<?>> getApiEndPoints(){
        try{

            Map<String,String> endPoints = new HashMap<>();
            endPoints.put("getEmployees",EMPLOYEES_API);
            endPoints.put("createEmployee",EMPLOYEES_API + "/create");
            endPoints.put("updateEmployee",EMPLOYEES_API + "/update");
            endPoints.put("deleteEmployee",EMPLOYEES_API + "/delete");
            
            endPoints.put("getDepartments",DEPARTMENT_API);
            endPoints.put("createDepartment",DEPARTMENT_API);
            endPoints.put("deleteDepartment",DEPARTMENT_API);
            endPoints.put("updateDepartment",DEPARTMENT_API);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDTO<Map<String,String>>(
                            "Endpoints have successfully been initialized!",
                            HttpStatus.OK,
                            endPoints
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR,null));
        }
    }
}
