import React from 'react';
import '../styles/SplashPage.css';
import { getAuth, signInWithPopup, GoogleAuthProvider } from 'firebase/auth';
import { initializeApp } from 'firebase/app';
import app from '../firebaseConfig';




const SplashPage = () => {
  const handleGoogleSignIn = () => {
    const auth = getAuth();
    const provider = new GoogleAuthProvider();
    signInWithPopup(auth, provider)
      .then((result) => {
        // This gives you a Google Access Token. You can use it to access the Google API.
        // const credential = GoogleAuthProvider.credentialFromResult(result);
        // const token = credential.accessToken;
        // The signed-in user info.
        // const user = result.user;
        // ...
      }).catch((error) => {
        // Handle Errors here.
        // const errorCode = error.code;
        // const errorMessage = error.message;
        // The email of the user's account used.
        // const email = error.email;
        // The AuthCredential type that was used.
        // const credential = GoogleAuthProvider.credentialFromError(error);
        // ...
      });
  }

  return (
    <div className="splash-container">
      <h1>Let us solve your project management needs.</h1>
      <h4>Our Opinionated Project Management software manages  projects efficiently and effectively.</h4>
      <p>Team Members: James Adelhelm, Ryu Barrett, Giovanni Propersi</p>
      <p>Course: CS467 Online Capstone Project W2024.</p>
      <div className="auth-buttons">
        
        <button className="google-sign-in" onClick={handleGoogleSignIn}>
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