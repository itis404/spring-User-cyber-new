package org.example.mebkuch.api.controller;

import org.example.mebkuch.api.dto.ErrorMessageDto;
import org.example.mebkuch.domain.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalAdviceController {

    @ExceptionHandler({
            UserException.class,
            CategoryException.class,
            ProductSectionException.class,
            ProductStatusException.class,
            ProductStyleException.class,
            ProductTypeException.class,
            ComponentException.class,
            ProductException.class
    })
    public ResponseEntity<ErrorMessageDto> handleDomainExceptions(RuntimeException e) {

        ErrorMessageDto error = ErrorMessageDto.builder()
                .messageError(e.getMessage())
                .build();

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(error);
    }
}