package org.opm.busybeaver.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.dto.Columns.NewColumnDto;
import org.opm.busybeaver.dto.Columns.NewColumnTitleDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Columns.ColumnsExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.ColumnsRecord;
import org.opm.busybeaver.repository.*;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ColumnsService implements ValidateUserAndProjectInterface {
    private final UsersRepository usersRepository;
    private final ColumnsRepository columnsRepository;
    private final TasksRepository tasksRepository;
    private final ProjectUsersRepository projectUsersRepository;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public ColumnsService(
            UsersRepository usersRepository,
            ColumnsRepository columnsRepository,
            TasksRepository tasksRepository,
            ProjectUsersRepository projectUsersRepository
    ) {
        this.usersRepository = usersRepository;
        this.columnsRepository = columnsRepository;
        this.tasksRepository = tasksRepository;
        this.projectUsersRepository = projectUsersRepository;
    }

    public NewColumnDto addNewColumn(
            UserDto userDto,
            NewColumnDto newColumnDto,
            int projectID,
            HttpServletRequest request) {
        // Validate user, is user in project
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        // Create column, add to project after validating for duplicate, move to end
        NewColumnDto newColumn = columnsRepository.addNewColumnToProject(newColumnDto, projectID, request);
        newColumn.setColumnLocation(request.getContextPath(), projectID);

        return newColumn;
    }

    public void deleteColumn(UserDto userDto, int projectID, int columnID, HttpServletRequest request) {
        // Validate user, is user in project
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        // Validate column exists in project
        columnsRepository.doesColumnExistInProject(columnID, projectID, request);

        // Validate column no longer contains any tasks
        if (tasksRepository.doesColumnContainTasks(projectID, columnID)) {
            ColumnsExceptions.ColumnStillContainsTasks columnStillContainsTasks =
                    new ColumnsExceptions.ColumnStillContainsTasks(
                            ErrorMessageConstants.COLUMN_CONTAINS_TASKS.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.COLUMN_CONTAINS_TASKS.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    columnStillContainsTasks);

            throw columnStillContainsTasks;
        }

        // Remove column, shift column indexes of other columns
        columnsRepository.removeColumnAndShiftOtherColumns(projectID, columnID);
    }

    public NewColumnDto moveColumn(UserDto userDto,
                                   int projectID,
                                   int columnID,
                                   int newColumnIndex,
                                   HttpServletRequest request)
        throws ColumnsExceptions.ColumnIndexIdentical,
            ColumnsExceptions.ColumnIndexOutOfBounds {
        // Validate user, is user in project
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        // Validate column exists in project
        ColumnsRecord columnInProject = columnsRepository.doesColumnExistInProject(columnID, projectID, request);
        int currentIndex = columnInProject.getColumnIndex();

        // Validate new index is different from previous
        if (currentIndex == newColumnIndex) {
            ColumnsExceptions.ColumnIndexIdentical columnIndexIdentical =
                    new ColumnsExceptions.ColumnIndexIdentical(
                            ErrorMessageConstants.COLUMN_POSITION_THE_SAME.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.COLUMN_POSITION_THE_SAME.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    columnIndexIdentical);

            throw columnIndexIdentical;
        }

        // Validate index is within valid range of columns in project, zero-based indexing
        int columnsInProject = columnsRepository.getNumberOfColumnsInProject(projectID);
        int maxIndex = columnsInProject - 1;

        if (newColumnIndex > maxIndex || newColumnIndex < 0) {
            ColumnsExceptions.ColumnIndexOutOfBounds columnIndexOutOfBounds =
                    new ColumnsExceptions.ColumnIndexOutOfBounds(
                            ErrorMessageConstants.COLUMN_INDEX_OUT_OF_BOUNDS.getValue());
            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.COLUMN_INDEX_OUT_OF_BOUNDS.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    columnIndexOutOfBounds);

            throw columnIndexOutOfBounds;
        }

        // If new index is less than current index, then find all columns from new up to current - 1, and increment
        if (newColumnIndex < currentIndex) {
            // For example going from 3 (currentIndex) to 1 (newIndex), shift 1 and 2 up to 2 and 3
            columnsRepository.incrementColumnIndexes(projectID, newColumnIndex, currentIndex - 1);
            log.info("Moved columns beneath this column, forward. | RID: {}", request.getAttribute(RID));
        }

        // If new index is greater than current index, find all columns from current + 1 to new and decrement
        if (newColumnIndex > currentIndex) {
            // For example going from 1 (currentIndex) to 3 (newIndex), shift 2 and 3 down to 1 and 2
            columnsRepository.decrementColumnIndexes(projectID, currentIndex + 1, newColumnIndex);
            log.info("Moved columns above this column, backward. | RID: {}", request.getAttribute(RID));
        }

        // Update column index
        NewColumnDto movedColumn = columnsRepository.changeColumnIndexAndReturn(projectID, columnID, newColumnIndex);
        movedColumn.setColumnLocation(request.getContextPath(), projectID);
        return movedColumn;
    }

    public NewColumnDto changeColumnTitle(
            UserDto userDto,
            int projectID,
            int columnID,
            @NotNull NewColumnTitleDto newColumnTitleDto,
            HttpServletRequest request) throws ColumnsExceptions.ColumnTitleIdentical {

        // Validate user, is user in project
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        // Validate column exists in project
        columnsRepository.doesColumnExistInProject(columnID, projectID, request);

        NewColumnDto changedColumn = columnsRepository.changeColumnTitle(
                projectID,
                columnID,
                newColumnTitleDto.columnTitle(),
                request
        );

        changedColumn.setColumnLocation(request.getContextPath(), projectID);

        return changedColumn;
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
}
