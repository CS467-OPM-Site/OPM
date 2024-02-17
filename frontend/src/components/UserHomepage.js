import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AddProjectForm from './AddProjectForm';
import { useAuth } from '../contexts/AuthContext'; // Adjust the path as necessary
import '../styles/UserHomepage.css';
import BusyBeaverNoBG from '../assets/BusyBeaverNoBG.png';
import { getAuth } from 'firebase/auth';

const UserHomepage = () => {
  const [projects, setProjects] = useState([]);
  const [teams, setTeams] = useState([
    { teamID: 1, teamName: 'Team Name 1', isTeamCreator: true, members: ['creator'] },
    { teamID: 2, teamName: 'Team Name 2', isTeamCreator: true, members: ['creator'] },
  ]);
  const [teamName, setTeamName] = useState('');
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { currentUser } = useAuth(); // Use currentUser from AuthContext

  const handleLogout = () => {
    const auth = getAuth();
    auth.signOut().then(() => {
      navigate('/');
    }).catch((error) => {
      console.error('Logout Error:', error);
    });
  };

  const handleAddTeam = () => {
    if (!teamName.trim()) {
      setError('Team name cannot be empty');
      return;
    }
    const newTeam = {
      teamID: teams.length + 1,
      teamName,
      isTeamCreator: true,
      members: ['creator'],
    };
    setTeams((prevTeams) => [...prevTeams, newTeam]);
    setTeamName('');
    setError('');
  };

  const handleTeamNameChange = (event) => {
    setTeamName(event.target.value);
  };

  const handleTeamClick = (teamID) => {
    setSelectedTeam(teams.find((t) => t.teamID === teamID));
  };

  const handleCloseTeam = () => {
    setSelectedTeam(null);
  };

  const handleDeleteTeam = (teamID) => {
    if (window.confirm('Are you sure you want to delete this team?')) {
      setTeams((prevTeams) => prevTeams.filter((team) => team.teamID !== teamID));
    }
  };

  const handleAddMember = () => {
    if (!selectedTeam) {
      alert('Please select a team first.');
      return;
    }
    const newMemberName = window.prompt("Enter the new team member's name:");
    if (!newMemberName) {
      alert('Member name cannot be empty.');
      return;
    }
    setTeams((prevTeams) =>
      prevTeams.map((team) =>
        team.teamID === selectedTeam.teamID ? { ...team, members: [...team.members, newMemberName] } : team
      )
    );
  };

  const handleAddProject = (projectName) => {
    const newProject = {
      id: projects.length + 1,
      name: projectName,
      tasks: [],
      teamID: selectedTeam ? selectedTeam.teamID : null,
    };
    setProjects([...projects, newProject]);
  };

  return (
    <div className="user-homepage-container">
      <header className="user-homepage-header">
        <div className="busy-beaver-logo">
          <img src={BusyBeaverNoBG} alt="Busy Beaver" />
        </div>
        <div className="user-homepage-header-card">
          <h1>User Homepage</h1>
        </div>
        <div className="user-homepage-buttons">
          <input
            type="text"
            value={teamName}
            onChange={handleTeamNameChange}
            placeholder="Team Name"
            className={error ? "input-error" : ""}
          />
          <button onClick={handleAddTeam}>Add Team</button>
          <button onClick={handleLogout}>Logout</button>
          {error && <div className="error-message">{error}</div>}
        </div>
      </header>
      <div className="content-container">
        <aside className="team-list">
          <h2>Teams</h2>
          {/* Render teams */}
        </aside>
        <main className="project-list">
          {/* Render projects */}
          <AddProjectForm onAddProject={handleAddProject} />
        </main>
      </div>
        {/* PLACEHOLDER BUTTON TO NAVIGATE TO PROJECT PAGE, WILL FIX + CHANGE LATER*/}
        <div>
          <button onClick={() => navigate('/project')} className="project-button">
            <h3>PROJECT</h3>
          </button>
        </div>
    </div>
  );
};

export default UserHomepage;