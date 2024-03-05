import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FormControl, FormLabel, LinearProgress, Radio } from '@mui/material';
import { Button, Typography, TextField, RadioGroup, FormControlLabel } from '@mui/material';
import { ArrowBack, Cancel, LibraryAdd } from '@mui/icons-material';
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { fetchProjectUsers } from '../services/projects';
import dayjs  from 'dayjs';
import { addTask } from '../services/tasks';

const TITLE_REQUIREMENTS = "Task title must be 3-50 characters";
const DESCRIPTION_REQUIREMENTS = "Description must be 500 characters or less";

const AddTaskForm = ( { projectID, columnID, setColumnIDtoAddTaskTo, columns, setColumns, setIsLoading } ) => {
  const [projectUsers, setProjectUsers] = useState([]);
  const [isLoadingUsers, setIsLoadingUsers] = useState(true);
  const [taskTitle, setTaskTitle] = useState('');
  const [taskDescription, setTaskDescription] = useState('');
  const [taskPriority, setTaskPriority] = useState('None');
  const [taskDueDate, setTaskDueDate] = useState(null);
  const [assignedTo, setAssignedTo] = useState('None');
  const [taskTitleError, setTaskTitleError] = useState(false);
  const [taskDescriptionError, setTaskDescriptionError] = useState(false);
  const [taskDueDateError, setTaskDueDateError] = useState(null);
  const [generalError, setGeneralError] = useState('');
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

  const handleValidatingOnAddTask = () => {
    let invalidInput = false;
    if (taskTitle === null || taskTitle.length < 3 || taskTitle.length > 50) {
      setTaskTitleError(true);
      invalidInput = true;
    } else {
      setTaskTitleError(false);
    }

    if (taskDescription !== null && taskDescription.length > 500) {
      setTaskDescriptionError(true);
      invalidInput = true;
    } else {
      setTaskDescriptionError(false);
    }

    if (taskDueDate !== null) {
      const today = new dayjs();
      if (today.isAfter(taskDueDate, 'day')) {
        setTaskDueDateError("disablePast");
        invalidInput = true;
      } else {
        setTaskDueDateError(null);
      }
    }

    if (invalidInput) return;

    handleAddingTask();
  }

  const handleAddingTask = async () => {
    const newTaskDetails = {
      title: taskTitle,
      description: (taskDescription === null || taskDescription.trim() === '') ? null: taskDescription,
      columnID: columnID,
      assignedTo: assignedTo === "None" ? null : assignedTo,
      dueDate: taskDueDate === null ? null: taskDueDate.format('YYYY-MM-DD'),
      priority: taskPriority
    }

    setIsLoading(true);
    const response = await addTask(projectID, newTaskDetails);
    const responseJSON = await response.json();
    setIsLoading(false);

    switch (response.status) {
      case 201: {
        addTaskToColumn(responseJSON);
        setColumnIDtoAddTaskTo(-1);
        return;
      }
      default: {
        if (responseJSON !== null && "message" in responseJSON) {
          setGeneralError(responseJSON.message);
        }
        setGeneralError("Unable to add task");
      }
    }

    setTimeout(() => {
      setGeneralError('');
    }, 5005);
  }

  const addTaskToColumn = (newTask) => {
    // Do some cleanup from RestAPI response for missing/unnecessary fields
    let refinedNewTask = {...newTask, comments: 0};
    delete refinedNewTask.columnID;
    delete refinedNewTask.description;

    // Create new shallow copy of columns and tasks
    const newColumns = columns.map(column => ({ ...column, tasks: [...column.tasks] }));

    const currentColumnIndex = newColumns.find(column => column.columnID === columnID).columnIndex;
    newColumns[currentColumnIndex].tasks.push(refinedNewTask);

    setColumns(newColumns);
  }

  const setTitleTextClass = () => {
    let titleClassName = "add-task-title-field";
    if (taskTitleError) {
      titleClassName += " invalid-task-text-entry";
    } else {
      titleClassName += " valid-task-text-entry";
    }
    return titleClassName;
  }

  const setDescriptionTextClass = () => {
    let descriptionClassName = "add-task-description-field";
    if (taskDescriptionError) {
      descriptionClassName += " invalid-task-text-entry";
    } else {
      descriptionClassName += " valid-task-text-entry";
    }
    return descriptionClassName;
  }

  const descriptionErrorText = () => {
    if (taskDescription === null) {
      setTaskDescriptionError(false);
      return;
    }
    return `${DESCRIPTION_REQUIREMENTS}. Current count: ${taskDescription.length}`;
  }

  const setTaskDueDateClass = () => {
    let dueDateClassName = "add-task-due-date-calendar";
    if (taskDueDateError !== null && taskDueDateError === "disablePast") {
      dueDateClassName += " invalid-due-date";
    } else {
      dueDateClassName += " valid-due-date";
    }
    return dueDateClassName;
  }

  const setDueDateHelperTextErrorMessage = useMemo(() => {
    switch (taskDueDateError) {
      case "disablePast": {
        return "Please enter a valid date starting today or after";
      }
      case "maxDate": {
        return "Please enter a valid date starting today or after";
      }
      default: return null;
    }
  }, [taskDueDateError]);

  return <div className="add-task-form-container">
            <div id="back-to-project-link-add-task-form">
              <div id="back-to-project-container" onClick={returnToProjectPage}>
                <ArrowBack id="back-to-project-arrow" />
                <Typography>Back to Project</Typography>
              </div>
            </div>
            {(generalError !== '') &&
              <Typography id="add-task-general-error">{generalError}</Typography>
            }
            <div className="add-task-form-scrollable-container">
              <TextField 
                className={setTitleTextClass()}
                label="Task Title" 
                variant="filled"
                color="success"
                value={taskTitle}
                error={taskTitleError}
                helperText={taskTitleError && TITLE_REQUIREMENTS}
                onChange={(e) => {
                  setTaskTitle(e.target.value.trim() === '' ? '' : e.target.value)}
                }
                required
                sx={{ 
                  label: { color: "#000000" },
                  "& .MuiFilledInput-root::after": { borderColor: "rgba(129, 255, 154, 0.6)" }
                }}/>
              <TextField 
                className={setDescriptionTextClass()}
                label="Task Description (optional)" 
                color="success"
                variant="filled" 
                multiline
                minRows={4}
                maxRows={8}
                value={taskDescription}
                error={taskDescriptionError}
                helperText={taskDescriptionError && descriptionErrorText()}
                onChange={(e) => {
                    setTaskDescription(e.target.value.trim() === '' ? null : e.target.value)}
                }
                sx={{
                  label: { color: "#000000" },
                  fieldset: { color: "#000000" },
                  "& .MuiFilledInput-root::after": { borderColor: "rgba(129, 255, 154, 0.6)" }
                }}/>
              <FormControl id="priority-form-container">
                <FormLabel id="task-priority-radio-buttons-group-label">Priority</FormLabel>
                <RadioGroup 
                    className="add-task-radio-group-container"
                    name="task-priority-radio-buttons-group" 
                    aria-labelledby='task-priority-radio-buttons-group-label'
                    id="task-priority-radio-group-container"
                    value={taskPriority}
                    onChange={(e) => {setTaskPriority(e.target.value)}}
                    required
                    row>
                  <FormControlLabel key="None" value="None" control={<Radio color="secondary" />} label="None"/>
                  <FormControlLabel value="Low" control={<Radio color="secondary"/>} label="Low"/>
                  <FormControlLabel value="Medium" control={<Radio color="secondary"/>} label="Medium"/>
                  <FormControlLabel value="High" control={<Radio color="secondary"/>} label="High"/>
                </RadioGroup>
              </FormControl>
              <FormControl id="due-date-form-container">
                <FormLabel id="task-due-date-label">Due Date (optional)</FormLabel>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                  <DatePicker 
                    className={setTaskDueDateClass()}
                    color="success" 
                    disablePast
                    onError={(newDateError) => setTaskDueDateError(newDateError)}
                    slotProps={{
                      textField: {
                        helperText: setDueDateHelperTextErrorMessage
                      },
                    }}
                    value={taskDueDate}
                    onChange={(newDate) => setTaskDueDate(newDate)}
                    />
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
                      className="add-task-radio-group-container"
                      name="assigned-to-radio-buttons-group" 
                      value={assignedTo}
                      onChange={(e) => {setAssignedTo(e.target.value)}}
                      aria-labelledby='task-assigned-to-radio-buttons-group-label'
                      >
                    <FormControlLabel key="NoUser" value="None" control={<Radio color="secondary" />} label="None" aria-selected/>
                  {projectUsers.map(user => (
                    <FormControlLabel key={user.userID} value={user.userID} control={<Radio color="secondary" />} label={user.username}/>
                  ))}
                  </RadioGroup>
                </div>
                }
              </FormControl>
            </div>
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
                onClick={handleValidatingOnAddTask}
                startIcon={<LibraryAdd />}>Add Task!</Button>
            </div>
          </div>
}

export default AddTaskForm;
