package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.Comments.CommentInTaskDto;
import org.opm.busybeaver.dto.Comments.NewCommentBodyDto;
import org.opm.busybeaver.dto.SmallJsonResponse;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.SuccessMessageConstants;
import org.opm.busybeaver.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ApiPrefixController
@RestController
@CrossOrigin
public final class CommentsController implements GetUserFromBearerTokenInterface {

    private final CommentsService commentsService;
    private static final String PROJECTS_PATH = BusyBeavPaths.Constants.PROJECTS;
    private static final String TASK_PATH = BusyBeavPaths.Constants.TASKS;
    private static final String COMMENT_PATH = BusyBeavPaths.Constants.COMMENTS;

    @Autowired
    public CommentsController(CommentsService commentsService) { this.commentsService = commentsService; }

    @PostMapping(PROJECTS_PATH + "/{projectID}" + TASK_PATH + "/{taskID}")
    public CommentInTaskDto addCommentToTask(
            HttpServletRequest request,
            @Valid @RequestBody NewCommentBodyDto newCommentBodyDto,
            @PathVariable int projectID,
            @PathVariable int taskID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) {
        CommentInTaskDto newComment = commentsService.addCommentToTask(
                userDto, projectID, taskID, newCommentBodyDto, request.getContextPath()
        );

        response.setHeader(BusyBeavConstants.LOCATION.getValue(), newComment.getCommentLocation());
        response.setStatus(HttpStatus.CREATED.value());
        return newComment;
    }

    @PutMapping(PROJECTS_PATH +"/{projectID}" + TASK_PATH + "/{taskID}" + COMMENT_PATH + "/{commentID}")
    public SmallJsonResponse addCommentToTask(
            @Valid @RequestBody NewCommentBodyDto newCommentBodyDto,
            @PathVariable int projectID,
            @PathVariable int taskID,
            @PathVariable int commentID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        commentsService.modifyCommentOnTask(userDto, projectID, taskID, commentID, newCommentBodyDto);

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.COMMENT_MODIFIED.getValue()
        );
    }
    @DeleteMapping(PROJECTS_PATH +"/{projectID}" + TASK_PATH + "/{taskID}" + COMMENT_PATH + "/{commentID}")
    public SmallJsonResponse removeCommentFromTask(
            @PathVariable int projectID,
            @PathVariable int taskID,
            @PathVariable int commentID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        commentsService.removeCommentFromTask(userDto, projectID, taskID, commentID);

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.COMMENT_DELETED.getValue()
        );
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
