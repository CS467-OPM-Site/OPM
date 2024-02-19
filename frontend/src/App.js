import React, { useEffect, useState } from 'react';
import { initializeApp } from 'firebase/app';
import { firebaseConfig } from './firebaseConfig';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext'; // Make sure the path matches where your AuthContext.js is located
import './styles/App.css';
import SplashPage from './components/SplashPage';
import UserHomePage from './components/UserHomepage';
import UsernameSetup from './components/UsernameSetup';


function App() {

  const [isFirebaseInitialized, setIsFirebaseInitialized] = useState(false);

  useEffect(() => {
    const app = initializeApp(firebaseConfig);

    setIsFirebaseInitialized(true);
  }, []);

  if (!isFirebaseInitialized) {
    return <div>Loading...</div>;
  }

  return (
    <Router>
      <div className="App">
        <AuthProvider> {/* Wrap the Routes component with AuthProvider */}
          <Routes>
            {/* The path "/" corresponds to the SplashPage */}
            <Route path="/" element={<SplashPage />} />
            {/* The path "/home" corresponds to the UserHomePage */}
            <Route path="/home" element={<UserHomePage />} />
            {/* The path "/username" corresponds to the UsernameSetup */}
            <Route path="/username" element={<UsernameSetup />} />
          </Routes>
        </AuthProvider>
      </div>
    </Router>
  );
}

export default App;
