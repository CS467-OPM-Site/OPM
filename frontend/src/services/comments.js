import { getIdToken } from '../services/users';
import dayjs from 'dayjs';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL; // Access the .env variable
const URL_TRIM = "/api/v1"

export const addComment = async(taskLocation, commentDetails) => {
  const idToken = await getIdToken();
  const response = await fetch(`${API_BASE_URL}${taskLocation}/comments`, {
    method: "POST",
    headers: {
      'Authorization': `Bearer ${idToken}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(commentDetails)
  });

  return response;
}

export const deleteComment = async(commentLocation) => {
  const idToken = await getIdToken();
  const commentURL = commentLocation.slice(URL_TRIM.length);

  const response = await fetch(`${API_BASE_URL}${commentURL}`, {
    method: "DELETE",
    headers: {
      'Authorization': `Bearer ${idToken}`
    },
  });

  return response;
}

export const calculateTimeSinceComment = (timeCommentedAt) => {
  const today = dayjs();
  const commentTime = dayjs(timeCommentedAt);

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
      // 0-7 days ago
      if (daysDiff === 0) {
        const hoursDiff = today.diff(commentTime, 'hour');
        timeToPrint = hoursDiff === 1 ? `${hoursDiff} hour ago` : `${hoursDiff} hours ago`;

      } else {
        timeToPrint = daysDiff === 1 ? `${daysDiff} day ago` : `${daysDiff} days ago`;
      }
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
      if (minutesDiff > 1) {
          timeToPrint = `${minutesDiff} minutes ago`;
      } else if (minutesDiff === 1) {
          timeToPrint = "1 minute ago";
      } else {
          timeToPrint = "Less than a minute ago";
      }
    }
  }

  return timeToPrint;
}
