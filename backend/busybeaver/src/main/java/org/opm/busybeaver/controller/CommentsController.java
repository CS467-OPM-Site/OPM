package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.Columns.NewColumnDto;
import org.opm.busybeaver.dto.Comments.NewCommentBodyDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.exceptions.Columns.ColumnsExceptions;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.opm.busybeaver.exceptions.Users.UsersExceptions;
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

    @Autowired
    public CommentsController(CommentsService commentsService) { this.commentsService = commentsService; }

    @PostMapping(PROJECTS_PATH + "/{projectID}" + TASK_PATH + "/{taskID}")
    public void addCommentToTask(
            HttpServletRequest request,
            @Valid @RequestBody NewCommentBodyDto newCommentBodyDto,
            @PathVariable int projectID,
            @PathVariable int taskID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) {
        commentsService.addCommentToTask(userDto, projectID, taskID, newCommentBodyDto);
//        response.setHeader(BusyBeavConstants.LOCATION.getValue(), newColumn.getColumnLocation());
//        response.setStatus(HttpStatus.CREATED.value());
//
//        return newColumn;
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
