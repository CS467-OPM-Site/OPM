package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.dto.SmallJsonResponse;
import org.opm.busybeaver.dto.Tasks.NewTaskDto;
import org.opm.busybeaver.dto.Tasks.TaskCreatedDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.SuccessMessageConstants;
import org.opm.busybeaver.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ApiPrefixController
@RestController
@CrossOrigin
public class TasksController {
    private final TaskService taskService;
    private static final String PROJECTS_PATH = BusyBeavPaths.Constants.PROJECTS;
    private static final String TASKS_PATH  = BusyBeavPaths.Constants.TASKS;
    private static final String COLUMNS_PATH  = BusyBeavPaths.Constants.COLUMNS;

    @Autowired
    public TasksController (TaskService taskService) { this.taskService = taskService; }

    @PostMapping(PROJECTS_PATH + "/{projectID}" + TASKS_PATH)
    public TaskCreatedDto addTask(
            HttpServletRequest request,
            @PathVariable int projectID,
            @Valid @RequestBody NewTaskDto newTaskDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) {
        TaskCreatedDto taskCreatedDto = taskService.addTask(newTaskDto, userDto, projectID, request.getContextPath());
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), taskCreatedDto.getTaskLocation());
        response.setStatus(HttpStatus.CREATED.value());

        return taskCreatedDto;
    }

    @PutMapping(PROJECTS_PATH + "/{projectID}" + TASKS_PATH + "/{taskID}" + COLUMNS_PATH + "/{columnID}")
    public SmallJsonResponse moveTask(
            @PathVariable int projectID,
            @PathVariable int taskID,
            @PathVariable int columnID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        taskService.moveTask(userDto, projectID, taskID, columnID);

        return new SmallJsonResponse(HttpStatus.OK.value(), SuccessMessageConstants.TASK_MOVED.getValue());
    }

    @DeleteMapping(PROJECTS_PATH + "/{projectID}" + TASKS_PATH + "/{taskID}")
    public SmallJsonResponse deleteTask(
            @PathVariable int projectID,
            @PathVariable int taskID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        taskService.deleteTask(userDto, projectID, taskID);

        return new SmallJsonResponse(HttpStatus.OK.value(), SuccessMessageConstants.TASK_DELETED.getValue());
    }


    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    public UserDto user(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }

}
