package com.luckvicky.blur.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({Exception.class})
    public ProblemDetail handleException(Exception e) {
        return ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    }
}
