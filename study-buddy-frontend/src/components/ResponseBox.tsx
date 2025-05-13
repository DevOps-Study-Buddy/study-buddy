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
  
  const parsedQuiz = useMemo(() => {
    if (!response || response.trim() === '') {
      return null;
    }
  
    try {
      const parsed = JSON.parse(response);
      if (parsed.quiz && Array.isArray(parsed.quiz)) {
        const formattedQuiz = parsed.quiz.map((q: any) => ({
          questionText: q.question,
          answers: q.options.map((opt: any) => ({
            answerText: opt.option,
            correct: opt.isCorrect
          }))
        }));
        setVisibleAnswers(formattedQuiz.map(() => revealAll));
        return formattedQuiz;
      }
      return null;
    } catch (error) {
      console.error("Failed to parse quiz:", error);
      return null;
    }
  }, [response, revealAll]);
  

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
                  const correctIndex = q.answers.findIndex(ans => ans.correct);
                  const correctAnswer = correctIndex !== -1 ? {
                    letter: String.fromCharCode(65 + correctIndex), // A, B, C...
                    text: q.answers[correctIndex].answerText
                  } : null;

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
                      {visibleAnswers[idx] && correctAnswer && (
                        <div className="quiz-answer">
                          <strong>Answer:</strong> {correctAnswer.letter}. {correctAnswer.text}
                        </div>
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
