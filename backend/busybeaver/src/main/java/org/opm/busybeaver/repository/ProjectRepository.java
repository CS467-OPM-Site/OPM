package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.opm.busybeaver.dto.HomePageProjectDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.select;
import static org.opm.busybeaver.jooq.Tables.*;

@Repository
@Component
public class ProjectRepository {

    private final DSLContext create;

    @Autowired
    public ProjectRepository(DSLContext dslContext) { this.create = dslContext; }

    public List<HomePageProjectDto> getUserHomePageProjects(Integer userId) {
        // TO model:
        // SELECT Projects.project_name, Projects.project_id, Projects.team_id, Teams.team_name
        // FROM Projects
        // JOIN Teams
        // ON Teams.team_id = Projects.team_id
        // WHERE Projects.project_id
        // IN
        //      (SELECT ProjectUsers.project_id
        //          FROM ProjectUsers
        //          WHERE ProjectUsers.user_id=userId
        //          )
        // ORDER BY Projects.last_updated DESC;
        return create
                .select(PROJECTS.PROJECT_NAME, PROJECTS.PROJECT_ID, PROJECTS.LAST_UPDATED, PROJECTS.TEAM_ID, TEAMS.TEAM_NAME)
                .from(PROJECTS)
                .join(TEAMS)
                .on(TEAMS.TEAM_ID.eq(PROJECTS.TEAM_ID))
                .where(PROJECTS.PROJECT_ID.in(
                        select(PROJECTUSERS.PROJECT_ID)
                            .from(PROJECTUSERS)
                            .where(PROJECTUSERS.USER_ID.eq(userId))
                        )
                )
                .orderBy(PROJECTS.LAST_UPDATED.desc())
                .fetchInto(HomePageProjectDto.class);

    }
}
