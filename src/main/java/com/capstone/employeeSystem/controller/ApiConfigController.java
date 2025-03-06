package com.capstone.employeeSystem.controller;

import com.capstone.employeeSystem.dto.ResponseDTO;
import com.capstone.employeeSystem.config.ExternalApiConfig;  // Import the ExternalApiConfig
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

    private final ExternalApiConfig externalApiConfig; // Inject ExternalApiConfig

    // Constructor to inject ExternalApiConfig
    public ApiConfigController(ExternalApiConfig externalApiConfig) {
        this.externalApiConfig = externalApiConfig;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<?>> getApiEndPoints() {
        try {
            // Use ExternalApiConfig to get the URL values
            Map<String, String> endPoints = new HashMap<>();

            endPoints.put("getEmployees", externalApiConfig.getEmployeeGetUrl());
            endPoints.put("getGroupedEmployees", externalApiConfig.getEmployeeGetGroupUrl());
            endPoints.put("createEmployee", externalApiConfig.getEmployeeCreateUrl());
            endPoints.put("updateEmployee", externalApiConfig.getEmployeeUpdateUrl());
            endPoints.put("deleteEmployee", externalApiConfig.getEmployeeDeleteUrl());

            endPoints.put("getDepartments", externalApiConfig.getDepartmentGetUrl());
            endPoints.put("createDepartment", externalApiConfig.getDepartmentCreateUrl());
            endPoints.put("updateDepartment", externalApiConfig.getDepartmentUpdateUrl());
            endPoints.put("deleteDepartment", externalApiConfig.getDepartmentDeleteUrl());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDTO<Map<String, String>>(
                            "Endpoints have successfully been initialized!",
                            HttpStatus.OK,
                            endPoints
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }
}
