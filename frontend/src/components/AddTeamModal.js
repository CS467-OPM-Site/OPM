// Create a new file Modal.js in your components folder

import React from 'react';


const AddTeamModal = ({ isOpen, onClose, onSubmit, children }) => {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal">
        <button className="modal-close-button" onClick={onClose}>X</button>
        <form onSubmit={onSubmit}>
          {children}
          <button type="submit" className="modal-submit-button">Submit</button>
        </form>
      </div>
    </div>
  );
};

export default AddTeamModal;
