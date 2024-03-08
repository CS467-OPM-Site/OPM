import React from 'react';
import ProjectColumn from './ProjectColumns';
import '../styles/ProjectContentContainer.css';
import { Divider, Typography } from '@mui/material';
import { useLocation } from 'react-router-dom';

const ProjectContentContainer = ( { columns, extraProps } ) => {
  const location = useLocation();
  console.log(location.path);
  console.log(location.state);


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
