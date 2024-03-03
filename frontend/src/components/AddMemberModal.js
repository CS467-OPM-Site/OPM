// AddMemberModal.js
import '../styles/AddProjectModal.css'; // Reuse the CSS for consistent styling
import React, { useState } from 'react';

const AddMemberModal = ({ isOpen, onClose, onSubmit }) => {
  const [memberName, setMemberName] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(memberName);
    setMemberName(''); // Reset the input field
    onClose(); // Close the modal
  };

  if (!isOpen) return null;

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <form onSubmit={handleSubmit}>
          <div>
            <label htmlFor="memberName">Member Name:</label>
            <input
              id="memberName"
              type="text"
              value={memberName}
              onChange={(e) => setMemberName(e.target.value)}
              required
            />
          </div>
          <button type="submit">Add Member</button>
          <button type="button" onClick={onClose}>Cancel</button>
        </form>
      </div>
    </div>
  );
};

export default AddMemberModal;
