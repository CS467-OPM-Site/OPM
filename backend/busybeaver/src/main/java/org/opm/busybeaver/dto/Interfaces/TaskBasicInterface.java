package org.opm.busybeaver.dto.Interfaces;

import java.time.LocalDate;

public interface TaskBasicInterface {
    String getTitle();
    LocalDate getDueDate();
    String getPriority();
}
