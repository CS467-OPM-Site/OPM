from flask import Flask, jsonify, request
from flask_cors import CORS  # Import CORS

app = Flask(__name__)
CORS(app)  # Enable CORS for all routes

# Mock database for demonstration purposes
mock_users = [
    {"username": "user1", "userID": 1},
    {"username": "user2", "userID": 2}
]

mock_teams = [
    {"teamName": "Team 1", "teamID": 1},
    {"teamName": "Team 2", "teamID": 2}
]

mock_projects = [
    {"projectName": "Project 1", "projectID": 1, "teamID": 1},
    {"projectName": "Project 2", "projectID": 2, "teamID": 1}
]

#adding a team
@app.route('/api/v1/teams', methods=['POST'])
def create_team():
    # Simulate team creation
    data = request.json
    team_name = data.get("teamName")
    # Check if team name exists
    if any(team["teamName"] == team_name for team in mock_teams):
        return jsonify({"code": "400", "message": "Team name exists"}), 400
    # Simulate adding team to mock database
    new_team = {"teamName": team_name, "teamID": len(mock_teams) + 1}
    mock_teams.append(new_team)
    return jsonify(new_team), 201

@app.route('/api/v1/users/register', methods=['POST'])
def register_user():
    # Simulate user registration
    data = request.json
    username = data.get("username")
    # Check if username exists
    if any(user["username"] == username for user in mock_users):
        return jsonify({"code": "400", "message": "Username exists"}), 400
    # Simulate adding user to mock database
    new_user = {"username": username, "userID": len(mock_users) + 1}
    mock_users.append(new_user)
    return jsonify(new_user), 201


@app.route('/api/v1/projects', methods=['GET'])
def get_projects():
    # Simulate retrieving user's projects
    # This example does not authenticate or select projects based on user
    return jsonify({"projects": mock_projects}), 200

@app.route('/api/v1/projects', methods=['POST'])
def create_project():
    # Simulate project creation
    data = request.json
    project_name = data.get("projectName")
    team_id = data.get("team", {}).get("teamID")
    # Check if project name exists within the team
    if any(project["projectName"] == project_name and project["teamID"] == team_id for project in mock_projects):
        return jsonify({"code": "400", "message": "Project name for that team already exists"}), 400
    # Simulate adding project to mock database
    new_project = {"projectName": project_name, "projectID": len(mock_projects) + 1, "teamID": team_id}
    mock_projects.append(new_project)
    return jsonify(new_project), 201

# Add more routes as needed following the structure of your API documentation

if __name__ == '__main__':
    app.run(debug=True)