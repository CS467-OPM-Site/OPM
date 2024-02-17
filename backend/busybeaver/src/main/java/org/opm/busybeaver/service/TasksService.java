package org.opm.busybeaver.service;

import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.dto.Tasks.NewTaskDtoExtended;
import org.opm.busybeaver.dto.Tasks.TaskCreatedDto;
import org.opm.busybeaver.dto.Tasks.TaskDetailsDto;
import org.opm.busybeaver.dto.Users.UserDto;
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
public class TasksService implements ValidateUserAndProjectInterface {
    private final ProjectsRepository projectsRepository;
    private final UsersRepository usersRepository;
    private final TasksRepository tasksRepository;
    private final ColumnsRepository columnsRepository;
    private final SprintsRepository sprintsRepository;
    private final ProjectUsersRepository projectUsersRepository;

    @Autowired
    public TasksService(
            ProjectsRepository projectsRepository,
            UsersRepository usersRepository,
            TasksRepository tasksRepository,
            ColumnsRepository columnsRepository,
            SprintsRepository sprintsRepository,
            ProjectUsersRepository projectUsersRepository
    ) {
        this.projectsRepository = projectsRepository;
        this.usersRepository = usersRepository;
        this.tasksRepository = tasksRepository;
        this.columnsRepository = columnsRepository;
        this.sprintsRepository = sprintsRepository;
        this.projectUsersRepository = projectUsersRepository;
    }

    public TaskCreatedDto addTask(
            NewTaskDtoExtended newTaskDto,
            UserDto userDto,
            int projectID,
            String contextPath) {
        // Validate user exists, is in project and project exists
        validateUserValidAndInsideValidProject(userDto, projectID);

        // If column, validate column in project
        if (newTaskDto.getColumnID() != null) {
            columnsRepository.doesColumnExistInProject(newTaskDto.getColumnID(), projectID);
        } else {
            // Default to first in-order column if user did not specify a column
            newTaskDto.setColumnID(columnsRepository.getFirstInOrderColumnFromProject(projectID));
        }

        // If assignedTo, validate user exists in Project
        if (newTaskDto.getAssignedTo() != null) {
            projectUsersRepository.isAssignedToUserInProject(newTaskDto.getAssignedTo(), projectID);
        }

        // If sprint, validate sprint exists in project
        if (newTaskDto.getSprintID() != null) {
            sprintsRepository.doesSprintExistInProject(newTaskDto.getSprintID(), projectID);
        }

        // If priority is null, default to 'None'
        if (newTaskDto.getPriority() == null) {
            newTaskDto.setPriority(DatabaseConstants.PRIORITY_NONE.getValue());
        }

        // Add task
        TaskCreatedDto taskCreatedDto = tasksRepository.addTask(newTaskDto, projectID);
        taskCreatedDto.setTaskLocation(contextPath, projectID);

        // Update project last updated time
        projectsRepository.updateLastUpdatedForProject(projectID);
        return taskCreatedDto;
    }

    public TaskDetailsDto getTaskDetails(UserDto userDto, int projectID, int taskID, String contextPath) {
        // Validate user, and user in valid project
        validateUserValidAndInsideValidProject(userDto, projectID);

        TaskDetailsDto taskDetailsDto = tasksRepository.getTaskDetails(taskID);
        taskDetailsDto.setTaskLocation(contextPath, projectID);

        return taskDetailsDto;
    }

    public void moveTask(UserDto userDto, int projectID, int taskID, int columnID) {
        // Validate user exists, is in project and project exists
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Validate new column exists in project
        columnsRepository.doesColumnExistInProject(columnID, projectID);

        // Validate task, task in project
        tasksRepository.doesTaskExistInProject(taskID, projectID);

        // Validate if task already in the column to move to
        tasksRepository.isTaskAlreadyInColumn(taskID, columnID);

        // Move task over to the other column
        tasksRepository.moveTaskToAnotherColumn(taskID, projectID, columnID);

        // Update project last updated time
        projectsRepository.updateLastUpdatedForProject(projectID);
    }

