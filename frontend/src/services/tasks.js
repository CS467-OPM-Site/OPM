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
    }
  });

  return response;
}

export const moveTask = async(taskLocation, newColumnID) => {
  const idToken = await getIdToken();
  const taskURL = taskLocation.slice(URL_TRIM.length);
  const response = await fetch(`${API_BASE_URL}${taskURL}/columns/${newColumnID}`, {
    method: "PUT",
    headers: {
      'Authorization': `Bearer ${idToken}`,
    }
  });

  return response;
}

export const addTask = async(projectID, taskDetails) => {
  const idToken = await getIdToken();
  const response = await fetch(`${API_BASE_URL}/projects/${projectID}/tasks`, { 
    method: "POST",
    headers: {
      'Authorization': `Bearer ${idToken}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(taskDetails)
  });

  return response;
}

export const getTask = async(taskLocation) => {
  const idToken = await getIdToken();
  const response = await fetch(`${API_BASE_URL}${taskLocation}`, { 
    method: "GET",
    headers: {
      'Authorization': `Bearer ${idToken}`,
    },
  });

  return response;
}
