import React, { useEffect, useState } from 'react';
import { initializeApp } from 'firebase/app';
import { firebaseConfig } from './firebaseConfig';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext'; // Make sure the path matches where your AuthContext.js is located
import './styles/App.css';
import SplashPage from './components/SplashPage';
import UserHomePage from './components/UserHomepage';
import UsernameSetup from './components/UsernameSetup';
import ProjectManagementPage from './components/ProjectManagementPage';



function App() {


  const [isFirebaseInitialized, setIsFirebaseInitialized] = useState(false);

  useEffect(() => {
    initializeApp(firebaseConfig);
    setIsFirebaseInitialized(true);
  }, []);

  if (!isFirebaseInitialized) {
    // Wait until firebase is initialized before doing anything else, or
    // the AuthContext will fail
    return <div>Loading...</div>;
  }

  return (
    <BrowserRouter>
      <div className="App">
        <AuthProvider> {/* Wrap the Routes component with AuthProvider */}
          <Routes>
            {/* The path "/" corresponds to the SplashPage */}
            <Route path="/" element={<SplashPage />} />
            {/* The path "/home" corresponds to the UserHomePage */}
            <Route path="/home" element={<UserHomePage />} />
            {/* The path "/username" corresponds to the UsernameSetup */}
            <Route path="/username" element={<UsernameSetup />} />
            {/* The path "/projects" corresponds to the ProjectPage */}
            <Route path="/projects/:projectID/*" element={<ProjectManagementPage />} />
          </Routes>
        </AuthProvider>
      </div>
    </BrowserRouter>
  );
}

export default App;
