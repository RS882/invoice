package com.billing.invoice.exception_handler;

import com.billing.invoice.domain.dto.invoice_dto.ResponseMessageDto;
import com.billing.invoice.exception_handler.exceptions.bad_request.BadRequestException;
import com.billing.invoice.exception_handler.exceptions.dto.ValidationErrorDto;
import com.billing.invoice.exception_handler.exceptions.dto.ValidationErrorsDto;
import com.billing.invoice.exception_handler.exceptions.not_found.NotFoundException;
import com.billing.invoice.exception_handler.exceptions.server_exception.ServerIOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseMessageDto> handlerException(BadRequestException ex) {
        return new ResponseEntity<>(new ResponseMessageDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseMessageDto> handlerException(NotFoundException ex) {
        return new ResponseEntity<>(new ResponseMessageDto(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorsDto> handleValidationException(MethodArgumentNotValidException ex) {
        List<ValidationErrorDto> validationErrors = new ArrayList<>();
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();

        for (ObjectError error : errors) {
            FieldError fieldError = (FieldError) error;

            ValidationErrorDto errorDto = ValidationErrorDto.builder()
                    .field(fieldError.getField())
                    .message("Field " + fieldError.getDefaultMessage())
                    .build();
            if (fieldError.getRejectedValue() != null)
                errorDto.setRejectedValue(fieldError.getRejectedValue().toString());

            validationErrors.add(errorDto);
        }
        return ResponseEntity.badRequest()
                .body(ValidationErrorsDto.builder()
                        .errors(validationErrors)
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseMessageDto> handleException(MethodArgumentTypeMismatchException ex) {
        return new ResponseEntity<>(new ResponseMessageDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ValidationErrorsDto> handleValidationException(HandlerMethodValidationException ex) {
        List<ValidationErrorDto> validationErrors = new ArrayList<>();

        ex.getParameterValidationResults().forEach(vr -> {
                    String parameterName = vr.getMethodParameter().getParameterName();
                    String message = vr.getResolvableErrors().stream()
                            .map(MessageSourceResolvable::getDefaultMessage)
                            .findFirst()
                            .orElse(null);

                    ValidationErrorDto errorDto = ValidationErrorDto.builder()
                            .field(parameterName)
                            .message(
                                    parameterName + ": " + (message == null ? "is wrong" : message))
                            .build();
                    validationErrors.add(errorDto);
                }
        );
        return ResponseEntity.badRequest()
                .body(ValidationErrorsDto.builder()
                        .errors(validationErrors)
                        .build());
    }

    @ExceptionHandler(ServerIOException.class)
    public ResponseEntity<ResponseMessageDto> handleException(ServerIOException ex) {
        return new ResponseEntity<>(new ResponseMessageDto(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseMessageDto> handleException(RuntimeException ex) {
        log.error("RuntimeException occurred", ex);
        return new ResponseEntity<>(new ResponseMessageDto("Something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
