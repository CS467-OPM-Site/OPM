// App.js
import React, { useEffect, useState } from 'react';
import logo from './logo.svg';
import { auth, googleProvider } from './Firebase';

function App() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const unsubscribe = auth.onAuthStateChanged((authUser) => {
      setUser(authUser);
    });

    return () => {
      unsubscribe();
    };
  }, []);

  const handleGoogleLogin = async () => {
    try {
      const result = await auth.signInWithPopup(googleProvider);
      // Handle successful login
      console.log('Google Login Success:', result.user);
    } catch (error) {
      // Handle error
      console.error('Google Login Error:', error);
    }
  };

  return (
    <div className="App">
      {user ? (
        <p>Welcome, {user.displayName}!</p>
      ) : (
        <div className="Splash">
          <img src={logo} className="Splash-logo" alt="logo" />
          <h1>OPM Site</h1>
          <p>Let us solve your project management needs.</p>
          <button onClick={handleGoogleLogin}>Login with Google</button>
        </div>
      )}
    </div>
  );
}

export default App;