package org.opm.busybeaver.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.dto.ProjectUsers.ProjectUserShortDto;
import org.opm.busybeaver.dto.Tasks.NewTaskDtoExtended;
import org.opm.busybeaver.dto.Tasks.TaskCreatedDto;
import org.opm.busybeaver.dto.Tasks.TaskDetailsDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.DatabaseConstants;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.enums.TaskFields;
import org.opm.busybeaver.exceptions.ProjectUsers.ProjectUsersExceptions;
import org.opm.busybeaver.exceptions.Sprints.SprintsExceptions;
import org.opm.busybeaver.exceptions.Tasks.TasksExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.TasksRecord;
import org.opm.busybeaver.repository.*;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;


@Service
@Slf4j
public class TasksService implements ValidateUserAndProjectInterface {
    private final UsersRepository usersRepository;
    private final TasksRepository tasksRepository;
    private final ColumnsRepository columnsRepository;
    private final SprintsRepository sprintsRepository;
    private final ProjectUsersRepository projectUsersRepository;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public TasksService(
            UsersRepository usersRepository,
            TasksRepository tasksRepository,
            ColumnsRepository columnsRepository,
            SprintsRepository sprintsRepository,
            ProjectUsersRepository projectUsersRepository
    ) {
        this.usersRepository = usersRepository;
        this.tasksRepository = tasksRepository;
        this.columnsRepository = columnsRepository;
        this.sprintsRepository = sprintsRepository;
        this.projectUsersRepository = projectUsersRepository;
    }

    public TaskCreatedDto addTask(
            @NotNull NewTaskDtoExtended newTaskDto,
            UserDto userDto,
            int projectID,
            HttpServletRequest request) {
        // Validate user exists, is in project and project exists
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        // If column, validate column in project
        if (newTaskDto.getColumnID() != null) {
            columnsRepository.doesColumnExistInProject(newTaskDto.getColumnID(), projectID, request);
        } else {
            // Default to first in-order column if user did not specify a column
            newTaskDto.setColumnID(columnsRepository.getFirstInOrderColumnFromProject(projectID));
        }

        // If assignedTo, validate user exists in Project
        if (newTaskDto.getAssignedTo() != null) {
            projectUsersRepository.isAssignedToUserInProject(newTaskDto.getAssignedTo(), projectID, request);
        }

        // If sprint, validate sprint exists in project
        if (newTaskDto.getSprintID() != null) {
            sprintsRepository.doesSprintExistInProject(newTaskDto.getSprintID(), projectID, request);
        }

        // If priority is null, default to 'None'
        if (newTaskDto.getPriority() == null) {
            newTaskDto.setPriority(DatabaseConstants.PRIORITY_NONE.getValue());
        }

        // Add task
        TaskCreatedDto taskCreatedDto = tasksRepository.addTask(newTaskDto, projectID);
        taskCreatedDto.setTaskLocation(request.getContextPath(), projectID);

        return taskCreatedDto;
    }

    public TaskDetailsDto getTaskDetails(UserDto userDto, int projectID, int taskID, HttpServletRequest request) {
        // Validate user, and user in valid project
        ProjectUserShortDto projectUser = validateProjectUserValidAndInsideValidProject(userDto, projectID, request);

        TaskDetailsDto taskDetailsDto = tasksRepository.getTaskDetails(taskID, projectUser.userProjectID(), request);
        taskDetailsDto.setTaskLocation(request.getContextPath(), projectID);

        return taskDetailsDto;
    }

    public void moveTask(UserDto userDto, int projectID, int taskID, int columnID, HttpServletRequest request) {
        // Validate user exists, is in project and project exists
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        // Validate new column exists in project
        columnsRepository.doesColumnExistInProject(columnID, projectID, request);

        // Validate task, task in project
        tasksRepository.doesTaskExistInProject(taskID, projectID, request);

        // Validate if task already in the column to move to
        tasksRepository.isTaskAlreadyInColumn(taskID, columnID, request);

        // Move task over to the other column
        tasksRepository.moveTaskToAnotherColumn(taskID, projectID, columnID);
    }

