import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext'; // Adjust the path as necessary
import '../styles/UsernameSetup.css';

const UsernameSetup = () => {
  const [username, setUsername] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { currentUser } = useAuth(); // Use currentUser from AuthContext
  const API_BASE_URL = process.env.REACT_APP_API_BASE_URL; // Access the .env variable

  useEffect(() => {
    // Redirect users who are not logged in
    if (!currentUser) {
      navigate('/'); // Adjust this to your login route
    }
  }, [navigate, currentUser]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!currentUser) return; // Early return if no currentUser exists

    try {
      const response = await fetch(`${API_BASE_URL}/users/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${currentUser.token}` // Use token from currentUser
        },
        body: JSON.stringify({ username: username })
      });

      const responseData = await response.json();
      if (response.ok) {
        navigate('/home');
      } else {
        // Set error message from the response
        setError(responseData.message || 'Failed to register. Please try again.');
      }
    } catch (error) {
      console.error('Error registering user:', error);
      setError('An unexpected error occurred. Please try again.');
    }
  };

  return (
    <div className="username-setup-container">
      <h2 className="username-setup-header">Choose a Username</h2>
      {error && <p className="error-message">{error}</p>}
      <form className="username-setup-form" onSubmit={handleSubmit}>
        <input
          type="text"
          className={`username-input ${error ? 'input-error' : ''}`}
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="Username"
          minLength="3"
          maxLength="100"
          required
        />
        <button type="submit" className="username-submit">Register</button>
      </form>
    </div>
  );
};

export default UsernameSetup;
