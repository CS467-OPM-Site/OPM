package org.opm.busybeaver.repository;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.opm.busybeaver.dto.Projects.ProjectSummaryDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Projects.ProjectAlreadyExistsForTeamException;
import org.opm.busybeaver.jooq.tables.records.ProjectsRecord;
import org.opm.busybeaver.jooq.tables.records.ProjectusersRecord;
import org.opm.busybeaver.jooq.tables.records.TeamusersRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.opm.busybeaver.jooq.Tables.*;

@Repository
@Component
public class ProjectRepository {

    private final DSLContext create;

    @Autowired
    public ProjectRepository(DSLContext dslContext) { this.create = dslContext; }

    @Transactional
    public ProjectsRecord makeNewProject(String projectName, Integer teamID, Integer userID) {
        try {
            ProjectsRecord newProject = create.insertInto(PROJECTS, PROJECTS.PROJECT_NAME, PROJECTS.TEAM_ID)
                    .values(projectName, teamID)
                    .returningResult(PROJECTS.PROJECT_ID, PROJECTS.PROJECT_NAME)
                    .fetchSingleInto(ProjectsRecord.class);

            // Trigger option - Add to ProjectUsers the team who just made the object
            // First, get all team members
            List<TeamusersRecord> teamusersRecordList = create.selectFrom(TEAMUSERS)
                    .where(TEAMUSERS.TEAM_ID.eq(teamID))
                    .fetchInto(TeamusersRecord.class);

            ArrayList<ProjectusersRecord> newProjectUsers = createProjectusersRecords(teamusersRecordList, newProject);

            create.insertInto(PROJECTUSERS).set(newProjectUsers).execute();

            return newProject;

        } catch (DuplicateKeyException e) {
            throw new ProjectAlreadyExistsForTeamException(
                    ErrorMessageConstants.PROJECT_ALREADY_EXISTS_FOR_TEAM.getValue());
        }
    }

    @NotNull
    private static ArrayList<ProjectusersRecord> createProjectusersRecords(List<TeamusersRecord> teamusersRecordList, ProjectsRecord newProject) {
        ArrayList<ProjectusersRecord> newProjectUsers = new ArrayList<>(teamusersRecordList.size());
        // Second, make new project users for each project, and insert
        for (TeamusersRecord teamusersRecord : teamusersRecordList) {
            ProjectusersRecord newProjectUser = new ProjectusersRecord();
            newProjectUser.setProjectId(newProject.getProjectId());
            newProjectUser.setUserId(teamusersRecord.getUserId());
            newProjectUsers.add(newProjectUser);
        }
        return newProjectUsers;
    }

    public List<ProjectSummaryDto> getUserHomePageProjects(Integer userId) {
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
                        create.select(PROJECTUSERS.PROJECT_ID)
                            .from(PROJECTUSERS)
                            .where(PROJECTUSERS.USER_ID.eq(userId))
                        )
                )
                .orderBy(PROJECTS.LAST_UPDATED.desc())
                .fetchInto(ProjectSummaryDto.class);
    }
}
