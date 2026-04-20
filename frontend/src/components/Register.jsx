import React, { useState } from 'react';
import axios from 'axios';

const Register = () => {
    const [formData, setFormData] = useState({
        username: '',
        password: ''
    });
    const [message, setMessage] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/auth/register', formData);
            setMessage(response.data); // "User registered successfully"
        } catch (error) {
            const errorMsg = error.response?.data?.message || "Registration failed";
            setMessage(errorMsg);
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

            {message && <p style={{ color: message.includes('success') ? 'green' : 'red' }}>{message}</p>}
        </div>
    );
};

export default Register;