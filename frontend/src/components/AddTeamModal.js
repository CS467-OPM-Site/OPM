import '../styles/AddTeamModal.css';
import React, { useState } from 'react';

const AddTeamModal = ({ isOpen, onClose, onSubmit }) => {
  const [teamName, setTeamName] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(teamName); // Pass the teamName to the onSubmit function
    setTeamName(''); // Reset the input field
    onClose(); // Close the modal
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal">
        <form onSubmit={handleSubmit}>
          <div>
            <label htmlFor="teamName">Team Name:</label>
            <input
              id="teamName"
              type="text"
              value={teamName}
              onChange={(e) => setTeamName(e.target.value)}
              required
            />
          </div>
          <div className="modal-actions">
            <button type="submit" className="modal-submit-button">Submit</button>
            <button type="button" onClick={onClose} className="modal-cancel-button">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddTeamModal;
