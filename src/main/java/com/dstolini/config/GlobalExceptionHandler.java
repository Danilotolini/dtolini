package com.dstolini.config;

import com.dstolini.adapter.in.rest.dto.ErrorResponseDto;
import com.dstolini.domain.exception.RateNotFoundException;
import com.dstolini.domain.exception.SolverConvergenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SolverConvergenceException.class)
    ResponseEntity<ErrorResponseDto> handleSolverConvergence(SolverConvergenceException e) {
        log.warn("Solver falhou: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponseDto.of("SOLVER_CONVERGENCE_FAILED", e.getMessage()));
    }

    @ExceptionHandler(RateNotFoundException.class)
    ResponseEntity<ErrorResponseDto> handleRateNotFound(RateNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDto.of("RATE_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException e) {
        String details = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDto.of("VALIDATION_ERROR", details));
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    ResponseEntity<ErrorResponseDto> handleNotImplemented(UnsupportedOperationException e) {
        log.warn("Funcionalidade ainda nao implementada: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(ErrorResponseDto.of("NOT_IMPLEMENTED",
                        "Esta funcionalidade ainda nao esta disponivel."));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponseDto> handleUnexpected(Exception e) {
        log.error("Erro inesperado", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDto.of("INTERNAL_ERROR", "Erro interno do servidor"));
    }
}
