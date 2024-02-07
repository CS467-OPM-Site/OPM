package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.opm.busybeaver.dto.HomePageTeamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.select;
import static org.opm.busybeaver.jooq.Tables.*;

@Repository
@Component
public class TeamRepository {
    private final DSLContext create;

    @Autowired
    public TeamRepository(DSLContext dslContext) { this.create = dslContext; }

    public List<HomePageTeamDto> getUserHomePageTeams(Integer userId) {
        // TO model:
        // SELECT Teams.team_id, Teams.team_name, Teams.team_creator
        // FROM Teams
        // WHERE Teams.team_id IN (
        //      SELECT TeamUsers.team_id
        //      FROM TeamUsers
        //      WHERE TeamUsers.user_id = 1
        //      );
        return create
                .select(TEAMS.TEAM_ID, TEAMS.TEAM_NAME, TEAMS.TEAM_CREATOR)
                .from(TEAMS)
                .where(TEAMS.TEAM_ID.in(
                                select(TEAMUSERS.TEAM_ID)
                                        .from(TEAMUSERS)
                                        .where(TEAMUSERS.USER_ID.eq(userId))
                        )
                )
                .fetchInto(HomePageTeamDto.class);

    }
}
