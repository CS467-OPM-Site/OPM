import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AddTaskForm from './AddTaskForm';
import '../styles/UserProjectPage.css';
import BusyBeaverNoBG from '../assets/BusyBeaverNoBG.png';
import Popup from 'reactjs-popup';
import Button from '@mui/material/Button';
import ButtonGroup from '@mui/material/ButtonGroup';
import Card from '@mui/material/Card';
import Typography from '@mui/material/Typography';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import { CardActionArea, CardContent } from '@mui/material';

function ProjectPage() {

    // Mock data to show UI filled out task cards and columns
    const [columns, setColumns] = useState([
      { columnID: 1, columnName: 'To-Do'},
      { columnID: 2, columnName: 'In Process'},
      { columnID: 3, columnName: 'Done'},
    ]);
    const [tasks, setTasks] = useState([
      { taskID: 1, columnID: 1, taskName: 'Task 1', taskDescription: 'This is me tasking away!', dueDate: '2024-02-23', taskPriority: 'High'},
      { taskID: 2, columnID: 1, taskName: 'Task 2', taskDescription: 'This is my task! And I will Task!', dueDate: '2024-02-26', taskPriority: 'High'},
      ]);
    const [columnName, setColumnName] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const [editingThisTask, setEditingTask] = useState(null);
    const [taskPopup, setTaskPopup] = useState(null);

    // Logic for signing out the user
    const handleLogout = () => {
        navigate('/');
    };

    const handleColumnNameChange = event => {
      setColumnName(event.target.value);
    };

    const handleEditTask = (editingTask) => {
      setEditingTask(editingTask);
    };

    const handleUpdateTask = (updatedTask) => {
      setTasks(tasks.map(task => task.taskID === updatedTask.taskID ? updatedTask : task));
      setEditingTask(null); // Reset editing task to null after update
    };

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

    const handleAddTask = (newTask, columnID) => {
      newTask.columnID = columnID;
      newTask.taskID = tasks.length + 1;
      setTasks(prevTasks => [...prevTasks, newTask]);
    };

    const handleDeleteTask = (taskID) => {
      setTasks(tasks.filter(task => task.taskID !== taskID));
    }

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
          {tasks.filter(task => task.columnID === columnID).map(task => (
            <Card sx={{ maxWidth: 220 }}>
            <CardActionArea onClick={() => {setTaskPopup(task.taskID)}}>
              <CardContent>
                <Typography gutterBottom variant="h5" component="div">
                  {task.taskName}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {task.taskDescription}
                </Typography>
              </CardContent>
            <Popup
              key={task.taskID}
              modal
              nested
              open={taskPopup===task.taskID}
            >
              {close => (
                <Card>
                    <Stack direction="row" spacing={1}>
                  {(() => {
                    switch (task.taskPriority){
                      case 'High':
                        return <Chip label="High" color="error" />
                      case 'Medium': 
                        return <Chip label="Medium" color="warning" />
                      case 'Low':
                        return <Chip label="Low" color="success" />
                      default:
                        return null
                    }
                  })()}
                    </Stack>
                  <Typography gutterBottom variant="h5" component="div" align='center'>
                    {task.taskName}
                  </Typography>
                  <div className="content">
                    <Typography variant="body2" color="text.secondary">
                      {task.taskDescription}
                    </Typography>
                  </div>
                  <ButtonGroup variant="filled">
                    <Button
                      variant = "contained"
                      className="popup-button"
                      onClick={() => {setTaskPopup(null); close(); }}>
                      Close
                    </Button>
                    <Button
                      className="popup-button"
                      variant = "contained"
                      onClick={() => handleDeleteTask(task.taskID)}>
                      Delete
                    </Button>
                    <Button
                      variant='contained'
                      className='popup-button'
                      onClick={() => handleEditTask(task)}>
                      Edit
                    </Button>
                    <AddTaskForm onAddTask={(newTask) => handleUpdateTask(newTask)} columnID={null} editingTask={task}/>
                  </ButtonGroup>
                </Card>
              )}
            </Popup>
            </CardActionArea>
            </Card>
          ))}
        </ul>
          <AddTaskForm onAddTask={(newTask) => handleAddTask(newTask, columnID)} columnID={columnID}/>
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