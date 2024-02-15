import { useNavigate } from 'react-router-dom';
import '../styles/SplashPage.css';
import app from '../firebaseConfig';
// import the image file from the assets folder
import { getAuth, signInWithPopup, GoogleAuthProvider, GithubAuthProvider } from 'firebase/auth';
import BusyBeaverNoBG from '../assets/BusyBeaverNoBG.png';
// // Citation for BusyBeaverImage: OpenAI DALL E Image Generator (https://openai.com/research/dall-e/)
// Downloaded image at 2024-02-07 14:27:00 with prompt "generate some good logos to go on the front page and maybe like a corner icon for this project that match the css. It's our senior computer science capstone project at Oregon State university and we want our project management site to be called BusyBeaver (like oregon state beavers)"

const SplashPage = () => {
  const navigate = useNavigate();

  const authenticateUserWithBackend = async (idToken) => {
    // Attempt to authenticate the user with the backend
    const response = await fetch('https://opm-api.propersi.me/api/v1/users/auth', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${idToken}`,
        'Content-Type': 'application/json',
      },
    });

    // Check the response from the backend
    if (response.ok) {
      // If authentication is successful (200), navigate to the user's home page
      navigate('/home');
    } else if (response.status === 404) {
      // If user does not exist (404), redirect to the registration page
      navigate('/username', { state: { idToken } });
    } else {
      // Handle other responses or errors
      console.error('An error occurred during authentication');
    }
  };

  const handleSignIn = async (provider) => {
    const auth = getAuth();
    signInWithPopup(auth, provider).then(async (result) => {
      const idToken = await auth.currentUser.getIdToken();
      // Use the ID token to authenticate with the backend
      authenticateUserWithBackend(idToken);
    }).catch((error) => {
      // Handle Errors here.
      if (error.code === 'auth/account-exists-with-different-credential') {
        // Handle account exists with different credential error
        alert("An account with this email already exists. Please sign in using your existing account.");
      } else {
        console.error('Authentication error with Firebase:', error);
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
        <button className="google-sign-in" onClick={handleGoogleSignIn}>
          Sign In with Google
        </button>
        <button className="github-sign-in" onClick={handleGithubSignIn}>
          Sign In with GitHub
        </button>
      </div>
    </div>
  );
};

export default SplashPage;
