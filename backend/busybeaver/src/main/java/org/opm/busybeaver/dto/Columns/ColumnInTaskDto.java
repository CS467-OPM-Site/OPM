package org.opm.busybeaver.dto.Columns;

import org.opm.busybeaver.dto.Interfaces.ColumnInterface;
import org.opm.busybeaver.enums.BusyBeavPaths;

public final class ColumnInTaskDto implements ColumnInterface {
    private final String columnTitle;
    private final int columnID;
    private final int columnIndex;
    private String columnLocation;

    public ColumnInTaskDto(String columnTitle, int columnID, int columnIndex) {
        this.columnTitle = columnTitle;
        this.columnID = columnID;
        this.columnIndex = columnIndex;
    }

    @Override
    public void setColumnLocation(String contextPath, int projectID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.columnLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.COLUMNS.getValue() + "/" + getColumnID();
    }

    @Override
    public String getColumnTitle() {
        return columnTitle;
    }

    @Override
    public int getColumnID() {
        return columnID;
    }

    @Override
    public int getColumnIndex() {
        return columnIndex;
    }

    public String getColumnLocation() {
        return columnLocation;
    }
}
