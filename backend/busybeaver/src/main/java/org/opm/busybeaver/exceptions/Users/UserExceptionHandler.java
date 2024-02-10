package org.opm.busybeaver.exceptions.Users;

import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.opm.busybeaver.utils.Utils.generateExceptionResponse;

@ControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> userAlreadyExists() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.USER_ALREADY_EXISTS.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<?> userDoesNotExist() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }
}
