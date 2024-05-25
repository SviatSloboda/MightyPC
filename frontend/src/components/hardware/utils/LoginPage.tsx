import React, { useState } from 'react';
import { googleLogin } from '../../../contexts/authUtils.ts';
import { useAuth } from '../../../contexts/AuthContext.tsx';
import axios from 'axios';

export default function LoginPage() {
    const { updateUser } = useAuth();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => setEmail(e.target.value);
    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => setPassword(e.target.value);

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await axios.post('/api/user/login', { email, password }, { withCredentials: true });
            updateUser(response.data);
        } catch (error) {
            console.error('Login failed', error);
        }
    };

    const handleGoogleLogin = () => googleLogin();

    return (
        <div className="login-page">
            <div className="login-container">
                <h1 className="login-title">Sign in</h1>
                <form className="login-form" onSubmit={handleLogin}>
                    <div className="form-group">
                        <label className="form-label" htmlFor="email">Username or email address</label>
                        <input
                            type="email"
                            id="email"
                            className="form-input"
                            value={email}
                            onChange={handleEmailChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label" htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            className="form-input"
                            value={password}
                            onChange={handlePasswordChange}
                            required
                        />
                    </div>
                    <button type="submit" className="login-button">Log In</button>
                </form>
                <div className="divider">
                    <span>OR</span>
                </div>
                <div className="login-text-links">
                    <button className="login-text-link" onClick={handleGoogleLogin}>with Google</button>
                    <button className="login-text-link">with GitHub</button>
                </div>
                <div className="login-links">
                    <a href="/register" className="login-link">Register</a>
                    <a href="/reset-password" className="login-link">Forgot password?</a>
                </div>
            </div>
        </div>
    );
}
