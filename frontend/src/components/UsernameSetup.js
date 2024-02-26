import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext'; // Adjust the path as necessary
import '../styles/UsernameSetup.css';

const UsernameSetup = () => {
  const [username, setUsername] = useState('');
  const [error, setError] = useState('');
  const [isUsernameValid, setIsUsernameValid] = useState(true); // New state variable
  const navigate = useNavigate();
  const { currentUser } = useAuth();
  const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

  useEffect(() => {
    if (!currentUser) {
      navigate('/');
    }
  }, [navigate, currentUser]);

  const validateUsername = (username) => {
    // Username is valid if its length is between 3 and 100 characters
    return username.length >= 3 && username.length <= 100;
  };

  // Update setUsername function to include validation
  const handleUsernameChange = (e) => {
    const newUsername = e.target.value;
    setUsername(newUsername);
    setIsUsernameValid(validateUsername(newUsername)); // Validate on change
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!currentUser || !isUsernameValid) return; // Also prevent submission if username is invalid

    try {
      const response = await fetch(`${API_BASE_URL}/users/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${currentUser.token}`
        },
        body: JSON.stringify({ username: username })
      });

      const responseData = await response.json();
      if (response.ok) {
        navigate('/home');
      } else {
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
          className={`username-input ${!isUsernameValid ? 'input-error' : ''}`}
          value={username}
          onChange={handleUsernameChange}
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