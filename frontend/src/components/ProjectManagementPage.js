import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import AddTaskForm from './AddTaskForm';
import '../styles/ProjectManagementPage.css';
import BusyBeaverNoBG from '../assets/BusyBeaverNoBG.png';
import Popup from 'reactjs-popup';
import { Button, ButtonGroup, Card, Typography, Chip, Stack, CardActionArea, CardContent } from '@mui/material';

const ProjectManagementPage = () => {
  const { projectId } = useParams();
  const navigate = useNavigate();

  // State management
  const [columns, setColumns] = useState([
    { columnID: 1, columnName: 'To-Do' },
    { columnID: 2, columnName: 'In Process' },
    { columnID: 3, columnName: 'Done' },
  ]);
  const [tasks, setTasks] = useState([
    // Sample tasks, you might fetch these from a server based on projectId
  ]);
  const [columnName, setColumnName] = useState('');
  const [error, setError] = useState('');
  const [editingTask, setEditingTask] = useState(null);
  const [taskPopup, setTaskPopup] = useState(null);

  // Event handlers
  const handleNavigateToHome = () => navigate('/home');
  const handleLogout = () => navigate('/');
  const handleColumnNameChange = (event) => setColumnName(event.target.value);
  const handleAddColumn = () => {
    if (!columnName.trim()) {
      setError('Column name cannot be empty');
      return;
    }
    const newColumn = { columnID: columns.length + 1, columnName };
    setColumns(prevColumns => [...prevColumns, newColumn]);
    setColumnName('');
    setError('');
  };
  const handleAddTask = (newTask, columnID) => {
    newTask.columnID = columnID;
    newTask.taskID = tasks.length + 1;
    setTasks(prevTasks => [...prevTasks, newTask]);
  };
  const handleEditTask = (task) => setEditingTask(task);
  const handleUpdateTask = (updatedTask) => {
    setTasks(tasks.map(task => task.taskID === updatedTask.taskID ? updatedTask : task));
    setEditingTask(null);
  };
  const handleDeleteTask = (taskID) => setTasks(tasks.filter(task => task.taskID !== taskID));

  // Rendering functions
  const renderTasks = (columnID) => tasks.filter(task => task.columnID === columnID).map(task => (
    <Card key={task.taskID} sx={{ maxWidth: 345, margin: 2 }}>
      <CardActionArea onClick={() => setTaskPopup(task.taskID)}>
        <CardContent>
          <Typography gutterBottom variant="h5" component="div">
            {task.taskName}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {task.taskDescription}
          </Typography>
          {/* Task priority indicator */}
          <Stack direction="row" spacing={1}>
            <Chip label={task.taskPriority} color={task.taskPriority === 'High' ? 'error' : task.taskPriority === 'Medium' ? 'warning' : 'success'} />
          </Stack>
        </CardContent>
      </CardActionArea>
    </Card>
  ));

  const renderColumns = () => columns.map(column => (
    <div key={column.columnID} style={{ flex: 1, padding: 8 }}>
      <Typography variant="h6" gutterBottom component="div">
        {column.columnName}
      </Typography>
      {renderTasks(column.columnID)}
      <AddTaskForm onAddTask={(newTask) => handleAddTask(newTask, column.columnID)} />
    </div>
  ));

  return (
    <div className="user-homepage-container">
      <header className="user-homepage-header">
        <div className="busy-beaver-logo" onClick={handleNavigateToHome} style={{ cursor: 'pointer' }}>
          <img src={BusyBeaverNoBG} alt="Busy Beaver Logo" />
        </div>
        <div className="user-homepage-header-card">
          <Typography variant="h4" component="h1">
            Project Management - {projectId}
          </Typography>
        </div>
        <div className="user-homepage-buttons">
          <input type="text" value={columnName} onChange={handleColumnNameChange} placeholder="Column Name" className={error ? "input-error" : ""} />
          <Button variant="contained" onClick={handleAddColumn} style={{ marginRight: 8 }}>Add Column</Button>
          <Button variant="contained" color="error" onClick={handleLogout}>Logout</Button>
          {error && <div className="error-message">{error}</div>}
        </div>
      </header>
      <div style={{ display: 'flex', justifyContent: 'center', flexWrap: 'wrap' }}>
        {renderColumns()}
      </div>
    </div>
  );
};

export default ProjectManagementPage;
