import { getIdToken } from '../services/users';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL; // Access the .env variable

export const fetchProjectSprints = async(projectID) => {
      const idToken = await getIdToken();
      const response = await fetch(`${API_BASE_URL}/projects/${projectID}/sprints`, { 
        method: "GET",
        headers: {
          'Authorization': `Bearer ${idToken}`,
        },
      });

      return response;
}

export const addSprint = async(projectID, newSprintDetails) => {
      const idToken = await getIdToken();
      const response = await fetch(`${API_BASE_URL}/projects/${projectID}/sprints`, { 
        method: "POST",
        headers: {
          'Authorization': `Bearer ${idToken}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newSprintDetails)
      });

      return response;
}

export const deleteSprint = async(projectID, sprintID) => {
      const idToken = await getIdToken();
      const response = await fetch(`${API_BASE_URL}/projects/${projectID}/sprints/${sprintID}`, { 
        method: "DELETE",
        headers: {
          'Authorization': `Bearer ${idToken}`,
        },
      });

      return response;
}


