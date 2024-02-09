package org.opm.busybeaver.dto.Projects;

import org.opm.busybeaver.enums.BusyBeavPaths;

public record TeamSummaryInProjectSummaryDto(String teamName, Integer teamID) {
    private static String teamLocation;

    public void setTeamLocation(String path) {
        teamLocation = path + BusyBeavPaths.TEAMS.getValue() + "/" + teamID;
    }

    public String getTeamLocation() {
        return teamLocation;
    }
}