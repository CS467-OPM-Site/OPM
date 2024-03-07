package org.opm.busybeaver.dto.Interfaces;

import org.opm.busybeaver.dto.Projects.TeamSummaryInProjectSummaryDto;

import java.time.OffsetDateTime;

public interface ProjectAndTeamInterface extends ProjectBasicInterface {
    TeamSummaryInProjectSummaryDto getTeam();

    OffsetDateTime getLastUpdated();
}
