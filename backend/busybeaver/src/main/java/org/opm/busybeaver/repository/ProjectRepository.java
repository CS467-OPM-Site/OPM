package org.opm.busybeaver.repository;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.*;
import org.opm.busybeaver.dto.Columns.ColumnDto;
import org.opm.busybeaver.dto.Projects.ProjectDetailsDto;
import org.opm.busybeaver.dto.Projects.ProjectSummaryDto;
import org.opm.busybeaver.dto.Projects.ProjectsSummariesDto;
import org.opm.busybeaver.dto.Tasks.TaskSummaryDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Projects.ProjectAlreadyExistsForTeamException;
import org.opm.busybeaver.jooq.tables.records.ProjectsRecord;
import org.opm.busybeaver.jooq.tables.records.ProjectusersRecord;
import org.opm.busybeaver.jooq.tables.records.TasksRecord;
import org.opm.busybeaver.jooq.tables.records.TeamusersRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.table;
import static org.opm.busybeaver.jooq.Tables.*;

@Repository
@Component
public class ProjectRepository {

    private final DSLContext create;

    private final ColumnRepository columnRepository;
    @Autowired
    public ProjectRepository(DSLContext dslContext, ColumnRepository columnRepository) {
        this.create = dslContext;
        this.columnRepository = columnRepository;
    }

    public Boolean doesTeamHaveProjectsAssociatedWithIt(int teamID) {
        // SELECT COUNT(*)
        // FROM Projects
        // WHERE Projects.team_id = teamID
        // LIMIT 1;
        int projectCount = create
                .selectCount()
                .from(PROJECTS)
                .where(PROJECTS.TEAM_ID.eq(teamID))
                .fetchSingleInto(int.class);

        return (projectCount >= 1);
    }

