package org.opm.busybeaver.repository;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.opm.busybeaver.enums.DefaultColumnNames;
import org.opm.busybeaver.jooq.tables.records.ColumnsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

import static org.opm.busybeaver.jooq.Tables.COLUMNS;

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
