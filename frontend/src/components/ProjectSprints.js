import React, { useEffect, useState, useMemo } from 'react';
import { useParams } from 'react-router-dom';
import { fetchProjectSprints, addSprint, deleteSprint } from '../services/sprints';
import { Button, TextField, Typography } from '@mui/material';
import { DriveFileRenameOutlineTwoTone, DeleteTwoTone } from "@mui/icons-material";
import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material';
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';

const BASE_SPRINT_CONTAINER_CLASS = "add-sprint-container";
const SHOW_SPRINT_CONTAINER_CLASS = "add-sprint-container-show";
const SLIDE_IN_SPRINT_CLASS = "show-add-sprint-container-animate";
const SLIDE_OUT_SPRINT_CLASS="hide-add-sprint-container-animate";
const SPRINT_NAME_ERROR = "Unable to add sprint."


const ProjectSprints = () => {
  const [sprints, setSprints] = useState([]);
  const [sprintToShowIconsFor, setSprintToShowIconsFor] = useState(null)
  const [isAddingSprint, setIsAddingSprint] = useState(false);
  const [shouldShowAddSprint, setShouldShowAddSprint] = useState(null);
  const [newSprintName, setAddNewSprintName] = useState('');
  const [newSprintNameError, setNewSprintNameError] = useState(false);
  const [newSprintStartDate, setNewSprintStartDate] = useState(dayjs());
  const [newSprintEndDate, setNewSprintEndDate] = useState(dayjs().add(1, 'day'));
  const [newSprintStartDateError, setNewSprintStartDateError] = useState(null);
  const [newSprintEndDateError, setNewSprintEndDateError] = useState(null);
  const [showDeleteSprintModal, setShowDeleteSprintModal] = useState(false);
  const [sprintIDtoDelete, setSprintIDtoDelete] = useState(-1);
  const [additionalModalDialogText, setAdditionalModalDialogText] = useState(null);
  const [generalError, setGeneralError] = useState('');
  const params = useParams();

  useEffect(() => {
    const fetchSprints = async () => {
      try {
        const response = await fetchProjectSprints(params.projectID);         
        const jsonData = await response.json();

        if (response.status !== 200) {
          return;
        }
        const allSprints = jsonData.sprints.reverse();
        setSprints(allSprints);

      } catch (error) {
        console.error('Error fetching data: ', error);
      }
    };


    fetchSprints();
  }, []);

  const onMouseEnterSprint = (sprintID) => {
    setSprintToShowIconsFor(sprintID);
  }

  const onMouseExitSprint = () => {
    setSprintToShowIconsFor(null);
  }

  const setIconsContainerClassName = () => {
    let baseClass = "sprint-icons-container";
    if (sprintToShowIconsFor !== null) {
      baseClass += " show-sprint-icons-container";
    }
    return baseClass;
  }

  const setAddSprintContainerClassName = () => {
    let sprintContainerClass = BASE_SPRINT_CONTAINER_CLASS;
    sprintContainerClass += isAddingSprint ? ` ${SLIDE_IN_SPRINT_CLASS}` : ` ${SLIDE_OUT_SPRINT_CLASS}`
      
    if (shouldShowAddSprint) {
      sprintContainerClass += ` ${SHOW_SPRINT_CONTAINER_CLASS}`
    }
    return sprintContainerClass; 
  }
  
  const sprintOutputContainerName = () => {
    let baseClass = "sprint-output-container";
    baseClass += isAddingSprint ? ` ${SLIDE_OUT_SPRINT_CLASS}` : ` ${SLIDE_IN_SPRINT_CLASS}`;
    return baseClass;
  }

  const handleShowAddSprintForm = () => {
    setShouldShowAddSprint(true);
    setIsAddingSprint(true);
    setGeneralError('');
  }

  const handleHideAddSprintForm = () => {
    setIsAddingSprint(false);
    setTimeout(() => {
      setShouldShowAddSprint(false);
      setNewSprintNameError(false);
    }, 405);
  }

  const sprintNameClassTitle = () => {
    let baseClass = "add-sprint-name-field";
    baseClass += newSprintNameError ? " invalid-sprint-name" : ""

    return baseClass;
  }

  const handleValidatingSprint = () => {
    if (newSprintName.trim() === '' || newSprintName.trim().length < 3) {
      console.log("Invalid title");
      setNewSprintNameError(true);
      return;
    }
    setNewSprintNameError(false);

    if (newSprintStartDateError !== null || newSprintEndDateError !== null) {
      return;
    }

    const newSprint = {
      sprintName: newSprintName,
      startDate: newSprintStartDate.format('YYYY-MM-DD'),
      endDate: newSprintEndDate.format('YYYY-MM-DD'),
    }

    handleAddingSprint(newSprint);
  }

  const handleAddingSprint = async (sprintDetails) => {
    const response = await addSprint(params.projectID, sprintDetails);
    const responseJSON = await response.json();

    if (response.status !== 201) {
      if ("message" in responseJSON) {
        setGeneralError(responseJSON.message);
        return;
      } 
      setGeneralError("Could not add sprint.");
      return;
    }

    // Shallow copy sprint and splice new one in
    let newSprints = [...sprints];
    newSprints.splice(0, 0, responseJSON);
    setSprints(newSprints);

    handleHideAddSprintForm();
  }


  const sprintNameErrorDescription = (customError = "") => {
    if (customError === "") return SPRINT_NAME_ERROR;

    return customError;
  }

  const startDateErrorMessage = useMemo(() => {
    switch (newSprintStartDateError) {
      case 'invalidDate': {
        return 'Your date is not valid';
      }
      default: {
        return '';
      }
    }
  }, [newSprintStartDateError]);

  const endDateErrorMessage = useMemo(() => {
    switch (newSprintEndDateError) {
      case 'invalidDate': {
        return 'Your date is not valid';
      }
      case 'minDate': {
        return "Must be after start";
      }
      default: {
        return '';
      }
    }
  }, [newSprintEndDateError]);

  const handleClickOnDeleteSprint = (sprintIDtoRemove) => {
    setAdditionalModalDialogText(null);
    setShowDeleteSprintModal(true);
    setSprintIDtoDelete(sprintIDtoRemove);
  }

  const handleDeleteDialogClosed = () => {
    setAdditionalModalDialogText('');
    setShowDeleteSprintModal(false);
    setSprintIDtoDelete(-1);
  }

  const removeSprint = async () => {
    const response = await deleteSprint(params.projectID, sprintIDtoDelete);

    if (response.status !== 200) {
      setAdditionalModalDialogText("Unable to delete sprint");
      return;
    };

    let oldSprints = [...sprints];
    setSprints(oldSprints.filter(sprint => sprint.sprintID !== sprintIDtoDelete));
    handleDeleteDialogClosed();
  }

  return ( 
      <div className="sprints-container">
        <div className="sprints-container-title">
          <Typography className="sprints-title">Sprints</Typography>
        </div>
        <div className="sprints-inner-container">
          {!shouldShowAddSprint &&
            <div className={sprintOutputContainerName()}>
              {sprints.length === 0 ?
                <div>No sprints available</div>
              :
                <>
                {sprints.map( sprint => (
                  <div key={sprint.sprintID} className="single-sprint-container" onMouseEnter={() => onMouseEnterSprint(sprint.sprintID)} onMouseLeave={onMouseExitSprint}>
                    <div className="sprint-title-and-icons-container">
                      <Typography variant="h6">{sprint.sprintName}</Typography>
                      { sprintToShowIconsFor === sprint.sprintID &&
                        <div className={setIconsContainerClassName()}>
                          <DeleteTwoTone className="icon sprint-member-icon" color="warning" onClick={() => handleClickOnDeleteSprint(sprint.sprintID)} />
                          <DriveFileRenameOutlineTwoTone className="icon sprint-member-icon" color="secondary" />
                        </div>
                      }
                    </div>
                    <div className="single-sprint-date-container">
                      <Typography>From: {sprint.startDate}</Typography>
                      <Typography>To: {sprint.endDate}</Typography>
                    </div>
                  </div>
                ))}
                </>
              }
            </div>
          }
          {shouldShowAddSprint &&
            <div className={setAddSprintContainerClassName()}>
              <TextField 
                className={sprintNameClassTitle()}
                variant="filled"
                label="Sprint Name" 
                required 
                size='small'
                value={newSprintName}
                error={newSprintNameError}
                helperText={newSprintNameError && sprintNameErrorDescription()}
                onChange={(e) => {
                  setAddNewSprintName(e.target.value.trim() === '' ? '' : e.target.value)}
                }
                sx={{ 
                  label: { color: "#000000" },
                  "& .MuiFilledInput-root::after": { borderColor: "rgba(129, 255, 154, 0.6)" }
                }}/>
              <LocalizationProvider dateAdapter={AdapterDayjs}>
                <div className="add-sprint-dates-container">
                  <DatePicker 
                    className="new-sprint-date"
                    label="Sprint Start" 
                    value={newSprintStartDate} 
                    onChange={(newValue) => setNewSprintStartDate(newValue)} 
                    onError={(newError) => setNewSprintStartDateError(newError)}
                    slotProps={{
                      textField: {
                        helperText: startDateErrorMessage,
                      },
                    }}/>
                  <DatePicker 
                    className="new-sprint-date"
                    label="Sprint End" 
                    value={newSprintEndDate} 
                    onChange={(newValue) => setNewSprintEndDate(newValue)}                     
                    onError={(newError) => setNewSprintEndDateError(newError)}
                    slotProps={{
                      textField: {
                        helperText: endDateErrorMessage,
                      },
                    }}
                    minDate={newSprintStartDate.add(1, 'day')}/>
                </div>
              </LocalizationProvider>
              {(generalError !== '') &&
                <Typography className='general-sprint-error'>{generalError}</Typography>
              } 
            </div>
          }
        </div>
        <div className="sprints-footer-container">
          <div className="add-sprint-button-container">
            { isAddingSprint ? 
              <>
              <Button 
                key="add-sprint-final" 
                variant="contained" 
                color="success" 
                onClick={handleValidatingSprint}>Add</Button>
              <Button 
                key="cancel-sprint-add" 
                variant="contained" 
                color="error" 
                onClick={handleHideAddSprintForm}>Cancel</Button>
              </>
            :
              <Button key="add-sprint-initial" variant="contained" color="success" onClick={handleShowAddSprintForm}>Add Sprint</Button>
            }
          </div>
        </div>
        
        <Dialog
          open={showDeleteSprintModal}
          onClose={handleDeleteDialogClosed}
          aria-labelledby="alert-dialog-title"
          aria-describedby="alert-dialog-description">
          <DialogTitle className="alert-delete-sprint" id="alert-delete-sprint-dialog-title">
            {"Delete this sprint?"}
          </DialogTitle>
          <DialogContent className="alert-delete-sprint" id="alert-delete-sprint-dialog-content">
            <DialogContentText id="alert-dialog-description">
              This cannot be undone. 
            </DialogContentText>
          {additionalModalDialogText &&
            <DialogContentText id="alert-dialog-confirmation">
              {additionalModalDialogText}
            </DialogContentText>
          }
          </DialogContent>
          <DialogActions className="alert-delete-sprint" id="alert-delete-sprint-dialog-actions">
            <Button variant="contained" color="success" onClick={handleDeleteDialogClosed}>Cancel</Button>
            <Button 
              variant="contained" 
              color="error" 
              onClick={removeSprint} 
              autoFocus>
             Delete 
            </Button>
          </DialogActions>
        </Dialog>
      </div>
  );
};

export default ProjectSprints;
