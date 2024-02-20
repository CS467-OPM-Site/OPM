package org.opm.busybeaver.repository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.opm.busybeaver.dto.Sprints.NewSprintDto;
import org.opm.busybeaver.dto.Sprints.SprintSummaryDto;
import org.opm.busybeaver.dto.Sprints.SprintsInProjectDto;
import org.opm.busybeaver.dto.Sprints.TasksInSprintDto;
import org.opm.busybeaver.dto.Tasks.TaskBasicInSprintDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Sprints.SprintsExceptions;
import org.opm.busybeaver.jooq.tables.records.SprintsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.count;
import static org.opm.busybeaver.jooq.Tables.*;

@Repository
@Component
@Slf4j
public class SprintsRepository {

    private final DSLContext create;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public SprintsRepository(DSLContext dslContext) { this.create = dslContext; }

    public void isSprintNameInProject(int projectID, String sprintName, HttpServletRequest request)
        throws SprintsExceptions.SprintNameAlreadyInProject {

        // SELECT EXISTS (
        //      SELECT * FROM Sprints
        //      WHERE Sprints.project_id = projectID
        //      AND Sprints.sprint_name = sprintName);
        boolean isSprintNameInProject = create.fetchExists(
                create.selectFrom(SPRINTS)
                        .where(SPRINTS.PROJECT_ID.eq(projectID))
                        .and(SPRINTS.SPRINT_NAME.eq(sprintName))
        );

        if (isSprintNameInProject) {
            SprintsExceptions.SprintNameAlreadyInProject sprintNameAlreadyInProject =
                    new SprintsExceptions.SprintNameAlreadyInProject(
                            ErrorMessageConstants.PROJECT_CONTAINS_SPRINT_NAME.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.PROJECT_CONTAINS_SPRINT_NAME,
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    sprintNameAlreadyInProject);

            throw sprintNameAlreadyInProject;
        }
    }

    public SprintSummaryDto addSprintToProject(int projectID, NewSprintDto newSprintDto) {
        // INSERT INTO Sprints (project_id, sprint_name, begin_date, end_date)
        // VALUES ...
         return create.insertInto(SPRINTS, SPRINTS.PROJECT_ID, SPRINTS.SPRINT_NAME, SPRINTS.BEGIN_DATE, SPRINTS.END_DATE)
                 .values(projectID, newSprintDto.sprintName(), newSprintDto.startDate(), newSprintDto.endDate())
                 .returningResult(SPRINTS.SPRINT_ID, SPRINTS.SPRINT_NAME, SPRINTS.BEGIN_DATE, SPRINTS.END_DATE)
                 .fetchSingleInto(SprintSummaryDto.class);
    }

    public void removeSprintFromProject(int sprintID, int projectID) {
        // DELETE FROM Sprints
        // WHERE Sprints.project_id = projectID
        // AND Sprints.sprint_id = sprintID;
        create.deleteFrom(SPRINTS)
                .where(SPRINTS.PROJECT_ID.eq(projectID))
                .and(SPRINTS.SPRINT_ID.eq(sprintID))
                .execute();
    }

    public SprintsRecord doesSprintExistInProject(int sprintID, int projectID, HttpServletRequest request)
            throws SprintsExceptions.SprintDoesNotExistInProject {
        //  SELECT *
        //  FROM Sprints
        //  WHERE Sprint.sprint_id = sprintID
        //  AND Sprint.project_id = projectID;
        SprintsRecord sprintInProject =
                create.selectFrom(SPRINTS)
                        .where(SPRINTS.SPRINT_ID.eq(sprintID))
                        .and(SPRINTS.PROJECT_ID.eq(projectID))
                        .fetchOneInto(SprintsRecord.class);

        if (sprintInProject == null) {
            SprintsExceptions.SprintDoesNotExistInProject sprintDoesNotExistInProject =
                    new SprintsExceptions.SprintDoesNotExistInProject(
                            ErrorMessageConstants.SPRINT_NOT_IN_PROJECT.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.SPRINT_NOT_IN_PROJECT,
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    sprintDoesNotExistInProject);

            throw sprintDoesNotExistInProject;
        }
        return sprintInProject;
    }

