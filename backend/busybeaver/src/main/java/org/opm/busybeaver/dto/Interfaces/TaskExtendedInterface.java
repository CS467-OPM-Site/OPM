package org.opm.busybeaver.dto.Interfaces;

import java.time.LocalDate;

public interface TaskExtendedInterface extends TaskBasicInterface {
    String getTitle();
    String getDescription();
    LocalDate getDueDate();
    String getPriority();
    Integer getSprintID();
    Integer getAssignedTo();
    Integer getColumnID();
}
