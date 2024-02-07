import { useNavigate } from 'react-router-dom';
import '../styles/SplashPage.css';
import app from '../firebaseConfig';
// import the image file from the assets folder
import { getAuth, signInWithPopup, GoogleAuthProvider, GithubAuthProvider } from 'firebase/auth';
import BusyBeaverImage from '../assets/BusyBeaverImage.webp';
// // Citation for BusyBeaverImage: OpenAI DALL E Image Generator (https://openai.com/research/dall-e/)
// Downloaded image at 2024-02-07 14:27:00 with prompt "generate some good logos to go on the front page and maybe like a corner icon for this project that match the css. It's our senior computer science capstone project at Oregon State university and we want our project management site to be called BusyBeaver (like oregon state beavers)"

const SplashPage = () => {
  const navigate = useNavigate();

  const handleGoogleSignIn = () => {
    const auth = getAuth();
    const provider = new GoogleAuthProvider();
    signInWithPopup(auth, provider)
      .then((result) => {
        // After sign-in, navigate to the UserHomepage
        navigate('/username');
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
        navigate('/username');
      })
      .catch((error) => {
        if (error.code === 'auth/account-exists-with-different-credential') {
          // Display message to user if they already have an account with the same email address but different sign-in method
          alert("An account with this email already exists. Please sign in using your existing Google account.");
        } 
      });
  };

  
  return (
    <div className="splash-container">
      
      <h1>Welcome to Busy Beaver</h1>
      
      
      <img src={BusyBeaverImage} alt="Busy Beaver" />  
      <h4>Let us solve your project management needs.</h4>
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