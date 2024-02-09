-- queries to add in mock data
INSERT INTO BeaverUsers (email, username, firebase_id) VALUES
('ryu@gmail.com', 'ryu', 'replacing'),
('james@gmail.com', 'james', 'changelater'),
('giovanni@gmail.com', 'giovanni', 'tobechanged');

INSERT INTO Teams (team_name, team_creator) VALUES
('busybeaver', (SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'ryu@gmail.com')),
('goingsteady', (SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'james@gmail.com')),
('herewego', (SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'giovanni@gmail.com'));

INSERT INTO TeamUsers (user_id, team_id) VALUES
((SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'ryu@gmail.com'), (SELECT Teams.team_id FROM Teams WHERE Teams.team_name = 'busybeaver')),
((SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'james@gmail.com'), (SELECT Teams.team_id FROM Teams WHERE Teams.team_name = 'goingsteady')),
((SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'giovanni@gmail.com'), (SELECT Teams.team_id FROM Teams WHERE Teams.team_name = 'busybeaver'));

INSERT INTO Projects (project_name, current_sprint_id, team_id) VALUES
('arcticmonkeys', null, (SELECT Teams.team_id FROM Teams WHERE Teams.team_name = 'busybeaver')),
('bladee', null, (SELECT Teams.team_id FROM Teams WHERE Teams.team_name = 'goingsteady')),
('mitski', null, (SELECT Teams.team_id FROM Teams WHERE Teams.team_name = 'busybeaver'));

INSERT INTO ProjectUsers (user_id, project_id) VALUES
((SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'ryu@gmail.com'), (SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'arcticmonkeys')),
((SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'james@gmail.com'), (SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'mitski')),
((SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'giovanni@gmail.com'), (SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'bladee'));

INSERT INTO Sprints (sprint_name, project_id, begin_date, end_date) VALUES
('the1975', (SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'bladee'), '2024-02-01', '2024-02-12'),
('japanesebreakfast', (SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'mitski'), '2024-01-23', '2024-01-31'),
('blink182', (SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'arcticmonkeys'), '2023-12-28', '2024-01-23');

INSERT INTO Columns (project_id, column_title, column_index) VALUES
((SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'arcticmonkeys'), 'to-do', 1),
((SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'arcticmonkeys'), 'In progress', 2),
((SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'arcticmonkeys'), 'Done', 3),
((SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'bladee'), 'Test', 1);

INSERT INTO Tasks (sprint_id, assigned_to, column_id, project_id, due_date, title, description, custom_fields) VALUES
((SELECT Sprints.sprint_id FROM Sprints WHERE Sprints.sprint_name = 'the1975'), (SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'james@gmail.com'), (SELECT Columns.column_id FROM Columns WHERE Columns.column_title = 'Done'), (SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'arcticmonkeys'), '2024-02-12', 'tameimpala', 'his music is cool', null),
((SELECT Sprints.sprint_id FROM Sprints WHERE Sprints.sprint_name = 'the1975'), (SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'james@gmail.com'), (SELECT Columns.column_id FROM Columns WHERE Columns.column_title = 'Done'), (SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'arcticmonkeys'), '2024-02-12', 'grimes', 'married elon musk', null),
((SELECT Sprints.sprint_id FROM Sprints WHERE Sprints.sprint_name = 'the1975'), (SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'giovanni@gmail.com'), (SELECT Columns.column_id FROM Columns WHERE Columns.column_title = 'Done'), (SELECT Projects.project_id FROM Projects WHERE Projects.project_name = 'arcticmonkeys'), null, 'yeule', 'for times of angst', null);

INSERT INTO Comments (user_id, task_id, comment_body) VALUES
((SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'ryu@gmail.com'), (SELECT Tasks.task_id FROM Tasks WHERE Tasks.title = 'yeule'), 'we the people!'),
((SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'ryu@gmail.com'), (SELECT Tasks.task_id FROM Tasks WHERE Tasks.title = 'yeule'), 'This is gonna be a comment about comments'),
((SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'james@gmail.com'), (SELECT Tasks.task_id FROM Tasks WHERE Tasks.title = 'yeule'), 'Third!'),
((SELECT BeaverUsers.user_id FROM BeaverUsers WHERE BeaverUsers.email = 'ryu@gmail.com'), (SELECT Tasks.task_id FROM Tasks WHERE Tasks.title = 'grimes'), 'Who goes there?');