package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.opm.busybeaver.dto.Sprints.NewSprintDto;
import org.opm.busybeaver.dto.Sprints.SprintSummaryDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Sprints.SprintsExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import static org.opm.busybeaver.jooq.Tables.SPRINTS;

@Repository
@Component
public class SprintsRepository {

    private final DSLContext create;

    @Autowired
    public SprintsRepository(DSLContext dslContext) { this.create = dslContext; }

    public void isSprintNameInProject(int projectID, String sprintName)
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
            throw new SprintsExceptions.SprintNameAlreadyInProject(
                    ErrorMessageConstants.PROJECT_CONTAINS_SPRINT_NAME.getValue());
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

    public void doesSprintExistInProject(int sprintID, int projectID)
            throws SprintsExceptions.SprintDoesNotExistInProject {
        // SELECT EXISTS(
        //      SELECT *
        //      FROM Sprints
        //      WHERE Sprint.sprint_id = sprintID
        //      AND Sprint.project_id = projectID)
        boolean isSprintInProject = create.fetchExists(
                create.selectFrom(SPRINTS)
                        .where(SPRINTS.SPRINT_ID.eq(sprintID))
                        .and(SPRINTS.PROJECT_ID.eq(projectID))
        );

        if (!isSprintInProject) {
            throw new SprintsExceptions.SprintDoesNotExistInProject(
                    ErrorMessageConstants.SPRINT_NOT_IN_PROJECT.getValue());
        }
    }
}
