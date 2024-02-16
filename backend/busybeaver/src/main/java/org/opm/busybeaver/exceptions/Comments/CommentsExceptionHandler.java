package org.opm.busybeaver.exceptions.Comments;

import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.opm.busybeaver.utils.Utils.generateExceptionResponse;

@ControllerAdvice
public class CommentsExceptionHandler {
    @ExceptionHandler(CommentsExceptions.CommentDoesNotExistOnTask.class)
    public ResponseEntity<?> commentDoesNotExistOnTask() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.COMMENT_NOT_FOUND_ON_TASK.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(CommentsExceptions.UserDidNotLeaveThisComment.class)
    public ResponseEntity<?> userDidNotLeaveThisComment() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.USER_DID_NOT_LEAVE_THIS_COMMENT.getValue(),
                        HttpStatus.FORBIDDEN.value()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(CommentsExceptions.CommentBodyIdenticalNotModified.class)
    public ResponseEntity<?> commentBodyIdenticalNotModified() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.COMMENT_EQUIVALENT_NOT_MODIFIED.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
