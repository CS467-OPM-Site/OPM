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
    team_creator INTEGER REFERENCES BeaverUsers(user_id) NOT NULL,

    -- Enforce uniqueness per team creator of team name
    CONSTRAINT unique_team_per_creator UNIQUE (team_name, team_creator)
);

DROP TABLE IF EXISTS TeamUsers CASCADE;
CREATE TABLE TeamUsers (
    user_team_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES BeaverUsers(user_id) NOT NULL,
    team_id INTEGER REFERENCES Teams(team_id) NOT NULL,
    user_team_role VARCHAR(20) DEFAULT 'User' CHECK (user_team_role IN ('User', 'Creator', 'Mod'))
);

DROP TABLE IF EXISTS Projects CASCADE;
CREATE TABLE Projects (
    project_id SERIAL PRIMARY KEY,
    project_name VARCHAR(50) NOT NULL,
    current_sprint_id INTEGER,
    team_id INTEGER REFERENCES Teams(team_id) NOT NULL,

    -- Enforce uniqueness of project per team
    CONSTRAINT unique_project_per_team UNIQUE (project_name, team_id)
);

DROP TABLE IF EXISTS ProjectUsers CASCADE;
CREATE TABLE ProjectUsers (
    user_project_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES BeaverUsers(user_id) NOT NULL,
    project_id INTEGER REFERENCES Projects(project_id) NOT NULL,
    user_project_role VARCHAR(20) Default 'Member' CHECK (user_project_role IN ('Member', 'Dev', 'Project Manager', 'Manager', 'External', 'Manager'))
);

DROP TABLE IF EXISTS Sprints CASCADE;
CREATE TABLE Sprints (
    sprint_id SERIAL PRIMARY KEY,
    sprint_name VARCHAR(50) NOT NULL,
    project_id INTEGER REFERENCES Projects(project_id) NOT NULL,
    begin_date DATE NOT NULL,
    end_date DATE NOT NULL,

    -- Enforcing uniqueness among projects for sprint begin date and end date
    CONSTRAINT unique_sprint_dates_per_project UNIQUE (project_id, begin_date, end_date)
);

-- Make sure the Project's table can associate with the Sprints table 
ALTER TABLE Projects ADD CONSTRAINT fk_project_sprint FOREIGN KEY (current_sprint_id) REFERENCES Sprints(sprint_id);

DROP TABLE IF EXISTS Columns CASCADE;
CREATE TABLE Columns (
    column_id SERIAL PRIMARY KEY,
    project_id INTEGER REFERENCES Projects(project_id) NOT NULL,
    column_title VARCHAR(50) NOT NULL,
    column_index SMALLINT NOT NULL, -- The order of the column in the Project

    -- Enforcing unique columns in a project
    CONSTRAINT unique_column_per_project UNIQUE (project_id, column_title)
);

DROP TABLE IF EXISTS Tasks CASCADE;
CREATE TABLE Tasks (
    task_id SERIAL PRIMARY KEY,
    sprint_id INTEGER REFERENCES Sprints(sprint_id), -- Tasks can start unassociated with a sprint by default
    assigned_to INTEGER REFERENCES ProjectUsers(user_project_id), -- Tasks can start unassigned by default
    column_id INTEGER REFERENCES Columns(column_id) NOT NULL, -- Tasks cannot start unassociated with a column by default
    project_id INTEGER REFERENCES Projects(project_id) NOT NULL,
    priority VARCHAR(20) Default 'None' CHECK (priority IN ('None', 'Low', 'Medium', 'High')),
    due_date DATE, -- Tasks can start without a due date by default
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500), -- Enforcing 500 character maximum
    custom_fields JSONB,
    task_created TIMESTAMP
);

DROP TABLE IF EXISTS Comments CASCADE;
CREATE TABLE Comments (
    comment_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES ProjectUsers(user_project_id),
    task_id INTEGER REFERENCES Tasks(task_id),
    comment_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comment_body TEXT
);

