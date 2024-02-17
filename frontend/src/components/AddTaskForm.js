// AddTaskForm.js
import React, { useState } from 'react';
import '../styles/UserProjectPage.css';

const AddTaskForm = ({ onAddTask}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [taskName, setTaskName] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [description, setDescription] = useState('');

  // Event handler to open the popup window
  const openPopup = () => {
    setIsOpen(true);
  };

  // Event handler to close the popup window
  const closePopup = () => {
    setIsOpen(false);
  };

  // Event handler to handle form submission
  const handleSubmit = (event) => {
    event.preventDefault();
    // You can handle form submission here, for example, send data to a server or update state
    onAddTask(taskName);
    onAddTask(dueDate);
    onAddTask(description);
    // Reset form fields and close popup
    setTaskName('');
    setDueDate('');
    setDescription('');
    setIsOpen(false);
  };

  return (
    <div>
      <button onClick={openPopup}>Add Task</button>
      {isOpen && (
        <div className="popup-overlay">
          <div className="popup">
            <span className="close" onClick={closePopup}>&times;</span>
            <h2>Add Task</h2>
            <form onSubmit={handleSubmit}>
              <label>
                Task Name:
                <input
                  type="text"
                  value={taskName}
                  onChange={(e) => setTaskName(e.target.value)}
                  required
                />
              </label>
              <label>
                DueeeeeeeeeeeeeDate:
                <input
                  type="date"
                  value={dueDate}
                  onChange={(e) => setDueDate(e.target.value)}
                  required
                />
              </label>
              <label>
                Description:
                <textarea
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  required
                />
              </label>
              <button type="submit">Add</button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default AddTaskForm;
