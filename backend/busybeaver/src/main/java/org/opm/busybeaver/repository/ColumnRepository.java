package org.opm.busybeaver.repository;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.opm.busybeaver.dto.Columns.NewColumnDto;
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
import static org.opm.busybeaver.jooq.Tables.TEAMS;

@Repository
@Component
public class ColumnRepository {
    private final DSLContext create;

    @Autowired
    public ColumnRepository(DSLContext dslContext) { this.create = dslContext; }

    public void createDefaultColumns(int projectID) {
        ArrayList<ColumnsRecord> defaultColumnRecords =
                createDefaultColumnRecords(DefaultColumnNames.values(), projectID);

        // Add all default columns for new project
        create.insertInto(COLUMNS).set(defaultColumnRecords).execute();
    }

    public Boolean doesColumnExistInProject(int columnID, int projectID) {
        // SELECT EXISTS(
        //      SELECT *
        //      FROM Columns
        //      WHERE Columns.column_id = columnID
        //      AND Columns.project_id = projectID)
        return create.fetchExists(
                create.selectFrom(COLUMNS)
                        .where(COLUMNS.COLUMN_ID.eq(columnID))
                        .and(COLUMNS.PROJECT_ID.eq(projectID))
        );
    }

    public Boolean doesColumnExistInProject(String columnTitle, int projectID) {
        // SELECT EXISTS(
        //      SELECT *
        //      FROM Columns
        //      WHERE Columns.column_title = columnTitle
        //      AND Columns.project_id = projectID)
        return create.fetchExists(
                create.selectFrom(COLUMNS)
                        .where(COLUMNS.COLUMN_TITLE.eq(columnTitle))
                        .and(COLUMNS.PROJECT_ID.eq(projectID))
        );
    }

    public void removeColumnAndShiftOtherColumns(int projectID, int columnID) {
        // Delete the column, and return its index
        int indexOfDeletedColumn = create
                .deleteFrom(COLUMNS)
                .where(COLUMNS.COLUMN_ID.eq(columnID))
                .returningResult(COLUMNS.COLUMN_INDEX)
                .fetchSingle().component1();

        // Then, find all columns above that column's index, and decrement their index
        create.update(COLUMNS)
                .set(COLUMNS.COLUMN_INDEX, COLUMNS.COLUMN_INDEX.minus(1))
                .where(COLUMNS.PROJECT_ID.eq(projectID))
                .and(COLUMNS.COLUMN_INDEX.greaterThan((short) indexOfDeletedColumn))
                .execute();
    }

    @Transactional
    public NewColumnDto addNewColumnToProject(NewColumnDto newColumnDto, int projectID) {
        // First, find the highest index of columns associated in project
        // SELECT MAX(Columns.column_index)
        // FROM Columns
        // WHERE Columns.project_id = projectID
        int maxColumnIndex = create.select(max(COLUMNS.COLUMN_INDEX))
                .from(COLUMNS)
                .where(COLUMNS.PROJECT_ID.eq(projectID))
                .fetchSingle().component1();

        // Second, insert new column with next column, making it last in-order column
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
            throw new ColumnsExceptions.ColumnTitleAlreadyInProject(ErrorMessageConstants.COLUMN_TITLE_ALREADY_IN_PROJECT.getValue());
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
    private static ArrayList<ColumnsRecord> createDefaultColumnRecords(DefaultColumnNames[] defaultColumnNames, int projectID) {
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
