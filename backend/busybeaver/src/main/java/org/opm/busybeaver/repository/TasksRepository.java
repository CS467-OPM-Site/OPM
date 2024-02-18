package org.opm.busybeaver.repository;

import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.opm.busybeaver.dto.Tasks.NewTaskDtoExtended;
import org.opm.busybeaver.dto.Tasks.TaskCreatedDto;
import org.opm.busybeaver.dto.Tasks.TaskDetailsDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Tasks.TasksExceptions;
import org.opm.busybeaver.jooq.tables.records.TasksRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.count;
import static org.opm.busybeaver.jooq.Tables.*;
import static org.opm.busybeaver.jooq.tables.Tasks.TASKS;

@Repository
@Component
public class TasksRepository {
    private final DSLContext create;
    private final CommentsRepository commentsRepository;

    @Autowired
    public TasksRepository(DSLContext dslContext, CommentsRepository commentsRepository) {
        this.create = dslContext;
        this.commentsRepository = commentsRepository;
    }

    public TaskCreatedDto addTask(NewTaskDtoExtended newTaskDto, int projectID) {
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

    public TaskDetailsDto getTaskDetails(int taskID) throws TasksExceptions.TaskDoesNotExistInProject {
        // SELECT Tasks.task_id, Tasks.title, Tasks.description,
        //      Tasks.priority, Tasks.due_date, Columns.columns_id, Columns.column_title,
        //      Columns.column_index, ProjectUsers.user_id, BeaverUsers.username,
        //      Sprints.sprint_id, Sprints.begin_date, Sprints.end_date, Sprints.sprint_name
        // FROM Tasks
        // JOIN Columns
        // ON Tasks.column_id = Columns.column_id
        // JOIN ProjectUsers
        // ON Tasks.assigned_to = ProjectUsers.user_project_id
        // JOIN BeaverUsers
        // ON BeaverUsers.user_id = ProjectUsers.user_id
        // JOIN Sprints
        // ON Tasks.sprint_id = Sprints.sprint_id
        // WHERE Tasks.task_id = taskID;
        @Nullable TaskDetailsDto task = create.select(
                TASKS.TASK_ID,
                TASKS.TITLE,
                TASKS.DESCRIPTION,
                TASKS.PRIORITY,
                TASKS.DUE_DATE,
                COLUMNS.COLUMN_ID,
                COLUMNS.COLUMN_TITLE,
                COLUMNS.COLUMN_INDEX,
                PROJECTUSERS.USER_ID,
                BEAVERUSERS.USERNAME,
                SPRINTS.SPRINT_ID,
                SPRINTS.BEGIN_DATE,
                SPRINTS.END_DATE,
                SPRINTS.SPRINT_NAME)
                .from(TASKS)
                .join(COLUMNS)
                .on(TASKS.COLUMN_ID.eq(COLUMNS.COLUMN_ID))
                .leftJoin(PROJECTUSERS)
                .on(TASKS.ASSIGNED_TO.eq(PROJECTUSERS.USER_PROJECT_ID))
                .leftJoin(BEAVERUSERS)
                .on(PROJECTUSERS.USER_ID.eq(BEAVERUSERS.USER_ID))
                .leftJoin(SPRINTS)
                .on(TASKS.SPRINT_ID.eq(SPRINTS.SPRINT_ID))
                .where(TASKS.TASK_ID.eq(taskID))
                .fetchOneInto(TaskDetailsDto.class);

        if (task == null) {
            throw new TasksExceptions.TaskDoesNotExistInProject(ErrorMessageConstants.TASK_NOT_IN_PROJECT.getValue());
        }

        task.setComments(commentsRepository.getCommentsOnTask(taskID));

        return task;
    }

    public TasksRecord doesTaskExistInProject(int taskID, int projectID)
            throws TasksExceptions.TaskDoesNotExistInProject {
        // SELECT Tasks.task_id
        // FROM Tasks
        // WHERE Tasks.task_id = taskID
        // AND Tasks.project_id = projectID);
        TasksRecord taskInProject =
                create.selectFrom(TASKS)
                        .where(TASKS.TASK_ID.eq(taskID))
                        .and(TASKS.PROJECT_ID.eq(projectID)).fetchOneInto(TasksRecord.class);


        if (taskInProject == null) {
            throw new TasksExceptions.TaskDoesNotExistInProject(ErrorMessageConstants.TASK_NOT_IN_PROJECT.getValue());
        }
        return taskInProject;
    }

    public Boolean doesProjectHaveZeroTasks(int projectID) {
        // SELECT Tasks.project_id, COUNT(*)
        // FROM Tasks
        // WHERE Tasks.project_id = projectID
        // GROUP BY Tasks.project_Id
        @Nullable Record2<Integer, Integer> countOfTasksInProject = create.select(TASKS.PROJECT_ID, count())
                .from(TASKS)
                .where(TASKS.PROJECT_ID.eq(projectID))
                .groupBy(TASKS.PROJECT_ID)
                .fetchOne();

        if (countOfTasksInProject == null) {
            return true;
        }

        return (countOfTasksInProject.value2() == 0);
    }

    public void isTaskAlreadyInColumn(int taskID, int columnID) throws TasksExceptions.TaskAlreadyInColumn {
        // SELECT EXISTS (
        //      SELECT Tasks.task_id
        //      FROM Tasks
        //      WHERE Tasks.task_id = taskID
        //      AND Tasks.column_id = columnID);
        boolean isTaskInColumn =  create.fetchExists(
                create.select(TASKS.TASK_ID)
                        .from(TASKS)
                        .where(TASKS.TASK_ID.eq(taskID))
                        .and(TASKS.COLUMN_ID.eq(columnID))
        );

        if (isTaskInColumn) {
            throw new TasksExceptions.TaskAlreadyInColumn(ErrorMessageConstants.TASK_ALREADY_IN_COLUMN.getValue());
        }
    }

    public Boolean doesColumnContainTasks(int projectID, int columnID) {
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
