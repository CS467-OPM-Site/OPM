import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import '../styles/ProjectManagementPage.css';
import TopBar from './TopBar';
import ProjectColumn from './ProjectColumns';
import ProjectMenuBar from './ProjectMenuBar';
import AddTaskForm from './AddTaskForm';
import TaskDetailPage from './TaskDetailPage';
import { fetchProjectDetails } from '../services/projects';


const ProjectManagementPage = () => {
  const [projectName, setProjectName] = useState('');
  const [projectLocation, setProjectLocation] = useState('');
  const [columns, setColumns] = useState(null);
  const [isColumnBeingMoved, setIsColumnBeingMoved] = useState(false);
  const [projectID, setProjectID] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [columnIDtoAddTaskTo, setColumnIDtoAddTaskTo] = useState(-1);
  const [taskIDtoShow, setTaskIDtoShow] = useState(-1);
  const location = useLocation();

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        const currentProjectID = location.state.projectID;
        setProjectID(currentProjectID);
        const response = await fetchProjectDetails(currentProjectID);         
        const jsonData = await response.json();
        setProjectLocation(jsonData.projectLocation);
        setProjectName(jsonData.projectName);
        console.log(jsonData);
        setColumns(jsonData.columns);

      } catch (error) {
        console.error('Error fetching data: ', error);
      }
    };

    fetchDetails();
  }, []);

  const handleAddingTask = (columnID) => {
    setColumnIDtoAddTaskTo(columnID);
  }
  
  const buildTaskLocation = () => {
    return `${projectLocation}/tasks/${taskIDtoShow}`;
  }

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
        setIsLoading={setIsLoading} 
        isTaskBeingAddedOrShown={(columnIDtoAddTaskTo !== -1 || taskIDtoShow !== -1)}
        setColumnIDtoAddTaskTo={setColumnIDtoAddTaskTo}
        setTaskIDtoShow={setTaskIDtoShow}/>
      {(columnIDtoAddTaskTo === -1 && taskIDtoShow === -1) ?
      <div className='project-content-container'>
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
                setIsOtherColumnBeingMoved={setIsColumnBeingMoved}
                setIsAddingTask={handleAddingTask}
                setTaskIDtoShow={setTaskIDtoShow}/>
          ))}
      </div>
      : (columnIDtoAddTaskTo === -1 && taskIDtoShow !== -1) ? 
        <div className='task-details-content-container'>
            <TaskDetailPage taskLocation={buildTaskLocation()} />
        </div>
        :
        <div className='add-task-content-container'>
          <AddTaskForm 
              projectID={projectID} 
              columnID={columnIDtoAddTaskTo}
              setColumnIDtoAddTaskTo={setColumnIDtoAddTaskTo}
              columns={columns}
              setColumns={setColumns}
              setIsLoading={setIsLoading}/>
        </div>
      }
    </div>
  );
};

export default ProjectManagementPage;
