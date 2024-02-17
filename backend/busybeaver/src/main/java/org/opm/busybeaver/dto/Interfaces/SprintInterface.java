package org.opm.busybeaver.dto.Interfaces;

import java.time.LocalDate;

public interface SprintInterface {
    String getSprintName();
    LocalDate getStartDate();
    LocalDate getEndDate();
}
