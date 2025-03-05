package com.capstone.employeeSystem.exceptions;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(String message) {
        super(message);
    }
}
