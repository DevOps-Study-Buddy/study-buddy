// src/contexts/AuthContext.tsx
import React, { createContext, useState, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

// Define User type based on backend response
interface User {
  id: string;
  email: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (token: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const serverAdress = import.meta.env.VITE_API_BASE_URL;

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {

const checkAuthStatus = async () => {
  
  try {
    const response = await fetch(`${serverAdress}/users/me`, {
      method: 'GET',
      credentials: 'include',  // This ensures cookies are sent automatically
    });
    

    if (response.ok) {
      const userData = await response.json();
      console.log(userData);
      setUser(userData);
    } else {
      console.error('Failed to fetch user details');
    }
  } catch (error) {
    console.error('Error checking auth status:', error);
  } finally {
    setLoading(false);
  }
};

    checkAuthStatus();
  }, []);

  const login = (token: string) => {
    localStorage.setItem('studybuddy_token', token);
    navigate('/dashboard');
  };

  const logout = async () => {
    try {
      await fetch(`${serverAdress}/api/auth/logout`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('studybuddy_token')}`,
        },
        credentials: 'include',
      });

      localStorage.removeItem('studybuddy_token');
      setUser(null);
      navigate('/login');
    } catch (error) {
      console.error('Error logging out:', error);
    }
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
