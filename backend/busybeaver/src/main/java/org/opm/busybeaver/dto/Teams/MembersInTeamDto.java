package org.opm.busybeaver.dto.Teams;

import org.opm.busybeaver.enums.BusyBeavPaths;

import java.util.List;

public class MembersInTeamDto {
    private final String teamName;
    private final Integer teamId;
    private final List<MemberInTeamDto> members;

    private String teamLocation;

    public MembersInTeamDto(String teamName, Integer teamId, List<MemberInTeamDto> members) {
        this.teamName = teamName;
        this.teamId = teamId;
        this.members = members;
    }

    public void setTeamLocation(String contextPath) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();
        this.teamLocation = PATH + BusyBeavPaths.TEAMS.getValue() + "/" + getTeamId();
    }


    public Integer getTeamId() {
        return teamId;
    }
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
