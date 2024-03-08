import React, { useEffect, useState } from 'react';
import { useLocation, Route, Routes, Outlet, useParams } from 'react-router-dom';
import '../styles/ProjectManagementPage.css';
import TopBar from './TopBar';
import ProjectMenuBar from './ProjectMenuBar';
import AddTaskForm from './AddTaskForm';
import TaskDetailPage from './TaskDetailPage';
import { fetchProjectDetails } from '../services/projects';
import ProjectContentContainer from './ProjectContentContainer';

const UNABLE_TO_LOAD_PROJECT = "Unable to load given project."

const ProjectManagementPage = () => {
  const [projectName, setProjectName] = useState('');
  const [columns, setColumns] = useState(null);
  const [isColumnBeingMoved, setIsColumnBeingMoved] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isTaskBeingAdded, setIsTaskBeingAdded] = useState(false);
  const [isTaskBeingShown, setIsTaskBeingShown] = useState(false);
  const [cannotLoadProjectError, setCannotLoadProjectError] = useState('');
  const params = useParams();

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        const response = await fetchProjectDetails(params.projectID);         
        const jsonData = await response.json();

        if (response.status !== 200) {
          setProjectName("N/A");
          setIsLoading(false);
          setCannotLoadProjectError(UNABLE_TO_LOAD_PROJECT);
          return;
        }
        setCannotLoadProjectError('');
        setProjectName(jsonData.projectName);
        console.log(jsonData);
        setColumns(jsonData.columns);

      } catch (error) {
        console.error('Error fetching data: ', error);
      }
    };

    fetchDetails();
  }, []);

  const setTaskNotBeingAddedOrShown = () => {
    setIsTaskBeingShown(false);
    setIsTaskBeingAdded(false);
  }

  const setColumnProps = () => {
    return {
      setColumns: setColumns,
      setIsLoading: setIsLoading,
      isOtherColumnBeingMoved: isColumnBeingMoved,
      setIsOtherColumnBeingMoved: setIsColumnBeingMoved,
      setIsTaskBeingAdded: setIsTaskBeingAdded,
      setIsTaskBeingShown: setIsTaskBeingShown,
      cannotLoadProjectError: cannotLoadProjectError
    }
  }
  
  return (
      <div className="project-management-container">
        <TopBar />
        <ProjectMenuBar 
          key={projectName} 
          projectName={projectName} 
          columns={columns} 
          setColumns={setColumns} 
          isLoading={isLoading}
          setIsLoading={setIsLoading} 
          isTaskBeingAddedOrShown={isTaskBeingAdded || isTaskBeingShown}
          setTaskNotBeingAddedOrShown={setTaskNotBeingAddedOrShown}/>
        <Routes>
          <Route path='' element={<Outlet />}>
            <Route index element={
                <ProjectContentContainer columns={columns} extraProps={setColumnProps()} />
            } />
            <Route path='/tasks' element={
                <AddTaskForm 
                    columns={columns}
                    setColumns={setColumns}
                    setIsLoading={setIsLoading}
                    setIsTaskBeingAdded={setIsTaskBeingAdded}/>
                } />
            <Route path='/tasks/:taskId' element={
                <TaskDetailPage columns={columns} setColumns={setColumns} />
                } />
          </Route>
        </Routes>
    </div>
  );
};

export default ProjectManagementPage;

