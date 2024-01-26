// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "your-api-key",
  authDomain: "opm-webapp.firebaseapp.com",
  projectId: "opm-webapp",
  storageBucket: "opm-webapp.appspot.com",
  messagingSenderId: "your-messaging-sender-id",
  appId: "your-app-id"
};


// Initialize Firebase
const app = initializeApp(firebaseConfig);