/*
    PostgreSQL creation script for Opinionated Project Management, CS467, at Oregon State University.

    Recreates database from scratch. Any data currently within database will be dropped, and tables
    will be made new.

    Project title: Busy Beaver

    Team:
        James Adelhelm
        Ryu Barrett
        Giovanni Propersi
*/

-- General users table
DROP TABLE IF EXISTS BeaverUsers CASCADE;
CREATE TABLE BeaverUsers (
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    username VARCHAR(100) UNIQUE NOT NULL,
    firebase_id VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(20) DEFAULT 'User' CHECK (role IN ('User'))
);

DROP TABLE IF EXISTS Teams CASCADE;
CREATE TABLE Teams (
    team_id SERIAL PRIMARY KEY,
    team_name VARCHAR(50) NOT NULL,
    team_creator INTEGER NOT NULL REFERENCES BeaverUsers(user_id),

    -- Enforce uniqueness per team creator of team name
    CONSTRAINT unique_team_per_creator UNIQUE (team_name, team_creator)
);

DROP TABLE IF EXISTS TeamUsers CASCADE;
CREATE TABLE TeamUsers (
    user_team_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES BeaverUsers(user_id) ON DELETE CASCADE,
    team_id INTEGER NOT NULL REFERENCES Teams(team_id) ON DELETE CASCADE,
    user_team_role VARCHAR(20) DEFAULT 'User' CHECK (user_team_role IN ('User', 'Creator', 'Mod')),

    -- Enforce user can't be in table twice
    CONSTRAINT unique_user_per_team UNIQUE (user_id, team_id)
);

DROP TABLE IF EXISTS Projects CASCADE;
CREATE TABLE Projects (
    project_id SERIAL PRIMARY KEY,
    project_name VARCHAR(50) NOT NULL,
    current_sprint_id INTEGER,
    team_id INTEGER NOT NULL REFERENCES Teams(team_id) ON DELETE CASCADE,
    last_updated TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Enforce uniqueness of project per team
    CONSTRAINT unique_project_per_team UNIQUE (project_name, team_id)
);

DROP TABLE IF EXISTS ProjectUsers CASCADE;
CREATE TABLE ProjectUsers (
    user_project_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES BeaverUsers(user_id) ON DELETE CASCADE,
    project_id INTEGER NOT NULL REFERENCES Projects(project_id) ON DELETE CASCADE,
    user_project_role VARCHAR(20) Default 'Member' CHECK (user_project_role IN ('Member', 'Dev', 'Project Manager', 'Manager', 'External', 'Manager')),

    -- Enforce user can't be in team twice
    CONSTRAINT unique_user_per_project UNIQUE (user_id, project_id)
);

DROP TABLE IF EXISTS Sprints CASCADE;
CREATE TABLE Sprints (
    sprint_id SERIAL PRIMARY KEY,
    sprint_name VARCHAR(50) NOT NULL,
    project_id INTEGER NOT NULL REFERENCES Projects(project_id) ON DELETE CASCADE,
    begin_date DATE NOT NULL,
    end_date DATE NOT NULL,

    -- Enforcing uniqueness among projects for sprint  name
    CONSTRAINT unique_sprint_names_per_project UNIQUE (project_id, sprint_name)
);

-- Make sure the Project's table can associate with the Sprints table 
ALTER TABLE Projects ADD CONSTRAINT fk_project_sprint FOREIGN KEY (current_sprint_id) REFERENCES Sprints(sprint_id);

DROP TABLE IF EXISTS Columns CASCADE;
CREATE TABLE Columns (
    column_id SERIAL PRIMARY KEY,
    project_id INTEGER NOT NULL REFERENCES Projects(project_id) ON DELETE CASCADE,
    column_title VARCHAR(50) NOT NULL,
    column_index SMALLINT NOT NULL, -- The order of the column in the Project

    -- Enforcing unique columns in a project
    CONSTRAINT unique_column_per_project UNIQUE (project_id, column_title)
);

DROP TABLE IF EXISTS Tasks CASCADE;
CREATE TABLE Tasks (
    task_id SERIAL PRIMARY KEY,
    sprint_id INTEGER REFERENCES Sprints(sprint_id) ON DELETE SET NULL, -- Tasks can start unassociated with a sprint by default
    assigned_to INTEGER REFERENCES ProjectUsers(user_project_id) ON DELETE SET NULL, -- Tasks can start unassigned by default
    column_id INTEGER REFERENCES Columns(column_id) NOT NULL, -- Tasks cannot start unassociated with a column by default
    project_id INTEGER REFERENCES Projects(project_id) NOT NULL,
    priority VARCHAR(20) Default 'None' CHECK (priority IN ('None', 'Low', 'Medium', 'High')),
    due_date DATE, -- Tasks can start without a due date by default
    title VARCHAR(50) NOT NULL,
    description VARCHAR(500), -- Enforcing 500 character maximum
    custom_fields JSONB,
    task_created TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    task_index SMALLINT DEFAULT -1
);

DROP TABLE IF EXISTS Comments CASCADE;
CREATE TABLE Comments (
    comment_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES ProjectUsers(user_project_id) ON DELETE SET NULL,
    task_id INTEGER REFERENCES Tasks(task_id) ON DELETE CASCADE,
    comment_created TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    comment_body TEXT
);


