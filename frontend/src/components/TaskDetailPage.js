import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import '../styles/TaskDetailPage.css';
import { Divider, Typography, CircularProgress, Button, TextField } from '@mui/material';
import { getTask } from '../services/tasks';
import TaskComment from './TaskComment';
import { addComment } from '../services/comments';

const BASE_ADD_COMMENT_CLASS = "add-comment-field";
const SLIDE_IN_COMMENT_CLASS = "show-add-comment-field";
const SLIDE_OUT_COMMENT_CLASS = "hide-add-comment-field";
const NEW_COMMENT_ERROR = "Comment must not be empty or only spaces"
const GENERAL_ERROR = "Unable to add comment";

const TaskDetailPage = ( { columns, setColumns } ) => { 
  const [taskTitle, setTaskTitle] = useState(null);
  const [taskPriority, setTaskPriority] = useState(null);
  const [taskDescription, setTaskDescription] = useState(null);
  const [taskAssignedTo, setTaskAssignedTo] = useState(null);
  const [taskSprint, setTaskSprint] = useState(null);
  const [taskDueDate, setTaskDueDate] = useState(null);
  const [taskComments, setTaskComments] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorLoadingTask, setErrorLoadingTask] = useState('');
  const [isAddingComment, setIsAddingComments] = useState(null);
  const [shouldShowCommentField, setShouldShowCommentField] = useState(false);
  const [newCommentError, setNewCommentError] = useState('');
  const [newComment, setNewComment] = useState('');
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

  const setTaskDetails = (taskDetails) => {
    setTaskTitle(taskDetails.title);
    setTaskPriority(taskDetails.priority);
    setTaskDescription(taskDetails.description);
    setTaskDueDate(taskDetails.dueDate);
    setTaskAssignedTo(taskDetails.assignedTo);
    setTaskSprint(taskDetails.sprint);

    if (taskDetails.comments.length !== 0) {
      const comments = taskDetails.comments.reverse();
      
      setTaskComments(comments);
    }

  }

  const outputTaskDescription = () => {
    const isNull = taskDescription === null;
    const description = isNull ? 
        "No description available" :
        taskDescription;

    return (
      <Typography className="task-description">
        {isNull ? <span className="not-filled">{description}</span> : description}
      </Typography>
    );
  }

  const outputDueDate = () => {
    const isNull = taskDueDate === null;
    const dueDate = isNull ? 
        "Not available" :
        taskDueDate;
  
    return (
      <Typography 
        variant="h6">
        Due Date: {isNull ? <span className="not-filled">{dueDate}</span> : dueDate}
      </Typography>
    );
  };

  const outputAssignedTo = () => {
    const isNull = taskAssignedTo === null;
    const assignedTo = isNull ?
        "Not available" :
        taskAssignedTo.username;

    return (
      <Typography 
        variant="h6">
        Assigned To: {isNull ? <span className="not-filled">{assignedTo}</span> : assignedTo}
      </Typography>
    );
  }

  const outputSprint = () => {
    const isNull = taskSprint === null;
    const sprint = isNull ? 
        "Not available" :
        taskSprint;

    return (
    <>
      <Typography 
        variant="h6">
        Sprint: {isNull ? <span className="not-filled">{sprint}</span> : sprint.sprintName}
      </Typography>
      <div className="inner-sprint-container">
          <Typography variant="h6">Start Date: {isNull ? "--" : sprint.startDate}</Typography>
          <Typography variant="h6">End Date: {isNull ? "--" : sprint.endDate}</Typography>
      </div>
    </>
    );
  }

  const buildComments = () => {
    return taskComments.map(comment => 
      <TaskComment 
        key={comment.commentID} 
        comment={comment} 
        removeComment={removeComment}/>);
  }

  const commentFieldClassName = () => {
    let className = BASE_ADD_COMMENT_CLASS;
    if (shouldShowCommentField) {
      className += ` ${SLIDE_IN_COMMENT_CLASS}`;
    } else {
      className += ` ${SLIDE_OUT_COMMENT_CLASS}`;
    }

    return className;
  }

  const handleAddCommentClicked = () => {
    setIsAddingComments(true);
    setShouldShowCommentField(true);
  }

  const handleCancelCommentClicked = () => {
    setShouldShowCommentField(false);
    slideUpCommentEntry();
  }

  const slideUpCommentEntry = () => {
    setTimeout(() => {
      setIsAddingComments(false);
      setNewCommentError('');
    }, 405);
  }

  const handleOnSubmitComment = async () => {
    if (newComment.trim() === '') {
      setNewCommentError(NEW_COMMENT_ERROR);
      return;
    }
    setNewCommentError('');

    const newCommentDetails = { commentBody: newComment };

    const response = await addComment(location.pathname, newCommentDetails);
    const responseJSON = await response.json();

    switch (response.status) {
      case 201: {
        addNewComment(responseJSON);
        setShouldShowCommentField(false);
        slideUpCommentEntry();
        break;
      } 
      default: {
        if ("message" in responseJSON) {
          setNewCommentError(responseJSON.message);
          return;
        }
        setNewCommentError(GENERAL_ERROR);
        return;
      }
    }
  }

  const addNewComment = (newCommentDetails) => {
    // Create a shallow copy of the comments
    const oldComments = [...taskComments];

    oldComments.splice(0, 0, newCommentDetails);
    setTaskComments(oldComments);

    // Update the columns so comment count properly increases
    updateColumnsAfterAddingComment();
  }

  const updateColumnsAfterAddingComment = () => {
    // Create a shallow copy of the columns
    const newColumns = columns.map(column => ({ ...column, tasks: [...column.tasks] }));

    const { columnIndex, taskIndex } = findParentColumnAndIndex();
    newColumns[columnIndex].tasks[taskIndex].comments += 1;

    setColumns(newColumns);
  }

  const findParentColumnAndIndex = () => {
    let columnIndex = -1;
    let taskIndex = -1;

    columns.some((column, index) => {
      taskIndex = column.tasks.findIndex(task => task.taskID === location.state.taskID);

      if (taskIndex !== -1) {
        columnIndex = index;
        return true;
      }
      return false;
    });

    return { columnIndex, taskIndex };
  }

  const removeComment = (commentID) => {
    let comments = taskComments.filter(comment => comment.commentID !== commentID);
    setTaskComments(comments);

    const newColumns = columns.map(column => ({ ...column, tasks: [...column.tasks] }));

    const { columnIndex, taskIndex } = findParentColumnAndIndex();
    newColumns[columnIndex].tasks[taskIndex].comments -= 1;

    setColumns(newColumns);
  }

  return (
  <>
  {!isLoading ?
    <div className="task-details-content-container">
      <div className="task-details-inner-container">
          <Typography variant="h3">{taskTitle}</Typography>
          <div className="priority-due-date-container">
            {outputDueDate()}
            <Typography variant="h6">{`Priority: ${taskPriority}`}</Typography>
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
        <div className="comments-and-add-comment-field-container">
          <div className="inner-comments-container">
            {taskComments ? 
              buildComments()
            :
              <Typography>No comments available</Typography>
            }
          </div>
          {isAddingComment && 
                <div className="add-comment-field-container">
                  <TextField 
                    className={commentFieldClassName()}
                    label="Comment"
                    required
                    color="success"
                    variant="filled" 
                    error={newCommentError !== ''}
                    helperText={(newCommentError !== '') && newCommentError}
                    multiline 
                    rows={4}
                    value={newComment}
                    onChange={(e) => { 
                      setNewComment(e.target.value.trim() === '' ? '' : e.target.value) }
                    }
                    sx={{
                      label: { color: "#000000" },
                      fieldset: { color: "#000000" },
                      "& .MuiFilledInput-root::after": { borderColor: "rgba(129, 255, 154, 0.6)" }
                    }}/>
                </div>
              }
        </div>
        <div className="add-comment-button-container">
        {isAddingComment ?
            <>
            <Button 
              key="cancelButton"
              size='small'
              variant="contained"
              color="error" 
              onClick={handleCancelCommentClicked} 
              disabled={false}
              className="add-comment-button"
              >Cancel
            </Button>
            <Button 
              key="submitComment"
              size='small'
              variant="contained"
              color="success" 
              onClick={handleOnSubmitComment} 
              disabled={false}
              className="add-comment-button"
              >Submit
            </Button>
            </>
          :
            <Button 
              key="intentToAddNewComment"
              size='small'
              variant="contained"
              color="success" 
              onClick={handleAddCommentClicked} 
              disabled={false}
              className="add-comment-button"
              >Add Comment
            </Button>
          }
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
