import axios from "axios";

// Use environment variable for base URL
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8005/";

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;