-- FUNCTIONS
-- Updates the last_updated of Projects table
CREATE OR REPLACE FUNCTION update_project_last_updated()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Projects SET last_updated = CURRENT_TIMESTAMP WHERE project_id = NEW.project_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Updates the last_updated of Tasks table
CREATE OR REPLACE FUNCTION update_task_last_updated()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Tasks SET last_updated = CURRENT_TIMESTAMP WHERE task_id = NEW.task_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Updates last_updated of called for Table
CREATE OR REPLACE FUNCTION update_last_updated()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_updated = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Updates last_updated for Tasks and Projects for comments
CREATE OR REPLACE FUNCTION update_comments_task_project_updated()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Tasks SET last_updated = CURRENT_TIMESTAMP WHERE task_id = NEW.task_id;
    --Comments require subquery to find correct project_id
    UPDATE Projects SET last_updated = CURRENT_TIMESTAMP 
    WHERE project_id = (SELECT project_id FROM Tasks WHERE task_id = NEW.task_id);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ON DELETE
-- Updates for last_updatd for Tasks and Projects for commments when comment is deleted
CREATE OR REPLACE FUNCTION delete_comment_task_project_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    -- Update the last_updated timestamp of the associated task
    UPDATE Tasks SET last_updated = CURRENT_TIMESTAMP WHERE task_id = OLD.task_id;
    -- Get the project_id from the task and update the project's last_updated timestamp
    UPDATE Projects SET last_updated = CURRENT_TIMESTAMP
    WHERE project_id = (SELECT project_id FROM Tasks WHERE task_id = OLD.task_id);
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Updates the last_updated of Projects table
CREATE OR REPLACE FUNCTION delete_project_last_updated()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Projects SET last_updated = CURRENT_TIMESTAMP WHERE project_id = OLD.project_id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Updates the last_updated of Tasks table
CREATE OR REPLACE FUNCTION delete_task_last_updated()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Tasks SET last_updated = CURRENT_TIMESTAMP WHERE task_id = OLD.task_id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Updates last_updated of called for Table
CREATE OR REPLACE FUNCTION delete_last_updated()
RETURNS TRIGGER AS $$
BEGIN
    OLD.last_updated = CURRENT_TIMESTAMP;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;


--TRIGGERS
-- TASKS
-- Updating Task's last_updated along with Task's associated Projects last_updated when task is CRUD
CREATE TRIGGER trigger_update_task_last_updated
BEFORE INSERT OR UPDATE ON Tasks
FOR EACH ROW
EXECUTE FUNCTION update_last_updated();

CREATE TRIGGER trigger_update_tasks_project_last_updated
BEFORE INSERT OR UPDATE ON Tasks
FOR EACH ROW 
EXECUTE FUNCTION update_project_last_updated();

CREATE TRIGGER trigger_delete_tasks_project_last_updated
BEFORE DELETE ON Tasks
FOR EACH ROW 
EXECUTE FUNCTION delete_project_last_updated();

-- PROJECTS
-- Updating Project's last_updated when Project is CRUD
CREATE TRIGGER trigger_update_project_last_updated
BEFORE INSERT OR UPDATE ON Projects
FOR EACH ROW
EXECUTE FUNCTION update_last_updated();

-- COLUMNS
-- Updating Projects last_updated when Columns is CRUD
CREATE TRIGGER trigger_update_columns_project_last_updated
BEFORE INSERT OR UPDATE ON Columns
FOR EACH ROW
EXECUTE FUNCTION update_project_last_updated();

CREATE TRIGGER trigger_delete_columns_project_last_updated
BEFORE DELETE ON Columns
FOR EACH ROW
EXECUTE FUNCTION delete_project_last_updated();

-- SPRINTS
-- Updating Projects last_updated when Sprints is CRUD
CREATE TRIGGER trigger_update_sprints_project_last_updated
BEFORE INSERT OR UPDATE ON Sprints
FOR EACH ROW
EXECUTE FUNCTION update_project_last_updated();

CREATE TRIGGER trigger_delete_sprints_project_last_updated
BEFORE DELETE ON Sprints
FOR EACH ROW
EXECUTE FUNCTION delete_project_last_updated();

-- COMMENTS
-- Updating Tasks and Projects last_updated when Comments is CRUD
CREATE TRIGGER trigger_update_comments_project_task_last_updated
BEFORE INSERT OR UPDATE ON Comments
FOR EACH ROW
EXECUTE FUNCTION update_comments_task_project_updated();

CREATE TRIGGER trigger_delete_comments_project_task_last_updated
BEFORE DELETE ON Comments
FOR EACH ROW
EXECUTE FUNCTION delete_comment_task_project_timestamp();

-- PROJECTUSERS
CREATE TRIGGER trigger_update_user_last_updated
BEFORE INSERT OR UPDATE ON ProjectUsers
FOR EACH ROW
EXECUTE FUNCTION update_project_last_updated();

CREATE TRIGGER trigger_delete_user_last_updated
BEFORE DELETE ON ProjectUsers
FOR EACH ROW
EXECUTE FUNCTION delete_project_last_updated();