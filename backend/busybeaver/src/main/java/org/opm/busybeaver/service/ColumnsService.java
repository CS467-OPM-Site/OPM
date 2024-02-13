package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Columns.NewColumnDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Columns.ColumnsExceptions;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.opm.busybeaver.exceptions.Users.UsersExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.ColumnRepository;
import org.opm.busybeaver.repository.ProjectRepository;
import org.opm.busybeaver.repository.TaskRepository;
import org.opm.busybeaver.repository.UserRepository;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnsService implements ValidateUserAndProjectInterface {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ColumnRepository columnRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public ColumnsService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            ColumnRepository columnRepository,
            TaskRepository taskRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.columnRepository = columnRepository;
        this.taskRepository = taskRepository;
    }

    public NewColumnDto addNewColumn(UserDto userDto, NewColumnDto newColumnDto, int projectID, String contextPath)
        throws UsersExceptions.UserDoesNotExistException,
            ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException,
            ColumnsExceptions.ColumnDoesNotExistInProject
            {
        // Validate user, is user in project
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Validate column name not already in project
        if (columnRepository.doesColumnExistInProject(newColumnDto.getColumnTitle(), projectID)) {
            throw new ColumnsExceptions.ColumnTitleAlreadyInProject(
                    ErrorMessageConstants.COLUMN_TITLE_ALREADY_IN_PROJECT.getValue());
        }

        // Create column, add to project, move to end
        NewColumnDto newColumn = columnRepository.addNewColumnToProject(newColumnDto, projectID);
        newColumn.setColumnLocation(contextPath, projectID);

        // Update last updated time for project
        projectRepository.updateLastUpdatedForProject(projectID);

        return newColumn;
    }

    public void deleteColumn(UserDto userDto, int projectID, int columnID) {
        // Validate user, is user in project
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Validate column exists in project
        if (!columnRepository.doesColumnExistInProject(columnID, projectID)) {
            throw new ColumnsExceptions.ColumnDoesNotExistInProject(ErrorMessageConstants.COLUMN_NOT_IN_PROJECT.getValue());
        }

        // Validate column no longer contains any tasks
        if (taskRepository.doesTaskExistInColumnInProject(projectID, columnID)) {
            throw new ColumnsExceptions.ColumnStillContainsTasks(ErrorMessageConstants.COLUMN_CONTAINS_TASKS.getValue());
        }

        // Remove column, shift column indexes of other columns
        columnRepository.removeColumnAndShiftOtherColumns(projectID, columnID);

        // Update last updated time for project
        projectRepository.updateLastUpdatedForProject(projectID);
    }

    @Override
    public void validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        // Validate user in project and project exists
        if (!projectRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID)) {
            throw new ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException(
                    ErrorMessageConstants.USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST.getValue());
        }
    }
}