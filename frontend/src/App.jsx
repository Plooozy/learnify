import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Register from './components/Register';
import Login from './components/Login';

function App() {
  return (
    <Router>
      <nav style={{ padding: '10px', borderBottom: '1px solid #ccc' }}>
        <Link style={{ marginRight: '10px' }} to="/">Главная</Link>
        <Link style={{ marginRight: '10px' }} to="/login">Вход</Link>
        <Link to="/register">Регистрация</Link>
      </nav>

      <Routes>
        <Route path="/" element={<h1>Добро пожаловать в Learning Platform!</h1>} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Routes>
    </Router>
  );
}

export default App;