    public boolean modifyTask(
            UserDto userDto,
            int projectID,
            int taskID,
            @NotNull Map<String, Object> fieldsToEdit,
            HttpServletRequest request)
        throws TasksExceptions.TaskFieldNotFound {

        if (fieldsToEdit.isEmpty()) return false;

        validateUserValidAndInsideValidProject(userDto, projectID, request);

        if (!TaskFields.areTaskFieldsValid(fieldsToEdit)) {
            TasksExceptions.TaskFieldNotFound taskFieldNotFound =
                    new TasksExceptions.TaskFieldNotFound(ErrorMessageConstants.TASK_FIELD_NOT_FOUND.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.TASK_FIELD_NOT_FOUND.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    taskFieldNotFound);

            throw taskFieldNotFound;
        }

        TasksRecord taskToEdit = tasksRepository.doesTaskExistInProject(taskID, projectID, request);

        modifyTaskPriority(taskToEdit, fieldsToEdit, request);
        modifyTaskDueDate(taskToEdit, fieldsToEdit, request);
        modifyTaskSprintID(taskToEdit, fieldsToEdit, projectID, request);
        modifyTaskAssignedTo(taskToEdit, fieldsToEdit, projectID, request);
        modifyTaskDescription(taskToEdit, fieldsToEdit, request);
        modifyTaskTitle(taskToEdit, fieldsToEdit, request);

        boolean taskUpdated = taskToEdit.changed();
        if (taskUpdated) {
            taskToEdit.update();
        }

        return taskUpdated;
    }

    public void deleteTask(UserDto userDto, int projectID, int taskID, HttpServletRequest request) {
        // Validate user exists, is in project and project exists
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        // Validate task, task in project
        tasksRepository.doesTaskExistInProject(taskID, projectID, request);

        // Delete task from project
        tasksRepository.deleteTask(taskID);
    }

    private void modifyTaskPriority(
            TasksRecord taskToEdit,
            @NotNull Map<String, Object> fieldsToEdit,
            HttpServletRequest request)
        throws TasksExceptions.InvalidTaskPriority {

        final String PRIORITY = TaskFields.priority.toString();
        if (fieldsToEdit.containsKey(PRIORITY) && fieldsToEdit.get(PRIORITY) != null) {
            log.info("Changing task priority to: {} | RID: {}", fieldsToEdit.get(PRIORITY), request.getAttribute(RID));

            if (!TaskFields.priorityFields.isPriorityFieldValid(fieldsToEdit.get(PRIORITY).toString())) {
                TasksExceptions.InvalidTaskPriority taskPriorityException =
                        new TasksExceptions.InvalidTaskPriority(ErrorMessageConstants.TASK_PRIORITY_INVALID.getValue());

                log.error("{}. | RID: {} {}",
                        ErrorMessageConstants.TASK_PRIORITY_INVALID,
                        request.getAttribute(RID),
                        System.lineSeparator(),
                        taskPriorityException);

                throw taskPriorityException;
            }

            if (taskToEdit.getPriority() == null || !taskToEdit.getPriority().equals(fieldsToEdit.get(PRIORITY).toString())) {
                taskToEdit.setPriority(fieldsToEdit.get(PRIORITY).toString());
            }
        }
    }

