package com.luckvicky.blur.global.util;

import com.luckvicky.blur.global.model.dto.Result;
import org.springframework.http.HttpStatus;

public class ResponseUtil {

    public static org.springframework.http.ResponseEntity ok(Result result) {
        return new org.springframework.http.ResponseEntity(
                result,
                HttpStatus.OK
        );
    }

    public static org.springframework.http.ResponseEntity noContent(Result result) {
        return new org.springframework.http.ResponseEntity(
                result,
                HttpStatus.NO_CONTENT
        );
    }

    public static org.springframework.http.ResponseEntity created(Result result) {
        return new org.springframework.http.ResponseEntity(
                result,
                HttpStatus.CREATED
        );
    }

}