    public boolean modifyTask(UserDto userDto, int projectID, int taskID, Map<String, Object> fieldsToEdit)
        throws TasksExceptions.TaskFieldNotFound {

        if (fieldsToEdit.isEmpty()) return false;

        validateUserValidAndInsideValidProject(userDto, projectID);

        if (!TaskFields.areTaskFieldsValid(fieldsToEdit)) {
            throw new TasksExceptions.TaskFieldNotFound(ErrorMessageConstants.TASK_FIELD_NOT_FOUND.getValue());
        }

        TasksRecord taskToEdit = tasksRepository.doesTaskExistInProject(taskID, projectID);

        modifyTaskPriority(taskToEdit, fieldsToEdit);
        modifyTaskDueDate(taskToEdit, fieldsToEdit);
        modifyTaskSprintID(taskToEdit, fieldsToEdit, projectID);
        modifyTaskAssignedTo(taskToEdit, fieldsToEdit, projectID);
        modifyTaskDescription(taskToEdit, fieldsToEdit);
        modifyTaskTitle(taskToEdit, fieldsToEdit);

        boolean taskUpdated = taskToEdit.changed();
        if (taskUpdated) {
            taskToEdit.update();
            projectsRepository.updateLastUpdatedForProject(projectID);
        }

        return taskUpdated;
    }

    public void deleteTask(UserDto userDto, int projectID, int taskID) {
        // Validate user exists, is in project and project exists
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Validate task, task in project
        tasksRepository.doesTaskExistInProject(taskID, projectID);

        // Delete task from project
        tasksRepository.deleteTask(taskID);

        // Update project last updated time
        projectsRepository.updateLastUpdatedForProject(projectID);
    }

    private void modifyTaskPriority(TasksRecord taskToEdit, @NotNull Map<String, Object> fieldsToEdit)
        throws TasksExceptions.InvalidTaskPriority {

        final String PRIORITY = TaskFields.priority.toString();
        if (fieldsToEdit.containsKey(PRIORITY) && fieldsToEdit.get(PRIORITY) != null) {

            if (!TaskFields.priorityFields.isPriorityFieldValid(fieldsToEdit.get(PRIORITY).toString())) {
                throw new TasksExceptions.InvalidTaskPriority(ErrorMessageConstants.TASK_PRIORITY_INVALID.getValue());
            }

            if (taskToEdit.getPriority() == null || !taskToEdit.getPriority().equals(fieldsToEdit.get(PRIORITY).toString())) {
                taskToEdit.setPriority(fieldsToEdit.get(PRIORITY).toString());
            }
        }
    }

    private void modifyTaskDueDate(TasksRecord taskToEdit, @NotNull Map<String, Object> fieldsToEdit)
        throws TasksExceptions.InvalidTaskDueDate {
        // Task due date can be set to NULL, indicated by incoming JSON field for dueDate being set to null

        final String DUE_DATE = TaskFields.dueDate.toString();
        if (fieldsToEdit.containsKey(DUE_DATE)) {
            if (fieldsToEdit.get(DUE_DATE) != null) {
                DateTimeFormatter dueDateFormat = DateTimeFormatter.ISO_LOCAL_DATE;
                try {
                    LocalDate dueDate = LocalDate.parse(fieldsToEdit.get(DUE_DATE).toString(), dueDateFormat);
                    if (dueDate.isBefore(LocalDate.now())) {
                        throw new TasksExceptions.InvalidTaskDueDate(
                                ErrorMessageConstants.TASK_DUE_DATE_INVALID.getValue());
                    }

                    if (taskToEdit.getDueDate() == null || !taskToEdit.getDueDate().isEqual(dueDate)) {
                        taskToEdit.setDueDate(dueDate);
                    }

                } catch (DateTimeParseException dateTimeParseException) {
                    throw new TasksExceptions.InvalidTaskDueDate(
                            ErrorMessageConstants.TASK_DUE_DATE_INVALID.getValue());
                }
            } else {
                if (taskToEdit.getDueDate() != null) {
                    taskToEdit.setDueDate(null);
                }
            }
        }
    }