    public SprintsInProjectDto getAllSprintsForProject(int projectID) {
        // SELECT Projects.project_name, Projects.project_id
        // FROM Projects
        // WHERE Projects.project_id = projectID;
        SprintsInProjectDto sprintsInProjectDto = create
                .select(PROJECTS.PROJECT_NAME, PROJECTS.PROJECT_ID)
                .from(PROJECTS)
                .where(PROJECTS.PROJECT_ID.eq(projectID))
                .fetchSingleInto(SprintsInProjectDto.class);

        // SELECT Sprints.sprint_id, Sprints.sprint_name, Sprints.begin_date, Sprints.end_date
        // FROM Sprints
        // WHERE Sprints.project_id = projectID
        // ORDER BY Sprints.end_date ASC;
        List<SprintSummaryDto> sprints = create
                .select(SPRINTS.SPRINT_ID, SPRINTS.SPRINT_NAME, SPRINTS.BEGIN_DATE, SPRINTS.END_DATE)
                .from(SPRINTS)
                .where(SPRINTS.PROJECT_ID.eq(projectID))
                .orderBy(SPRINTS.END_DATE.asc())
                .fetchInto(SprintSummaryDto.class);

        sprintsInProjectDto.setSprints(sprints);
        return sprintsInProjectDto;
    }

    public TasksInSprintDto getAllTasksInSprint(int projectID, int sprintID) {
        // First get all tasks in sprint
        // SELECT Tasks.title, Tasks.task_id, Tasks.priority, Tasks.due_date, Tasks.description,
        //          COUNT(Comments.task_id) as comments,
        //          Tasks.assigned_to,
        //          (SELECT BeaverUsers.username FROM BeaverUsers WHERE BeaverUsers.user_id = Tasks.assigned_to),
        //          Tasks.column_id, Columns.column_index, Columns.column_name, Columns.column_id
        // FROM Tasks
        // LEFT JOIN Comments
        // ON Tasks.task_id = Comments.task_id
        // JOIN Columns
        // ON Tasks.column_id = Columns.column_id
        // WHERE Tasks.project_id = projectID
        // GROUP BY Tasks.task_id, Columns.column_index, Columns.column_title, Columns.column_id;
        @Nullable List<TaskBasicInSprintDto> tasksInSprintInProject =
                create.select(TASKS.TITLE, TASKS.TASK_ID, TASKS.PRIORITY, TASKS.DUE_DATE, TASKS.DESCRIPTION,
                                count(COMMENTS.TASK_ID).as(COMMENTS.getName()),
                                TASKS.ASSIGNED_TO,
                                create.select(BEAVERUSERS.USERNAME)
                                        .from(BEAVERUSERS)
                                        .where(BEAVERUSERS.USER_ID.eq(TASKS.ASSIGNED_TO))
                                        .asField(BEAVERUSERS.USERNAME.getName()),
                                COLUMNS.COLUMN_INDEX, COLUMNS.COLUMN_TITLE, COLUMNS.COLUMN_ID)
                        .from(TASKS)
                        .leftJoin(COMMENTS)
                        .on(TASKS.TASK_ID.eq(COMMENTS.TASK_ID))
                        .join(COLUMNS)
                        .on(TASKS.COLUMN_ID.eq(COLUMNS.COLUMN_ID))
                        .where(TASKS.PROJECT_ID.eq(projectID))
                        .and(TASKS.SPRINT_ID.eq(sprintID))
                        .groupBy(TASKS.TASK_ID, COLUMNS.COLUMN_INDEX, COLUMNS.COLUMN_TITLE, COLUMNS.COLUMN_ID)
                        .fetchInto(TaskBasicInSprintDto.class);

        // Next, get sprint details
        // SELECT Sprints.sprint_id, Sprints.sprint_name, Sprints.begin_date, Sprints.end_date,
        // FROM Sprints
        // WHERE Sprints.sprint_id = sprintID
        // AND Sprints.project_id = projectID;
        TasksInSprintDto tasksInSprintDto = create
                .select(SPRINTS.SPRINT_ID, SPRINTS.SPRINT_NAME, SPRINTS.BEGIN_DATE, SPRINTS.END_DATE)
                .from(SPRINTS)
                .where(SPRINTS.SPRINT_ID.eq(sprintID))
                .and(SPRINTS.PROJECT_ID.eq(projectID))
                .fetchSingleInto(TasksInSprintDto.class);

        tasksInSprintDto.setTasks(tasksInSprintInProject);

        return tasksInSprintDto;
    }
}
