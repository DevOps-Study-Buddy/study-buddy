// src/components/LandingPage.tsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import './LandingPage.css';

const LandingPage: React.FC = () => {
  const navigate = useNavigate();

  const handleGoogleLogin = () => {
    // This will be replaced with actual Google OAuth implementation
    window.location.href = '/api/auth/google';
  };

  return (
    <div className="landing-container">
      <div className="landing-header">
        <div className="app-logo">
          <img src="/study-buddy-icon.svg" alt="Study Buddy" />
          <h1>Study Buddy</h1>
        </div>
      </div>
      
      <div className="landing-content">
        <div className="hero-section">
          <h2>Your AI Study Assistant</h2>
          <p>Upload your documents and get instant summaries, study notes, and practice questions.</p>
          
          <div className="cta-buttons">
            <button className="google-login-btn" onClick={handleGoogleLogin}>
              <img src="/google-icon.svg" alt="Google" />
              Sign in with Google
            </button>
            <button className="demo-btn" onClick={() => navigate('/demo')}>
              Try Demo
            </button>
          </div>
        </div>
        
        <div className="features-section">
          <div className="feature">
            <div className="feature-icon">📚</div>
            <h3>Document Analysis</h3>
            <p>Upload lecture notes, textbooks, and research papers for instant insights.</p>
          </div>
          
          <div className="feature">
            <div className="feature-icon">🧠</div>
            <h3>AI-Powered Summaries</h3>
            <p>Get concise summaries of complex topics to boost your understanding.</p>
          </div>
          
          <div className="feature">
            <div className="feature-icon">✏️</div>
            <h3>Practice Questions</h3>
            <p>Generate practice questions to test your knowledge and prepare for exams.</p>
          </div>
        </div>
      </div>
      
      <footer className="landing-footer">
        <p>&copy; {new Date().getFullYear()} Study Buddy. All rights reserved.</p>
        <div className="footer-links">
          <a href="/privacy">Privacy Policy</a>
          <a href="/terms">Terms of Service</a>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage;
