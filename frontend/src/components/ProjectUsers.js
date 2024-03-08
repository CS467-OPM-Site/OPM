import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { fetchProjectUsers } from '../services/projects';
import { Button, Typography } from '@mui/material';
import { DeleteTwoTone } from '@mui/icons-material';

const ProjectUsers = () => {
  const [members, setMembers]  = useState([]);
  const [currentMember, setCurrentMember] = useState(null);
  const [memberToShowIcons, setMemberToShowIcons] = useState(null);
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
        console.log(jsonData);

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

  return ( 
      <div className="members-container">
        <div className="members-container-title">
          <Typography className="members-title">Members</Typography>
        </div>
        <div className="members-inner-container">
          {members.length === 0 ?
            <div>No members available</div>
          :
            <>
            {members.map( member => (
              <div key={member.userID} className="single-member-container" onMouseEnter={() => onMouseEnterMember(member.userProjectID)} onMouseLeave={onMouseExitMember}>
                <Typography className="single-member">{member.username}</Typography>
                {(member.userID !== currentMember.userID && member.userProjectID === memberToShowIcons) &&
                  <DeleteTwoTone className={setIconsContainerClassName()} color="warning" />
                }
              </div>
            ))}
            </>
          }
        </div>
        <div className="members-footer-container">
          <Button variant="contained" color="success">Add Member</Button>
          <Button variant="contained" color="error">Leave Project</Button>
        </div>
      </div>
  );
};

export default ProjectUsers;
