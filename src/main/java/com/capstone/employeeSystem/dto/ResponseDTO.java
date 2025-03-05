package com.capstone.employeeSystem.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class ResponseDTO<T> {
    private String message;
    private HttpStatus status;
    private Integer statusCode;
    private T data;

    public ResponseDTO(String message, HttpStatus status, T data){
        this.message = message;
        this.status = status;
        this.statusCode = status.value();
        this.data = data;
    }

}
