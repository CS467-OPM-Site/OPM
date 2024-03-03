import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import '../styles/ProjectManagementPage.css';
import TopBar from './TopBar';
import ProjectColumn from './ProjectColumns';
import ProjectMenuBar from './ProjectMenuBar';
import { fetchProjectDetails } from '../services/projects';


const ProjectManagementPage = () => {
  const [projectName, setProjectName] = useState('');
  const [columns, setColumns] = useState(null);
  const [isColumnBeingMoved, setIsColumnBeingMoved] = useState(false);
  const [projectID, setProjectID] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const location = useLocation();

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        const currentProjectID = location.state.projectID;
        setProjectID(currentProjectID);
        const response = await fetchProjectDetails(currentProjectID);         
        const jsonData = await response.json();
        setProjectName(jsonData.projectName);
        console.log(jsonData);
        setColumns(jsonData.columns);

      } catch (error) {
        console.error('Error fetching data: ', error);
      }
    };

    fetchDetails();
  }, []);

  return (
    <div className="user-homepage-container">
      <TopBar />
      <ProjectMenuBar 
        key={projectID} 
        projectName={projectName} 
        projectID={projectID} 
        columns={columns} 
        setColumns={setColumns} 
        isLoading={isLoading}
        setIsLoading={setIsLoading} />
      <div style={{ display: 'flex', justifyContent: 'left', flexWrap: 'nowrap', overflow: 'auto' }} className='content-container'>
          {columns && columns
            .sort((a, b) => a.columnIndex - b.columnIndex)
            .map( column => ( 
              <ProjectColumn 
                key={column.columnID} 
                currentColumnIndex={column.columnIndex}
                columns={columns} // State is truly based on this array, shallow copy and set this array on changes
                setColumns={setColumns}
                setIsLoading={setIsLoading}
                isOtherColumnBeingMoved={isColumnBeingMoved}
                setIsOtherColumnBeingMoved={setIsColumnBeingMoved}/>
          ))}
      </div>
    </div>
  );
};

export default ProjectManagementPage;
