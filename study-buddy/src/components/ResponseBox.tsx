// src/components/ResponseBox.tsx
import React, { useState, useEffect, useRef } from 'react';
import { Document } from '../App';
import './ResponseBox.css';

interface ResponseBoxProps {
  response: string;
  documents: Document[];
}

const ResponseBox: React.FC<ResponseBoxProps> = ({ response, documents }) => {
  const [textHeight, setTextHeight] = useState<number>(300);
  const [isResizing, setIsResizing] = useState<boolean>(false);
  const startYRef = useRef<number>(0);
  const startHeightRef = useRef<number>(0);
  
  const handleMouseDown = (e: React.MouseEvent) => {
    setIsResizing(true);
    startYRef.current = e.clientY;
    startHeightRef.current = textHeight;
    
    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);
  };
  
  const handleMouseMove = (e: MouseEvent) => {
    if (isResizing) {
      const delta = startYRef.current - e.clientY;
      const newHeight = Math.max(150, startHeightRef.current + delta);
      setTextHeight(newHeight);
    }
  };
  
  const handleMouseUp = () => {
    setIsResizing(false);
    document.removeEventListener('mousemove', handleMouseMove);
    document.removeEventListener('mouseup', handleMouseUp);
  };
  
  useEffect(() => {
    return () => {
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleMouseUp);
    };
  }, []);

  return (
    <div className="response-box">
      <div className="documents-list">
        <h3>Uploaded Documents</h3>
        {documents.length > 0 ? (
          <ul>
            {documents.map(doc => (
              <li key={doc.id}>
                <span className="document-name">{doc.name}</span>
                <span className="document-type">{doc.type.split('/').pop()}</span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="no-documents">No documents uploaded</p>
        )}
      </div>
      
      <div className="response-container">
        <h3>Response</h3>
        <div 
          className="text-area-container" 
          style={{ height: `${textHeight}px` }}
        >
          <div className="resize-handle" onMouseDown={handleMouseDown}></div>
          <div className="response-text">
            {response ? (
              <pre>{response}</pre>
            ) : (
              <p className="no-response">Upload documents to see the response here</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ResponseBox;
