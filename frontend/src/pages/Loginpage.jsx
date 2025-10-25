import { useState } from 'react';
import { User, Lock } from 'lucide-react';
import './login.css';
import { IoEyeOutline } from "react-icons/io5";
import { FaRegEyeSlash } from "react-icons/fa";
export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const handleSubmit = () => {
    console.log('Login attempt:', { username, password });
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  }

  return (
    <>
     <div className="login-container">
        {/* Diagonal background stripes */}
        <div className="background-stripes">
          <div className="stripe-1"></div>
          <div className="stripe-2"></div>
          <div className="stripe-3"></div>
          <div className="stripe-4"></div>
        </div>

        {/* Main login card container */}
        <div className="login-card">
          
          {/* Left side - Login form */}
          <div className="login-form-section">
            <div className="form-container">
              
              {/* Login header */}
              <div className="login-header">
                <h1 className="login-title">Log in</h1>
              </div>

              {/* Login form */}
              <div className="form-fields">
                
                {/* Username field */}
                <div className="input-group">
                  <User className="input-icon" />
                  <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    className="form-input"
                    required
                  />
                </div>

                {/* Password field */}
                <div className="input-group">
                  <Lock className="input-icon" />
                  <input
                    type={showPassword ? "text" : "password"}
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="form-input"
                    required
                  />
                  <button 
                  className='input-icon1'
                  type="button"
                  onClick={togglePasswordVisibility}
                  aria-label={showPassword ? "Hide password" : "Show password"}
                  >
                    {showPassword ?  <FaRegEyeSlash className='eye-icon' />: <IoEyeOutline className='eye-icon' />}
                  </button>
                </div>

                {/* Login button */}
                <button
                  onClick={handleSubmit}
                  className="login-button"
                >
                  Log In
                </button>
              </div>
            </div>
          </div>

          {/* Right side - Purple gradient panel */}
          <div className="gradient-panel">
            
            {/* Decorative elements */}
            <div className="panel-decorations">
              <div className="decoration-1"></div>
              <div className="decoration-2"></div>
              <div className="decoration-3"></div>
            </div>

            {/* Diagonal stripes effect */}
            <div className="panel-stripes">
              <div className="diagonal-stripe"></div>
              <div className="diagonal-stripe"></div>
              <div className="diagonal-stripe"></div>
              <div className="diagonal-stripe"></div>
              <div className="diagonal-stripe"></div>
              <div className="diagonal-stripe"></div>
              <div className="diagonal-stripe"></div>
              <div className="diagonal-stripe"></div>
            </div>

            {/* Content overlay */}
            <div className="panel-content">
              <div className="welcome-content">
                <div className="welcome-icon-container">
                  <div className="welcome-icon-inner">
                   <img src="/logo.png" alt="Bake House Logo" className="welcome-icon" />
                  </div>
                </div>
                <h2 className="welcome-title">Welcome Back</h2>
                <p className="welcome-subtitle">Enter your credentials to access your account</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}