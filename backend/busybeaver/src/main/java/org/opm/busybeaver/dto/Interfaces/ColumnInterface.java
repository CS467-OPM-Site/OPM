package org.opm.busybeaver.dto.Interfaces;

public interface ColumnInterface {
    String getColumnTitle();
    int getColumnIndex();
    int getColumnID();
    String getColumnLocation();
    void setColumnLocation(String contextPath, int projectID);
}
