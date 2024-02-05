/*
 * This file is generated by jOOQ.
 */
package org.opm.busybeaver.jooq;


import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.opm.busybeaver.jooq.tables.Beaverusers;
import org.opm.busybeaver.jooq.tables.Columns;
import org.opm.busybeaver.jooq.tables.Comments;
import org.opm.busybeaver.jooq.tables.Projects;
import org.opm.busybeaver.jooq.tables.Projectusers;
import org.opm.busybeaver.jooq.tables.Sprints;
import org.opm.busybeaver.jooq.tables.Tasks;
import org.opm.busybeaver.jooq.tables.Teams;
import org.opm.busybeaver.jooq.tables.Teamusers;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.ColumnsRecord;
import org.opm.busybeaver.jooq.tables.records.CommentsRecord;
import org.opm.busybeaver.jooq.tables.records.ProjectsRecord;
import org.opm.busybeaver.jooq.tables.records.ProjectusersRecord;
import org.opm.busybeaver.jooq.tables.records.SprintsRecord;
import org.opm.busybeaver.jooq.tables.records.TasksRecord;
import org.opm.busybeaver.jooq.tables.records.TeamsRecord;
import org.opm.busybeaver.jooq.tables.records.TeamusersRecord;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<BeaverusersRecord> BEAVERUSERS_EMAIL_KEY = Internal.createUniqueKey(Beaverusers.BEAVERUSERS, DSL.name("beaverusers_email_key"), new TableField[] { Beaverusers.BEAVERUSERS.EMAIL }, true);
    public static final UniqueKey<BeaverusersRecord> BEAVERUSERS_FIREBASE_ID_KEY = Internal.createUniqueKey(Beaverusers.BEAVERUSERS, DSL.name("beaverusers_firebase_id_key"), new TableField[] { Beaverusers.BEAVERUSERS.FIREBASE_ID }, true);
    public static final UniqueKey<BeaverusersRecord> BEAVERUSERS_PKEY = Internal.createUniqueKey(Beaverusers.BEAVERUSERS, DSL.name("beaverusers_pkey"), new TableField[] { Beaverusers.BEAVERUSERS.USER_ID }, true);
    public static final UniqueKey<BeaverusersRecord> BEAVERUSERS_USERNAME_KEY = Internal.createUniqueKey(Beaverusers.BEAVERUSERS, DSL.name("beaverusers_username_key"), new TableField[] { Beaverusers.BEAVERUSERS.USERNAME }, true);
    public static final UniqueKey<ColumnsRecord> COLUMNS_PKEY = Internal.createUniqueKey(Columns.COLUMNS, DSL.name("columns_pkey"), new TableField[] { Columns.COLUMNS.COLUMN_ID }, true);
    public static final UniqueKey<ColumnsRecord> UNIQUE_COLUMN_PER_PROJECT = Internal.createUniqueKey(Columns.COLUMNS, DSL.name("unique_column_per_project"), new TableField[] { Columns.COLUMNS.PROJECT_ID, Columns.COLUMNS.COLUMN_TITLE }, true);
    public static final UniqueKey<CommentsRecord> COMMENTS_PKEY = Internal.createUniqueKey(Comments.COMMENTS, DSL.name("comments_pkey"), new TableField[] { Comments.COMMENTS.COMMENT_ID }, true);
    public static final UniqueKey<ProjectsRecord> PROJECTS_PKEY = Internal.createUniqueKey(Projects.PROJECTS, DSL.name("projects_pkey"), new TableField[] { Projects.PROJECTS.PROJECT_ID }, true);
    public static final UniqueKey<ProjectsRecord> UNIQUE_PROJECT_PER_TEAM = Internal.createUniqueKey(Projects.PROJECTS, DSL.name("unique_project_per_team"), new TableField[] { Projects.PROJECTS.PROJECT_NAME, Projects.PROJECTS.TEAM_ID }, true);
    public static final UniqueKey<ProjectusersRecord> PROJECTUSERS_PKEY = Internal.createUniqueKey(Projectusers.PROJECTUSERS, DSL.name("projectusers_pkey"), new TableField[] { Projectusers.PROJECTUSERS.USER_PROJECT_ID }, true);
    public static final UniqueKey<SprintsRecord> SPRINTS_PKEY = Internal.createUniqueKey(Sprints.SPRINTS, DSL.name("sprints_pkey"), new TableField[] { Sprints.SPRINTS.SPRINT_ID }, true);
    public static final UniqueKey<SprintsRecord> UNIQUE_SPRINT_DATES_PER_PROJECT = Internal.createUniqueKey(Sprints.SPRINTS, DSL.name("unique_sprint_dates_per_project"), new TableField[] { Sprints.SPRINTS.PROJECT_ID, Sprints.SPRINTS.BEGIN_DATE, Sprints.SPRINTS.END_DATE }, true);
    public static final UniqueKey<TasksRecord> TASKS_PKEY = Internal.createUniqueKey(Tasks.TASKS, DSL.name("tasks_pkey"), new TableField[] { Tasks.TASKS.TASK_ID }, true);
    public static final UniqueKey<TeamsRecord> TEAMS_PKEY = Internal.createUniqueKey(Teams.TEAMS, DSL.name("teams_pkey"), new TableField[] { Teams.TEAMS.TEAM_ID }, true);
    public static final UniqueKey<TeamsRecord> UNIQUE_TEAM_PER_CREATOR = Internal.createUniqueKey(Teams.TEAMS, DSL.name("unique_team_per_creator"), new TableField[] { Teams.TEAMS.TEAM_NAME, Teams.TEAMS.TEAM_CREATOR }, true);
    public static final UniqueKey<TeamusersRecord> TEAMUSERS_PKEY = Internal.createUniqueKey(Teamusers.TEAMUSERS, DSL.name("teamusers_pkey"), new TableField[] { Teamusers.TEAMUSERS.USER_TEAM_ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<ColumnsRecord, ProjectsRecord> COLUMNS__COLUMNS_PROJECT_ID_FKEY = Internal.createForeignKey(Columns.COLUMNS, DSL.name("columns_project_id_fkey"), new TableField[] { Columns.COLUMNS.PROJECT_ID }, Keys.PROJECTS_PKEY, new TableField[] { Projects.PROJECTS.PROJECT_ID }, true);
    public static final ForeignKey<CommentsRecord, TasksRecord> COMMENTS__COMMENTS_TASK_ID_FKEY = Internal.createForeignKey(Comments.COMMENTS, DSL.name("comments_task_id_fkey"), new TableField[] { Comments.COMMENTS.TASK_ID }, Keys.TASKS_PKEY, new TableField[] { Tasks.TASKS.TASK_ID }, true);
    public static final ForeignKey<CommentsRecord, ProjectusersRecord> COMMENTS__COMMENTS_USER_ID_FKEY = Internal.createForeignKey(Comments.COMMENTS, DSL.name("comments_user_id_fkey"), new TableField[] { Comments.COMMENTS.USER_ID }, Keys.PROJECTUSERS_PKEY, new TableField[] { Projectusers.PROJECTUSERS.USER_PROJECT_ID }, true);
    public static final ForeignKey<ProjectsRecord, SprintsRecord> PROJECTS__FK_PROJECT_SPRINT = Internal.createForeignKey(Projects.PROJECTS, DSL.name("fk_project_sprint"), new TableField[] { Projects.PROJECTS.CURRENT_SPRINT_ID }, Keys.SPRINTS_PKEY, new TableField[] { Sprints.SPRINTS.SPRINT_ID }, true);
    public static final ForeignKey<ProjectsRecord, TeamsRecord> PROJECTS__PROJECTS_TEAM_ID_FKEY = Internal.createForeignKey(Projects.PROJECTS, DSL.name("projects_team_id_fkey"), new TableField[] { Projects.PROJECTS.TEAM_ID }, Keys.TEAMS_PKEY, new TableField[] { Teams.TEAMS.TEAM_ID }, true);
    public static final ForeignKey<ProjectusersRecord, ProjectsRecord> PROJECTUSERS__PROJECTUSERS_PROJECT_ID_FKEY = Internal.createForeignKey(Projectusers.PROJECTUSERS, DSL.name("projectusers_project_id_fkey"), new TableField[] { Projectusers.PROJECTUSERS.PROJECT_ID }, Keys.PROJECTS_PKEY, new TableField[] { Projects.PROJECTS.PROJECT_ID }, true);
    public static final ForeignKey<ProjectusersRecord, BeaverusersRecord> PROJECTUSERS__PROJECTUSERS_USER_ID_FKEY = Internal.createForeignKey(Projectusers.PROJECTUSERS, DSL.name("projectusers_user_id_fkey"), new TableField[] { Projectusers.PROJECTUSERS.USER_ID }, Keys.BEAVERUSERS_PKEY, new TableField[] { Beaverusers.BEAVERUSERS.USER_ID }, true);
    public static final ForeignKey<SprintsRecord, ProjectsRecord> SPRINTS__SPRINTS_PROJECT_ID_FKEY = Internal.createForeignKey(Sprints.SPRINTS, DSL.name("sprints_project_id_fkey"), new TableField[] { Sprints.SPRINTS.PROJECT_ID }, Keys.PROJECTS_PKEY, new TableField[] { Projects.PROJECTS.PROJECT_ID }, true);
    public static final ForeignKey<TasksRecord, ProjectusersRecord> TASKS__TASKS_ASSIGNED_TO_FKEY = Internal.createForeignKey(Tasks.TASKS, DSL.name("tasks_assigned_to_fkey"), new TableField[] { Tasks.TASKS.ASSIGNED_TO }, Keys.PROJECTUSERS_PKEY, new TableField[] { Projectusers.PROJECTUSERS.USER_PROJECT_ID }, true);
    public static final ForeignKey<TasksRecord, ColumnsRecord> TASKS__TASKS_COLUMN_ID_FKEY = Internal.createForeignKey(Tasks.TASKS, DSL.name("tasks_column_id_fkey"), new TableField[] { Tasks.TASKS.COLUMN_ID }, Keys.COLUMNS_PKEY, new TableField[] { Columns.COLUMNS.COLUMN_ID }, true);
    public static final ForeignKey<TasksRecord, ProjectsRecord> TASKS__TASKS_PROJECT_ID_FKEY = Internal.createForeignKey(Tasks.TASKS, DSL.name("tasks_project_id_fkey"), new TableField[] { Tasks.TASKS.PROJECT_ID }, Keys.PROJECTS_PKEY, new TableField[] { Projects.PROJECTS.PROJECT_ID }, true);
    public static final ForeignKey<TasksRecord, SprintsRecord> TASKS__TASKS_SPRINT_ID_FKEY = Internal.createForeignKey(Tasks.TASKS, DSL.name("tasks_sprint_id_fkey"), new TableField[] { Tasks.TASKS.SPRINT_ID }, Keys.SPRINTS_PKEY, new TableField[] { Sprints.SPRINTS.SPRINT_ID }, true);
    public static final ForeignKey<TeamsRecord, BeaverusersRecord> TEAMS__TEAMS_TEAM_CREATOR_FKEY = Internal.createForeignKey(Teams.TEAMS, DSL.name("teams_team_creator_fkey"), new TableField[] { Teams.TEAMS.TEAM_CREATOR }, Keys.BEAVERUSERS_PKEY, new TableField[] { Beaverusers.BEAVERUSERS.USER_ID }, true);
    public static final ForeignKey<TeamusersRecord, TeamsRecord> TEAMUSERS__TEAMUSERS_TEAM_ID_FKEY = Internal.createForeignKey(Teamusers.TEAMUSERS, DSL.name("teamusers_team_id_fkey"), new TableField[] { Teamusers.TEAMUSERS.TEAM_ID }, Keys.TEAMS_PKEY, new TableField[] { Teams.TEAMS.TEAM_ID }, true);
    public static final ForeignKey<TeamusersRecord, BeaverusersRecord> TEAMUSERS__TEAMUSERS_USER_ID_FKEY = Internal.createForeignKey(Teamusers.TEAMUSERS, DSL.name("teamusers_user_id_fkey"), new TableField[] { Teamusers.TEAMUSERS.USER_ID }, Keys.BEAVERUSERS_PKEY, new TableField[] { Beaverusers.BEAVERUSERS.USER_ID }, true);
}
