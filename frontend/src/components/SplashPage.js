import React from 'react';
import '../styles/SplashPage.css';


const SplashPage = () => {
  return (
    <div className="splash-container">
      <h1>Let us solve your project management needs.</h1>
      <h4>Our Opinionated Project Management software manages  projects efficiently and effectively.</h4>
      <p>Team Members: James Adelhelm, Ryu Barrett, Giovanni Propersi</p>
      <p>Course: CS467 Online Capstone Project W2024.</p>
      <div className="auth-buttons">
        
        <button className="google-sign-in">
          {/* If you have a Google icon, insert it here */}
          <span>Sign In with Google</span>
        </button>
        <button className="github-sign-in">
          {/* If you have a Google icon, insert it here */}
          <span>Sign In with GitHub</span>
        </button>
        <button className="facebook-sign-in">
          {/* If you have a Google icon, insert it here */}
          <span>Sign In with Facebook</span>
        </button>
        
      </div>
      {/* Add any other content you want on your splash page */}
    </div>
  );
};

export default SplashPage;