import React, { useState } from 'react';
import { getAuth } from 'firebase/auth'; 

const AddProjectModal = ({ isOpen, onClose, teams, updateProjectsList }) => {
  const [projectName, setProjectName] = useState('');
  const [selectedTeamId, setSelectedTeamId] = useState('');
  const [error, setError] = useState(''); // Error state to handle any errors
  const API_BASE_URL = process.env.REACT_APP_API_BASE_URL; // API endpoint

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    if (!selectedTeamId) {
      setError('Please select a team.');
      return;
    }
  
    // Find the selected team object from the teams array
    const selectedTeam = teams.find(team => team.teamID.toString() === selectedTeamId);
    if (!selectedTeam) {
      setError('Invalid team selected.');
      return;
    }
  
    try {
      const auth = getAuth();
      const idToken = await auth.currentUser.getIdToken();
      const response = await fetch(`${API_BASE_URL}/projects`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${idToken}`,
        },
        body: JSON.stringify({
          projectName,
          teamID: selectedTeamId,
          teamName: selectedTeam.teamName, // Include the teamName in the request body
        }),
      });
  
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to add project');
      }
  
      // Handle success
      setProjectName('');
      setSelectedTeamId('');
      onClose(); // Close the modal on success
      updateProjectsList(); // Invoke the callback here to update the project list in the parent component
    } catch (error) {
      console.error('Add Project Error:', error);
      setError(error.message);
    }
  };
  

  if (!isOpen) return null;

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <form onSubmit={handleSubmit}>
          <div>
            <label htmlFor="projectName">Project Name:</label>
            <input
              id="projectName"
              type="text"
              value={projectName}
              onChange={(e) => setProjectName(e.target.value)}
              required
            />
          </div>
          <div>
            <label htmlFor="teamSelection">Select Team:</label>
            <select
              id="teamSelection"
              value={selectedTeamId}
              onChange={(e) => setSelectedTeamId(e.target.value)}
              required
            >
              <option value="">Select a team</option>
              {teams.map((team) => (
                <option key={team.teamID} value={team.teamID}>
                  {team.teamName}
                </option>
              ))}
            </select>
          </div>
          {error && <p className="error">{error}</p>}
          <button type="submit">Add Project</button>
          <button type="button" onClick={onClose}>Cancel</button>
        </form>
      </div>
    </div>
  );
};

export default AddProjectModal;