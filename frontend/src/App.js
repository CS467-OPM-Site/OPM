import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './styles/App.css';
import SplashPage from './components/SplashPage';
import UserHomePage from './components/UserHomepage';

function App() {
  return (
    <Router>
      <div className="App">
        {/* Define your routes within the Routes component */}
        <Routes>
          {/* The path "/" corresponds to the SplashPage */}
          <Route path="/" element={<SplashPage />} />
          {/* The path "/home" corresponds to the UserHomePage */}
          <Route path="/home" element={<UserHomePage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
