import React, { useState, memo } from "react";
import { Button, Typography } from "@mui/material";
import { DriveFileRenameOutlineTwoTone, DeleteTwoTone, KeyboardDoubleArrowLeft, KeyboardDoubleArrowRight } from "@mui/icons-material";
import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material';
import { deleteTask } from "../services/tasks";


const BASE_TASK_CONTAINER_CLASS = "task-summary-container"
const HOVER_TASK_CONTAINER = "task-summary-container-hover-border"
const MOUSE_EXIT_TASK_CONTAINER = "task-summary-container-unhover-border"
const BASE_ICONS_CONTAINER_CLASS = "task-summary-icon-menu-bar-container"
const SHOW_ICONS = "show-task-summary-icon-container"
const HIDE_ICONS = "hide-task-summary-icon-container"


const ProjectTask = memo(( { currentTask, removeTask, moveTask }) => {
  const [shouldPlayHideSlideAnimation, setShouldPlayHideSlideAnimation] = useState(0);
  const [showDeleteTaskModal, setShowDeleteTaskModal] = useState(false);
  const [deleteTaskModalAdditionalText, setDeleteTaskModalAdditionalText] = useState('');

  //console.log(currentTask);

  const hideIconsAnimation = () => {
    setShouldPlayHideSlideAnimation(2)
    setTimeout(() => {
      setShouldPlayHideSlideAnimation(0)
    }, 100);
  }

  const handleOnMouseOut = () => {
    if (showDeleteTaskModal) return;
    hideIconsAnimation();
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

  const handleDeleteTask = async () => {
    const response = await deleteTask(currentTask.taskLocation);

    if (response.status === 200) {
      handleDeleteTaskDialogClosed();
      removeTask(currentTask.taskID);
      return;
    }

    const responseJSON = await response.json();
    setDeleteTaskModalAdditionalText(responseJSON.message);
  }

  const handleDeleteTaskDialogClosed = () => {
    setShowDeleteTaskModal(false);
    setDeleteTaskModalAdditionalText('');
    hideIconsAnimation();
  }

  const handleMoveTask = (isLeftMove) => {
    moveTask(currentTask.taskID, isLeftMove);
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
          <KeyboardDoubleArrowLeft className="task-summary-move-icons" onClick={() => handleMoveTask(true)} />
          <KeyboardDoubleArrowRight className="task-summary-move-icons" onClick={() => handleMoveTask(false)} />
        </div>
        <div className="task-summary-edit-delete-icon-container">
          <DeleteTwoTone className="task-summary-edit-delete-icons" color="warning" onClick={() => setShowDeleteTaskModal(true)} />
          <DriveFileRenameOutlineTwoTone className="task-summary-edit-delete-icons" color="secondary" />
          <Dialog
            open={showDeleteTaskModal}
            onClose={handleDeleteTaskDialogClosed}
            aria-labelledby="alert-dialog-title"
            aria-describedby="alert-dialog-description"
            className="alert-delete-task-dialog-container"
            >
            <DialogTitle className="alert-delete-task" id="alert-delete-task-dialog-title">
              {"Delete this task?"}
            </DialogTitle>
            <DialogContent className="alert-delete-task" id="alert-delete-task-dialog-content">
              <DialogContentText id="alert-dialog-description">
                Deleting a task cannot be undone.
              </DialogContentText>
            {deleteTaskModalAdditionalText &&
              <DialogContentText id="alert-dialog-confirmation">
                {deleteTaskModalAdditionalText}
              </DialogContentText>
            }
            </DialogContent>
            <DialogActions className="alert-delete-task" id="alert-delete-task-dialog-actions">
              <Button variant="contained" color="success" onClick={handleDeleteTaskDialogClosed}>Do Not Delete</Button>
              <Button 
                variant="contained" 
                color="error" 
                onClick={handleDeleteTask} 
                disabled={(deleteTaskModalAdditionalText !== '')}
                >
               Delete 
              </Button>
            </DialogActions>
          </Dialog>
        </div>
      </div>
    </div>
  </>
})

export default ProjectTask;
