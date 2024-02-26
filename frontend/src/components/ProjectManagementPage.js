import React, { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useLocation } from 'react-router-dom';
import '../styles/ProjectManagementPage.css';
import TopBar from './TopBar';
import ProjectColumn from './ProjectColumns';
import ProjectMenuBar from './ProjectMenuBar';

const ProjectManagementPage = () => {
  const { currentUser } = useAuth();
  const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
  const location = useLocation();

  const [projectName, setProjectName] = useState("Loading...");
  const [columns, setColumns] = useState([]);

  useEffect(() => {
    const fetchProjectDetails = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/projects/${location.state.projectID}`, { 
          headers: {'Authorization': `Bearer ${currentUser.token}`}});
        const jsonData = await response.json();
        setProjectName(jsonData.projectName);
        console.log(jsonData);
        setColumns(jsonData.columns);

      } catch (error) {
        console.error('Error fetching data: ', error);
      }
    };

    fetchProjectDetails();
    console.log(columns);
  }, []);

  return (
    <div className="user-homepage-container">
      <TopBar />
      <ProjectMenuBar projectName={projectName} />
      <div style={{ display: 'flex', justifyContent: 'left', flexWrap: 'nowrap' }} className='content-container'>
          {columns.map( column => ( 
            <ProjectColumn key={column.columnID} columnTitle={column.columnTitle} columnID={column.columnID}/>
          ))}
      </div>
    </div>
  );
};

export default ProjectManagementPage;
