package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Columns.NewColumnDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Columns.ColumnsExceptions;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.opm.busybeaver.exceptions.Users.UsersExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.*;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnsService implements ValidateUserAndProjectInterface {
    private final ProjectsRepository projectsRepository;
    private final UsersRepository usersRepository;
    private final ColumnsRepository columnsRepository;
    private final TasksRepository tasksRepository;
    private final ProjectUsersRepository projectUsersRepository;

    @Autowired
    public ColumnsService(
            ProjectsRepository projectsRepository,
            UsersRepository usersRepository,
            ColumnsRepository columnsRepository,
            TasksRepository tasksRepository,
            ProjectUsersRepository projectUsersRepository
    ) {
        this.projectsRepository = projectsRepository;
        this.usersRepository = usersRepository;
        this.columnsRepository = columnsRepository;
        this.tasksRepository = tasksRepository;
        this.projectUsersRepository = projectUsersRepository;
    }

    public NewColumnDto addNewColumn(UserDto userDto, NewColumnDto newColumnDto, int projectID, String contextPath) {
        // Validate user, is user in project
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Create column, add to project after validating for duplicate, move to end
        NewColumnDto newColumn = columnsRepository.addNewColumnToProject(newColumnDto, projectID);
        newColumn.setColumnLocation(contextPath, projectID);

        // Update last updated time for project
        projectsRepository.updateLastUpdatedForProject(projectID);

        return newColumn;
    }

    public void deleteColumn(UserDto userDto, int projectID, int columnID) {
        // Validate user, is user in project
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Validate column exists in project
        columnsRepository.doesColumnExistInProject(columnID, projectID);

        // Validate column no longer contains any tasks
        if (tasksRepository.doesColumnContainTasks(projectID, columnID)) {
            throw new ColumnsExceptions.ColumnStillContainsTasks(ErrorMessageConstants.COLUMN_CONTAINS_TASKS.getValue());
        }

        // Remove column, shift column indexes of other columns
        columnsRepository.removeColumnAndShiftOtherColumns(projectID, columnID);

        // Update last updated time for project
        projectsRepository.updateLastUpdatedForProject(projectID);
    }

    @Override
    public void validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID);
    }
}
