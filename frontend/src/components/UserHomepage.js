import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/UserHomepage.css'; // Make sure the path to your CSS file is correct

const UserHomepage = () => {
  const [projects, setProjects] = useState([]);
  const [teams, setTeams] = useState([
    // Mock data for testing
    // Assuming the creator of the team is also the team creator
    // Will need to fetch this data from the backend
    { teamID: 1, teamName: "Team Name 1", isTeamCreator: true, members: ["creator"] },
    { teamID: 2, teamName: "Team Name 2", isTeamCreator: true, members: ["creator",] },
  ]);
  const [teamName, setTeamName] = useState('');
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch projects and teams here
  }, []);

  const handleAddTeam = () => {
    if (!teamName.trim()) {
      setError('Team name cannot be empty');
      return;
    }
    
    // Simulate adding a new team
    const newTeam = {
      teamID: teams.length + 1, // Assuming teamID is numeric and auto-incremented
      teamName: teamName,
      isTeamCreator: true, // Assuming the creator of the team is also the team creator
      members: ["creator"], // Starting with a single member (the creator)
    };
  
    setTeams(prevTeams => [...prevTeams, newTeam]); // Add the new team to the existing list
    setTeamName(''); // Reset the team name input field
    setError(''); // Clear any existing errors
  };
  

  const handleTeamNameChange = (event) => {
    setTeamName(event.target.value);
  };

  const handleTeamClick = (teamID) => {
    setSelectedTeam(teams.find(t => t.teamID === teamID));
  };

  const handleCloseTeam = () => {
    setSelectedTeam(null);
  };

  const handleDeleteTeam = (teamID) => {
    if (window.confirm('Are you sure you want to delete this team?')) {
      // Delete team logic here (API call)
      setTeams(teams.filter(team => team.teamID !== teamID));
    }
  };

  const handleAddMember = () => {
    // Placeholder will incorporate API logic

    // Ensure a team is selected
    if (!selectedTeam) {
      alert('Please select a team first.');
      return;
    }
  
    // Prompt user for the new member's name
    const newMemberName = window.prompt('Enter the new team member\'s name:');
    if (!newMemberName) {
      alert('Member name cannot be empty.');
      return;
    }
  
    // Find the team and add the new member
    setTeams(teams => teams.map(team => {
      if (team.teamID === selectedTeam.teamID) {
        return {
          ...team,
          members: [...team.members, newMemberName], // Add the new member to the existing members array
        };
      }
      return team; // Return unmodified for other teams
    }));
  
    alert('Member added successfully!');
  };

  const renderTeams = () => {
    return teams.map((team) => (
      <div key={team.teamID} className={`team-card ${selectedTeam?.teamID === team.teamID ? 'selected' : ''}`}>
        <div className="team-header">
          <button onClick={() => handleTeamClick(team.teamID)} className="team-button">
            {team.teamName}
          </button>
          {team.isTeamCreator && (
            <button onClick={() => handleDeleteTeam(team.teamID)} className="delete-button">Delete Team</button>
          )}
          {selectedTeam?.teamID === team.teamID && (
            <button className="close-button" onClick={handleCloseTeam}>X</button>
          )}
        </div>
        {selectedTeam?.teamID === team.teamID && (
          <div className="team-details-container">
            <div className="team-members-list">
              {renderTeamMembers(team)}
            </div>
            <div className="team-actions">
              <button onClick={handleAddMember}>Add Member</button>
              <button>Leave Team</button>
            </div>
          </div>
        )}
      </div>
    ));
  };
  
  

  const renderTeamMembers = (team) => {
    return team.members.map((member, index) => (
      <div key={index} className="team-member">
        {member}
        {/* Add a button to remove a member. Hide or disable this for the team creator if needed */}
        <button onClick={() => handleRemoveMember(team.teamID, member)} className="remove-member-button">Remove</button>
      </div>
    ));
  };

  const handleRemoveMember = (teamID, memberToRemove) => {
    setTeams(teams => teams.map(team => {
      if (team.teamID === teamID) {
        // Filter out the member to remove
        return {
          ...team,
          members: team.members.filter(member => member !== memberToRemove),
        };
      }
      return team; // Return unmodified for other teams
    }));
  };
  
  

  const handleAddProject = () => {
    // Add project logic here (API call)
  };

  const handleFilterTeam = () => {
    // Filter team logic here (API call)
  };

  const handleProjectClick = (projectId) => {
    navigate(`/project/${projectId}`);
  };

  const handleLogout = () => {
    navigate('/');
  };

  return (
    <div className="user-homepage-container">
      <header className="user-homepage-header">
        <h1>User Homepage</h1>
        <div className="user-homepage-buttons">
          <input 
            type="text"
            value={teamName}
            onChange={handleTeamNameChange}
            placeholder="Team Name"
            className={error ? "input-error" : ""}
          />
          <button onClick={handleAddTeam}>Add Team</button>
          <button onClick={handleAddProject}>Add Project</button>
          <button onClick={handleFilterTeam}>Team Filter</button>
          <button onClick={handleLogout}>Logout</button>
          {error && <div className="error-message">{error}</div>}
        </div>
      </header>
      <div className="content-container">
        <aside className="team-list">
          <h2>Teams</h2>
          {renderTeams()}
        </aside>
        <main className="project-list">
          <h2>Projects</h2>
          {/* Render project list here */}
        </main>
      </div>
    </div>
  );
};

export default UserHomepage;