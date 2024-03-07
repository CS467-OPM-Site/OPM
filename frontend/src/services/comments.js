import { getIdToken } from '../services/users';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL; // Access the .env variable

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
