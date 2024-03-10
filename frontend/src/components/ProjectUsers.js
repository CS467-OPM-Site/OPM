import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { fetchProjectUsers } from '../services/projects';
import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material';
import { Button, Typography, TextField } from '@mui/material';
import { DeleteTwoTone } from '@mui/icons-material';
import { deleteProjectUser } from '../services/projectUsers';


const BASE_MEMBER_CONTAINER_CLASS = "add-members-container";
const SHOW_MEMBER_CONTAINER_CLASS = "add-members-container-show";
const SLIDE_IN_MEMBER_CLASS = "show-add-members-container-animate";
const SLIDE_OUT_MEMBER_CLASS="hide-add-members-container-animate";
const USERNAME_ERROR = "Unable to add user."

const ProjectUsers = () => {
  const [members, setMembers]  = useState([]);
  const [currentMember, setCurrentMember] = useState(null);
  const [memberToShowIcons, setMemberToShowIcons] = useState(null);
  const [isAddingMember, setIsAddingMember] = useState(false);
  const [usernameToRemove, setUsernameToRemove] = useState(null);
  const [showDeleteMemberModal, setShowDeleteMemberModal] = useState(false);
  const [additionalModalDialogText, setAdditionalModalDialogText] = useState(null);
  const [shouldShowAddMember, setShouldShowAddMember] = useState(false);
  const [newUsername, setNewUsername] = useState('');
  const [newUsernameError, setNewUsernameError] = useState(false);
  const [newUsernameErrorString, setNewUsernameErrorString] = useState('');
  const [generalError, setGeneralError] = useState('');
  const params = useParams();

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await fetchProjectUsers(params.projectID);         
        const jsonData = await response.json();

        if (response.status !== 200) {
          return;
        }
        setMembers(jsonData.users);
        setCurrentMember(jsonData.currentUser);

      } catch (error) {
        console.error('Error fetching data: ', error);
      }
    };

    fetchUsers();
  }, []);

  const onMouseEnterMember = (userProjectID) => {
    setMemberToShowIcons(userProjectID);
  }

  const onMouseExitMember = () => {
    setMemberToShowIcons(null);
  }

  const setIconsContainerClassName = () => {
    let baseClass = "icon sprint-member-icon member-icons-container";
    if (memberToShowIcons !== null) {
      baseClass += " show-member-icons-container";
    }
    return baseClass;
  }

  const handleHideAddMemberForm = () => {
    setShouldShowAddMember(false);
    setIsAddingMember(false);
    setGeneralError('');
  }

  const handleShowAddMemberForm = () => {
    setShouldShowAddMember(true);
    setIsAddingMember(true);
    setGeneralError('');
  }

  const handleClickOnRemoveMember = (username) => {
    setUsernameToRemove(username);
    setShowDeleteMemberModal(true);
    setAdditionalModalDialogText(null);
  }

  const handleDeleteDialogClosed = () => {
    setUsernameToRemove(null);
    setShowDeleteMemberModal(false);
    setAdditionalModalDialogText(null);
  }

  const removeMember = async () => {
    const response = await deleteProjectUser(params.projectID, { username: usernameToRemove });

    if (response.status !== 200) {
      setAdditionalModalDialogText("Unable to remove user");
      return;
    }
    setAdditionalModalDialogText(null);

    let oldMembers = [...members];
    setMembers(oldMembers.filter(member => member.username !== usernameToRemove));
    handleDeleteDialogClosed();
  }

  const memberOutputContainerName = () => {
    let baseClass = "members-output-container";
    baseClass += isAddingMember ? ` ${SLIDE_OUT_MEMBER_CLASS}` : ` ${SLIDE_IN_MEMBER_CLASS}`;
    return baseClass;
  }

  const setAddMemberContainerClassName = () => {
    let memberContainerClass = BASE_MEMBER_CONTAINER_CLASS;
    memberContainerClass += isAddingMember ? ` ${SLIDE_IN_MEMBER_CLASS}` : ` ${SLIDE_OUT_MEMBER_CLASS}`
      
    if (shouldShowAddMember) {
      memberContainerClass += ` ${SHOW_MEMBER_CONTAINER_CLASS}`
    }
    return memberContainerClass; 
  }

  const usernameClassTitle = () => {
    let baseClass = "add-members-name-field";
    baseClass += newUsernameError ? " invalid-member-name" : ""

    return baseClass;
  }

  const usernameErrorDescription = () => {
    if (newUsernameErrorString === "") return USERNAME_ERROR;
    return newUsernameErrorString;
  }

  const handleRequestNewMember = () => {
    if (newUsername === null || newUsername.length < 3) {
      setNewUsernameErrorString("Username must be at least 3 characters");
      setNewUsernameError(true);
      return
    }
    setNewUsernameErrorString('');
    setNewUsernameError(false);


  }


  return ( 
      <div className="members-container">
        <div className="members-container-title">
          <Typography className="members-title">Members</Typography>
        </div>
        <div className="members-inner-container">
          {!shouldShowAddMember &&
            <div className={memberOutputContainerName()}>
            {members.length === 0 ?
              <div>No members available</div>
            :
              <>
              {members.map( member => (
                <div key={member.userID} className="single-member-container" onMouseEnter={() => onMouseEnterMember(member.userProjectID)} onMouseLeave={onMouseExitMember}>
                  <Typography className="single-member">{member.username}</Typography>
                  {(member.userID !== currentMember.userID && member.userProjectID === memberToShowIcons) &&
                    <DeleteTwoTone className={setIconsContainerClassName()} onClick={() => handleClickOnRemoveMember(member.username)} color="warning" />
                  }
                </div>
              ))}
              </>
            }
            </div>
          } 
          {shouldShowAddMember &&
            <div className={setAddMemberContainerClassName()}>
              <TextField 
                className={usernameClassTitle()}
                variant="filled"
                label="Username" 
                required 
                size='large'
                value={newUsername}
                error={newUsernameError}
                helperText={newUsernameError && usernameErrorDescription()}
                onChange={(e) => {
                  setNewUsername(e.target.value.trim() === '' ? '' : e.target.value)}
                }
                sx={{ 
                  label: { color: "#000000" },
                  "& .MuiFilledInput-root::after": { borderColor: "rgba(129, 255, 154, 0.6)" }
                }}/>
              {(generalError !== '') &&
                <Typography className='general-sprint-error'>{generalError}</Typography>
              } 
            </div>
          }
        </div>
        <div className="members-footer-container">
          <div className="add-member-button-container">
            { isAddingMember ? 
              <>
              <Button 
                key="add-sprint-final" 
                variant="contained" 
                color="success" 
                onClick={handleRequestNewMember}>Add</Button>
              <Button 
                key="cancel-sprint-add" 
                variant="contained" 
                color="error" 
                onClick={handleHideAddMemberForm}>Cancel</Button>
              </>
            :
              <>
                <Button variant="contained" color="success" onClick={handleShowAddMemberForm}>Add Member</Button>
                <Button variant="contained" color="error">Leave Project</Button>
              </>
            }
          </div>
        </div>

        <Dialog
          open={showDeleteMemberModal}
          onClose={handleDeleteDialogClosed}
          aria-labelledby="alert-dialog-title"
          aria-describedby="alert-dialog-description">
          <DialogTitle className="alert-delete-member" id="alert-delete-member-dialog-title">
            {"Delete this member?"}
          </DialogTitle>
          <DialogContent className="alert-delete-member" id="alert-delete-member-dialog-content">
            <DialogContentText id="alert-dialog-description">
              All associated tasks will be unassigned.
            </DialogContentText>
          {additionalModalDialogText &&
            <DialogContentText id="alert-dialog-confirmation">
              {additionalModalDialogText}
            </DialogContentText>
          }
          </DialogContent>
          <DialogActions className="alert-delete-member" id="alert-delete-member-dialog-actions">
            <Button variant="contained" color="success" onClick={handleDeleteDialogClosed}>Cancel</Button>
            <Button 
              variant="contained" 
              color="error" 
              onClick={removeMember} 
              autoFocus>
             Delete 
            </Button>
          </DialogActions>
        </Dialog>
      </div>
  );
};

export default ProjectUsers;
