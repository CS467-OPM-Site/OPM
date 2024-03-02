import React, { useState } from "react";
import { Typography } from "@mui/material";
import { DriveFileRenameOutlineTwoTone, DeleteTwoTone, KeyboardDoubleArrowLeft, KeyboardDoubleArrowRight } from "@mui/icons-material";


const BASE_TASK_CONTAINER_CLASS = "task-summary-container"
const HOVER_TASK_CONTAINER = "task-summary-container-hover-border"
const MOUSE_EXIT_TASK_CONTAINER = "task-summary-container-unhover-border"
const BASE_ICONS_CONTAINER_CLASS = "task-summary-icon-menu-bar-container"
const SHOW_ICONS = "show-task-summary-icon-container"
const HIDE_ICONS = "hide-task-summary-icon-container"


const ProjectTask = ({ currentTask }) => {
  const [shouldPlayHideSlideAnimation, setShouldPlayHideSlideAnimation] = useState(0);

  const handleOnMouseOut = () => {
    setShouldPlayHideSlideAnimation(2)
    setTimeout(() => {
      setShouldPlayHideSlideAnimation(0)
    }, 100);
  }

  const setIconContainerClass = () => {
    switch (shouldPlayHideSlideAnimation) {
      case 0: return BASE_ICONS_CONTAINER_CLASS;
      case 1: {
        return `${BASE_ICONS_CONTAINER_CLASS} ${SHOW_ICONS}`
      } 
      case 2: {
        return `${BASE_ICONS_CONTAINER_CLASS} ${HIDE_ICONS}`
      }
    }
  }

  const setTaskContainerClass = () => {
    switch (shouldPlayHideSlideAnimation) {
      case 0: return BASE_TASK_CONTAINER_CLASS;
      case 1: {
        return `${BASE_TASK_CONTAINER_CLASS} ${HOVER_TASK_CONTAINER}`
      } 
      case 2: {
        return `${BASE_TASK_CONTAINER_CLASS} ${MOUSE_EXIT_TASK_CONTAINER}`
      }
    }
  }

  

  return <>
    <div className={setTaskContainerClass()} onMouseLeave={handleOnMouseOut} onMouseEnter={() => {setShouldPlayHideSlideAnimation(1)}}>
      <Typography className="task-summary-title" sx={{ fontWeight: 700, fontSize: '0.9rem' }} gutterBottom={true}>{currentTask.title}</Typography>
      <Typography sx={{ fontWeight: 500, fontSize: '0.9rem' }}>Priority: {currentTask.priority}</Typography>
      <div className="task-summary-due-date-comments-container">
        <Typography sx={{ fontWeight: 500, fontSize: '0.9rem' }}>Due Date: {currentTask.dueDate ? currentTask.dueDate : "None"}</Typography>
        <Typography sx={{ fontWeight: 500, fontSize: '0.9rem' }}>Comments: {currentTask.comments}</Typography>
      </div>
      <div className={setIconContainerClass()}>
        <div className="task-summary-move-task-icon-container">
          <KeyboardDoubleArrowLeft className="task-summary-move-icons" />
          <KeyboardDoubleArrowRight className="task-summary-move-icons" />
        </div>
        <div className="task-summary-edit-delete-icon-container">
          <DeleteTwoTone className="task-summary-edit-delete-icons" color="warning" />
          <DriveFileRenameOutlineTwoTone className="task-summary-edit-delete-icons" color="secondary" />
        </div>
      </div>
    </div>
  </>
}

export default ProjectTask;
