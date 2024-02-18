package org.opm.busybeaver.controller;

import com.google.api.Http;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.SmallJsonResponse;
import org.opm.busybeaver.dto.Tasks.EditTaskDto;
import org.opm.busybeaver.dto.Tasks.NewTaskDtoExtended;
import org.opm.busybeaver.dto.Tasks.TaskCreatedDto;
import org.opm.busybeaver.dto.Tasks.TaskDetailsDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.SuccessMessageConstants;
import org.opm.busybeaver.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ApiPrefixController
@RestController
@CrossOrigin
public final class TasksController implements GetUserFromBearerTokenInterface {
    private final TasksService tasksService;
    private static final String PROJECTS_PATH = BusyBeavPaths.Constants.PROJECTS;
    private static final String TASKS_PATH  = BusyBeavPaths.Constants.TASKS;
    private static final String COLUMNS_PATH  = BusyBeavPaths.Constants.COLUMNS;

    @Autowired
    public TasksController (TasksService tasksService) { this.tasksService = tasksService; }

    @GetMapping(PROJECTS_PATH + "/{projectID}" + TASKS_PATH + "/{taskID}")
    public TaskDetailsDto getTaskDetails(
            HttpServletRequest request,
            @PathVariable int projectID,
            @PathVariable int taskID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        return tasksService.getTaskDetails(userDto, projectID, taskID, request.getContextPath());
    }

    @PostMapping(PROJECTS_PATH + "/{projectID}" + TASKS_PATH)
    public TaskCreatedDto addTask(
            HttpServletRequest request,
            @PathVariable int projectID,
            @Valid @RequestBody NewTaskDtoExtended newTaskDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) {
        TaskCreatedDto taskCreatedDto = tasksService.addTask(newTaskDto, userDto, projectID, request.getContextPath());
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
        tasksService.moveTask(userDto, projectID, taskID, columnID);

        return new SmallJsonResponse(HttpStatus.OK.value(), SuccessMessageConstants.TASK_MOVED.getValue());
    }

    @PutMapping(PROJECTS_PATH + "/{projectID}" + TASKS_PATH + "/{taskID}")
    public SmallJsonResponse modifyTask(
            @PathVariable int projectID,
            @PathVariable int taskID,
            @Valid @RequestBody Map<String, Object> editTaskData,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        // Including the
        if (tasksService.modifyTask(userDto, projectID, taskID, editTaskData)) {
           return new SmallJsonResponse(
                   HttpStatus.OK.value(),
                   SuccessMessageConstants.TASK_MODIFIED.getValue()
           );
        }

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.TASK_NOT_MODIFIED.getValue()
        );
    }

    @DeleteMapping(PROJECTS_PATH + "/{projectID}" + TASKS_PATH + "/{taskID}")
    public SmallJsonResponse deleteTask(
            @PathVariable int projectID,
            @PathVariable int taskID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        tasksService.deleteTask(userDto, projectID, taskID);

        return new SmallJsonResponse(HttpStatus.OK.value(), SuccessMessageConstants.TASK_DELETED.getValue());
    }


    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }

}