    private void modifyTaskDueDate(
            TasksRecord taskToEdit,
            @NotNull Map<String, Object> fieldsToEdit,
            HttpServletRequest request)
        throws TasksExceptions.InvalidTaskDueDate {
        // Task due date can be set to NULL, indicated by incoming JSON field for dueDate being set to null

        final String DUE_DATE = TaskFields.dueDate.toString();
        if (fieldsToEdit.containsKey(DUE_DATE)) {
            if (fieldsToEdit.get(DUE_DATE) != null) {
                DateTimeFormatter dueDateFormat = DateTimeFormatter.ISO_LOCAL_DATE;
                try {
                    LocalDate dueDate = LocalDate.parse(fieldsToEdit.get(DUE_DATE).toString(), dueDateFormat);
                    log.info("Changing task due date to: {} | RID: {}", dueDate, request.getAttribute(RID));
                    if (dueDate.isBefore(LocalDate.now())) {
                        TasksExceptions.InvalidTaskDueDate taskDueDateException =
                                new TasksExceptions.InvalidTaskDueDate(
                                        ErrorMessageConstants.TASK_DUE_DATE_INVALID.getValue());

                        log.error("{}. | RID: {} {}",
                                ErrorMessageConstants.TASK_DUE_DATE_INVALID,
                                request.getAttribute(RID),
                                System.lineSeparator(),
                                taskDueDateException);

                        throw taskDueDateException;
                    }

                    if (taskToEdit.getDueDate() == null || !taskToEdit.getDueDate().isEqual(dueDate)) {
                        taskToEdit.setDueDate(dueDate);
                    }

                } catch (DateTimeParseException dateTimeParseException) {
                    TasksExceptions.InvalidTaskDueDate taskDueDateException =
                            new TasksExceptions.InvalidTaskDueDate(
                                    ErrorMessageConstants.TASK_DUE_DATE_INVALID.getValue());

                    log.error("{}. | RID: {} {}",
                            ErrorMessageConstants.TASK_DUE_DATE_INVALID,
                            request.getAttribute(RID),
                            System.lineSeparator(),
                            taskDueDateException);

                    throw taskDueDateException;
                }
            } else {
                if (taskToEdit.getDueDate() != null) {
                    taskToEdit.setDueDate(null);
                }
            }
        }
    }

    private void modifyTaskSprintID(
            TasksRecord taskToEdit,
            @NotNull Map<String, Object> fieldsToEdit,
            int projectID,
            HttpServletRequest request)
        throws SprintsExceptions.SprintDoesNotExistInProject {
        // Task sprintID can be set to NULL, indicated by incoming JSON field for sprintID being set to null

        final String SPRINT_ID = TaskFields.sprintID.toString();
        if (fieldsToEdit.containsKey(SPRINT_ID)) {
            log.info("Changing task sprint to: {} | RID: {}", fieldsToEdit.get(SPRINT_ID), request.getAttribute(RID));
            if (fieldsToEdit.get(SPRINT_ID) != null) {
                try {
                    int sprintID = Integer.parseInt(fieldsToEdit.get(SPRINT_ID).toString());
                    sprintsRepository.doesSprintExistInProject(sprintID, projectID, request);

                    if (taskToEdit.getSprintId() == null || !taskToEdit.getSprintId().equals(sprintID)) {
                        taskToEdit.setSprintId(sprintID);
                    }

                } catch (IllegalArgumentException illegalArgumentException) {
                    SprintsExceptions.SprintDoesNotExistInProject sprintDoesNotExistInProject =
                            new SprintsExceptions.SprintDoesNotExistInProject(
                                    ErrorMessageConstants.SPRINT_NOT_IN_PROJECT.getValue());

                    log.error("{}. | RID: {} {}",
                            ErrorMessageConstants.SPRINT_NOT_IN_PROJECT.getValue(),
                            request.getAttribute(RID),
                            System.lineSeparator(),
                            sprintDoesNotExistInProject);

                    throw sprintDoesNotExistInProject;
                }
            } else {
                if (taskToEdit.getSprintId() != null) {
                    taskToEdit.setSprintId(null);
                }
            }
        }
    }

    private void modifyTaskDescription(
            TasksRecord taskToEdit,
            @NotNull Map<String, Object> fieldsToEdit,
            HttpServletRequest request)
        throws TasksExceptions.InvalidTaskDescription {
        // Task description can be set to NULL, indicated by incoming JSON field for description being set to null

        final String DESCRIPTION = TaskFields.description.toString();
        if (fieldsToEdit.containsKey(DESCRIPTION)) {
            Object description = fieldsToEdit.get(DESCRIPTION);
            if (description != null) {
                if (!(description instanceof String) || (((String) description).length() > 500)) {
                    TasksExceptions.InvalidTaskDescription invalidTaskDescription =
                            new TasksExceptions.InvalidTaskDescription(
                                    ErrorMessageConstants.TASK_DESCRIPTION_INVALID.getValue());

                    log.error("{}. | RID: {} {}",
                            ErrorMessageConstants.TASK_DESCRIPTION_INVALID.getValue(),
                            request.getAttribute(RID),
                            System.lineSeparator(),
                            invalidTaskDescription);

                    throw invalidTaskDescription;
                }
                if (taskToEdit.getDescription() == null || !taskToEdit.getDescription().equals(description)) {
                    taskToEdit.setDescription((String) description);
                }
            } else {
                if (taskToEdit.getDescription() != null) {
                    taskToEdit.setDescription(null);
                }
            }
        }
    }

