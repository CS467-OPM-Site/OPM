import React from 'react';
import ProjectColumn from './ProjectColumns';
import '../styles/ProjectColumnsContainer.css';

const ProjectColumnsContainer = ( { columns, extraProps } ) => {

  return ( 
  <div className='project-content-container'>
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
  );
};

export default ProjectColumnsContainer;
