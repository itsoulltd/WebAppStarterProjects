package com.infoworks.controllers.advice;

import com.infoworks.objects.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalResponseEntityException extends ResponseEntityExceptionHandler {

    private static Logger LOG = LoggerFactory.getLogger(GlobalResponseEntityException.class);

    @SuppressWarnings("Duplicates")
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        body.put("errors", errors);

        LOG.error("Validation failed: {}", body);

        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllKindOfExceptions(
            Exception ex, WebRequest request) {
        //
        Response response = Response.CreateErrorResponse(ex);
        LOG.error(ex.getMessage(), ex);
        return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
