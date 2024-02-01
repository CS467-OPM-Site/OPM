DROP TABLE IF EXISTS "BBusers" cascade;
CREATE TABLE "BBusers" (
                         "user_id" integer PRIMARY KEY,
                         "email" varchar,
                         "username" varchar,
                         "role" varchar
);

DROP TABLE IF EXISTS"teams" cascade;
CREATE TABLE "teams" (
                         "team_id" integer PRIMARY KEY,
                         "team_title" varchar
);

DROP TABLE IF EXISTS "projects" cascade;
CREATE TABLE "projects" (
                            "project_id" integer PRIMARY KEY,
                            "team_id" integer,
                            "associated_team" varchar,
                            "project_name" varchar,
                            "current_sprint_deadline" timestamp
);

DROP TABLE IF EXISTS "sprints" cascade;
CREATE TABLE "sprints" (
                           "sprint_id" integer PRIMARY KEY,
                           "project_id" int,
                           "begin_date" timestamp,
                           "end_date" timestamp
);

DROP TABLE IF EXISTS "columns" cascade;
CREATE TABLE "columns" (
                           "column_id" integer PRIMARY KEY,
                           "project_id" int,
                           "column_title" varchar
);

DROP TABLE IF EXISTS "tasks" cascade;
CREATE TABLE "tasks" (
                         "task_id" integer PRIMARY KEY,
                         "sprint_id" int,
                         "column_id" int,
                         "assigned_to" varchar,
                         "priority" varchar,
                         "due_date" timestamp,
                         "title" varchar,
                         "description" text,
                         "sprint_association" varchar
);

DROP TABLE IF EXISTS "comments" cascade;
CREATE TABLE "comments" (
                            "comment_id" integer PRIMARY KEY,
                            "user_id" int,
                            "task_id" int,
                            "comment_created" timestamp,
                            "comment_body" text
);

DROP TABLE IF EXISTS "user_project" cascade;
CREATE TABLE "user_project" (
                                "use_project_id" integer PRIMARY KEY,
                                "project_id" int,
                                "user_id" int
);

DROP TABLE IF EXISTS "user_team" cascade;
CREATE TABLE "user_team" (
                             "use_team_id" integer PRIMARY KEY,
                             "user_id" int,
                             "team_id" int
);

ALTER TABLE "projects" ADD FOREIGN KEY ("team_id") REFERENCES "teams" ("team_id");

ALTER TABLE "sprints" ADD FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id");

ALTER TABLE "columns" ADD FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id");

ALTER TABLE "tasks" ADD FOREIGN KEY ("sprint_id") REFERENCES "sprints" ("sprint_id");

ALTER TABLE "tasks" ADD FOREIGN KEY ("column_id") REFERENCES "columns" ("column_id");

ALTER TABLE "comments" ADD FOREIGN KEY ("user_id") REFERENCES "BBusers" ("user_id");

ALTER TABLE "comments" ADD FOREIGN KEY ("task_id") REFERENCES "tasks" ("task_id");

ALTER TABLE "user_project" ADD FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id");

ALTER TABLE "user_project" ADD FOREIGN KEY ("user_id") REFERENCES "BBusers" ("user_id");

ALTER TABLE "user_team" ADD FOREIGN KEY ("user_id") REFERENCES "BBusers" ("user_id");

ALTER TABLE "user_team" ADD FOREIGN KEY ("team_id") REFERENCES "teams" ("team_id");