import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import '../styles/UserHomepage.css';
import BusyBeaverNoBG from '../assets/BusyBeaverNoBG.png';

const UserHomepage = () => {
  const [projects, setProjects] = useState([]);
  const [teams, setTeams] = useState([]);
  const [teamName, setTeamName] = useState('');
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [teamMembers, setTeamMembers] = useState([]);
  const [loadingMembers, setLoadingMembers] = useState(false);
  const [membersError, setMembersError] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { currentUser } = useAuth();

  useEffect(() => {
    fetchTeams();
  }, []);

  useEffect(() => {
    if (selectedTeam) {
      fetchTeamMembers(selectedTeam.teamID);
    }
  }, [selectedTeam]);

  const fetchTeams = async () => {
    const API_ENDPOINT = 'https://opm-api.propersi.me/api/v1/teams';
    try {
      const response = await fetch(API_ENDPOINT, {
        headers: { 'Authorization': `Bearer ${currentUser.token}` },
      });
      if (!response.ok) throw new Error('Failed to fetch teams');
      const data = await response.json();
      setTeams(data.teams);
    } catch (error) {
      console.error('Fetch Teams Error:', error);
      setError('Failed to load teams.');
    }
  };

  const fetchTeamMembers = async (teamID) => {
    setLoadingMembers(true);
    setMembersError('');
    try {
      const response = await fetch(`https://opm-api.propersi.me/api/v1/teams/${teamID}/members`, {
        headers: { 'Authorization': `Bearer ${currentUser.token}` },
      });
      if (!response.ok) throw new Error('Failed to fetch team members');
      const data = await response.json();
      setTeamMembers(data.members);
    } catch (error) {
      console.error('Fetch Team Members Error:', error);
      setMembersError('Failed to load team members.');
    } finally {
      setLoadingMembers(false);
    }
  };

  const handleAddTeam = async () => {
    if (!teamName.trim()) {
      setError('Team name cannot be empty');
      return;
    }

    try {
      const response = await fetch('https://opm-api.propersi.me/api/v1/teams', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${currentUser.token}`,
        },
        body: JSON.stringify({ teamName }),
      });
      if (!response.ok) throw new Error('Failed to add team');
      const newTeam = await response.json();
      setTeams(prev => [...prev, newTeam]);
      setTeamName('');
      setError('');
    } catch (error) {
      console.error('Add Team Error:', error);
      setError('Failed to add team.');
    }
  };

  const handleAddMember = async (teamID) => {
    const memberName = prompt("Enter the new team member's name:");
    if (!memberName) {
      alert('Member name cannot be empty.');
      return;
    }

    try {
      const response = await fetch(`https://opm-api.propersi.me/api/v1/teams/${teamID}/members`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${currentUser.token}`,
        },
        body: JSON.stringify({ username: memberName }),
      });
      if (!response.ok) throw new Error('Failed to add member');
      fetchTeamMembers(teamID); // Refresh team members list
    } catch (error) {
      console.error('Add Member Error:', error);
      setError('Failed to add team member.');
    }
  };

  const handleRemoveMember = async (teamID, memberID) => {
    try {
      const response = await fetch(`https://opm-api.propersi.me/api/v1/teams/${teamID}/members/${memberID}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${currentUser.token}`,
        },
      });
      if (!response.ok) throw new Error('Failed to remove member');
      fetchTeamMembers(teamID); // Refresh team members list
    } catch (error) {
      console.error('Remove Member Error:', error);
      setError('Failed to remove team member.');
    }
  };

  const handleDeleteTeam = async (teamID) => {
    if (!window.confirm('Are you sure you want to delete this team?')) return;

    try {
      const response = await fetch(`https://opm-api.propersi.me/api/v1/teams/${teamID}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${currentUser.token}`,
        },
      });
      if (!response.ok) throw new Error('Failed to delete team');
      setTeams(prev => prev.filter(team => team.teamID !== teamID));
      setSelectedTeam(null);
      setTeamMembers([]); // Clear team members state
    } catch (error) {
      console.error('Delete Team Error:', error);
      setError('Failed to delete team.');
    }
  };

  const handleAddProject = async (projectName) => {
    if (!selectedTeam) {
      setError('Please select a team to add projects to.');
      return;
    }

    try {
      const response = await fetch(`https://opm-api.propersi.me/api/v1/teams/${selectedTeam.teamID}/projects`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${currentUser.token}`,
        },
        body: JSON.stringify({ name: projectName }),
      });
      if (!response.ok) throw new Error('Failed to add project');
      const newProject = await response.json();
      setProjects(prev => [...prev, newProject]);
    } catch (error) {
      console.error('Add Project Error:', error);
      setError('Failed to add project.');
    }
  };

  const handleLogout = () => {
    // Implement logout functionality
    navigate('/'); // Redirect to splash page after logout
  };

  const renderTeamMembers = () => {
    if (loadingMembers) return <div>Loading members...</div>;
    if (membersError) return <div className="error-message">{membersError}</div>;
    return teamMembers.map((member, index) => (
      <div key={index} className="team-member">
        {member.username}
        {/* Add button to remove member if needed */}
      </div>
    ));
  };

  const renderTeams = () => {
    return teams.map((team) => (
      <div key={team.teamID} className={`team-card ${selectedTeam?.teamID === team.teamID ? 'selected' : ''}`}>
        <div className="team-header">
          <button onClick={() => setSelectedTeam(team)} className="team-button">
            {team.teamName}
          </button>
          {team.isTeamCreator && (
            <button onClick={() => handleDeleteTeam(team.teamID)} className="delete-button">
              Delete Team
            </button>
          )}
          {selectedTeam?.teamID === team.teamID && (
            <button onClick={() => setSelectedTeam(null)} className="close-button">
              X
            </button>
          )}
        </div>
        {selectedTeam?.teamID === team.teamID && (
          <div className="team-details">
            <div className="team-members-list">
              {renderTeamMembers()}
            </div>
            <div className="team-actions">
              <button onClick={() => handleAddMember(team.teamID)}>Add Member</button>
              {/* Add more team actions if required */}
            </div>
          </div>
        )}
      </div>
    ));
  };

  return (
    <div className="user-homepage-container">
      <header className="user-homepage-header">
        <div className="busy-beaver-logo">
          <img src={BusyBeaverNoBG} alt="Busy Beaver" />
        </div>
        <h1>User Homepage</h1>
        <div className="user-homepage-buttons">
          <input
            type="text"
            value={teamName}
            onChange={(e) => setTeamName(e.target.value)}
            placeholder="Enter new team name"
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
          {renderTeams()}
        </aside>
        <main className="project-list">
          <form onSubmit={(e) => { e.preventDefault(); handleAddProject(e.target.elements.projectName.value); }}>
            <input
              name="projectName"
              type="text"
              placeholder="Enter project name"
              required
            />
            <button type="submit" className="add-project-button">Add Project</button>
          </form>
        </main>
      </div>
    </div>
  );
};

export default UserHomepage;





// import React, { useEffect, useState } from 'react';
// import { useNavigate } from 'react-router-dom';
// import { useAuth } from '../contexts/AuthContext'; // Adjust based on your project structure
// import '../styles/UserHomepage.css';
// import BusyBeaverNoBG from '../assets/BusyBeaverNoBG.png';

// const UserHomepage = () => {
//   const [projects, setProjects] = useState([]);
//   const [teams, setTeams] = useState([]);
//   const [teamName, setTeamName] = useState('');
//   const [selectedTeam, setSelectedTeam] = useState(null);
//   const [teamMembers, setTeamMembers] = useState([]);
//   const [loadingMembers, setLoadingMembers] = useState(false);
//   const [membersError, setMembersError] = useState('');
//   const [error, setError] = useState('');
//   const navigate = useNavigate();
//   const { currentUser } = useAuth();

//   useEffect(() => {
//     fetchTeams();
//   }, []);

//   useEffect(() => {
//     if (selectedTeam) {
//       fetchTeamMembers(selectedTeam.teamID);
//     }
//   }, [selectedTeam]);

//   const fetchTeams = async () => {
//     const API_ENDPOINT = 'https://opm-api.propersi.me/api/v1/teams';
//     try {
//       const response = await fetch(API_ENDPOINT, {
//         headers: { 'Authorization': `Bearer ${currentUser.token}` },
//       });
//       if (!response.ok) throw new Error('Failed to fetch teams');
//       const data = await response.json();
//       setTeams(data.teams);
//     } catch (error) {
//       console.error('Fetch Teams Error:', error);
//       setError('Failed to load teams.');
//     }
//   };

//   const fetchTeamMembers = async (teamID) => {
//     setLoadingMembers(true);
//     setMembersError('');
//     try {
//       const response = await fetch(`https://opm-api.propersi.me/api/v1/teams/${teamID}/members`, {
//         headers: { 'Authorization': `Bearer ${currentUser.token}` },
//       });
//       if (!response.ok) throw new Error('Failed to fetch team members');
//       const data = await response.json();
//       setTeamMembers(data.members);
//     } catch (error) {
//       console.error('Fetch Team Members Error:', error);
//       setMembersError('Failed to load team members.');
//     } finally {
//       setLoadingMembers(false);
//     }
//   };

//   const handleAddTeam = async () => {
//     if (!teamName.trim()) {
//       setError('Team name cannot be empty');
//       return;
//     }

//     try {
//       const response = await fetch('https://opm-api.propersi.me/api/v1/teams', {
//         method: 'POST',
//         headers: {
//           'Content-Type': 'application/json',
//           'Authorization': `Bearer ${currentUser.token}`,
//         },
//         body: JSON.stringify({ teamName }),
//       });
//       if (!response.ok) throw new Error('Failed to add team');
//       const newTeam = await response.json();
//       setTeams(prev => [...prev, newTeam]);
//       setTeamName('');
//       setError('');
//     } catch (error) {
//       console.error('Add Team Error:', error);
//       setError('Failed to add team.');
//     }
//   };

//   const handleAddMember = async (teamID) => {
//     const memberName = prompt("Enter the new team member's name:");
//     if (!memberName) {
//       alert('Member name cannot be empty.');
//       return;
//     }

//     try {
//       const response = await fetch(`https://opm-api.propersi.me/api/v1/teams/${teamID}/members`, {
//         method: 'POST',
//         headers: {
//           'Content-Type': 'application/json',
//           'Authorization': `Bearer ${currentUser.token}`,
//         },
//         body: JSON.stringify({ username: memberName }),
//       });
//       if (!response.ok) throw new Error('Failed to add member');
//       fetchTeamMembers(teamID); // Refresh team members list
//     } catch (error) {
//       console.error('Add Member Error:', error);
//       setError('Failed to add team member.');
//     }
//   };

//   const handleRemoveMember = async (teamID, memberID) => {
//     try {
//       const response = await fetch(`https://opm-api.propersi.me/api/v1/teams/${teamID}/members/${memberID}`, {
//         method: 'DELETE',
//         headers: {
//           'Authorization': `Bearer ${currentUser.token}`,
//         },
//       });
//       if (!response.ok) throw new Error('Failed to remove member');
//       fetchTeamMembers(teamID); // Refresh team members list
//     } catch (error) {
//       console.error('Remove Member Error:', error);
//       setError('Failed to remove team member.');
//     }
//   };

//   const handleDeleteTeam = async (teamID) => {
//     if (!window.confirm('Are you sure you want to delete this team?')) return;

//     try {
//       const response = await fetch(`https://opm-api.propersi.me/api/v1/teams/${teamID}`, {
//         method: 'DELETE',
//         headers: {
//           'Authorization': `Bearer ${currentUser.token}`,
//         },
//       });
//       if (!response.ok) throw new Error('Failed to delete team');
//       setTeams(prev => prev.filter(team => team.teamID !== teamID));
//       setSelectedTeam(null);
//       setTeamMembers([]); // Clear team members state
//     } catch (error) {
//       console.error('Delete Team Error:', error);
//       setError('Failed to delete team.');
//     }
//   };

//   const AddProjectForm = ({ onAddProject }) => {
//     const [projectName, setProjectName] = useState('');
  
//     const handleSubmit = (event) => {
//       event.preventDefault();
//       onAddProject(projectName);
//       setProjectName(''); // Reset the input field after submission
//     };
  
//     return (
//       <form onSubmit={handleSubmit}>
//         <input
//           type="text"
//           value={projectName}
//           onChange={(e) => setProjectName(e.target.value)}
//           placeholder="Enter project name"
//           required
//         />
//         <button className="add-project-button">Add Project</button>
//       </form>
//     );
//   };

//   const handleLogout = () => {
//     // Implement logout functionality
//     navigate('/'); // Redirect to splash page after logout
//   };

//   const renderTeamMembers = () => {
//     if (loadingMembers) return <div>Loading members...</div>;
//     if (membersError) return <div className="error-message">{membersError}</div>;
//     return (
//       <div>
//         {teamMembers.map((member, index) => (
//           <div key={index} className="team-member">
//             {member.username}
//           </div>
//         ))}
//       </div>
//     );
//   };

//   const renderTeams = () => {
//     return teams.map((team) => (
//       <div key={team.teamID} className={`team-card ${selectedTeam?.teamID === team.teamID ? 'selected' : ''}`}>
//         <div className="team-header">
//           <button onClick={() => setSelectedTeam(team)} className="team-button">
//             {team.teamName}
//           </button>
//           {team.isTeamCreator && (
//             <button onClick={() => handleDeleteTeam(team.teamID)} className="delete-button">
//               Delete Team
//             </button>
//           )}
//           {selectedTeam?.teamID === team.teamID && (
//             <button onClick={() => setSelectedTeam(null)} className="close-button">
//               X
//             </button>
//           )}
//         </div>
//         {selectedTeam?.teamID === team.teamID && (
//           <div className="team-details">
//             <div className="team-members-list">
//               {renderTeamMembers()}
//             </div>
//             <div className="team-actions">
//               <button onClick={() => handleAddMember(team.teamID)}>Add Member</button>
//             </div>
//           </div>
//         )}
//       </div>
//     ));
//   };

//   return (
//     <div className="user-homepage-container">
//       <header className="user-homepage-header">
//         <div className="busy-beaver-logo">
//           <img src={BusyBeaverNoBG} alt="Busy Beaver" />
//         </div>
//         <h1>User Homepage</h1>
//         <div className="user-homepage-buttons">
//           <input
//             type="text"
//             value={teamName}
//             onChange={(e) => setTeamName(e.target.value)}
//             placeholder="Enter new team name"
//             className={error ? "input-error" : ""}
//           />
//           <button onClick={handleAddTeam}>Add Team</button>
//           <button onClick={handleLogout}>Logout</button>
//           {error && <div className="error-message">{error}</div>}
//         </div>
//       </header>
//       <div className="content-container">
//         <aside className="team-list">
//           <h2>Teams</h2>
//           {renderTeams()}
//         </aside>
//         <main className="project-list">
//           <AddProjectForm onAddProject={(projectName) => console.log(projectName)} />
//         </main>
//       </div>
//     </div>
//   );
// };

// export default UserHomepage;