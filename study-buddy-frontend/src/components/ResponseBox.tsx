// src/components/ResponseBox.tsx
import React, { useState, useEffect, useRef, useMemo } from 'react';
import { Document } from '../App';
import './ResponseBox.css';

interface ResponseBoxProps {
  response: string;
  documents: Document[];
}
interface BackendAnswer {
  answerText: string;
  correct: boolean;
}

interface BackendQuestion {
  questionText: string;
  answers: BackendAnswer[];
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
  
  const [visibleAnswers, setVisibleAnswers] = useState<boolean[]>([]);
  const [revealAll, setRevealAll] = useState<boolean>(false);
  
  const parsedQuiz: BackendQuestion[] | null = useMemo(() => {
    try {
      const parsed: BackendQuestion[] = JSON.parse(response);
      if (Array.isArray(parsed)) {
        setVisibleAnswers(parsed.map(() => revealAll));
        return parsed;
      }
      return null;
    } catch {
      return null;
    }
  }, [response]);
  

  const toggleAnswer = (index: number) => {
    setVisibleAnswers(prev => prev.map((v, i) => (i === index ? !v : v)));
  };
  
  const handleRevealAll = () => {
    const newState = !revealAll;
    setRevealAll(newState);
    setVisibleAnswers(parsedQuiz?.map(() => newState) || []);
  };
  
  

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
            {parsedQuiz ? (
              <div className="quiz-container">
                <button onClick={handleRevealAll} className="show-answer-btn reveal-all">
                  {revealAll ? 'Hide All Answers' : 'Reveal All Answers'}
                </button>

                {parsedQuiz.map((q, idx) => {
                  const correctAnswer = q.answers.find(ans => ans.correct)?.answerText;

                  return (
                    <div key={idx} className="quiz-question">
                      <h4>Q{idx + 1}: {q.questionText}</h4>
                      <ul className="quiz-options">
                        {q.answers.map((opt, i) => (
                          <li key={i}>{opt.answerText}</li>
                        ))}
                      </ul>
                      <button onClick={() => toggleAnswer(idx)} className="show-answer-btn">
                        {visibleAnswers[idx] ? 'Hide Answer' : 'Show Answer'}
                      </button>
                      {visibleAnswers[idx] && (
                        <div className="quiz-answer"><strong>Answer:</strong> {correctAnswer}</div>
                      )}
                    </div>
                  );
                })}
              </div>
            ) : (
              <pre>{response}</pre>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ResponseBox;
