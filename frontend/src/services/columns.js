import { getIdToken } from '../services/users';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL; // Access the .env variable
const URL_TRIM = "/api/v1"

export const addColumn = async(columnTitle, projectID) => {
  const idToken = await getIdToken();
  const response = await fetch(`${API_BASE_URL}/projects/${projectID}/columns`, {
    method: "POST",
    headers: {
      'Authorization': `Bearer ${idToken}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ columnTitle: columnTitle })
  });

  return response;
}

export const deleteColumn = async(columnLocation) => {
  const idToken = await getIdToken();
  const columnUrl = columnLocation.slice(URL_TRIM.length);
  const response = await fetch(`${API_BASE_URL}${columnUrl}`, {
    method: "DELETE",
    headers: {
      'Authorization': `Bearer ${idToken}`,
    },
  });

  return response;
}

export const moveColumn = async(columnLocation, newColumnIndex) => {
  const idToken = await getIdToken();
  const columnUrl = columnLocation.slice(URL_TRIM.length);
  const response = await fetch(`${API_BASE_URL}${columnUrl}/order`, {
    method: "PUT",
    headers: {
      'Authorization': `Bearer ${idToken}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ columnIndex: newColumnIndex})
  });

  return response;
}

