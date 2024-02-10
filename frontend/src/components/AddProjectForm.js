// AddProjectForm.js
import React, { useState } from 'react';
import '../styles/UserHomepage.css';

const AddProjectForm = ({ onAddProject }) => {
  const [projectName, setProjectName] = useState('');

  const handleSubmit = (event) => {
    event.preventDefault();
    onAddProject(projectName);
    setProjectName(''); // Reset the input field after submission
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={projectName}
        onChange={(e) => setProjectName(e.target.value)}
        placeholder="Enter project name"
        required
      />
      <button className="add-project-button">Add Project</button>
    </form>
  );
};

export default AddProjectForm;