package org.opm.busybeaver.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.opm.busybeaver.utils.Utils.generateExceptionResponse;

@ControllerAdvice
public class ValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> notValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorUnique = ErrorMessageConstants.INVALID_ARGUMENT.getValue();
        if (ex.getFieldError() != null) {
           errorUnique = ex.getFieldError().getDefaultMessage();
        }

        Map<String, Object> result = new HashMap<>();
        result.put(ErrorMessageConstants.MESSAGE.getValue(), errorUnique);
        result.put(ErrorMessageConstants.CODE.getValue(), HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> unableToReadHTTPMessage(HttpMessageNotReadableException err, HttpServletRequest request) {

        if (err.getMessage().contains(ErrorMessageConstants.REQUIRED_REQUEST_BODY_IS_MISSING.getValue())) {
            return new ResponseEntity<>(
                    generateExceptionResponse(
                            ErrorMessageConstants.REQUIRED_REQUEST_BODY_IS_MISSING.getValue(),
                            HttpStatus.BAD_REQUEST.value()
                    ), HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.INVALID_HTTP_REQUEST.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> methodNotSupported() {
        return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
    }

}
