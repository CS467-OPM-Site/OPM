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

    @ExceptionHandler(ColumnsExceptions.ColumnIndexIdentical.class)
    public ResponseEntity<?> columnIndexIdentical() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.COLUMN_POSITION_THE_SAME.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ColumnsExceptions.ColumnIndexOutOfBounds.class)
    public ResponseEntity<?> columnIndexOutOfBounds() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.COLUMN_INDEX_OUT_OF_BOUNDS.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