    @Transactional
    public ProjectsRecord makeNewProject(String projectName, int teamID, int userID) {
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

            // Add all team members to project
            create.insertInto(PROJECTUSERS).set(newProjectUsers).execute();

            // Second, make default columns for a new project
            columnRepository.createDefaultColumns(newProject.getProjectId());

            return newProject;

        } catch (DuplicateKeyException e) {
            throw new ProjectAlreadyExistsForTeamException(
                    ErrorMessageConstants.PROJECT_ALREADY_EXISTS_FOR_TEAM.getValue());
        }
    }

    @NotNull
    private static ArrayList<ProjectusersRecord> createProjectusersRecords(List<TeamusersRecord> teamusersRecordList, ProjectsRecord newProject) {
        ArrayList<ProjectusersRecord> newProjectUsers = new ArrayList<>(teamusersRecordList.size());

        for (TeamusersRecord teamusersRecord : teamusersRecordList) {
            ProjectusersRecord newProjectUser = new ProjectusersRecord();
            newProjectUser.setProjectId(newProject.getProjectId());
            newProjectUser.setUserId(teamusersRecord.getUserId());
            newProjectUsers.add(newProjectUser);
        }
        return newProjectUsers;
    }

    public List<ProjectSummaryDto> getUserProjectsSummary(int userId) {
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

    public Boolean isUserInProjectAndDoesProjectExist(int userID, int projectID) {
        // SELECT EXISTS(
        //      SELECT ProjectUsers.project_id,ProjectUsers.user_id
        //      FROM ProjectUsers
        //      WHERE ProjectUsers.project_id = projectID
        //      AND ProjectUsers.user_id = userID
        return create.fetchExists(
                create.selectFrom(PROJECTUSERS)
                        .where(PROJECTUSERS.PROJECT_ID.eq(projectID))
                        .and(PROJECTUSERS.USER_ID.eq(userID))
        );
    }

    public ProjectDetailsDto getSpecificProjectDetails(int projectID) {
        // First get all tasks
        // SELECT Tasks.title, Tasks.task_id, Tasks.priority, Tasks.due_date, Tasks.task_index,
        //          COUNT(Comments.task_id) as comments,
        //          Tasks.assigned_to, Sprints.sprint_name, Sprints.end_date, Tasks.sprint_id,
        //          (SELECT BeaverUsers.username FROM BeaverUsers WHERE BeaverUsers.user_id = Tasks.assigned_to),
        //          Tasks.column_id, Columns.column_index
        // FROM Tasks
        // LEFT JOIN Comments
        // ON Tasks.task_id = Comments.task_id
        // LEFT JOIN Sprints
        // ON Tasks.sprint_id = Sprints.sprint_id
        // JOIN Columns
        // ON Tasks.column_id = Columns.column_id
        // WHERE Tasks.project_id = projectID
        // GROUP BY Tasks.task_id, Sprints.sprint_name, Sprints.end_date, Columns.column_index;
        @Nullable List<TaskSummaryDto> tasksInProject =
                create.select(TASKS.TITLE, TASKS.TASK_ID, TASKS.PRIORITY, TASKS.DUE_DATE, TASKS.TASK_INDEX,
                                count(COMMENTS.TASK_ID).as(COMMENTS.getName()),
                        SPRINTS.SPRINT_NAME, SPRINTS.END_DATE, TASKS.SPRINT_ID, TASKS.ASSIGNED_TO,
                            create.select(BEAVERUSERS.USERNAME)
                                .from(BEAVERUSERS)
                                .where(BEAVERUSERS.USER_ID.eq(TASKS.ASSIGNED_TO))
                                .asField(BEAVERUSERS.USERNAME.getName()), COLUMNS.COLUMN_INDEX)
                    .from(TASKS)
                    .leftJoin(COMMENTS)
                    .on(TASKS.TASK_ID.eq(COMMENTS.TASK_ID))
                    .leftJoin(SPRINTS)
                    .on(TASKS.SPRINT_ID.eq(SPRINTS.SPRINT_ID))
                    .join(COLUMNS)
                    .on(TASKS.COLUMN_ID.eq(COLUMNS.COLUMN_ID))
                    .where(TASKS.PROJECT_ID.eq(projectID))
                    .groupBy(TASKS.TASK_ID, SPRINTS.SPRINT_NAME, SPRINTS.END_DATE, COLUMNS.COLUMN_INDEX)
                    .fetchInto(TaskSummaryDto.class);

        // Second get all columns
        // SELECT Columns.column_title, Columns.column_id, Columns.column_index
        // FROM Columns
        // WHERE Columns.project_id = projectID
        // ORDER BY Columns.column_index;
        List<ColumnDto> columnsInProject = create
                .select(COLUMNS.COLUMN_TITLE, COLUMNS.COLUMN_ID, COLUMNS.COLUMN_INDEX)
                .from(COLUMNS)
                .where(COLUMNS.PROJECT_ID.eq(projectID))
                .orderBy(COLUMNS.COLUMN_INDEX)
                .fetchInto(ColumnDto.class);

        // Combine tasks into columns
        setColumnTasks(columnsInProject, tasksInProject);

        // Third get project + team
        // SELECT Projects.project_name, Projects.project_id, Teams.team_name, Projects.team_id
        // FROM Projects
        // JOIN Teams
        // ON Projects.team_id = Teams.team_id
        // WHERE Projects.project_id = projectID;
        ProjectDetailsDto projectAndTeam = create
                .select(PROJECTS.PROJECT_NAME, PROJECTS.PROJECT_ID, TEAMS.TEAM_NAME, PROJECTS.TEAM_ID)
                .from(PROJECTS)
                .join(TEAMS)
                .on(PROJECTS.TEAM_ID.eq(TEAMS.TEAM_ID))
                .where(PROJECTS.PROJECT_ID.eq(projectID))
                .fetchSingleInto(ProjectDetailsDto.class);

        // Combine into one DTO and respond
        projectAndTeam.setColumns(columnsInProject);

        return projectAndTeam;
    }

    private void setColumnTasks(List<ColumnDto> columns, @Nullable List<TaskSummaryDto> tasks) {
        if (tasks == null) return;

        final int MAX_NUM_COLUMNS = columns.size();
        tasks.forEach(task -> {
            int columnIndex = task.getColumnIndex();
            if (columnIndex < MAX_NUM_COLUMNS && columnIndex >= 0) columns.get(task.getColumnIndex()).addTask(task);
        });
    }
}
