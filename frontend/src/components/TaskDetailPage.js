import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import '../styles/TaskDetailPage.css';
import { Divider, Typography, CircularProgress } from '@mui/material';
import { getTask } from '../services/tasks';

const TaskDetailPage = ( {} ) => { 
  const [taskDetails, setTaskDetails] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorLoadingTask, setErrorLoadingTask] = useState('');
  const location = useLocation();

  useEffect(() => {
    const fetchTaskDetails = async () => {
      try {
        setErrorLoadingTask('');
        setIsLoading(true);
        const response = await getTask(location.pathname);
        const responseJSON = await response.json();

        switch (response.status) {
          case 200: {
            setTaskDetails(responseJSON);
            break;
          }
          default: {
            if ("message" in responseJSON) {
              setErrorLoadingTask(responseJSON.message);
              return;
            }
            setErrorLoadingTask("Error retrieving task details");
            return;
          }
        }
        setIsLoading(false)
      } catch {
        setErrorLoadingTask("Error retrieving task details");
        return;
      }
    }
    fetchTaskDetails();
  }, []);

  const outputTaskDescription = () => {
    const taskDescription = taskDetails.description === null ? 
        "None" :
        taskDetails.taskDescription;

    return `Description: ${taskDescription}`;
  }

  const outputDueDate = () => {
    const taskDueDate = taskDetails.dueDate === null ? 
        "None" :
        taskDetails.dueDate;

    return `Due Date: ${taskDueDate}`;
  }

  const outputAssignedTo = () => {
    const assignedTo = taskDetails.assignedTo === null ?
        "None" :
        taskDetails.assignedTo.username;

    return `Assigned to: ${assignedTo}`;
  }


  return (
  <>
  {!isLoading ?
    <div className="task-details-content-container">
      <div className="task-details-inner-container">
          <Typography variant="h4">{taskDetails.title}</Typography>
          <Typography variant="h5">{outputTaskDescription()}</Typography>
          <Typography variant="h5">{outputDueDate()}</Typography>
          <Typography variant="h5">{`Priority: ${taskDetails.priority}`}</Typography>
          <Typography variant="h5">{outputAssignedTo()}</Typography>
      </div>
      <Divider id="task-detail-divider" orientation="vertical" flexItem />
      <div className="comments-container">
        <Typography>Here lies my comments</Typography>
      </div>
    </div>
  : 
    <div className="task-details-loading-container">
      { (errorLoadingTask === '') ?
        <CircularProgress color="info"/>
        :
        <Typography variant="h4" color="error">{errorLoadingTask}</Typography>
      }
    </div>
    }
  </> )
}

export default TaskDetailPage;
