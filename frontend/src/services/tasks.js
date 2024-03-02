import { getIdToken } from '../services/users';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL; // Access the .env variable
const URL_TRIM = "/api/v1"

export const deleteTask = async(taskLocation) => {
  const idToken = await getIdToken();
  const taskURL = taskLocation.slice(URL_TRIM.length);
  const response = await fetch(`${API_BASE_URL}${taskURL}`, {
    method: "DELETE",
    headers: {
      'Authorization': `Bearer ${idToken}`,
      'Content-Type': 'application/json',
    }
  });

  return response;
}