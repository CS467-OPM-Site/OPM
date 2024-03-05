import React, { memo, useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Button, Typography } from '@mui/material';
import { 
  DeleteForever, 
  LibraryAdd, 
  Cancel, 
  KeyboardDoubleArrowRight, 
  KeyboardDoubleArrowLeft, 
  CompareArrows, 
  NotInterested } from '@mui/icons-material';
import { deleteColumn, moveColumn } from '../services/columns';
import ProjectTask from './ProjectTasks';
import { moveTask } from '../services/tasks';


const CANNOT_REMOVE = "Cannot remove column";
const COULD_NOT_MOVE = "Could not move column";
const MOVED_COLUMN = "Moved column!";
const TASKS_REMAIN = "Remove all tasks before deleting column"
const FADE_IN = "fade-in-animation";
const FADE_OUT = "fade-out-animation";
const COLUMN_CARD = "column-card";

const ProjectColumn = memo(( { 
      currentColumnIndex, 
      columns, 
      setColumns, 
      setIsLoading, 
      isOtherColumnBeingMoved, 
      setIsOtherColumnBeingMoved, 
      setIsTaskBeingAdded,
      setIsTaskBeingShown} ) => {
  const [columnError, setColumnError] = useState('');
  const [columnSuccess, setColumnSuccess] = useState('');
  const [shouldFadeOutSuccess, setShouldFadeOutSuccess] = useState(false);
  const [isColumnNew, setIsColumnNew] = useState(true);
  const [isColumnBeingDeleted, setIsColumnBeingDeleted] = useState(false);
  const [isWantingToMoveColumn, setIsWantingToMoveColumn] = useState(false);
  const [originalColumnOrder, setOriginalColumnOrder] = useState(null);
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const removeOnLoadAnimation = () => {
      setTimeout(() => {
        setIsColumnNew(false);
      }, 300);
    }

    removeOnLoadAnimation();
  }, []);

  const handleNavigateToAddTask = () => {
    setIsTaskBeingAdded(true);
    navigate(`${location.pathname}/tasks`, { 
        state: { 
          projectID: location.state.projectID,
          columnID: columns[currentColumnIndex].columnID} })
  }

  const onDeleteColumnPressed = async() => {
    setIsLoading(true);
    const response = await deleteColumn(columns[currentColumnIndex].columnLocation);
    
    switch (response.status) {
      case 200: {
        setIsColumnBeingDeleted(true)
        setTimeout(() => {
          const newColumns = columns.filter((column) => (column.columnID !== columns[currentColumnIndex].columnID));
          newColumns.forEach((column, index) => column.columnIndex = index);
          setColumns(newColumns);
        }, 300);
        break;
      }
      case 403: {
        const responseJson = await response.json();
        if (responseJson.message.includes("tasks")) {
          setColumnError(TASKS_REMAIN);
          setTimeout(() => {setColumnError('')}, 5000);
        };
        break;
      }
      default: {
        setColumnError(CANNOT_REMOVE);
        setTimeout(() => {setColumnError('')}, 5000);
      }
    }
    setIsLoading(false);
  }

  const onHideColumnError = () => {
    setColumnError('');
  }

  const onHideColumnSuccess = () => {
    setColumnSuccess('');
  }

  const columnClassNameSet = () => {
    if (!isColumnNew && !isColumnBeingDeleted) {
      return COLUMN_CARD;
    }

    if (isColumnNew) {
      return `${COLUMN_CARD} ${FADE_IN}`
    }

    if (isColumnBeingDeleted) {
      return `${COLUMN_CARD} ${FADE_OUT}`
    }
  }

  const onMoveColumnPressed = () => {
    setIsOtherColumnBeingMoved(true);
    setIsWantingToMoveColumn(true);
    
    // To safely deep copy
    setOriginalColumnOrder(JSON.parse(JSON.stringify(columns)));
  }

  const onMoveCancelPressed = () => {
    setColumns(originalColumnOrder);
    setIsWantingToMoveColumn(false)
    setIsOtherColumnBeingMoved(false);
  }

  const onAcceptColumnMoved = async () => {
    const newColumnIndex = columns[currentColumnIndex].columnIndex;
    const wasValidMove = await sendMoveRequest(newColumnIndex);
    
    if (!wasValidMove) { 
      onMoveCancelPressed();
      return;
    }

    setOriginalColumnOrder(columns);

    setIsOtherColumnBeingMoved(false);
    setIsWantingToMoveColumn(false);
    setTimeout(() => {
      setShouldFadeOutSuccess(true);
      setTimeout(() => {
        setColumnSuccess('');
        setShouldFadeOutSuccess(false)
      }, 500);
    }, 800);
  }

  const onMovePressed = async (isLeftMove) => {
    // To safely deep copy in case invalid move
    setOriginalColumnOrder(JSON.parse(JSON.stringify(columns)));

    // Create a shallow copy of columns in order to replace original
    const newColumns = [...columns];

    const columnIndexToMove = columns[currentColumnIndex].columnIndex;
    let newIndex;

    if (isLeftMove) {
      newIndex = (columnIndexToMove === 0) ? columns.length - 1 : columnIndexToMove - 1;
    } else {
      newIndex = (columnIndexToMove === (columns.length - 1)) ? 0 : columnIndexToMove + 1;
    }

    // Now swap the two using splice, in the shallow copy
    const movedColumn = newColumns.splice(columnIndexToMove, 1)[0]
    newColumns.splice(newIndex, 0, movedColumn);

    newColumns.forEach((column, index) => {
      column.columnIndex = index;
    });
    
    setColumns(newColumns);
  }

  const sendMoveRequest = async(newIndex) => {
    setIsLoading(true);
    const response = await moveColumn(columns[currentColumnIndex].columnLocation, newIndex);

    switch (response.status) {
      case 200: {
        setColumnSuccess(MOVED_COLUMN);
        setIsLoading(false);
        return true;
      }
      default: {
        setColumnError(COULD_NOT_MOVE);
        setIsLoading(false);
        setTimeout(() => {setColumnError('')}, 5000);
        return false;
      }
    }
  }

  const getOriginalColumnIndex = () => {
    const originalColumn = originalColumnOrder.find(column => column.columnID === columns[currentColumnIndex].columnID);
    return originalColumn.columnIndex;
  }

  const removeTask = (taskID) => {
    // Create shallow copy of tasks
    const newColumns = columns.map(column => ({ 
      ...column, tasks: column.tasks.filter(task => task.taskID !== taskID) }));

    setColumns(newColumns);
  }

  const onMoveTask = async (taskID, isLeftMove) => {
    // Create new shallow copy of columns and tasks
    const newColumns = columns.map(column => ({ ...column, tasks: [...column.tasks] }));

    const nextColumnIndex = isLeftMove
      ? (currentColumnIndex === 0 ? columns.length - 1 : currentColumnIndex - 1)
      : (currentColumnIndex === columns.length - 1 ? 0 : currentColumnIndex + 1);

    // Guaranteed columnIndex is the in-order location of the column in the array
    const currentColumnForTask = newColumns[currentColumnIndex];
    const nextColumnForTask = newColumns[nextColumnIndex];

    // Use task index to remove it from current column, and to copy it over to next column
    const taskToMoveIndex = currentColumnForTask.tasks.findIndex(task => task.taskID === taskID);
    const taskToMove = currentColumnForTask.tasks[taskToMoveIndex];

    nextColumnForTask.tasks.push(taskToMove);
    currentColumnForTask.tasks.splice(taskToMoveIndex, 1);

    const isValidMove = await sendTaskMoveRequest(taskToMove.taskLocation, nextColumnForTask.columnID);

    if (isValidMove) {
      setColumns(newColumns);
      return;
    }
  }

  const sendTaskMoveRequest = async (taskLocation, newColumnID) => {
    setIsLoading(true)
    const response = await moveTask(taskLocation, newColumnID);

    if (response.status === 200) {
      setIsLoading(false)
      return true;
    }

    const responseJSON = await response.json();

    setColumnError(responseJSON.message);
    setIsLoading(false)
    return false;
  }

                  /*onClick={() => ( setIsAddingTask(columns[currentColumnIndex].columnID))}*/
  return <div className={columnClassNameSet()} key={columns[currentColumnIndex].columnID}>
            <div className="column-container">
              <div className="column-title-container">
                <Typography 
                  variant="h5" 
                  component="div" 
                  className="column-title {columnTitle}"
                  sx={{
                    fontSize: '1.1rem'
                  }}>
                  {columns[currentColumnIndex].columnTitle}
                </Typography>
                { columnError &&
                  <div className="column-error-container">
                    <Typography className="column-error" sx={{ fontWeight: 700, fontSize: '0.9rem' }}>{columnError}</Typography>
                    <Cancel fontSize="small" color="action" className="icon column-error-close" onClick={onHideColumnError} />
                  </div>
                }
                { columnSuccess &&
                  <div className={shouldFadeOutSuccess ? 'column-success-container hide-column-success' : 'column-success-container'}>
                    <Typography className="column-success" sx={{ fontWeight: 700, fontSize: '0.9rem' }}>{columnSuccess}</Typography>
                    <Cancel fontSize="small" color="action" className="icon column-success-close" onClick={onHideColumnSuccess} />
                  </div>
                }
              </div>
              <div className="task-container">
              {columns[currentColumnIndex].tasks && columns[currentColumnIndex].tasks.map( task => ( 
                <ProjectTask 
                  key={task.taskID + columns[currentColumnIndex].columnID} 
                  currentTask={task} 
                  removeTask={removeTask} 
                  moveTask={onMoveTask}
                  setIsLoading={setIsLoading}
                  setIsTaskBeingShown={setIsTaskBeingShown}/> ) 
              )}
              </div>
              <div className="column-bottom-button-container">
                {isWantingToMoveColumn ?
                <Button 
                  onClick={onMoveCancelPressed} 
                  variant="contained" 
                  color="error" 
                  size="medium" 
                  startIcon={<NotInterested />}>Stop Moving</Button>
                :
                <Button 
                  onClick={handleNavigateToAddTask}
                  disabled={isOtherColumnBeingMoved} 
                  variant="contained" 
                  color="success" 
                  size="medium" 
                  startIcon={<LibraryAdd />}>Add Task!</Button>
                }
                {!isWantingToMoveColumn ?
                <div className="delete-reorder-column-button-container">
                    <Button 
                      variant="contained" 
                      color="error" 
                      size="small" 
                      disabled={isOtherColumnBeingMoved}
                      startIcon={<DeleteForever />} 
                      onClick={onDeleteColumnPressed}>Delete Column</Button>

                    <Button 
                      variant="contained" 
                      className={isOtherColumnBeingMoved ? "" : "reorder-columns-buttons"}
                      size="small" 
                      disabled={isOtherColumnBeingMoved}
                      startIcon={<CompareArrows />} 
                      onClick={onMoveColumnPressed}>Move Column</Button>

                </div>
                :
                <div className="delete-reorder-column-button-container">
                  <Button 
                    variant="contained" 
                    className="move-column-pressed"
                    color="success"
                    size="small" 
                    disabled={getOriginalColumnIndex() === columns[currentColumnIndex].columnIndex}
                    startIcon={<CompareArrows />} 
                    onClick={onAcceptColumnMoved}>Accept Position</Button>
                </div>
                }
              </div>
            </div>
            {isWantingToMoveColumn &&
            <div className="move-column-icons-container">
              <KeyboardDoubleArrowLeft onClick={() => onMovePressed(true)} className="move-column-left-icon icon move-column-icons"></KeyboardDoubleArrowLeft>
              <KeyboardDoubleArrowRight onClick={() => onMovePressed(false)} className="move-column-right-icon icon move-column-icons"></KeyboardDoubleArrowRight>
            </div>
            }
          </div>
});

export default ProjectColumn;
