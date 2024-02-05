import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/UserHomepage.css'; // Ensure this path is correct

const UserHomepage = () => {
  const [projects, setProjects] = useState([]);
  const [teams, setTeams] = useState([]);
  const [teamName, setTeamName] = useState('');
  const [selectedTeam, setSelectedTeam] = useState(null); // Track the selected team
  const navigate = useNavigate();

  useEffect(() => {
    // Implement the logic to fetch projects and teams here
  }, []);

  const handleAddTeam = () => {
    // Example logic to handle adding a new team using Flask API mock
    const newTeam = { teamName, teamID: teams.length + 1, members: [] };
    setTeams([...teams, newTeam]);
    setTeamName(''); // Reset the input field after adding
  };

  const handleTeamNameChange = (event) => {
    setTeamName(event.target.value);
  };

  const handleTeamClick = (teamID) => {
    const team = teams.find(t => t.teamID === teamID);
    setSelectedTeam(team); // Set the selected team
  };

  const handleCloseTeam = () => {
    setSelectedTeam(null); // Clear the selected team to close the card
  };

  const handleAddMember = () => {
    // Logic to handle adding a member to the selected team
  };

  const renderTeamMembers = () => {
    return selectedTeam?.members.map((member, index) => (
      <div key={index} className="team-member">
        {member}
      </div>
    ));
  };

  const handleAddProject = () => {
    // Logic to handle adding a new project
  };

  const handleFilterTeam = () => {
    // Logic to handle filtering projects by team
  };

  const handleProjectClick = (projectId) => {
    navigate(`/project/${projectId}`);
  };

  const handleLogout = () => {
    // Logic to handle user logout
    navigate('/');
  };

  return (
    <div className="user-homepage-container">
      <header className="user-homepage-header">
        <h1>User Homepage</h1>
        <div className="user-homepage-buttons">
          <input type="text" value={teamName} onChange={handleTeamNameChange} placeholder="Team Name"/>
          <button onClick={handleAddTeam}>Add Team</button>
          <button onClick={handleAddProject}>Add Project</button>
          <button onClick={handleFilterTeam}>Team Filter</button>
          <button onClick={handleLogout}>Logout</button>
        </div>
      </header>
      <div className="content-container">
        <aside className="team-list">
          <h2>Teams</h2>
          {teams.map((team) => (
            <div key={team.teamID} className={`team-card ${selectedTeam?.teamID === team.teamID ? 'selected' : ''}`}>
              <div className="team-header">
                <button onClick={() => handleTeamClick(team.teamID)} className="team-button">
                  {team.teamName}
                </button>
                <button className="close-button" onClick={handleCloseTeam}>X</button>
              </div>
              {selectedTeam?.teamID === team.teamID && (
                <div className="team-actions">
                  <button onClick={handleAddMember}>Add Member</button>
                  <button>Leave Team</button>
                </div>
              )}
              {renderTeamMembers()}
            </div>
          ))}
        </aside>
        <main className="project-list">
          <h2>Projects</h2>
          {/* Render project list */}
        </main>
      </div>
    </div>
  );
};

export default UserHomepage;