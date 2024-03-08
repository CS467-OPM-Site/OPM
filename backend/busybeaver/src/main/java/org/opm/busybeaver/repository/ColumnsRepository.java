package org.opm.busybeaver.repository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.exception.NoDataFoundException;
import org.opm.busybeaver.dto.Columns.NewColumnDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.DefaultColumnNames;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Columns.ColumnsExceptions;
import org.opm.busybeaver.jooq.tables.records.ColumnsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.jooq.impl.DSL.max;
import static org.opm.busybeaver.jooq.Tables.COLUMNS;

@Repository
@Component
@Slf4j
public class ColumnsRepository {
    private final DSLContext create;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public ColumnsRepository(DSLContext dslContext) { this.create = dslContext; }

    public void createDefaultColumns(int projectID) {
        ArrayList<ColumnsRecord> defaultColumnRecords =
                createDefaultColumnRecords(DefaultColumnNames.values(), projectID);

        // Add all default columns for new project
        create.insertInto(COLUMNS).set(defaultColumnRecords).execute();
    }

    public ColumnsRecord doesColumnExistInProject(int columnID, int projectID, HttpServletRequest request)
            throws ColumnsExceptions.ColumnDoesNotExistInProject {
        // SELECT EXISTS(
        //      SELECT *
        //      FROM Columns
        //      WHERE Columns.column_id = columnID
        //      AND Columns.project_id = projectID)
        ColumnsRecord columnInProject = create.selectFrom(COLUMNS)
                        .where(COLUMNS.COLUMN_ID.eq(columnID))
                        .and(COLUMNS.PROJECT_ID.eq(projectID))
                        .fetchOne();

        if (columnInProject == null) {
            ColumnsExceptions.ColumnDoesNotExistInProject columnDoesNotExistInProject =
                    new ColumnsExceptions.ColumnDoesNotExistInProject(
                            ErrorMessageConstants.COLUMN_NOT_IN_PROJECT.getValue());

            log.error("{}. | RID: {}, {}",
                    ErrorMessageConstants.COLUMN_NOT_IN_PROJECT.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    columnDoesNotExistInProject);

            throw columnDoesNotExistInProject;
        }

        return columnInProject;
    }

    public int getNumberOfColumnsInProject(int projectID) {
        return create.fetchCount(
                create.selectFrom(COLUMNS)
                        .where(COLUMNS.PROJECT_ID.eq(projectID)));
    }

    public void decrementColumnIndexes(int projectID, int lowerIndex, int upperIndex) {
        // UPDATE Columns
        // SET column_index = column_index - 1
        // WHERE project_id = projectID
        // AND column_index >= lowerIndex
        // AND column_index <= upperIndex;
        create.update(COLUMNS)
                .set(COLUMNS.COLUMN_INDEX, COLUMNS.COLUMN_INDEX.minus(1))
                .where(COLUMNS.PROJECT_ID.eq(projectID))
                .and(COLUMNS.COLUMN_INDEX.ge((short) lowerIndex))
                .and(COLUMNS.COLUMN_INDEX.le((short) upperIndex))
                .execute();
    }

    public void incrementColumnIndexes(int projectID, int lowerIndex, int upperIndex) {
        // UPDATE Columns
        // SET column_index = column_index + 1
        // WHERE project_id = projectID
        // AND column_index >= lowerIndex
        // AND column_index <= upperIndex;
        create.update(COLUMNS)
                .set(COLUMNS.COLUMN_INDEX, COLUMNS.COLUMN_INDEX.plus(1))
                .where(COLUMNS.PROJECT_ID.eq(projectID))
                .and(COLUMNS.COLUMN_INDEX.ge((short) lowerIndex))
                .and(COLUMNS.COLUMN_INDEX.le((short) upperIndex))
                .execute();
    }

    public NewColumnDto changeColumnIndexAndReturn(int projectID, int columnID, int newColumnIndex) {
        // UPDATE Columns
        // SET column_index = newColumnIndex
        // WHERE project_id = projectID
        // AND column_id = columnID
        return create.update(COLUMNS)
                .set(COLUMNS.COLUMN_INDEX, (short) newColumnIndex)
                .where(COLUMNS.PROJECT_ID.eq(projectID))
                .and(COLUMNS.COLUMN_ID.eq(columnID))
                .returningResult(COLUMNS.COLUMN_TITLE, COLUMNS.COLUMN_INDEX, COLUMNS.COLUMN_ID)
                .fetchSingleInto(NewColumnDto.class);
    }

    public void removeColumnAndShiftOtherColumns(int projectID, int columnID) {
        // Delete the column, and return its index
        int indexOfDeletedColumn = create
                .deleteFrom(COLUMNS)
                .where(COLUMNS.COLUMN_ID.eq(columnID))
                .returningResult(COLUMNS.COLUMN_INDEX)
                .fetchSingle().component1();

        // Then, find all columns above that column's index, and decrement their index
        // UPDATE Columns
        // SET column_index = column_index - 1
        // WHERE project_id = projectID
        // AND column_index > indexOfDeletedColumn;
        create.update(COLUMNS)
                .set(COLUMNS.COLUMN_INDEX, COLUMNS.COLUMN_INDEX.minus(1))
                .where(COLUMNS.PROJECT_ID.eq(projectID))
                .and(COLUMNS.COLUMN_INDEX.greaterThan((short) indexOfDeletedColumn))
                .execute();
    }

