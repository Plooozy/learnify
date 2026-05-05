import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Login = () => {
    const [formData, setFormData] = useState({ username: '', password: '' });
    const [message, setMessage] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');

        const result = await login(formData.username, formData.password);

        if (result.success) {
            setMessage('Вход выполнен успешно!');
            setTimeout(() => {
                navigate('/chat');
            }, 1000);
        } else {
            setMessage(result.error);
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <h2>Вход</h2>
            <form onSubmit={handleSubmit}>
                <input name="username" placeholder="Имя" onChange={handleChange} /><br/><br/>
                <input type="password" name="password" placeholder="Пароль" onChange={handleChange} /><br/><br/>
                <button type="submit">Войти</button>
            </form>
            {message && <p style={{ color: message.includes('успешно') ? 'green' : 'red' }}>{message}</p>}
        </div>
    );
};

export default Login;