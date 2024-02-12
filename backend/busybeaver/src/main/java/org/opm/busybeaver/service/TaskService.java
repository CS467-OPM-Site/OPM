package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Tasks.NewTaskDto;
import org.opm.busybeaver.dto.Tasks.TaskCreatedDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.DatabaseConstants;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Columns.ColumnsExceptions;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.opm.busybeaver.exceptions.Sprints.SprintsExceptions;
import org.opm.busybeaver.exceptions.Users.UsersExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ColumnRepository columnRepository;
    private final SprintRepository sprintRepository;

    @Autowired
    public TaskService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            TeamRepository teamRepository,
            TaskRepository taskRepository,
            ColumnRepository columnRepository,
            SprintRepository sprintRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.columnRepository = columnRepository;
        this.sprintRepository = sprintRepository;
    }

    public TaskCreatedDto addTask(NewTaskDto newTaskDto, UserDto userDto, int projectID, String contextPath)
            throws UsersExceptions.UserDoesNotExistException,
            ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException,
            ColumnsExceptions.ColumnDoesNotExistInProject
    {
        // Validate user
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        // Validate user in project and project exists
        if (!projectRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID)) {
            throw new ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException(
                    ErrorMessageConstants.USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST.getValue());
        }

        // If column, validate column in project
        if (newTaskDto.getColumnID() != null) {
            if(!columnRepository.doesColumnExistInProject(newTaskDto.getColumnID(), projectID)) {
                throw new ColumnsExceptions.ColumnDoesNotExistInProject(
                        ErrorMessageConstants.COLUMN_NOT_IN_PROJECT.getValue());
            }
        } else {
            // Default to first in-order column if user did not specify a coumn
            newTaskDto.setColumnID(
                    columnRepository.getFirstInOrderColumnFromProject(projectID)
            );
        }

        // If assignedTo, validate user exists in Project
        if (newTaskDto.getAssignedTo() != null &&
            !projectRepository.isUserInProjectAndDoesProjectExist(newTaskDto.getAssignedTo(), projectID)) {
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
        return taskCreatedDto;
    }
}
