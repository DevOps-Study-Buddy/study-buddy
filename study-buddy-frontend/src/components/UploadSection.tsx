// src/components/UploadSection.tsx
import React, { useState, useRef } from 'react';
import { Document } from '../App';
import './UploadSection.css';

interface UploadSectionProps {
  onDocumentsSelected: (documents: Document[]) => void;
  isActive: boolean;
}

const UploadSection: React.FC<UploadSectionProps> = ({ onDocumentsSelected, isActive }) => {
  const [dragActive, setDragActive] = useState<boolean>(false);
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const fileInputRef = useRef<HTMLInputElement>(null);
  
  const handleDrag = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
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
  
  const handleButtonClick = () => {
    fileInputRef.current?.click();
  };
  
  const handleFiles = (files: File[]) => {
    const validFiles = files.filter(file => {
      const fileExt = file.name.split('.').pop()?.toLowerCase();
      return ['pdf', 'doc', 'docx', 'ppt', 'pptx'].includes(fileExt || '');
    });
    
    setSelectedFiles(validFiles);
  };
  
  const handleUpload = () => {
    if (selectedFiles.length > 0) {
      const documents: Document[] = selectedFiles.map(file => ({
        id: crypto.randomUUID(),
        name: file.name,
        type: file.type || 'application/octet-stream',
        size: file.size
      }));
      
      onDocumentsSelected(documents);
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
      
      {selectedFiles.length > 0 && (
        <div className="selected-files">
          <h3>Selected Files ({selectedFiles.length})</h3>
          <ul>
            {selectedFiles.map((file, index) => (
              <li key={index}>{file.name}</li>
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
