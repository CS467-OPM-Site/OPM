package org.opm.busybeaver.dto.Interfaces;

import org.opm.busybeaver.dto.Projects.TeamSummaryInProjectSummaryDto;

import java.time.LocalDateTime;

public interface ProjectAndTeamInterface extends ProjectBasicInterface {
    public TeamSummaryInProjectSummaryDto getTeam();

    public LocalDateTime getLastUpdated();
}