    public NewColumnDto changeColumnTitle(int projectID, int columnID, String newColumnTitle, HttpServletRequest request)
        throws ColumnsExceptions.ColumnTitleAlreadyInProject {

        try {
            return create.update(COLUMNS)
                    .set(COLUMNS.COLUMN_TITLE, newColumnTitle)
                    .where(COLUMNS.PROJECT_ID.eq(projectID))
                    .and(COLUMNS.COLUMN_ID.eq(columnID))
                    .and(COLUMNS.COLUMN_TITLE.ne(newColumnTitle))
                    .returningResult(COLUMNS.COLUMN_TITLE, COLUMNS.COLUMN_INDEX, COLUMNS.COLUMN_ID)
                    .fetchSingleInto(NewColumnDto.class);

        } catch (DuplicateKeyException e) {
            ColumnsExceptions.ColumnTitleAlreadyInProject columnTitleAlreadyInProject =
                    new ColumnsExceptions.ColumnTitleAlreadyInProject(
                            ErrorMessageConstants.COLUMN_TITLE_ALREADY_IN_PROJECT.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.COLUMN_TITLE_ALREADY_IN_PROJECT.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    columnTitleAlreadyInProject);

            throw columnTitleAlreadyInProject;

        } catch (NoDataFoundException e) {
            ColumnsExceptions.ColumnTitleIdentical columnTitleIdentical =
                    new ColumnsExceptions.ColumnTitleIdentical(
                            ErrorMessageConstants.COLUMN_TITLE_EQUIVALENT_NOT_MODIFIED.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.COLUMN_TITLE_EQUIVALENT_NOT_MODIFIED.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    columnTitleIdentical);

            throw columnTitleIdentical;
        }

    }

    @Transactional
    public NewColumnDto addNewColumnToProject(@NotNull NewColumnDto newColumnDto, int projectID, HttpServletRequest request) {
        // First, find the highest index of columns associated in project
        // SELECT MAX(Columns.column_index)
        // FROM Columns
        // WHERE Columns.project_id = projectID
        Record1<Short> maxColumnIndexRecord = create.select(max(COLUMNS.COLUMN_INDEX))
                .from(COLUMNS)
                .where(COLUMNS.PROJECT_ID.eq(projectID))
                .fetchOne();

        Short maxColumnIndex = -1;
        if (maxColumnIndexRecord != null) {
            maxColumnIndex = maxColumnIndexRecord.component1();
        }

        // Second, insert new column with next column index, making it last in-order column
        // INSERT INTO Columns (column_title, project_id, column_index)
        // VALUES (...)
        try {
            return create
                    .insertInto(COLUMNS, COLUMNS.COLUMN_TITLE, COLUMNS.PROJECT_ID, COLUMNS.COLUMN_INDEX)
                    .values(newColumnDto.getColumnTitle(), projectID, (short) (maxColumnIndex + 1))
                    .returningResult(COLUMNS.COLUMN_TITLE,COLUMNS.COLUMN_INDEX, COLUMNS.COLUMN_ID)
                    .fetchSingleInto(NewColumnDto.class);
        } catch (DuplicateKeyException e) {
            // Column title already exists in project, constraint does not allow this
            ColumnsExceptions.ColumnTitleAlreadyInProject columnTitleAlreadyInProject =
                    new ColumnsExceptions.ColumnTitleAlreadyInProject(
                            ErrorMessageConstants.COLUMN_TITLE_ALREADY_IN_PROJECT.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.COLUMN_TITLE_ALREADY_IN_PROJECT.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    columnTitleAlreadyInProject);

            throw columnTitleAlreadyInProject;
        }
    }

    public int getFirstInOrderColumnFromProject(int projectID) {
        // SELECT Columns.column_id
        // FROM Columns
        // WHERE Columns.project_id = projectID
        // AND Columns.column_index = 0;
        return create.select(COLUMNS.COLUMN_ID)
                .from(COLUMNS)
                .where(COLUMNS.PROJECT_ID.eq(projectID))
                .and(COLUMNS.COLUMN_INDEX.eq((short) 0))
                .fetchSingle().component1();
    }

    @NotNull
    private static ArrayList<ColumnsRecord> createDefaultColumnRecords(DefaultColumnNames @NotNull [] defaultColumnNames, int projectID) {
        ArrayList<ColumnsRecord> newColumnRecords = new ArrayList<>(defaultColumnNames.length);
        int columnIndex = 0;
        for (DefaultColumnNames defaultColumName : defaultColumnNames) {
            ColumnsRecord newDefaultColumn = new ColumnsRecord();

            newDefaultColumn.setColumnIndex((short) columnIndex);
            columnIndex = columnIndex + 1;

            newDefaultColumn.setColumnTitle(defaultColumName.getValue());
            newDefaultColumn.setProjectId(projectID);
            newColumnRecords.add(newDefaultColumn);
        }
        return newColumnRecords;
    }
}
