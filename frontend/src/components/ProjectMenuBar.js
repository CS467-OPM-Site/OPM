import React, { useState } from 'react';
import { Button, Typography, TextField } from '@mui/material';
import { Cancel } from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext'; // Adjust the path as necessary
import { addColumn } from '../services/columns';

const MAKE_NEW_COLUMN = "Make a New Column";
const ADD_COLUMN = "Add Column!";
const COLUMN_TITLE = "Column Title*";
const INVALID_TITLE = "Invalid title";
const CANNOT_BE_EMPTY = "Cannot be empty";
const TITLE_LENGTH_REQUIREMENT = "Must be 3-50 characters"

const ProjectMenuBar = ({ projectName, projectID, columns, setColumns }) => {
  const [isAddColumnFieldShown, setIsAddColumnFieldShown] = useState(false);
  const [isAddColumnButtonEnabled, setIsAddColumnButtonEnabled] = useState(true);
  const [addColumnButtonText, setAddColumnButtonText] = useState(MAKE_NEW_COLUMN);
  const [columnTitleToAdd, setColumnTitleToAdd] = useState(null);
  const [isErrorInAddColumn, setIsErrorInAddColumn] = useState(false);
  const [errorInAddColumn, setErrorInAddColumnBox] = useState("");
  const { currentUser } = useAuth(); // Use currentUser from AuthContext

  const handleOnAddColumnClickShowForm = () => {
    if (!isAddColumnFieldShown) {
      setIsAddColumnFieldShown(true);
      setIsAddColumnButtonEnabled(false);
      setAddColumnButtonText(ADD_COLUMN);
    } else {
      setIsAddColumnFieldShown(!isAddColumnFieldShown);
    }
  }

  const onColumnTitleInputChanged = (e) => {
    setColumnTitleToAdd(e.target.value);
    if (e.target.value.length === 3) {
      setIsAddColumnButtonEnabled(true);
    } else if (e.target.value.length < 3) {
      setIsAddColumnButtonEnabled(false);
    }
  }

  const onCloseAddColumnPressed = () => {
    setIsAddColumnFieldShown(false);
    setAddColumnButtonText(MAKE_NEW_COLUMN);
    setIsAddColumnButtonEnabled(true);
    setIsErrorInAddColumn(false);
    setColumnTitleToAdd(null);
  }

  const handleAddingColumn = async () => {
    if (columnTitleToAdd === null) {
      setIsErrorInAddColumn(true);
      setErrorInAddColumnBox(CANNOT_BE_EMPTY);
      return;
    } else if (columnTitleToAdd.length < 3 || columnTitleToAdd.length > 50) {
      setIsErrorInAddColumn(true);
      setErrorInAddColumnBox(TITLE_LENGTH_REQUIREMENT);
      return;
    }

    const response = await addColumn(currentUser.token, columnTitleToAdd, projectID);
    switch (response.status) {
      case 201: {
        const newColumn = await response.json();
        console.log(newColumn);
        setColumns([...columns, newColumn]);
        break;
      }
      case 400: {
          setIsErrorInAddColumn(true);
          setErrorInAddColumnBox(CANNOT_BE_EMPTY);
          break;
      }
      case 409: {
        const error = await response.json();
        setIsErrorInAddColumn(true);
        setErrorInAddColumnBox(error.message);
        break;
      }
      case 403:
      default: {
        setIsErrorInAddColumn(true);
        setErrorInAddColumnBox(INVALID_TITLE);
      }
    }
  }
  
  return <div style={{ width: 'inherit', height: '11%', minHeight: '105px', overflow: 'hidden' }}>
            <div className="project-page-menu-bar">
              <Button variant="contained" color="success">Back to Projects</Button>
              <Typography variant="h4" component="h1" className="project-page-project-title">
                {projectName}
              </Typography>
              <div className="project-page-add-column-container">
                <Button variant="contained" color="success" onClick={isAddColumnFieldShown ? handleAddingColumn : handleOnAddColumnClickShowForm} disabled={!isAddColumnButtonEnabled}>{addColumnButtonText}</Button>
                {isAddColumnFieldShown && 
                  <>
                  <TextField 
                    autoFocus
                    error={isErrorInAddColumn}
                    className="addColumnTitleElem"
                    helperText= {isErrorInAddColumn && errorInAddColumn}
                    id="columnTitle"
                    variant="filled" 
                    size="small" 
                    label={COLUMN_TITLE}
                    sx={{
                      input: {
                        color: "#000000",
                        background: "#81B29A",
                        borderRadius: '5px'
                      },
                      fieldset: {
                        color: '#000000',
                        borderRadius: '5px'
                      },
                      label: {
                        color: "#000000"
                      },
                      "& .MuiFilledInput-root::after": {
                        borderColor: "#2E7D32"
                      }
                    }} 
                    InputLabelProps={{ style: {color: isErrorInAddColumn ? "red" : "black" } }}
                    onChange={onColumnTitleInputChanged}
                    onKeyDown={(e) => { if (e.key === "Enter") handleAddingColumn();}}
                    >
                  </TextField>
                  <Cancel 
                    className="icon" 
                    onClick={onCloseAddColumnPressed} 
                    sx={{ 
                      color: "#FF1D8E",
                      "&:hover": {
                        color: "#7A1045" 
                    }
                  }}></Cancel>
                  </>
                }
              </div>
            </div>
          </div>
}

export default ProjectMenuBar;

