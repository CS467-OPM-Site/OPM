// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyAN8K2uBFBXqrrmhPoYGP5doyRBR2GhJp8",
  authDomain: "opm-site.firebaseapp.com",
  databaseURL: "https://opm-site-default-rtdb.firebaseio.com",
  projectId: "opm-site",
  storageBucket: "opm-site.appspot.com",
  messagingSenderId: "573743844189",
  appId: "1:573743844189:web:6001ba4d7b3a193044f6b4",
  measurementId: "G-TSTK7EMBDW"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);

export default firebaseConfig;