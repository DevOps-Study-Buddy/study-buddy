// src/components/GoogleCallback.tsx
import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const GoogleCallback: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { login } = useAuth();
  const [error, setError] = useState<string | null>(null);

  const serverAdress = import.meta.env.VITE_API_BASE_URL;
  
  useEffect(() => {
    const code = searchParams.get('code');
    
    if (!code) {
      setError('No authorization code found');
      return;
    }

    const exchangeCodeForToken = async () => {
      try {
        const response = await fetch(`${serverAdress}/api/auth/google/callback`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ code }),
        });

        if (!response.ok) {
          throw new Error('Failed to exchange code for token');
        }

        const userData = await response.json();
        login(userData);
        navigate('/app');
      } catch (error) {
        console.error('Error during authentication:', error);
        setError('Authentication failed. Please try again.');
      }
    };

    exchangeCodeForToken();
  }, [searchParams, login, navigate]);

  if (error) {
    return (
      <div className="auth-error">
        <h2>Authentication Error</h2>
        <p>{error}</p>
        <button onClick={() => navigate('/')}>Return to Home</button>
      </div>
    );
  }

  return (
    <div className="auth-loading">
      <h2>Authenticating...</h2>
      <p>Please wait while we complete the sign-in process.</p>
    </div>
  );
};

export default GoogleCallback;