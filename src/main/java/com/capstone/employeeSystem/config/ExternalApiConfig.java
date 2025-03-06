package com.capstone.employeeSystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExternalApiConfig {

    @Value("${external.employee.get.url}")
    private String employeeGetUrl;

    @Value("${external.employee.get.group.url}")
    private String employeeGetGroupUrl;

    @Value("${external.employee.create.url}")
    private String employeeCreateUrl;

    @Value("${external.employee.update.url}")
    private String employeeUpdateUrl;

    @Value("${external.employee.delete.url}")
    private String employeeDeleteUrl;

    @Value("${external.department.get.url}")
    private String departmentGetUrl;

    @Value("${external.department.create.url}")
    private String departmentCreateUrl;

    @Value("${external.department.update.url}")
    private String departmentUpdateUrl;

    @Value("${external.department.delete.url}")
    private String departmentDeleteUrl;

    public String getEmployeeGetUrl() {
        return employeeGetUrl;
    }

    public String getEmployeeCreateUrl() {
        return employeeCreateUrl;
    }

    public String getEmployeeUpdateUrl() {
        return employeeUpdateUrl;
    }

    public String getEmployeeDeleteUrl() {
        return employeeDeleteUrl;
    }

    public String getDepartmentGetUrl() {
        return departmentGetUrl;
    }

    public String getDepartmentCreateUrl() {
        return departmentCreateUrl;
    }

    public String getDepartmentUpdateUrl() {
        return departmentUpdateUrl;
    }

    public String getDepartmentDeleteUrl() {
        return departmentDeleteUrl;
    }

    public String getEmployeeGetGroupUrl() {
        return employeeGetGroupUrl;
    }
}
