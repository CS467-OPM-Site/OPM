package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.opm.busybeaver.dto.Tasks.NewTaskDto;
import org.opm.busybeaver.dto.Tasks.TaskCreatedDto;
import org.opm.busybeaver.jooq.tables.Tasks;
import org.opm.busybeaver.jooq.tables.records.TasksRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.opm.busybeaver.jooq.Tables.COLUMNS;
import static org.opm.busybeaver.jooq.tables.Tasks.TASKS;

@Repository
@Component
public class TaskRepository {
    private final DSLContext create;

    @Autowired
    public TaskRepository(DSLContext dslContext) { this.create = dslContext; }

    public TaskCreatedDto addTask(NewTaskDto newTaskDto, int projectID) {
        // INSERT INTO Tasks (title, description, project_id, column_id, assigned_to, sprint_id, priority, due_date)
        // VALUES....
        return create.insertInto(
                TASKS,
                TASKS.TITLE,
                TASKS.DESCRIPTION,
                TASKS.PROJECT_ID,
                TASKS.COLUMN_ID,
                TASKS.ASSIGNED_TO,
                TASKS.SPRINT_ID,
                TASKS.PRIORITY,
                TASKS.DUE_DATE)
                .values(
                        newTaskDto.getTitle(),
                        newTaskDto.getDescription(),
                        projectID,
                        newTaskDto.getColumnID(),
                        newTaskDto.getAssignedTo(),
                        newTaskDto.getSprintID(),
                        newTaskDto.getPriority(),
                        newTaskDto.getDueDate()
                ).returningResult(
                        TASKS.TITLE,
                        TASKS.TASK_ID,
                        TASKS.COLUMN_ID,
                        TASKS.PRIORITY,
                        TASKS.DESCRIPTION,
                        TASKS.DUE_DATE,
                        TASKS.SPRINT_ID,
                        TASKS.ASSIGNED_TO)
                .fetchSingleInto(TaskCreatedDto.class);
    }

    public TasksRecord doesTaskExistInProject(int taskID, int projectID) {
        // SELECT *
        // FROM Tasks
        // WHERE Tasks.task_id = taskID
        // AND Tasks.project_id = projectID)
        return create.selectFrom(TASKS)
                        .where(TASKS.TASK_ID.eq(taskID))
                        .and(TASKS.PROJECT_ID.eq(projectID))
                        .fetchOne();
    }

    public Boolean doesTaskExistInColumnInProject(int projectID, int columnID) {
        // SELECT EXISTS(
        //      SELECT *
        //      FROM Tasks
        //      WHERE Tasks.project_id = projectID
        //      AND Tasks.column_id = columnID)
        return create.fetchExists(
                create.selectFrom(TASKS)
                        .where(TASKS.PROJECT_ID.eq(projectID))
                        .and(TASKS.COLUMN_ID.eq(columnID))
        );
    }

    public void deleteTask(int taskID) {
        create.deleteFrom(TASKS).where(TASKS.TASK_ID.eq(taskID)).execute();
    }

    public void moveTaskToAnotherColumn(int taskID, int projectID, int columnID) {
        // UPDATE Tasks
        // SET column_id = columnID, last_updated = CURRENT_TIMESTAMP
        // WHERE Tasks.task_id = taskID
        // AND Tasks.project_id = projectID
        create.update(TASKS)
                .set(TASKS.COLUMN_ID, columnID)
                .set(TASKS.LAST_UPDATED, LocalDateTime.now())
                .where(TASKS.TASK_ID.eq(taskID))
                .and(TASKS.PROJECT_ID.eq(projectID))
                .execute();
    }

}
