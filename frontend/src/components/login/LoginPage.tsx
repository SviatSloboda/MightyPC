import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import RegisterModal from './RegisterModal';
import { useAuth } from '../../contexts/AuthContext.tsx';
import { googleLogin } from '../../contexts/authUtils.ts'; // Import the external modal

export default function LoginPage() {
    const { updateUser } = useAuth();
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [isRegisterModalOpen, setIsRegisterModalOpen] = useState(false);

    const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => setEmail(e.target.value);
    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => setPassword(e.target.value);

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await axios.post('/api/user/login', { email, password }, { withCredentials: true });
            if (response.status === 200) {
                updateUser(response.data);
                navigate('/');
            } else {
                console.error('Login failed with status', response.status);
                toast.error('Login failed. Please check your credentials.');
            }
        } catch (error: unknown) {
            if (axios.isAxiosError(error) && error.response && error.response.status === 401) {
                toast.error('Invalid credentials. Please try again.');
            } else {
                toast.error('Login failed. Please try again later.');
                console.error('Login failed', error);
            }
        }
    };

    const handleGoogleLogin = () => googleLogin();
    const handleGitHubLogin = () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/github";
    };

    const handleRegisterOpen = () => setIsRegisterModalOpen(true);
    const handleRegisterClose = () => setIsRegisterModalOpen(false);
    const handleRegisterSave = async (email: string, password: string) => {
        try {
            const response = await axios.post('/api/user/register', { email, password });
            if (response.status === 201) {
                toast.success('Registration successful. Please log in.');
            } else {
                toast.error('Registration failed. Please try again.');
            }
        } catch (error: unknown) {
            toast.error('Registration failed. Please try again later.');
            console.error('Registration failed', error);
        }
    };

    return (
        <div className="login-page">
            <div className="toast-container">
                <ToastContainer
                    position="top-center"
                    autoClose={5000}
                    hideProgressBar={false}
                    newestOnTop={false}
                    closeOnClick
                    rtl={false}
                    pauseOnFocusLoss
                    draggable
                    pauseOnHover
                />
            </div>
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
                    <button className="login-text-link" onClick={handleGitHubLogin}>with GitHub</button>
                </div>
                <div className="login-links">
                    <button className="login-link" onClick={handleRegisterOpen}>Register</button>
                    <button className="login-link" onClick={() => navigate('/reset-password')}>Forgot password?</button>
                </div>
            </div>
            <RegisterModal isOpen={isRegisterModalOpen} onClose={handleRegisterClose} onSave={handleRegisterSave} />
        </div>
    );
}