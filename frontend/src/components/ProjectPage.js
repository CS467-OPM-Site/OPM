import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AddTaskForm from './AddTaskForm';
import '../styles/UserProjectPage.css';
import BusyBeaverNoBG from '../assets/BusyBeaverNoBG.png';
//import ViewTaskFrom from './ViewTaskForm';

const ProjectPage = () => {
    // Mock data to show UI filled out task cards and columns
    const [columns, setColumns] = useState([
      { columnID: 1, columnName: 'To-Do'},
      { columnID: 2, columnName: 'In Process'},
      { columnID: 3, columnName: 'Done'},
    ]);
    const [tasks, setTasks] = useState([
      { taskID: 1, columnID: 1, taskName: 'Task 1', taskDescription: 'This is me tasking away!'},
      { taskID: 2, columnID: 1, taskName: 'Task 2', taskDescription: 'This is my task! And I will Task!'},
      ]);
    const [columnName, setColumnName] = useState('');
    const [taskName, setTaskName] = useState('');
    const [error, setError] = useState('');
    const [taskDescription, setTaskDescription] = useState('');
    const navigate = useNavigate();

    // Logic for signing out the user
    const handleLogout = () => {
        navigate('/');
    };

    const handleColumnNameChange = event => {
      setColumnName(event.target.value);
    };

    const handleTasksNameChange = event => {
    }

    const handleAddColumn = () => {
      // Return error if user tries to input empty column name
      if (!columnName.trim()) {
        setError('Column name cannot be empty');
        return;
      };
      const newColumn = {
        columnID: columns.length + 1,
        columnName: columnName
      };
      setColumns(prevColumns => [...prevColumns, newColumn]);
      setColumnName('');
      setError('');
    };

    const handleAddTask = () => {
      // Return error if user tries to input empty column name
      if (!taskName.trim()) {
        setError('Task name cannot be empty');
        return;
      };
      if (!taskDescription.trim()) {
        setError('Task description cannot be empty');
        return;
      };
      const newTask = {
        taskID: tasks.length + 1,
        taskName: taskName,
        taskDescription: taskDescription,
        columnID: 1
      };
      setTasks(prevTasks => [...prevTasks, newTask]);
      setTaskName('');
      setError('');
      setTaskDescription('');
      {renderTasks()}
    };

    //create a pop-up in the middle of the screen that shows more detail about the task
    const handleViewTask = () => {
    }

    // TO DO: NEED TO CREATE OWN CSS STYLE FOR PROJECTS, COLUMNS, AND TASKS
    const renderColumns = () => {
        return (
          <div style={{display:'flex'}}>
            {columns.map(column => (
              <div key={column.columnID} className="column-card">
                <h3 className='column-title'>{column.columnName}</h3>
                  {renderTasks(column.columnID)}
              </div>
            ))}
          </div>
        );
      };

    const renderTasks = (columnID) => {
      return (
        <>
        <ul>
          {tasks.map((task) => {
            if (task.columnID === columnID) {
              return <h3 key={task.columnID}>{task.taskName}</h3>;
            }
            return null;
          })}
        </ul>
          <AddTaskForm onAddTask={handleAddTask} />
        </>
      );
    };

    return (
      <div className="user-homepage-container">
        <header className="user-homepage-header">
          <div className="busy-beaver-logo">
            <img src={BusyBeaverNoBG} alt="Busy Beaver" />
          </div>
          <div className="user-homepage-header-card">
            <h1>User ProjectPage</h1>
          </div>
          <div className="user-homepage-buttons">
              <input
              type="text"
              value={columnName}
              onChange={handleColumnNameChange}
              placeholder="Column Name"
              className={error ? "input-error" : ""}
              />
              <button onClick={handleAddColumn}>Add Column</button>
              <button onClick={handleLogout}>Logout</button>
              {/*error && <div className="error-message">{error}</div>*/}
          </div>
        </header>
        <div className="column-list">
            {renderColumns()} 
        </div>
      </div>
    );
}

export default ProjectPage;