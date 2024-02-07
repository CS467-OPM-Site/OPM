package org.opm.busybeaver.dto;

import org.opm.busybeaver.enums.BusyBeavPaths;

public record HomePageProjectTeamDto(String teamName, Integer teamID) {
    private static String teamLocation;

    public void setTeamLocation(String path) {
        teamLocation = path + BusyBeavPaths.TEAMS.getValue() + "/" + teamID;
    }

    public String getTeamLocation() {
        return teamLocation;
    }
}