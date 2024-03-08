import React, { useEffect } from 'react';
import ProjectColumn from './ProjectColumns';
import '../styles/ProjectContentContainer.css';
import { Divider, Typography } from '@mui/material';
import { useLocation, useParams } from 'react-router-dom';
import { fetchProjectUsers } from '../services/projectUsers';
import { fetchProjectSprints } from '../services/sprints';

const ProjectContentContainer = ( { columns, extraProps } ) => {
  const params = useParams();

  useEffect(() => {
    const fetchSprints = async () => {
      try {
        const response = await fetchProjectSprints(params.projectID);         
        const jsonData = await response.json();

        if (response.status !== 200) {
          return;
        }
        console.log(jsonData);

      } catch (error) {
        console.error('Error fetching data: ', error);
      }
    };

    const fetchUsers = async () => {
      try {
        const response = await fetchProjectUsers(params.projectID);         
        const jsonData = await response.json();

        if (response.status !== 200) {
          return;
        }
        console.log(jsonData);

      } catch (error) {
        console.error('Error fetching data: ', error);
      }
    };

    fetchSprints();
    fetchUsers();
  }, []);

  return ( 
  <div className='project-content-container'>
    {(extraProps.cannotLoadProjectError === '') ?
      <>
        <div className='columns-container'>
          {columns && columns
            .sort((a, b) => a.columnIndex - b.columnIndex)
            .map( column => ( 
              <ProjectColumn 
                key={column.columnID} 
                currentColumnIndex={column.columnIndex}
                columns={columns} // State is truly based on this array, shallow copy and set this array on changes
                setColumns={extraProps.setColumns}
                setIsLoading={extraProps.setIsLoading}
                isOtherColumnBeingMoved={extraProps.isOtherColumnBeingMoved}
                setIsOtherColumnBeingMoved={extraProps.setIsOtherColumnBeingMoved}
                setIsTaskBeingAdded={extraProps.setIsTaskBeingAdded}
                setIsTaskBeingShown={extraProps.setIsTaskBeingShown}/>
          ))}
        </div>
      </>
      :
      <>
        <div className='columns-error-container'>
          <Typography className='columns-loading-error' variant="h4">{extraProps.cannotLoadProjectError}</Typography>
        </div>
      </>
    }
    <div className="sprint-member-container">
        <div className="sprints-container">
          <div className="sprints-container-title">
            <Typography className="sprints-title">Sprints</Typography>
          </div>
          <div className="sprints-inner-container">
            Sprints
          </div>
          <div className="sprints-footer-container">
            Buttons
          </div>
        </div>
        <Divider flexItem />
        <div className="members-container">
          <div className="members-container-title">
            <Typography className="members-title">Members</Typography>
          </div>
          <div className="members-inner-container">
            Members 
          </div>
          <div className="members-footer-container">
            Buttons
          </div>
        </div>
    </div>
  </div> 
  );
};

export default ProjectContentContainer;