    private void modifyTaskTitle(
            TasksRecord taskToEdit,
            @NotNull Map<String, Object> fieldsToEdit,
            HttpServletRequest request)
        throws TasksExceptions.InvalidTaskTitle {

        final String TITLE = TaskFields.title.toString();
        if (fieldsToEdit.containsKey(TITLE) && fieldsToEdit.get(TITLE) != null) {
            Object title = fieldsToEdit.get(TITLE);
            if (!(title instanceof String) || (((((String) title).length() > 50) || ((String) title).length() < 3))) {
                TasksExceptions.InvalidTaskTitle invalidTaskTitle =
                        new TasksExceptions.InvalidTaskTitle(ErrorMessageConstants.TASK_TITLE_INVALID.getValue());

                log.error("{}. | RID: {} {}",
                        ErrorMessageConstants.TASK_TITLE_INVALID.getValue(),
                        request.getAttribute(RID),
                        System.lineSeparator(),
                        invalidTaskTitle);

                throw invalidTaskTitle;
            }

            if (!taskToEdit.getTitle().equals(title)) {
                taskToEdit.setTitle((String) title);
            }
        }
    }

    private void modifyTaskAssignedTo(
            TasksRecord taskToEdit,
            @NotNull Map<String, Object> fieldsToEdit,
            int projectID,
            HttpServletRequest request)
        throws ProjectUsersExceptions.AssignedToUserNotInProjectOrNonexistent {
        // Task assignedTo can be set to NULL, indicated by incoming JSON field for assignedTo being set to null

        final String ASSIGNED_TO = TaskFields.assignedTo.toString();
        if (fieldsToEdit.containsKey(ASSIGNED_TO)) {
            if (fieldsToEdit.get(ASSIGNED_TO) != null) {
                try {
                    int assignedToID = Integer.parseInt(fieldsToEdit.get(ASSIGNED_TO).toString());
                    projectUsersRepository.isAssignedToUserInProject(assignedToID, projectID, request);

                    if (taskToEdit.getAssignedTo() == null || !taskToEdit.getAssignedTo().equals(assignedToID)) {
                        taskToEdit.setAssignedTo(assignedToID);
                    }

                } catch (IllegalArgumentException illegalArgumentException) {
                    ProjectUsersExceptions.AssignedToUserNotInProjectOrNonexistent assignedToUserNotInProjectOrNonexistent =
                            new ProjectUsersExceptions.AssignedToUserNotInProjectOrNonexistent(
                                    ErrorMessageConstants.ASSIGNED_TO_USER_NOT_IN_PROJECT_OR_USER_NOT_EXIST.getValue()
                            );

                    log.error("{}. | RID: {} {}",
                            ErrorMessageConstants.ASSIGNED_TO_USER_NOT_IN_PROJECT_OR_USER_NOT_EXIST.getValue(),
                            request.getAttribute(RID),
                            System.lineSeparator(),
                            assignedToUserNotInProjectOrNonexistent);

                    throw assignedToUserNotInProjectOrNonexistent;
                }
            } else {
                if (taskToEdit.getAssignedTo() != null) {
                    taskToEdit.setAssignedTo(null);
                }
            }
        }
    }

    @Override
    public BeaverusersRecord validateUserValidAndInsideValidProject(
            UserDto userDto,
            int projectID,
            HttpServletRequest request) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID, request);

        return beaverusersRecord;
    }

    private ProjectUserShortDto validateProjectUserValidAndInsideValidProject(
            UserDto userDto,
            int projectID,
            HttpServletRequest request) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        // Validate user in project and project exists
        return projectUsersRepository.getUserInProject(projectID, beaverusersRecord, request);
    }
}
