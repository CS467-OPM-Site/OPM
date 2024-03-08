import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { fetchProjectSprints } from '../services/sprints';
import { Button, Typography } from '@mui/material';
import { DriveFileRenameOutlineTwoTone, DeleteTwoTone, Cancel, CheckCircleRounded } from "@mui/icons-material";

const ProjectSprints = () => {
  const [sprints, setSprints] = useState([]);
  const [sprintToShowIconsFor, setSprintToShowIconsFor] = useState(null)
  const params = useParams();

  useEffect(() => {
    const fetchSprints = async () => {
      try {
        const response = await fetchProjectSprints(params.projectID);         
        const jsonData = await response.json();

        if (response.status !== 200) {
          return;
        }
        setSprints(jsonData.sprints);
        console.log(jsonData);

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

  return ( 
      <div className="sprints-container">
        <div className="sprints-container-title">
          <Typography className="sprints-title">Sprints</Typography>
        </div>
        <div className="sprints-inner-container">
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
                      <DeleteTwoTone className="icon sprint-member-icon" color="warning" />
                      <DriveFileRenameOutlineTwoTone className="icon sprint-member-icon" color="secondary" />
                    </div>
                  }
                </div>
                <div className="single-sprint-date-container">
                  <Typography>Start: {sprint.startDate}</Typography>
                  <Typography>End: {sprint.endDate}</Typography>
                </div>
              </div>
            ))}
            </>
          }
        </div>
        <div className="sprints-footer-container">
          <Button variant="contained" color="success">Add Sprint</Button>
        </div>
      </div>
  );
};

export default ProjectSprints;
