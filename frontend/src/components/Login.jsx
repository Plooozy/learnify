import React, { useState } from 'react';
import axios from 'axios';

const Login = () => {
    const [formData, setFormData] = useState({ username: '', password: '' });
    const [message, setMessage] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', formData);
            setMessage(response.data); // "Login successful"
        } catch (error) {
            setMessage(error.response?.data?.message || "Login failed");
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
            {message && <p>{message}</p>}
        </div>
    );
};

export default Login;