import React, { useState } from 'react';
import '../styles/UserProjectPage.css';
import Popup from 'reactjs-popup';
import Card from '@mui/material/Card';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormControl from '@mui/material/FormControl';
import FormLabel from '@mui/material/FormLabel';
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';

const AddTaskForm = ({ onAddTask, columnID, editingTask }) => {

  const [taskName, setTaskName] = useState(editingTask ? editingTask.taskName : '');
  const [dueDate, setDueDate] = useState(editingTask ? editingTask.dueDate : '');
  const [taskDescription, setTaskDescription] = useState(editingTask ? editingTask.taskDescription : '');
  const [taskPriority, setTaskPriority] = useState(editingTask ? editingTask.taskPriority : '');

  const handleSubmit = (event, closePopup) => {
    event.preventDefault();

    const newTask = {
      taskID: editingTask ? editingTask.taskID : 1, 
      columnID: editingTask ? editingTask.columnID : columnID, 
      taskName: taskName,
      taskDescription: taskDescription,
      dueDate: dueDate, 
      taskPriority: taskPriority
    };


    onAddTask(newTask);

    // Reset form fields
    setTaskName('');
    setDueDate('');
    setTaskDescription('');
    setTaskPriority('');

    // Close the popup
    closePopup();
  };

  

  return (
    <div>
      <Popup 
        trigger={<button>+</button>}
        modal 
        nested>
        {close => (
          <Card>                  
            <Typography gutterBottom variant="h5" component="div" align='center'>
              New Task
            </Typography>
          <form onSubmit={(event) => handleSubmit(event, close)}>
            <label>
              <TextField
                id="outlined-static"
                value={taskName}
                onChange={(e) => setTaskName(e.target.value)}
                required
                label="Task Title"
              />
            </label>
            <label>
              <TextField
                value={taskDescription}
                onChange={(e) => setTaskDescription(e.target.value)}
                required
                id="outlined-multiline-static"
                label="Task Description"
                multiline
                rows={4}
              />
            </label>
            <label>
              DueDate:
              <input
                type="date"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
              />
            </label>
            <FormControl>
              <FormLabel >Priority</FormLabel>
              <RadioGroup
                name="controlled-radio-buttons-group"
                value={taskPriority}
                onChange={(e) => setTaskPriority(e.target.value)}
              >
                <FormControlLabel value="High" control={<Radio />} label="High" />
                <FormControlLabel value="Medium" control={<Radio />} label="Medium" />
                <FormControlLabel value="Low" control={<Radio />} label="Low" />
              </RadioGroup>
            </FormControl>
            <Button type="submit" variant = "contained">Add</Button>
          </form>
          </Card>
        )}
      </Popup>
    </div>
  );
};

export default AddTaskForm;
