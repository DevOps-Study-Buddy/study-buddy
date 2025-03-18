// src/components/SessionList.tsx
import React from 'react';
import { Session } from '../App';
import './SessionList.css';

interface SessionListProps {
  sessions: Session[];
  currentSessionId: string | null;
  onSessionClick: (sessionId: string) => void;
}

const SessionList: React.FC<SessionListProps> = ({ sessions, currentSessionId, onSessionClick }) => {
  if (sessions.length === 0) {
    return (
      <div className="session-list">
        <h2>Previous Sessions</h2>
        <div className="empty-list">
          <p>No sessions yet</p>
        </div>
      </div>
    );
  }

  const formatDate = (date: Date) => {
    return date.toLocaleString(undefined, {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="session-list">
      <h2>Previous Sessions</h2>
      <div className="list-container">
        {sessions.map(session => (
          <div 
            key={session.id}
            className={`session-card ${session.id === currentSessionId ? 'active' : ''}`}
            onClick={() => onSessionClick(session.id)}
          >
            <div className="session-card-header">
              <h3>{session.title}</h3>
              <span className="timestamp">{formatDate(session.timestamp)}</span>
            </div>
            <div className="session-card-content">
              <span className="document-count">{session.documents.length} document(s)</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default SessionList;
