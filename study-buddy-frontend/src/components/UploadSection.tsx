// src/components/UploadSection.tsx
import React, { useState, useRef } from 'react';
import { Document } from '../App';
import './UploadSection.css';

interface UploadSectionProps {
  onDocumentsSelected: (files: File[], numQuestions: number) => void; // ← changed from Document[] to File[]
  isActive: boolean;
}

const UploadSection: React.FC<UploadSectionProps> = ({ onDocumentsSelected, isActive }) => {
  const [dragActive, setDragActive] = useState<boolean>(false);
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [numQuestions, setNumQuestions] = useState<number>(5); // Default to 5 questions
  const fileInputRef = useRef<HTMLInputElement>(null);
  
  const handleDrag = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(e.type === "dragenter" || e.type === "dragover");
  };
  
  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      handleFiles(Array.from(e.dataTransfer.files));
    }
  };
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();
    if (e.target.files && e.target.files.length > 0) {
      handleFiles(Array.from(e.target.files));
    }
  };
  
  const handleButtonClick = () => fileInputRef.current?.click();
  
  const MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB in bytes
  const handleFiles = (files: File[]) => {
    const validFiles = files.filter(file => {
      const fileExt = file.name.split('.').pop()?.toLowerCase();
      const isValidType = ['pdf', 'doc', 'docx', 'ppt', 'pptx'].includes(fileExt || '');
      const isValidSize = file.size <= MAX_FILE_SIZE;
      return isValidType && isValidSize;
    });
  
    if (validFiles.length < files.length) {
      alert("Some files were excluded because they exceed the 50MB limit or have an invalid file type.");
    }
  
    setSelectedFiles(validFiles);
  };
  const handleUpload = () => {
    if (selectedFiles.length > 0) {
      onDocumentsSelected(selectedFiles, numQuestions); // ← now passing real files
      setSelectedFiles([]);
    }
  };
  return (
    <div className="upload-section">
      <h2>Upload Documents</h2>
      <div 
        className={`dropzone ${dragActive ? 'active' : ''}`}
        onDragEnter={handleDrag}
        onDragLeave={handleDrag}
        onDragOver={handleDrag}
        onDrop={handleDrop}
      >
        <input 
          ref={fileInputRef}
          type="file" 
          multiple 
          accept=".pdf,.doc,.docx,.ppt,.pptx" 
          onChange={handleChange}
          style={{ display: 'none' }} 
        />
        <div className="dropzone-content">
          <p>Drag & drop your documents here</p>
          <p>OR</p>
          <button onClick={handleButtonClick} className="file-select-button">
            Select Files
          </button>
          <p className="file-types">Accepted file types: PDF, Word, PowerPoint</p>
        </div>
      </div>

      {/* Input for number of questions */}
      <div className="question-input-container">
        <label htmlFor="numQuestions">Number of Questions:</label>
        <input 
          type="number"
          id="numQuestions"
          min="1"
          value={numQuestions}
          onChange={(e) => setNumQuestions(Number(e.target.value))}
          className="question-input"
        />
      </div>

      {selectedFiles.length > 0 && (
        <div className="selected-files">
          <h3>Selected Files ({selectedFiles.length})</h3>
          <ul>
            {selectedFiles.map((file, index) => (
              <li key={index}>
                {file.name} ({(file.size / (1024 * 1024)).toFixed(2)} MB)
              </li>
            ))}
          </ul>
          <button 
            onClick={handleUpload} 
            className="upload-button"
          >
            Upload and Process
          </button>
        </div>
      )}
    </div>
  );
};

export default UploadSection;
