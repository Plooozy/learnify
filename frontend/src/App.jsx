import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Register from './components/Register';
import Login from './components/Login';
import Chat from './components/Chat';
import ProtectedRoute from './components/ProtectedRoute';

function AppContent() {
  const { isAuthenticated, user } = useAuth();

  return (
    <Router>
      <nav style={{ padding: '10px', borderBottom: '1px solid #ccc', backgroundColor: '#f8f9fa' }}>
        <Link style={{ marginRight: '10px', textDecoration: 'none', color: '#333' }} to="/">
          Главная
        </Link>
        {!isAuthenticated ? (
          <>
            <Link style={{ marginRight: '10px', textDecoration: 'none', color: '#333' }} to="/login">
              Вход
            </Link>
            <Link style={{ textDecoration: 'none', color: '#333' }} to="/register">
              Регистрация
            </Link>
          </>
        ) : (
          <>
            <Link style={{ marginRight: '10px', textDecoration: 'none', color: '#333' }} to="/chat">
              Чат
            </Link>
            <span style={{ marginLeft: '10px', color: '#666' }}>
              Привет, {user?.username}!
            </span>
          </>
        )}
      </nav>

      <Routes>
        <Route path="/" element={<h1>Добро пожаловать в Learning Platform!</h1>} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route
          path="/chat"
          element={
            <ProtectedRoute>
              <Chat />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;