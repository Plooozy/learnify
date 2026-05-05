import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Register = () => {
    const [formData, setFormData] = useState({
        username: '',
        password: ''
    });
    const [message, setMessage] = useState('');
    const { register, login } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');

        const result = await register(formData.username, formData.password);

        if (result.success) {
            setMessage('Регистрация выполнена успешно! Выполняется вход...');

            setTimeout(async () => {
                const loginResult = await login(formData.username, formData.password);
                if (loginResult.success) {
                    navigate('/chat');
                } else {
                    setMessage('Регистрация успешна, но не удалось выполнить автоматический вход. Пожалуйста, войдите вручную.');
                }
            }, 1000);
        } else {
            setMessage(result.error);
        }
    };

    return (
        <div style={{ padding: '20px', maxWidth: '400px' }}>
            <h2>Регистрация (React)</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Имя пользователя:</label><br/>
                    <input name="username" onChange={handleChange} />
                </div>
                <br/>
                <div>
                    <label>Пароль:</label><br/>
                    <input type="password" name="password" onChange={handleChange} />
                </div>
                <br/>
                <button type="submit">Зарегистрироваться</button>
            </form>

            {message && <p style={{ color: message.includes('успешно') ? 'green' : 'red' }}>{message}</p>}
        </div>
    );
};

export default Register;