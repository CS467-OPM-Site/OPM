package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.dto.Tasks.NewTaskDto;
import org.opm.busybeaver.dto.Tasks.TaskCreatedDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ApiPrefixController
@RestController
@CrossOrigin
public class TasksController {
    private final TaskService taskService;

    @Autowired
    public TasksController (TaskService taskService) { this.taskService = taskService; }

    @PostMapping(BusyBeavPaths.Constants.PROJECTS + "/{projectID}" + BusyBeavPaths.Constants.TASKS)
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

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    public UserDto user(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }

}
