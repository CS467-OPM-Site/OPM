import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/SplashPage.css';
import { getAuth, signInWithPopup, GoogleAuthProvider, GithubAuthProvider } from 'firebase/auth';
import BusyBeaverNoBG from '../assets/BusyBeaverNoBG.png';
import { useAuth } from '../contexts/AuthContext'; // Assuming useAuth is correctly set up

const SplashPage = () => {
  const navigate = useNavigate();
  // useAuth could be used here if you need to set or access authentication state
  const { currentUser } = useAuth(); // Example usage, adjust based on your AuthContext

  const authenticateUserWithBackend = async (idToken) => {
    try {
      const response = await fetch('https://opm-api.propersi.me/api/v1/users/auth', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${idToken}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        navigate('/home');
      } else if (response.status === 404) {
        navigate('/username', { state: { idToken } });
      } else {
        const errorData = await response.json();
        alert(`Authentication Failed: ${errorData.message}`);
      }
    } catch (error) {
      console.error('An error occurred during authentication with the backend:', error);
      alert('An error occurred during authentication. Please try again later.');
    }
  };

  const handleSignIn = async (provider) => {
    const auth = getAuth();
    signInWithPopup(auth, provider)
      .then(async (result) => {
        const idToken = await auth.currentUser.getIdToken();
        authenticateUserWithBackend(idToken);
      })
      .catch((error) => {
        // Handle the specific error for account existing with a different credential
        if (error.code === 'auth/account-exists-with-different-credential') {
          alert("An account with this email already exists. Please sign in using your existing Google account.");
        } else {
          // Handle other errors
          console.error('Authentication error with Firebase:', error);
          alert(`Authentication error: ${error.message}`);
        }
      });
  };

  const handleGoogleSignIn = () => {
    const provider = new GoogleAuthProvider();
    handleSignIn(provider);
  };

  const handleGithubSignIn = () => {
    const provider = new GithubAuthProvider();
    handleSignIn(provider);
  };

  return (
    <div className="splash-container">
      <h1>Welcome to BusyBeaver</h1>
      <img src={BusyBeaverNoBG} alt="Busy Beaver" />
      <h4>Let us solve your project management needs.</h4>
      <div className="auth-buttons">
        <button className="google-sign-in" onClick={handleGoogleSignIn}>Sign In with Google</button>
        <button className="github-sign-in" onClick={handleGithubSignIn}>Sign In with GitHub</button>
      </div>
    </div>
  );
};

export default SplashPage;