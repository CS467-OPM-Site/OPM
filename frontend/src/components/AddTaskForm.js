import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { CircularProgress, FormControl, FormLabel, LinearProgress, Radio } from '@mui/material';
import { Button, Typography, TextField, RadioGroup, FormControlLabel } from '@mui/material';
import { ArrowBack, Cancel, LibraryAdd } from '@mui/icons-material';
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { fetchProjectUsers } from '../services/projects';

const styleObjectTextFields = () => {
  const style = {
      color: "#000000",
      background: "rgba(129, 178, 154, 0.85)",
      borderColor: "#81B29A",
      borderRadius: "4px"
  }

  return style;
}


const AddTaskForm = ( { projectID, columnID, setColumnIDtoAddTaskTo } ) => {
  const [projectUsers, setProjectUsers] = useState([]);
  const [isLoadingUsers, setIsLoadingUsers] = useState(true);
  console.log(`Column to add task to is: ${columnID}`);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await fetchProjectUsers(projectID);
        const responseJSON = await response.json();
        setProjectUsers([...responseJSON.users]);
        setIsLoadingUsers(false);
      } catch (error) {
        console.log("Unable to fetch project users...");
      }
    }

    fetchUsers();
  }, []);

  const returnToProjectPage = () => {
    setColumnIDtoAddTaskTo(-1);
    navigate(`/projects/${projectID}`, { state: { projectID: `${projectID}` } })
  }
  console.log(projectUsers);

  return <div className="add-task-form-container">
            <div id="back-to-project-link-add-task-form">
              <div id="back-to-project-container" onClick={returnToProjectPage}>
                <ArrowBack id="back-to-project-arrow" />
                <Typography>Back to Project</Typography>
              </div>
            </div>
            <TextField 
              className="add-task-text-field"
              label="Task Title*" 
              variant="filled"
              color="success"
              sx={{ 
                input: styleObjectTextFields,
                label: { color: "#000000" },
                "& .MuiFilledInput-root::after": { borderColor: "rgba(129, 255, 154, 0.6)" }
              }}/>
            <TextField 
              className="add-task-text-field"
              label="Task Description (optional)" 
              color="success"
              variant="filled" 
              multiline
              minRows={4}
              sx={{
                label: { color: "#000000" },
                fieldset: { color: "#000000" },
                "& .MuiFilledInput-root": styleObjectTextFields,
                "& .MuiFilledInput-root:hover": {...styleObjectTextFields, background: "rgba(129, 178, 154, 1)"},
                "& .MuiFilledInput-root::after": { borderColor: "rgba(129, 255, 154, 0.6)" }
              }}/>
            <FormControl id="priority-form-container">
              <FormLabel id="task-priority-radio-buttons-group-label">Priority</FormLabel>
              <RadioGroup 
                  defaultValue="None" 
                  className="add-task-radio-group-container"
                  name="task-priority-radio-buttons-group" 
                  aria-labelledby='task-priority-radio-buttons-group-label'
                  id="task-priority-radio-group-container"
                  row>
                <FormControlLabel value="None" control={<Radio color="secondary" />} label="None"/>
                <FormControlLabel value="Low" control={<Radio color="secondary"/>} label="Low"/>
                <FormControlLabel value="Medium" control={<Radio color="secondary"/>} label="Medium"/>
                <FormControlLabel value="High" control={<Radio color="secondary"/>} label="High"/>
              </RadioGroup>
            </FormControl>
            <FormControl id="due-date-form-container">
              <FormLabel id="task-due-date-label">Due Date (optional)</FormLabel>
              <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DatePicker className="add-task-due-date-calendar" color="success" />
              </LocalizationProvider>
            </FormControl>
            <FormControl id="assigned-to-form-container">
              <div id="assigned-to-label-container">
                <FormLabel id="task-assigned-to-radio-buttons-group-label">Assigned To (optional)</FormLabel>
                {isLoadingUsers && <LinearProgress style={{width: "100%"}} color="success" />}
              </div>
              {!isLoadingUsers &&
              <div id="assigned-to-form-container-scrollable">
                <RadioGroup 
                    defaultValue="None" 
                    className="add-task-radio-group-container"
                    name="assigned-to-radio-buttons-group" 
                    aria-labelledby='task-assigned-to-radio-buttons-group-label'
                    >
                  <FormControlLabel value="None" control={<Radio color="secondary" />} label="None"/>
                {projectUsers.map(user => (
                  <FormControlLabel key={user.userID} value={user.userID} control={<Radio color="secondary" />} label={user.username}/>
                ))}
                </RadioGroup>
              </div>
              }
            </FormControl>
            <div className="add-cancel-task-button-container">
              <Button 
                variant="contained" 
                color="error" 
                size="medium" 
                id="cancel-add-task-confirm-button"
                onClick={returnToProjectPage}
                startIcon={<Cancel />}>Cancel</Button>
              <Button 
                variant="contained" 
                color="success" 
                size="medium" 
                id="add-task-confirm-button"
                startIcon={<LibraryAdd />}>Add Task!</Button>
            </div>
          </div>
}

export default AddTaskForm;
