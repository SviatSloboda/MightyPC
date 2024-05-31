import React, {FormEvent, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import axios from 'axios';
import {toast, ToastContainer} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import RegisterModal from './RegisterModal';
import {useAuth} from '../../contexts/AuthContext';
import useAxiosWithAuth from "../../contexts/useAxiosWithAuth.ts";

export default function LoginPage() {
    const {updateUser} = useAuth();
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [isRegisterModalOpen, setIsRegisterModalOpen] = useState(false);
    const axiosInstance = useAxiosWithAuth();

    const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => setEmail(e.target.value);
    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => setPassword(e.target.value);

    const handleLogin = async (e: FormEvent) => {
        e.preventDefault();
        try {
            const response = await axiosInstance.post('/user/login', {email, password});
            if (response.status === 200) {
                const token = response.headers['authorization'].split(' ')[1];
                localStorage.setItem('authToken', token);
                updateUser(response.data);
                navigate('/');
            } else {
                toast.error('Login failed. Please check your credentials.');
            }
        } catch (error: unknown) {
            if (axios.isAxiosError(error) && error.response) {
                if (error.response.status === 401) {
                    toast.error('Invalid credentials. Please check your email and password.');
                } else if (error.response.status === 400) {
                    toast.error('Bad request. Please ensure all fields are filled correctly.');
                } else {
                    toast.error('Login failed. Please try again later.');
                }
            } else {
                toast.error('Login failed. Please try again later.');
                console.error('Login failed', error);
            }
        }
    };

    const handleGoogleLogin = () => {
        toast.info("To use Google or Github Auth contact dev: slsvyatko@gmail.com")
        toast.info("Use classic way of logging in!")
    };

    const handleRegisterOpen = () => setIsRegisterModalOpen(true);
    const handleRegisterClose = () => setIsRegisterModalOpen(false);
    const handleRegisterSave = async (email: string, password: string) => {
        try {
            const response = await axios.post('/api/user/register', {email, password});
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
                    <button className="login-text-link" onClick={handleGoogleLogin}>with Github</button>
                </div>
                <div className="login-links">
                    <button className="login-link" onClick={handleRegisterOpen}>Register</button>
                </div>
            </div>
            <RegisterModal isOpen={isRegisterModalOpen} onClose={handleRegisterClose} onSave={handleRegisterSave}/>
        </div>
    );
}
