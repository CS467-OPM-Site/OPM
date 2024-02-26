
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL; // Access the .env variable

export const addColumn = async(idToken, columnTitle, projectID) => {
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

