import { Typography, Divider, Button } from '@mui/material';
import React, { useState } from 'react';
import '../styles/Comments.css';
import { calculateTimeSinceComment, deleteComment } from '../services/comments';
import { DriveFileRenameOutlineTwoTone, DeleteTwoTone } from "@mui/icons-material";
import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material';

const COULD_NOT_DELETE = "Could not delete comment"



const TaskComment = ( { comment, removeComment } ) => {
  const [showDeleteCommentModal, setShowDeleteCommentModal] = useState(false);
  const [deleteCommentModalAdditionalText, setDeleteCommentModalAdditionalText] = useState('');
  const timeToPrint = calculateTimeSinceComment(comment.commentedAt);

  const openDeleteCommentDialog = () => {
    setShowDeleteCommentModal(true);
  }

  const handleDeleteComment = async () => {
    const response = await deleteComment(comment.commentLocation);

    if (response.status !== 200) {
      setDeleteCommentModalAdditionalText(COULD_NOT_DELETE);
      return;
    }
    setDeleteCommentModalAdditionalText('');
    removeComment(comment.commentID);
    setShowDeleteCommentModal(false);
  }

  const handleDeleteCommentDialogClosed = () => {
    setShowDeleteCommentModal(false);
  }


  return (
  <div key={comment.commentID} className="single-comment">
      <div className="comment-header">
        <Typography className="italic">{comment.commenterUsername}</Typography>
      </div>
      <Divider className="comment-divider" orientation="horizontal" flexItem />
      <div className="comment-body">
        <Typography>{comment.commentBody}</Typography>
      </div>
      <Divider className="comment-divider" orientation="horizontal" flexItem />
      <div className="comment-footer">
        <Typography className="italic">{timeToPrint}</Typography>
        {comment.isCommenter &&
          <div className="comment-owner-button-container">
          <DeleteTwoTone className="comment-edit-delete-icons" color="warning" onClick={openDeleteCommentDialog} />
          <DriveFileRenameOutlineTwoTone 
            className="comment-edit-delete-icons" 
            onClick={() => {}} 
            color="secondary" />
          </div>
        }
      </div>
      <Dialog
        open={showDeleteCommentModal}
        onClose={handleDeleteCommentDialogClosed}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
        className="alert-delete-comment-dialog-container"
        >
        <DialogTitle className="alert-delete-comment" id="alert-delete-comment-dialog-title">
          {"Delete this comment?"}
        </DialogTitle>
        <DialogContent className="alert-delete-comment" id="alert-delete-comment-dialog-content">
          <DialogContentText id="alert-dialog-description">
            Deleting a comment cannot be undone.
          </DialogContentText>
          {deleteCommentModalAdditionalText &&
            <DialogContentText id="alert-dialog-confirmation">
              {deleteCommentModalAdditionalText}
            </DialogContentText>
          }
        </DialogContent>
        <DialogActions className="alert-delete-comment" id="alert-delete-comment-dialog-actions">
          <Button variant="contained" color="success" onClick={handleDeleteCommentDialogClosed}>Do Not Delete</Button>
          <Button 
            variant="contained" 
            color="error" 
            onClick={handleDeleteComment} 
            >
           Delete 
          </Button>
        </DialogActions>
      </Dialog>
  </div>
  )
};


export default TaskComment;
