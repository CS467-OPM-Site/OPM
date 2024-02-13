package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Columns.NewColumnDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Columns.ColumnsExceptions;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.ColumnRepository;
import org.opm.busybeaver.repository.ProjectRepository;
import org.opm.busybeaver.repository.UserRepository;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnsService implements ValidateUserAndProjectInterface {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ColumnRepository columnRepository;

    @Autowired
    public ColumnsService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            ColumnRepository columnRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.columnRepository = columnRepository;
    }

    public NewColumnDto addNewColumn(UserDto userDto, NewColumnDto newColumnDto, int projectID, String contextPath) {
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
