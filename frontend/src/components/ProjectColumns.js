import React, { memo, useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { Button, Typography } from '@mui/material';
import { DeleteForever, LibraryAdd, Cancel } from '@mui/icons-material';
import { deleteColumn } from '../services/columns';


const URL_TRIM = "/api/v1"
const CANNOT_REMOVE = "Cannot remove column"
const TASKS_REMAIN = "Remove all tasks before deleting column"
const FADE_IN = "fade-in-animation";
const FADE_OUT = "fade-out-animation";
const COLUMN_CARD = "column-card";

const ProjectColumn = memo(( { columnTitle, columnID, columnLocation, columns, setColumns } ) => {
  const { currentUser } = useAuth();
  const [columnError, setColumnError] = useState(null);
  const [isColumnError, setIsColumnError] = useState(false);
  const [isColumnNew, setIsColumnNew] = useState(true);
  const [isColumnBeingDeleted, setIsColumnBeingDeleted] = useState(false);

  useEffect(() => {
    const removeOnLoadAnimation = () => {
      setTimeout(() => {
        setIsColumnNew(false);
      }, 300);
    }

    removeOnLoadAnimation();
  }, []);

  const onDeleteColumnPressed = async() => {
    const response = await deleteColumn(currentUser.token, columnLocation.slice(URL_TRIM.length));
    
    switch (response.status) {
      case 200: {
        setIsColumnBeingDeleted(true)
        setTimeout(() => {
          const newColumns = columns.filter((column) => (column.columnID !== columnID));
          setColumns(newColumns);
        }, 300);
        break;
      }
      case 403: {
        const responseJson = await response.json();
        if (responseJson.message.includes("tasks")) {
          setColumnError(TASKS_REMAIN);
          setIsColumnError(true);
          return
        };
      }
      default: {
        setColumnError(CANNOT_REMOVE);
        setIsColumnError(true);
        return
      }
    }
  }

  const onHideColumnError = () => {
    setIsColumnError(false);
    setColumnError(null);
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

  return <div className={columnClassNameSet()} key={columnID}>
            <div className="column-container">
              <div className="column-title-container">
                <Typography 
                  variant="h5" 
                  component="div" 
                  className="column-title {columnTitle}"
                  sx={{
                    fontSize: '1.1rem'
                  }}>
                  {columnTitle}
                </Typography>
                { isColumnError &&
                  <div className="column-error-container">
                    <Typography className="column-error" sx={{ fontWeight: 700, fontSize: '0.9rem' }}>{columnError}</Typography>
                    <Cancel fontSize="small" color="action" className="icon column-error-close" onClick={onHideColumnError} />
                  </div>
                }
              </div>
              <div className="task-container"></div>
              <div className="column-bottom-button-container">
                <Button variant="contained" color="success" size="medium" startIcon={<LibraryAdd />}>Add Task!</Button>
                <Button 
                  variant="contained" 
                  color="error" 
                  size="small" 
                  startIcon={<DeleteForever />} 
                  onClick={onDeleteColumnPressed}>Delete Column</Button>
              </div>
            </div>
          </div>
});

export default ProjectColumn;
