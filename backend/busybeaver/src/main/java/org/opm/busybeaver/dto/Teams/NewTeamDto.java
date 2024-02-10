package org.opm.busybeaver.dto.Teams;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.opm.busybeaver.enums.BusyBeavPaths;

public final class NewTeamDto {
    @NotBlank(message = "Missing 'teamName' attribute to generate a new team")
    @Size(min = 3, max = 50, message = "Team name must be 3 to 50 characters")
    private String teamName;
    private int teamID;
    private int teamCreator;
    private String teamLocation;

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setTeamCreator(int teamCreator) {
        this.teamCreator = teamCreator;
    }

    public void setTeamLocation(String contextPath) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.teamLocation = PATH +
                BusyBeavPaths.TEAMS.getValue() +
                "/" + getTeamID();
    }

    public String getTeamName() {
        return teamName;
    }

    public int getTeamID() {
        return teamID;
    }

    public int getTeamCreator() {
        return teamCreator;
    }

    public String getTeamLocation() {
        return teamLocation;
    }

}
