package org.opm.busybeaver.exceptions.Tasks;

import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.opm.busybeaver.utils.Utils.generateExceptionResponse;

@ControllerAdvice
public class TasksExceptionHandler {
    @ExceptionHandler(TasksExceptions.TaskDoesNotExistInProject.class)
    public ResponseEntity<?> taskDoesNotExistInProject() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TASK_NOT_IN_PROJECT.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(TasksExceptions.TaskAlreadyInColumn.class)
    public ResponseEntity<?> taskAlreadyInColumn() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TASK_ALREADY_IN_COLUMN.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TasksExceptions.TaskFieldNotFound.class)
    public ResponseEntity<?> taskFieldNotFound() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TASK_FIELD_NOT_FOUND.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(TasksExceptions.InvalidTaskPriority.class)
    public ResponseEntity<?> taskInvalidPriority() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TASK_PRIORITY_INVALID.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TasksExceptions.InvalidTaskDueDate.class)
    public ResponseEntity<?> taskInvalidDueDate() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TASK_DUE_DATE_INVALID.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TasksExceptions.InvalidTaskTitle.class)
    public ResponseEntity<?> taskInvalidTitleLength() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TASK_TITLE_INVALID.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TasksExceptions.InvalidTaskDescription.class)
    public ResponseEntity<?> taskInvalidDescriptionLength() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TASK_DESCRIPTION_INVALID.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
