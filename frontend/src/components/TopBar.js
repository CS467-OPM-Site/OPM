import React from 'react';
import { useNavigate } from 'react-router-dom';
import BusyBeaverNoBG from '../assets/BusyBeaverNoBG.png';
import { Button, Typography } from '@mui/material';

const TopBar = () => {
  const navigate = useNavigate();
  const handleNavigateToHome = () => navigate('/home');
  const handleLogout = () => navigate('/');

  return <>
        <header className="user-homepage-header">
          <div className="busy-beaver-logo" onClick={handleNavigateToHome} style={{ cursor: 'pointer' }}>
            <img src={BusyBeaverNoBG} alt="Busy Beaver Logo" />
          </div>
          <div className="user-homepage-header-card">
            <Typography variant="h4" component="h1">
              Project Management
            </Typography>
          </div>
          <div className="user-homepage-buttons">
            <Button variant="contained" color="error" onClick={handleLogout}>Logout</Button>
          </div>
        </header>
      </>
}

export default TopBar;
