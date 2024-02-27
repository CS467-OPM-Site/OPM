import React, { useState } from 'react';

function AddTeamModal({ isOpen, onClose, onAddTeam }) {
  const [teamName, setTeamName] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onAddTeam(teamName);
    setTeamName(''); // Reset input field
    onClose(); // Close modal
  };

  if (!isOpen) return null;

  return (
    <div className="modal">
      <div className="modal-content">
        <span className="close" onClick={onClose}>&times;</span>
        <form onSubmit={handleSubmit}>
          <label htmlFor="teamName">Team Name:</label>
          <input
            type="text"
            id="teamName"
            name="teamName"
            value={teamName}
            onChange={(e) => setTeamName(e.target.value)}
          />
          <button type="submit">Submit</button>
        </form>
      </div>
    </div>
  );
}
