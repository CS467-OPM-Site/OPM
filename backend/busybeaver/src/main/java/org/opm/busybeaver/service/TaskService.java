package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Tasks.NewTaskDto;
import org.opm.busybeaver.dto.Tasks.TaskCreatedDto;
import org.opm.busybeaver.dto.Tasks.TaskDetailsDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.DatabaseConstants;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Columns.ColumnsExceptions;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.opm.busybeaver.exceptions.Sprints.SprintsExceptions;
import org.opm.busybeaver.exceptions.Tasks.TasksExceptions;
import org.opm.busybeaver.exceptions.Users.UsersExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.TasksRecord;
import org.opm.busybeaver.repository.*;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService implements ValidateUserAndProjectInterface {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ColumnRepository columnRepository;
    private final SprintRepository sprintRepository;
    private final ProjectUsersRepository projectUsersRepository;

    @Autowired
    public TaskService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            TaskRepository taskRepository,
            ColumnRepository columnRepository,
            SprintRepository sprintRepository,
            ProjectUsersRepository projectUsersRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.columnRepository = columnRepository;
        this.sprintRepository = sprintRepository;
        this.projectUsersRepository = projectUsersRepository;
    }

    public TaskCreatedDto addTask(NewTaskDto newTaskDto, UserDto userDto, int projectID, String contextPath)
            throws UsersExceptions.UserDoesNotExistException,
            ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException,
            ColumnsExceptions.ColumnDoesNotExistInProject
    {
        // Validate user exists, is in project and project exists
        validateUserValidAndInsideValidProject(userDto, projectID);

        // If column, validate column in project
        if (newTaskDto.getColumnID() != null) {
            doesColumnExistInProject(newTaskDto.getColumnID(), projectID);
        } else {
            // Default to first in-order column if user did not specify a coumn
            newTaskDto.setColumnID(columnRepository.getFirstInOrderColumnFromProject(projectID));
        }

        // If assignedTo, validate user exists in Project
        if (newTaskDto.getAssignedTo() != null &&
            !projectUsersRepository.isUserInProjectAndDoesProjectExist(newTaskDto.getAssignedTo(), projectID)) {
            throw new ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException(
                    ErrorMessageConstants.USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST.getValue());
        }

        // If sprint, validate sprint exists in project
        if (newTaskDto.getSprintID() != null &&
            !sprintRepository.doesSprintExistInProject(newTaskDto.getSprintID(), projectID)) {
            throw new SprintsExceptions.SprintDoesNotExistInProject(
                    ErrorMessageConstants.SPRINT_NOT_IN_PROJECT.getValue());
        }

        // If priority is null, default to 'None'
        if (newTaskDto.getPriority() == null) {
            newTaskDto.setPriority(DatabaseConstants.PRIORITY_NONE.getValue());
        }

        // Add task
        TaskCreatedDto taskCreatedDto = taskRepository.addTask(newTaskDto, projectID);
        taskCreatedDto.setTaskLocation(contextPath, projectID);

        // Update project last updated time
        projectRepository.updateLastUpdatedForProject(projectID);
        return taskCreatedDto;
    }

    public TaskDetailsDto getTaskDetails(UserDto userDto, int projectID, int taskID, String contextPath)
            throws UsersExceptions.UserDoesNotExistException,
                ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException
    {
        // Validate user, and user in valid project
        validateUserValidAndInsideValidProject(userDto, projectID);

        TaskDetailsDto taskDetailsDto = taskRepository.getTaskDetails(taskID);
        taskDetailsDto.setTaskLocation(contextPath, projectID);

        return taskDetailsDto;
    }

    public void moveTask(UserDto userDto, int projectID, int taskID, int columnID)
            throws UsersExceptions.UserDoesNotExistException,
            ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException,
            ColumnsExceptions.ColumnDoesNotExistInProject,
            TasksExceptions.TaskDoesNotExistInProject,
            TasksExceptions.TaskAlreadyInColumn {

        // Validate user exists, is in project and project exists
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Validate new column exists in project
        doesColumnExistInProject(columnID, projectID);

        // Validate task, task in project
        TasksRecord taskToMove = taskRepository.doesTaskExistInProject(taskID, projectID);
        if (taskToMove == null) {
            throw new TasksExceptions.TaskDoesNotExistInProject(ErrorMessageConstants.TASK_NOT_IN_PROJECT.getValue());
        }

        // Validate if task already in the column to move to
        if (taskToMove.getColumnId() == columnID) {
            throw new TasksExceptions.TaskAlreadyInColumn(ErrorMessageConstants.TASK_ALREADY_IN_COLUMN.getValue());
        }

        // Move task over to the other column
        taskRepository.moveTaskToAnotherColumn(taskID, projectID, columnID);

        // Update project last updated time
        projectRepository.updateLastUpdatedForProject(projectID);
    }

    public void deleteTask(UserDto userDto, int projectID, int taskID)
            throws UsersExceptions.UserDoesNotExistException,
            ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException,
            TasksExceptions.TaskDoesNotExistInProject {

        // Validate user exists, is in project and project exists
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Validate task, task in project
        TasksRecord taskToDelete = taskRepository.doesTaskExistInProject(taskID, projectID);
        if (taskToDelete == null) {
            throw new TasksExceptions.TaskDoesNotExistInProject(ErrorMessageConstants.TASK_NOT_IN_PROJECT.getValue());
        }

        // Delete task from project
        taskRepository.deleteTask(taskID);

        // Update project last updated time
        projectRepository.updateLastUpdatedForProject(projectID);
    }

    @Override
    public void validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        // Validate user in project and project exists
        if (!projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID)) {
            throw new ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException(
                    ErrorMessageConstants.USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST.getValue());
        }
    }

    private void doesColumnExistInProject(int columnID, int projectID)
            throws ColumnsExceptions.ColumnDoesNotExistInProject {
        if(!columnRepository.doesColumnExistInProject(columnID, projectID)) {
            throw new ColumnsExceptions.ColumnDoesNotExistInProject(
                    ErrorMessageConstants.COLUMN_NOT_IN_PROJECT.getValue());
        }
    }
}
