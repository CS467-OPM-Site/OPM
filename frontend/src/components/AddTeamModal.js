import '../styles/AddTeamModal.css'; 
import React from 'react';

const AddTeamModal = ({ isOpen, onClose, onSubmit, children }) => {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal">
        <form onSubmit={onSubmit}>
          {children}
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