import React, { useState } from "react";
import { Typography } from "@mui/material";
import { DriveFileRenameOutlineTwoTone, DeleteTwoTone, KeyboardDoubleArrowLeft, KeyboardDoubleArrowRight } from "@mui/icons-material";


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

  return <>
    <div className="task-summary-container" onMouseLeave={handleOnMouseOut} onMouseEnter={() => {setShouldPlayHideSlideAnimation(1)}}>
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
