import { getIdToken } from '../services/users';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL; // Access the .env variable

export const deleteProject = async(idToken, projectID) => {
  const response = await fetch(`${API_BASE_URL}/projects/${projectID}`, {
    method: "DELETE",
    headers: {
      'Authorization': `Bearer ${idToken}`,
    },
  });

  return response;
}

export const fetchProjectDetails = async(projectID) => {
      const idToken = await getIdToken();
      const response = await fetch(`${API_BASE_URL}/projects/${projectID}`, { 
        method: "GET",
        headers: {
          'Authorization': `Bearer ${idToken}`,
        },
      });

      return response;
}

