import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import '../styles/TaskDetailPage.css';
import { Divider, Typography, CircularProgress, Button } from '@mui/material';
import { getTask } from '../services/tasks';
import TaskComment from './TaskComment';

const TaskDetailPage = ({}) => { 
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
    const isNull = taskDetails.description === null;
    const taskDescription = isNull ? 
        "No description available" :
        taskDetails.description;

    return (
      <Typography className="task-description">
        {isNull ? <span className="not-filled">{taskDescription}</span> : taskDescription}
      </Typography>
    );
  }

  const outputDueDate = () => {
    const isNull = taskDetails.dueDate === null;
    const taskDueDate = isNull ? 
        "Not available" :
        taskDetails.dueDate;
  
    return (
      <Typography 
        variant="h6">
        Due Date: {isNull ? <span className="not-filled">{taskDueDate}</span> : taskDueDate}
      </Typography>
    );
  };

  const outputAssignedTo = () => {
    const isNull = taskDetails.assignedTo === null;
    const assignedTo = isNull ?
        "Not available" :
        taskDetails.assignedTo.username;

    return (
      <Typography 
        variant="h6">
        Assigned To: {isNull ? <span className="not-filled">{assignedTo}</span> : assignedTo}
      </Typography>
    );
  }

  const outputSprint = () => {
    const isNull = taskDetails.sprint === null;
    const taskSprint = isNull ? 
        "Not available" :
        taskDetails.sprint;

    return (
    <>
      <Typography 
        variant="h6">
        Sprint: {isNull ? <span className="not-filled">{taskSprint}</span> : taskSprint.sprintName}
      </Typography>
      <div className="inner-sprint-container">
          <Typography variant="h6">Start Date: {isNull ? "--" : taskSprint.startDate}</Typography>
          <Typography variant="h6">End Date: {isNull ? "--" : taskSprint.endDate}</Typography>
      </div>
    </>
    );
  }

  const buildComments = (comments) => {
    return comments.map(comment => <TaskComment comment={comment} />);
  }

  const outputComments = () => {
    const hasComments = taskDetails.comments.length !== 0;
    const comments = hasComments ?
      buildComments(taskDetails.comments) :
      <Typography>No comments available</Typography>;

    return (
    <>
      {comments}
    </>
    );
  }


  return (
  <>
  {!isLoading ?
    <div className="task-details-content-container">
      <div className="task-details-inner-container">
          <Typography variant="h3">{taskDetails.title}</Typography>
          <div className="priority-due-date-container">
            {outputDueDate()}
            <Typography variant="h6">{`Priority: ${taskDetails.priority}`}</Typography>
          </div>
          <div className="sprint-container">
              {outputSprint()}
          </div>
          <div className="assigned-to-container">
              {outputAssignedTo()}
          </div>
          <div className="description-container">
              <Typography variant="h6">Description:</Typography>
              {outputTaskDescription()}
          </div>
      </div>
      <Divider id="task-detail-divider" orientation="vertical" flexItem />
      <div className="comments-container">
        <Typography variant="h5">Comments</Typography>
        <div className="inner-comments-container">
          {outputComments()}
        </div>
        <div className="add-comment-container">
          <Button 
            variant="contained"
            color="success" 
            onClick={() => {}} 
            disabled={false}
            className="add-comment-button"
            >Add Comment
          </Button>
        </div>
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
