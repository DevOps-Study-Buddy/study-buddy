// src/App.tsx
import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import UploadSection from './components/UploadSection';
import ResponseBox from './components/ResponseBox';
import SessionList from './components/SessionList';
import LandingPage from './components/LandingPage';
import GoogleCallback from './components/GoogleCallback';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import './App.css';

// Types
export interface Document {
  id: string;
  name: string;
  type: string;
  size: number;
}

export interface Session {
  id: string;
  title: string;
  timestamp: Date;
  documents: Document[];
  response: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
  photoUrl?: string;
}

// Protected route component
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return <div className="loading">Checking authentication.....</div>;
  }

  return user ? <>{children}</> : <Navigate to="/" />;
};

// Main application component
const MainApp: React.FC = () => {
  const [sessions, setSessions] = useState<Session[]>([]);
  const [currentSessionId, setCurrentSessionId] = useState<string | null>(null);
  const { user, logout } = useAuth();
  
  // Get current session
  const currentSession = sessions.find(session => session.id === currentSessionId) || null;
  
  // Create a new session
  const createSession = (documents: Document[]) => {
    const newSession: Session = {
      id: crypto.randomUUID(),
      title: `Session ${sessions.length + 1}`,
      timestamp: new Date(),
      documents: documents,
      response: '', // Empty initial response
    };
    
    const updatedSessions = [...sessions, newSession];
    setSessions(updatedSessions);
    setCurrentSessionId(newSession.id);
    
    // Simulate receiving a response from backend
    setTimeout(() => {
      const responseText = `Processed ${documents.length} document(s):\n` + 
        documents.map(doc => `- ${doc.name} (${doc.type})`).join('\n');
      
      updateSessionResponse(newSession.id, responseText);
    }, 1500);
  };
  
  // Update a session's response
  const updateSessionResponse = (sessionId: string, response: string) => {
    setSessions(prevSessions => 
      prevSessions.map(session => 
        session.id === sessionId 
          ? { ...session, response } 
          : session
      )
    );
  };
  
  // Switch to a different session
  const switchSession = (sessionId: string) => {
    setCurrentSessionId(sessionId);
  };

  return (
    <div className="app-container">
      <div className="app-header">
        <div className="app-logo">
          <img src="/study-buddy-icon.svg" alt="Study Buddy" />
          <h1>Study Buddy</h1>
        </div>
        {user && (
          <div className="user-profile">
            {user.photoUrl && <img src={user.photoUrl} alt={user.name} className="user-avatar" />}
            <span className="user-name">{user.name}</span>
            <button className="logout-btn" onClick={logout}>Logout</button>
          </div>
        )}
      </div>
      <div className="app-content">
        <div className="left-panel">
          <SessionList 
            sessions={sessions} 
            currentSessionId={currentSessionId}
            onSessionClick={switchSession} 
          />
          <UploadSection 
            onDocumentsSelected={createSession} 
            isActive={true}
          />
        </div>
        <div className="right-panel">
          <ResponseBox 
            response={currentSession?.response || ''} 
            documents={currentSession?.documents || []}
          />
        </div>
      </div>
    </div>
  );
};

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/auth/google/callback" element={<GoogleCallback />} />

          {/* Protected Routes */}
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <MainApp />
            </ProtectedRoute>
          } />
          {/* Demo Page (Accessible without login) */}
          <Route path="/demo" element={<MainApp />} />

          {/* Catch-all route: Redirect unknown paths to home */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;