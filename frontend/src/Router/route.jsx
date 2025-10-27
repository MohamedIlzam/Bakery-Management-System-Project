import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from '../pages/Loginpage';
import Dashboard from '../pages/Dashboard';
// import other pages as needed

export default function AppRouter() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/dashboard" element={<Dashboard />} />
        {/* Add more routes here */}
      </Routes>
    </Router>
  );
}