    private void modifyTaskSprintID(TasksRecord taskToEdit, @NotNull Map<String, Object> fieldsToEdit, int projectID)
        throws SprintsExceptions.SprintDoesNotExistInProject {
        // Task sprintID can be set to NULL, indicated by incoming JSON field for sprintID being set to null

        final String SPRINT_ID = TaskFields.sprintID.toString();
        if (fieldsToEdit.containsKey(SPRINT_ID)) {
            if (fieldsToEdit.get(SPRINT_ID) != null) {
                try {
                    int sprintID = Integer.parseInt(fieldsToEdit.get(SPRINT_ID).toString());
                    sprintsRepository.doesSprintExistInProject(sprintID, projectID);

                    if (taskToEdit.getSprintId() == null || !taskToEdit.getSprintId().equals(sprintID)) {
                        taskToEdit.setSprintId(sprintID);
                    }

                } catch (IllegalArgumentException illegalArgumentException) {
                    throw new SprintsExceptions.SprintDoesNotExistInProject(ErrorMessageConstants.SPRINT_NOT_IN_PROJECT.getValue());
                }
            } else {
                if (taskToEdit.getSprintId() != null) {
                    taskToEdit.setSprintId(null);
                }
            }
        }
    }

    private void modifyTaskDescription(TasksRecord taskToEdit, @NotNull Map<String, Object> fieldsToEdit)
        throws TasksExceptions.InvalidTaskDescription {
        // Task description can be set to NULL, indicated by incoming JSON field for description being set to null

        final String DESCRIPTION = TaskFields.description.toString();
        if (fieldsToEdit.containsKey(DESCRIPTION)) {
            Object description = fieldsToEdit.get(DESCRIPTION);
            if (description != null) {
                if (!(description instanceof String) || (((String) description).length() > 500)) {
                    throw new TasksExceptions.InvalidTaskDescription(ErrorMessageConstants.TASK_DESCRIPTION_INVALID.getValue());
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

    private void modifyTaskTitle(TasksRecord taskToEdit, @NotNull Map<String, Object> fieldsToEdit)
        throws TasksExceptions.InvalidTaskTitle {

        final String TITLE = TaskFields.title.toString();
        if (fieldsToEdit.containsKey(TITLE) && fieldsToEdit.get(TITLE) != null) {
            Object title = fieldsToEdit.get(TITLE);
            if (!(title instanceof String) || (((((String) title).length() > 50) || ((String) title).length() < 3))) {
                throw new TasksExceptions.InvalidTaskTitle(ErrorMessageConstants.TASK_TITLE_INVALID.getValue());
            }

            if (!taskToEdit.getTitle().equals(title)) {
                taskToEdit.setTitle((String) title);
            }
        }
    }

    private void modifyTaskAssignedTo(TasksRecord taskToEdit, @NotNull Map<String, Object> fieldsToEdit, int projectID)
        throws ProjectUsersExceptions.AssignedToUserNotInProjectOrNonexistent {
        // Task assignedTo can be set to NULL, indicated by incoming JSON field for assignedTo being set to null

        final String ASSIGNED_TO = TaskFields.assignedTo.toString();
        if (fieldsToEdit.containsKey(ASSIGNED_TO)) {
            if (fieldsToEdit.get(ASSIGNED_TO) != null) {
                try {
                    int assignedToID = Integer.parseInt(fieldsToEdit.get(ASSIGNED_TO).toString());
                    projectUsersRepository.isAssignedToUserInProject(assignedToID, projectID);

                    if (taskToEdit.getAssignedTo() == null || !taskToEdit.getAssignedTo().equals(assignedToID)) {
                        taskToEdit.setAssignedTo(assignedToID);
                    }

                } catch (IllegalArgumentException illegalArgumentException) {
                    throw new ProjectUsersExceptions.AssignedToUserNotInProjectOrNonexistent(
                            ErrorMessageConstants.ASSIGNED_TO_USER_NOT_IN_PROJECT_OR_USER_NOT_EXIST.getValue());
                }
            } else {
                if (taskToEdit.getAssignedTo() != null) {
                    taskToEdit.setAssignedTo(null);
                }
            }
        }
    }

    @Override
    public BeaverusersRecord validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID);

        return beaverusersRecord;
    }
}
