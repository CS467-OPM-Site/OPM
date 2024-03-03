import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Typography, TextField } from '@mui/material';
import { Cancel } from '@mui/icons-material';
import { CircularProgress } from '@mui/material';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { addColumn } from '../services/columns';
import { deleteProject } from '../services/projects';
import { getAuth } from 'firebase/auth';

const MAKE_NEW_COLUMN = "Make a New Column";
const ADD_COLUMN = "Add Column!";
const COLUMN_TITLE = "Column Title*";
const INVALID_TITLE = "Invalid title";
const CANNOT_BE_EMPTY = "Cannot be empty";
const TITLE_LENGTH_REQUIREMENT = "Must be 3-50 characters"

const ProjectMenuBar = ({ projectName, projectID, columns, setColumns, isLoading, setIsLoading }) => {
  const [isAddColumnFieldShown, setIsAddColumnFieldShown] = useState(false);
  const [isAddColumnButtonEnabled, setIsAddColumnButtonEnabled] = useState(true);
  const [addColumnButtonText, setAddColumnButtonText] = useState(MAKE_NEW_COLUMN);
  const [columnTitleToAdd, setColumnTitleToAdd] = useState('');
  const [isErrorInAddColumn, setIsErrorInAddColumn] = useState(false);
  const [errorInAddColumn, setErrorInAddColumnBox] = useState("");
  const [showDeleteProjectModal, setShowDeleteProjectModal] = useState(false);
  const [additionalModalDialogText, setAdditionalModalDialogText] = useState(null);
  const [enableDeleteProjectButton, setEnableDeleteProjectButton] = useState(true);
  const navigate = useNavigate();
  const handleNavigateToHome = () => navigate('/home');

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

    setIsLoading(true);
    const response = await addColumn(columnTitleToAdd, projectID);
    switch (response.status) {
      case 201: {
        setColumnTitleToAdd('');
        const newColumn = await response.json();
        setColumns([...columns, newColumn]);
        onCloseAddColumnPressed();
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
    setIsLoading(false);
  }

  const handleOnDeleteProjectClicked = () => {
    setShowDeleteProjectModal(true);
    setAdditionalModalDialogText(null);
    setEnableDeleteProjectButton(true);
  }

  const handleDeleteDialogClosed = () => {
    setShowDeleteProjectModal(false);
    setAdditionalModalDialogText(null);
    setEnableDeleteProjectButton(true);
  }

  const handleDeletingProject = async() => {
    const auth = getAuth();
    const idToken = await auth.currentUser.getIdToken();
    const response = await deleteProject(idToken, projectID);

    switch (response.status) {
      case 200: {
        setAdditionalModalDialogText("Deleting your project...");
        setTimeout(() => {
          handleNavigateToHome();
        });
        break;
      }
      case 400: {
        const responseJson = await response.json();
        setAdditionalModalDialogText(responseJson.message);
        setEnableDeleteProjectButton(false);
        break;
      }
      default: {
        setAdditionalModalDialogText("Unable to delete this project.");
        setEnableDeleteProjectButton(false);
        break;
      }
    }
  }

  
  return <div style={{ width: 'inherit', height: '11%', minHeight: '105px', overflow: 'hidden' }}>
            <div className="project-page-menu-bar">
              <div className="project-page-buttons-loading-container">
                <div className="project-page-project-buttons-container">
                  <Button variant="contained" color="success" onClick={handleNavigateToHome}>Back to Projects</Button>
                  <Button variant="contained" color="error" onClick={handleOnDeleteProjectClicked}>Delete Project</Button>
                  <Dialog
                    open={showDeleteProjectModal}
                    onClose={handleDeleteDialogClosed}
                    aria-labelledby="alert-dialog-title"
                    aria-describedby="alert-dialog-description"
                  >
                    <DialogTitle className="alert-delete-project" id="alert-delete-project-dialog-title">
                      {"Delete this project?"}
                    </DialogTitle>
                    <DialogContent className="alert-delete-project" id="alert-delete-project-dialog-content">
                      <DialogContentText id="alert-dialog-description">
                        This cannot be undone. Note that this project must have no tasks remaining.
                      </DialogContentText>
                    {additionalModalDialogText &&
                      <DialogContentText id="alert-dialog-confirmation">
                        {additionalModalDialogText}
                      </DialogContentText>
                    }
                    </DialogContent>
                    <DialogActions className="alert-delete-project" id="alert-delete-project-dialog-actions">
                      <Button variant="contained" color="success" onClick={handleDeleteDialogClosed}>Do Not Delete</Button>
                      <Button 
                        variant="contained" 
                        color="error" 
                        onClick={handleDeletingProject} 
                        disabled={!enableDeleteProjectButton}
                        autoFocus>
                       Delete 
                      </Button>
                    </DialogActions>
                  </Dialog>
                </div>
                <CircularProgress 
                  className={!isLoading && "project-loading-icon-hide"}
                  id="project-loading-icon" 
                  color="info"/>
              </div>
              {projectName ? 
                  <>
                  <Typography variant="h4" component="h1" className="project-page-project-title">
                    {projectName}
                  </Typography>
                  </>
                :
                <CircularProgress className="project-page-project-title" color="success"/>
              }
              <div className="project-page-add-column-container">
                <Button 
                  variant="contained"
                  color="success" 
                  onClick={isAddColumnFieldShown ? handleAddingColumn : handleOnAddColumnClickShowForm} 
                  disabled={!isAddColumnButtonEnabled}
                  className={(columns && columns.length === 0) ? "no-columns-add-column-button" : ""}
                  >{addColumnButtonText}
                </Button>
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
                    inputProps={{ maxLength: 50 }}
                    onChange={onColumnTitleInputChanged}
                    value={columnTitleToAdd}
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

