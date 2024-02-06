import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/UserHomepage.css'; // Ensure this path is correct

const UserHomepage = () => {
  const [projects, setProjects] = useState([]);
  const [teams, setTeams] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    // Implement the logic to fetch projects and teams here
  }, []);

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
          <button onClick={handleAddProject}>Add Project</button>
          <button onClick={handleFilterTeam}>Team Filter</button>
          <button onClick={handleLogout}>Logout</button>
        </div>
      </header>
      <div className="content-container">
        <aside className="team-list">
          <h2>Teams</h2>
          {/* Render team list */}
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
