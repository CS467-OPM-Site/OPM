package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Columns.NewColumnDto;
import org.opm.busybeaver.dto.Columns.NewColumnTitleDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Columns.ColumnsExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.ColumnsRecord;
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

    public NewColumnDto moveColumn(UserDto userDto, int projectID, int columnID, int newColumnIndex, String contextPath)
        throws ColumnsExceptions.ColumnIndexIdentical,
            ColumnsExceptions.ColumnIndexOutOfBounds {
        // Validate user, is user in project
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Validate column exists in project
        ColumnsRecord columnInProject = columnsRepository.doesColumnExistInProject(columnID, projectID);
        int currentIndex = columnInProject.getColumnIndex();

        // Validate new index is different from previous
        if (currentIndex == newColumnIndex) {
            throw new ColumnsExceptions.ColumnIndexIdentical(ErrorMessageConstants.COLUMN_POSITION_THE_SAME.getValue());
        }

        // Validate index is within valid range of columns in project, zero-based indexing
        int columnsInProject = columnsRepository.getNumberOfColumnsInProject(projectID);
        int maxIndex = columnsInProject - 1;

        if (newColumnIndex > maxIndex || newColumnIndex < 0) {
            throw new ColumnsExceptions.ColumnIndexOutOfBounds(
                    ErrorMessageConstants.COLUMN_INDEX_OUT_OF_BOUNDS.getValue());
        }

        // If new index is less than current index, then find all columns from new up to current - 1, and increment
        if (newColumnIndex < currentIndex) {
            // For example going from 3 (currentIndex) to 1 (newIndex), shift 1 and 2 up to 2 and 3
            columnsRepository.incrementColumnIndexes(projectID, newColumnIndex, currentIndex - 1);
        }

        // If new index is greater than current index, find all columns from current + 1 to new and decrement
        if (newColumnIndex > currentIndex) {
            // For example going from 1 (currentIndex) to 3 (newIndex), shift 2 and 3 down to 1 and 2
            columnsRepository.decrementColumnIndexes(projectID, currentIndex + 1, newColumnIndex);
        }

        // Update column index
        NewColumnDto movedColumn = columnsRepository.changeColumnIndexAndReturn(projectID, columnID, newColumnIndex);
        movedColumn.setColumnLocation(contextPath, projectID);
        return movedColumn;
    }

    public NewColumnDto changeColumnTitle(
            UserDto userDto,
            int projectID,
            int columnID,
            NewColumnTitleDto newColumnTitleDto,
            String contextPath) throws ColumnsExceptions.ColumnTitleIdentical {

        // Validate user, is user in project
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Validate column exists in project
        columnsRepository.doesColumnExistInProject(columnID, projectID);

        NewColumnDto changedColumn = columnsRepository.changeColumnTitle(
                projectID,
                columnID,
                newColumnTitleDto.columnTitle()
        );

        changedColumn.setColumnLocation(contextPath, projectID);

        return changedColumn;
    }

    @Override
    public BeaverusersRecord validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID);

        return beaverusersRecord;
    }
}
