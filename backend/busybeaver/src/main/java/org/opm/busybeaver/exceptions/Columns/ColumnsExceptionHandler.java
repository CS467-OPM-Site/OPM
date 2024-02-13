package org.opm.busybeaver.exceptions.Columns;

import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.opm.busybeaver.utils.Utils.generateExceptionResponse;

@ControllerAdvice
public class ColumnsExceptionHandler {
    @ExceptionHandler(ColumnsExceptions.ColumnDoesNotExistInProject.class)
    public ResponseEntity<?> columnDoesNotExistInProject() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.COLUMN_NOT_IN_PROJECT.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ColumnsExceptions.ColumnTitleAlreadyInProject.class)
    public ResponseEntity<?> columnTitleAlreadyExistsInProject() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.COLUMN_TITLE_ALREADY_IN_PROJECT.getValue(),
                        HttpStatus.CONFLICT.value()
                ),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ColumnsExceptions.ColumnStillContainsTasks.class)
    public ResponseEntity<?> columnStillContainsTasks() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.COLUMN_CONTAINS_TASKS.getValue(),
                        HttpStatus.FORBIDDEN.value()
                ),
                HttpStatus.FORBIDDEN
        );
    }
}
