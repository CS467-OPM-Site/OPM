import React, { useState } from 'react';
import '../styles/UserHomepageModalStyles.css'; // Reuse the CSS for consistent styling


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
    <div className="modal-backdrop"> {/* Updated class name for consistency */}
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
          <button type="submit">Submit</button> {/* Simplified for CSS consistency */}
          <button type="button" onClick={onClose}>Cancel</button> {/* Simplified for CSS consistency */}
        </form>
      </div>
    </div>
  );
};

export default AddTeamModal;
