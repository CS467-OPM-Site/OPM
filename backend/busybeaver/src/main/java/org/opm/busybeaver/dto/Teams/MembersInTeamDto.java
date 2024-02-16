package org.opm.busybeaver.dto.Teams;

import org.opm.busybeaver.dto.Interfaces.TeamInterface;
import org.opm.busybeaver.enums.BusyBeavPaths;

import java.util.List;

public final class MembersInTeamDto implements TeamInterface {
    private final String teamName;
    private final int teamId;
    private final List<MemberInTeamDto> members;
    private String teamLocation;

    public MembersInTeamDto(String teamName, int teamId, List<MemberInTeamDto> members) {
        this.teamName = teamName;
        this.teamId = teamId;
        this.members = members;
    }

    @Override
    public void setLocations(String contextPath) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();
        this.teamLocation = PATH + BusyBeavPaths.TEAMS.getValue() + "/" + getTeamID();
    }

    @Override
    public int getTeamID() {
        return teamId;
    }

    @Override
    public String getTeamName() {
        return teamName;
    }

    public List<MemberInTeamDto> getMembers() {
        return members;
    }

    public String getTeamLocation() {
        return teamLocation;
    }
}
