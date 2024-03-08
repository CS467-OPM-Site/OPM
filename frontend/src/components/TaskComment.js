import { Typography, Divider, Button, TextField } from '@mui/material';
import React, { useState } from 'react';
import '../styles/Comments.css';
import { calculateTimeSinceComment, deleteComment, modifyComment } from '../services/comments';
import { DriveFileRenameOutlineTwoTone, DeleteTwoTone, Cancel, CheckCircleRounded } from "@mui/icons-material";
import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material';

const COULD_NOT_DELETE = "Could not delete comment"
const MODIFY_COMMENT_ERROR = "Comment must not be empty or only spaces"
const GENERAL_ERROR = "Unable to modify comment";


const TaskComment = ( { comment, removeComment } ) => {
  const [showDeleteCommentModal, setShowDeleteCommentModal] = useState(false);
  const [deleteCommentModalAdditionalText, setDeleteCommentModalAdditionalText] = useState('');
  const [isModifyingComment, setIsModifyingComment] = useState(false);
  const [commentBody, setCommentBody] = useState(comment.commentBody);
  const [modifyCommentError, setModifyCommentError] = useState('');

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

  const openModifyCommentTextfield = () => {
    setIsModifyingComment(true);
  }

  const closeModifyCommentTextfield = () => {
    setIsModifyingComment(false);
    setCommentBody(comment.commentBody);
  }

  const handleOnModifyComment = async () => {
    if (commentBody.trim() === '') {
      setModifyCommentError(MODIFY_COMMENT_ERROR);
      return;
    }
    setModifyCommentError('');

    const modifyCommentDetails = { commentBody: commentBody };

    const response = await modifyComment(comment.commentLocation, modifyCommentDetails);
    const responseJSON = await response.json();

    switch (response.status) {
      case 200: {
        setIsModifyingComment(false);
        break;
      } 
      default: {
        if ("message" in responseJSON) {
          setModifyCommentError(responseJSON.message);
          return;
        }
        setModifyCommentError(GENERAL_ERROR);
        return;
      }
    }
  }

  return (
  <div key={comment.commentID} className="single-comment">
      <div className="comment-header">
        <Typography className="italic">{comment.commenterUsername}</Typography>
      </div>
      <Divider className="comment-divider" orientation="horizontal" flexItem />
      <div className="comment-body">
        {isModifyingComment ? 
            <TextField 
              className="add-comment-field"
              label="Comment"
              required
              color="success"
              variant="filled" 
              error={modifyCommentError !== ''}
              helperText={(modifyCommentError !== '') && modifyCommentError}
              multiline 
              rows={4}
              value={commentBody}
              onChange={(e) => { 
                setCommentBody(e.target.value.trim() === '' ? '' : e.target.value) }
              }
              sx={{
                label: { color: "#000000" },
                fieldset: { color: "#000000" },
                "& .MuiFilledInput-root::after": { borderColor: "rgba(129, 255, 154, 0.6)" }
              }}/>
          :
            <Typography>{commentBody}</Typography>
        }
      </div>
      <Divider className="comment-divider" orientation="horizontal" flexItem />
      <div className="comment-footer">
        <Typography className="italic">{timeToPrint}</Typography>
        {comment.isCommenter &&
          <div className="comment-owner-button-container">
            { isModifyingComment ?
              <>
                <CheckCircleRounded className="icon modify-comment-icon" onClick={handleOnModifyComment} color="success" />
                <Cancel className="icon modify-comment-icon" onClick={closeModifyCommentTextfield} color="error" />
              </>
              :
              <>
                <DriveFileRenameOutlineTwoTone 
                  className="comment-edit-delete-icons" 
                  onClick={openModifyCommentTextfield} 
                  color="secondary" />
                <DeleteTwoTone className="comment-edit-delete-icons" color="warning" onClick={openDeleteCommentDialog} />
              </>
            }
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
