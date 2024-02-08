package org.opm.busybeaver.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    public ResponseEntity<?> invalidHttpMessage(HttpMessageNotReadableException err, HttpServletRequest request) {

        if (err.getMessage().contains(ErrorMessageConstants.REQUIRED_REQUEST_BODY_IS_MISSING.getValue())) {
            return new ResponseEntity<>(
                    generateResponse(
                            ErrorMessageConstants.REQUIRED_REQUEST_BODY_IS_MISSING.getValue(),
                            HttpStatus.BAD_REQUEST.value()
                    ), HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                generateResponse(
                        ErrorMessageConstants.INVALID_HTTP_REQUEST.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> userAlreadyExists() {
        return new ResponseEntity<>(
                generateResponse(
                        ErrorMessageConstants.USER_ALREADY_EXISTS.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<?> userDoesNotExist() {
        return new ResponseEntity<>(
                generateResponse(
                        ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(UserNotInTeamOrTeamDoesNotExistException.class)
    public ResponseEntity<?> userNotInTeam() {
        return new ResponseEntity<>(
                generateResponse(
                        ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(TeamAlreadyExistsForUserException.class)
    public ResponseEntity<?> teamAlreadyExistsForUser() {
        return new ResponseEntity<>(
                generateResponse(
                        ErrorMessageConstants.TEAM_ALREADY_EXISTS_FOR_USER.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserAlreadyInTeamException.class)
    public ResponseEntity<?> userAlreadyInTeam() {
        return new ResponseEntity<>(
                generateResponse(
                        ErrorMessageConstants.USER_ALREADY_IN_TEAM.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> methodNotSupported() {
        return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
    }

    private HashMap<String, Object> generateResponse(String message, Integer errorCode) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(ErrorMessageConstants.MESSAGE.getValue(), message);
        result.put(ErrorMessageConstants.CODE.getValue(), errorCode);

        return result;
    }
}
