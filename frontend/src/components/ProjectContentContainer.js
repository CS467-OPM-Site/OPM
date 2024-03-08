import React, { useEffect } from 'react';
import ProjectColumn from './ProjectColumns';
import '../styles/ProjectContentContainer.css';
import { Divider, Typography } from '@mui/material';
import { useParams } from 'react-router-dom';
import { fetchProjectUsers } from '../services/projectUsers';
import ProjectSprints from './ProjectSprints';
import ProjectUsers from './ProjectUsers';

const ProjectContentContainer = ( { columns, extraProps } ) => {
  const params = useParams();

  useEffect(() => {
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
        <ProjectSprints />
        <Divider flexItem />
        <ProjectUsers />
    </div>
  </div> 
  );
};

export default ProjectContentContainer;
