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
    </div>
  );
};

export default UserHomepage;


// import React, { useState } from 'react';
// import { useNavigate } from 'react-router-dom';
// import AddProjectForm from './AddProjectForm';
// import '../styles/UserHomepage.css';
// import BusyBeaverNoBG from '../assets/BusyBeaverNoBG.png';

// const UserHomepage = () => {
//   const [projects, setProjects] = useState([]);
//   // This is mock data that we will pull in from our API, used to show UI components
//   const [teams, setTeams] = useState([
//     { teamID: 1, teamName: 'Team Name 1', isTeamCreator: true, members: ['creator'] },
//     { teamID: 2, teamName: 'Team Name 2', isTeamCreator: true, members: ['creator'] },
//   ]);
//   const [teamName, setTeamName] = useState('');
//   const [selectedTeam, setSelectedTeam] = useState(null);
//   const [error, setError] = useState('');
//   const navigate = useNavigate();

//   const handleAddTeam = () => {
//     if (!teamName.trim()) {
//       setError('Team name cannot be empty');
//       return;
//     }
//     const newTeam = {
//       teamID: teams.length + 1,
//       teamName: teamName,
//       isTeamCreator: true,
//       members: ['creator'],
//     };
//     setTeams(prevTeams => [...prevTeams, newTeam]);
//     setTeamName('');
//     setError('');
//   };

//   const handleTeamNameChange = event => {
//     setTeamName(event.target.value);
//   };

//   const handleTeamClick = teamID => {
//     setSelectedTeam(teams.find(t => t.teamID === teamID));
//   };

//   const handleCloseTeam = () => {
//     setSelectedTeam(null);
//   };

//   const handleFilterTeam = () => {
//     // Logic to filter teams
//   };
  
//   const handleLogout = () => {
//     // Logic to sign out the user and redirect to splash page
//     navigate('/');
//   }

//   const handleDeleteTeam = teamID => {
//     if (window.confirm('Are you sure you want to delete this team?')) {
//       setTeams(teams.filter(team => team.teamID !== teamID));
//     }
//   };

//   const handleAddMember = () => {
//     if (!selectedTeam) {
//       alert('Please select a team first.');
//       return;
//     }
//     const newMemberName = window.prompt("Enter the new team member's name:");
//     if (!newMemberName) {
//       alert('Member name cannot be empty.');
//       return;
//     }
//     setTeams(teams.map(team => (team.teamID === selectedTeam.teamID ? { ...team, members: [...team.members, newMemberName] } : team)));
//   };

//   const handleRemoveMember = (teamID, memberToRemove) => {
//     setTeams(teams.map(team => (team.teamID === teamID ? { ...team, members: team.members.filter(member => member !== memberToRemove) } : team)));
//   };

//   const handleAddProject = projectName => {
//     const newProject = {
//       id: projects.length + 1,
//       name: projectName,
//       tasks: [],
//       teamID: selectedTeam ? selectedTeam.teamID : null,
//     };
//     setProjects([...projects, newProject]);
//   };

//   const renderProjects = () => {
//     return (
//       <>
//         <div className="project-list-header">
//           <h2 className="project-header">Projects</h2>
//           {/* If you want to add more details or a button here similar to the team section, add them here */}
//         </div>
//         {projects.map(project => (
//           <div key={project.id} className="project-card">
//             <h3>{project.name}</h3>
//             {/* Additional project details can be added here */}
//           </div>
//         ))}
//         <AddProjectForm onAddProject={handleAddProject} />
//       </>
//     );
//   };
  

//   const renderTeamMembers = team => {
//     return team.members.map((member, index) => (
//       <div key={index} className="team-member">
//         {member}
//         <button onClick={() => handleRemoveMember(team.teamID, member)} className="remove-member-button">
//           Remove
//         </button>
//       </div>
//     ));
//   };

//   const renderTeams = () => {
//     return teams.map(team => (
//       <div key={team.teamID} className={`team-card ${selectedTeam?.teamID === team.teamID ? 'selected' : ''}`}>
//         <div className="team-header">
//           <button onClick={() => handleTeamClick(team.teamID)} className="team-button">
//             {team.teamName}
//           </button>
//           {team.isTeamCreator && <button onClick={() => handleDeleteTeam(team.teamID)} className="delete-button">Delete Team</button>}
//           {selectedTeam?.teamID === team.teamID && <button className="close-button" onClick={handleCloseTeam}>X</button>}
//         </div>
//         {selectedTeam?.teamID === team.teamID && (
//           <div className="team-details-container">
//             {renderTeamMembers(team)}
//             <div className="team-actions">
//               <button onClick={handleAddMember}>Add Member</button>
//               <button>Leave Team</button>
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
//         <div className="user-homepage-header-card">
//           <h1>User Homepage</h1>
//         </div>
//         <div className="user-homepage-buttons">
//         <input 
//             type="text"
//             value={teamName}
//             onChange={handleTeamNameChange}
//             placeholder="Team Name"
//             className={error ? "input-error" : ""}
//           />
//           <button onClick={handleAddTeam}>Add Team</button>
//           <button onClick={handleFilterTeam}>Team Filter</button>
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
//           {renderProjects()}
//         </main>
//       </div>
//     </div>
//   );
// };

// export default UserHomepage;