package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.SmallJsonResponse;
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
@Slf4j
public final class TasksController implements GetUserFromBearerTokenInterface {
    private final TasksService tasksService;
    private static final String PROJECTS_PATH = BusyBeavPaths.Constants.PROJECTS;
    private static final String TASKS_PATH  = BusyBeavPaths.Constants.TASKS;
    private static final String COLUMNS_PATH  = BusyBeavPaths.Constants.COLUMNS;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public TasksController (TasksService tasksService) { this.tasksService = tasksService; }

    @GetMapping(PROJECTS_PATH + "/{projectID}" + TASKS_PATH + "/{taskID}")
    public TaskDetailsDto getTaskDetails(
            @NotNull HttpServletRequest request,
            @PathVariable int projectID,
            @PathVariable int taskID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        TaskDetailsDto taskDetailsDto = tasksService
                .getTaskDetails(userDto, projectID, taskID, request);

        log.info("Retrieved all details for task {}. | RID: {}", taskID, request.getAttribute(RID));

        return taskDetailsDto;
    }

    @PostMapping(PROJECTS_PATH + "/{projectID}" + TASKS_PATH)
    public @NotNull TaskCreatedDto addTask(
            @NotNull HttpServletRequest request,
            @PathVariable int projectID,
            @Valid @RequestBody NewTaskDtoExtended newTaskDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            @NotNull HttpServletResponse response
    ) {
        TaskCreatedDto taskCreatedDto = tasksService.addTask(newTaskDto, userDto, projectID, request);
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), taskCreatedDto.getTaskLocation());
        response.setStatus(HttpStatus.CREATED.value());

        log.info("Made a new task called '{}'. | RID: {}", newTaskDto.getTitle(), request.getAttribute(RID));

        return taskCreatedDto;
    }

    @Contract("_, _, _, _, _ -> new")
    @PutMapping(PROJECTS_PATH + "/{projectID}" + TASKS_PATH + "/{taskID}" + COLUMNS_PATH + "/{columnID}")
    public @NotNull SmallJsonResponse moveTask(
            @NotNull HttpServletRequest request,
            @PathVariable int projectID,
            @PathVariable int taskID,
            @PathVariable int columnID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        tasksService.moveTask(userDto, projectID, taskID, columnID, request);
        log.info("Moved task to a new column. | RID: {}", request.getAttribute(RID));

        return new SmallJsonResponse(HttpStatus.OK.value(), SuccessMessageConstants.TASK_MOVED.getValue());
    }

    @Contract("_, _, _, _, _ -> new")
    @PutMapping(PROJECTS_PATH + "/{projectID}" + TASKS_PATH + "/{taskID}")
    public @NotNull SmallJsonResponse modifyTask(
            HttpServletRequest request,
            @PathVariable int projectID,
            @PathVariable int taskID,
            @Valid @RequestBody Map<String, Object> editTaskData,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        // Including the
        if (tasksService.modifyTask(userDto, projectID, taskID, editTaskData, request)) {
           log.info("Task successfully modified. | RID: {}", request.getAttribute(RID));
           return new SmallJsonResponse(
                   HttpStatus.OK.value(),
                   SuccessMessageConstants.TASK_MODIFIED.getValue()
           );
        }

        log.info("Task was not modified, no changes found. | RID: {}", request.getAttribute(RID));
        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.TASK_NOT_MODIFIED.getValue()
        );
    }

    @Contract("_, _, _, _ -> new")
    @DeleteMapping(PROJECTS_PATH + "/{projectID}" + TASKS_PATH + "/{taskID}")
    public @NotNull SmallJsonResponse deleteTask(
            @NotNull HttpServletRequest request,
            @PathVariable int projectID,
            @PathVariable int taskID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        tasksService.deleteTask(userDto, projectID, taskID, request);
        log.info("Task was successfully deleted. | RID: {}", request.getAttribute(RID));

        return new SmallJsonResponse(HttpStatus.OK.value(), SuccessMessageConstants.TASK_DELETED.getValue());
    }


    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(@NotNull HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }

}
