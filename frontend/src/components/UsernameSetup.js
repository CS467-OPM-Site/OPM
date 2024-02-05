import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/UsernameSetup.css'; 

const UsernameSetup = () => {
    const [username, setUsername] = useState('');
    const navigate = useNavigate();
  
    const handleSubmit = async (event) => {
        event.preventDefault();
        if (username.length < 3 || username.length > 100) {
          alert('Username must be between 3 and 100 characters.');
          return;
        }
      
        try {
          const response = await fetch('http://localhost:5000/api/v1/users/register', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username }),
          });
      
          const data = await response.json(); // Parse JSON response
      
          if (response.ok) {
            navigate('/home');
          } else if (response.status === 400) {
            // Handle specific error based on the backend response
            if (data.message === "User already registered" || data.message === "Username exists") {
              alert(data.message); // Show alert with the specific error message from the backend
              // Optional: Redirect to login or home if user already registered
            } else {
              throw new Error(data.message); // Handle other 400 errors
            }
          } else {
            // Handle other HTTP errors
            throw new Error(`HTTP error! status: ${response.status}`);
          }
        } catch (error) {
          console.error('There was a problem with the fetch operation:', error);
        }
      };
      
      
  
    return (
        <div className="username-setup-container">
          <form className="username-setup-form" onSubmit={handleSubmit}>
            {/* If you have an error state, display error messages here */}
            <input
              type="text"
              className="username-input"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Username"
              minLength="3"
              maxLength="100"
              required
            />
            <button type="submit" className="username-submit">
              Register
            </button>
          </form>
        </div>
      );
  };
  
  export default UsernameSetup;