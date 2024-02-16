package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Tasks.NewTaskDtoExtended;
import org.opm.busybeaver.dto.Tasks.TaskCreatedDto;
import org.opm.busybeaver.dto.Tasks.TaskDetailsDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.DatabaseConstants;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.*;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            projectUsersRepository.isUserInProjectAndDoesProjectExist(newTaskDto.getAssignedTo(), projectID);
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

    @Override
    public BeaverusersRecord validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID);

        return beaverusersRecord;
    }
}
