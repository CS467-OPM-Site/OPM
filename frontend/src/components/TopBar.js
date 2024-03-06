import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import BusyBeaverNoBG from '../assets/BusyBeaverNoBG.png';
import { Button, Typography } from '@mui/material';

const TopBar = () => {
  const navigate = useNavigate();
  const { logout } = useAuth(); // Destructure logout from useAuth
  const handleNavigateToHome = () => navigate('/home');
  
  const handleLogout = async () => {
    try {
      await logout();
      navigate('/'); // Redirect to splash page
    } catch (error) {
      console.error("Failed to log out", error);
    }
  };

  return <>
        <header className="user-homepage-header">
          
          <div className="busy-beaver-logo" onClick={handleNavigateToHome} style={{ cursor: 'pointer' }}>
            <img src={BusyBeaverNoBG} alt="Busy Beaver Logo" />
          </div>
          <div className="user-homepage-header-card">
            <Typography variant="" component="h1">
              BusyBeaver OPM Site
            </Typography>
          </div>
          <div className="user-homepage-buttons">
            <Button variant="contained" color="error" onClick={handleLogout}>Logout</Button>
          </div>
        </header>
      </>
}

export default TopBar;