import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/SplashPage.css';
import app from '../firebaseConfig';
import { getAuth, signInWithPopup, GoogleAuthProvider, GithubAuthProvider } from 'firebase/auth';

const SplashPage = () => {
  const navigate = useNavigate();

  const handleGoogleSignIn = () => {
    const auth = getAuth();
    const provider = new GoogleAuthProvider();
    signInWithPopup(auth, provider)
      .then((result) => {
        // After sign-in, navigate to the UserHomepage
        navigate('/home');
      }).catch((error) => {
        // Handle Errors here.
      });
  }

  const handleGithubSignIn = () => {
    const auth = getAuth();
    const provider = new GithubAuthProvider();
    signInWithPopup(auth, provider)
      .then((result) => {
        // After sign-in, navigate to the UserHomepage
        navigate('/home');
      })
      .catch((error) => {
        // Handle Errors here.
      });
  };

  return (
    <div className="splash-container">
      <h1>Let us solve your project management needs.</h1>
      <h4>Our Opinionated Project Management software manages projects efficiently and effectively.</h4>
      <p>Team Members: James Adelhelm, Ryu Barrett, Giovanni Propersi</p>
      <p>Course: CS467 Online Capstone Project W2024.</p>
      <div className="auth-buttons">
        <button className="google-sign-in" onClick={handleGoogleSignIn}>
          <span>Sign In with Google</span>
        </button>
        <button className="github-sign-in" onClick={handleGithubSignIn}>
          <span>Sign In with GitHub</span>
        </button>
      </div>
    </div>
  );
};

export default SplashPage;
