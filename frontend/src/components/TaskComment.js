import { Typography, Divider } from '@mui/material';
import React from 'react';
import dayjs from 'dayjs';
import '../styles/Comments.css';


const TaskComment = ( { comment } ) => {
  const today = dayjs();
  const commentTime = dayjs(comment.commentedAt);

  let timeToPrint = commentTime;
  if (commentTime.isBefore(today, 'day')) {
    const daysDiff = today.diff(commentTime, 'day');

    if (daysDiff > 7) {
      // Last week
      const monthsDiff = today.diff(commentTime, 'month');

      if (monthsDiff >= 1) {
        // Some months ago

        if (monthsDiff >= 12) {
          // 1 year or more ago
          const yearsDiff = today.diff(commentTime, 'year');
          timeToPrint = yearsDiff === 1 ? `${yearsDiff} years ago` : `${yearsDiff} year ago` ;
        } else {
          // Within the last year
          timeToPrint = monthsDiff === 1 ? `${monthsDiff} month ago` : `${monthsDiff} months ago`;
        }

      } else {
        // 1-4 weeks ago
        const weeksDiff = today.diff(commentTime, 'week');
        timeToPrint = weeksDiff === 1 ? `${weeksDiff} week ago` : `${weeksDiff} weeks ago`;
      }

    } else {
      // 1-7 days ago
      timeToPrint = daysDiff === 1 ? `${daysDiff} day ago` : `${daysDiff} days ago`;

    }
  } else {
    // Within 24 hours
    const hoursDiff = today.diff(commentTime, 'hour');
    if (hoursDiff >= 1) {
      // 1 or more hours ago
      timeToPrint = hoursDiff === 1 ? `${hoursDiff} hour ago` : `${hoursDiff} hours ago`;

    } else {
      // Less than an hour ago
      const minutesDiff = today.diff(commentTime, 'minute');
      const secondsDiff = today.diff(commentTime, 'second');
      console.log(today);
      console.log(commentTime);
      console.log(`Minutes diff: ${minutesDiff}`)
      console.log(`Seconds diff: ${secondsDiff}`)
      switch (minutesDiff) {
        case (minutesDiff < 1): {
          timeToPrint = "Less than a minute ago";
          break;
        }
        case (minutesDiff === 1): {
          timeToPrint = "1 minute ago";
          break;
        }
        default: {
          timeToPrint = `${minutesDiff} minutes ago`;
        }
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
        <Typography>{comment.commentBody}</Typography>
      </div>
      <Divider className="comment-divider" orientation="horizontal" flexItem />
      <div className="comment-footer">
        <Typography className="italic">{timeToPrint}</Typography>
      </div>
  </div>
  )
};


export default TaskComment;
