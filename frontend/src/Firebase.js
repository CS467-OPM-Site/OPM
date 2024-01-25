// Firebase.js
import { initializeApp } from 'firebase/app';
import { getAuth, GoogleAuthProvider } from 'firebase/auth';
import firebaseConfig from './firebaseConfig';  // Updated import statement

const app = initializeApp(firebaseConfig);
const authInstance = getAuth(app);
const googleProvider = new GoogleAuthProvider();

export { authInstance as auth, googleProvider };