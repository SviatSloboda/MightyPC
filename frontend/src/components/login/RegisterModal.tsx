import React, { useState } from 'react';

interface RegisterModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSave: (email: string, password: string) => void;
}

const RegisterModal: React.FC<RegisterModalProps> = ({ isOpen, onClose, onSave }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [emailError, setEmailError] = useState('');
    const [passwordError, setPasswordError] = useState('');

    if (!isOpen) return null;

    const handleSave = () => {
        if (!validateEmail(email)) {
            setEmailError('Invalid email address');
            return;
        }
        if (!validatePassword(password)) {
            setPasswordError('Password must be at least 8 characters long');
            return;
        }
        onSave(email, password);
        onClose();
    };

    const validateEmail = (email: string) => {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    };

    const validatePassword = (password: string) => {
        return password.length >= 8;
    };

    return (
        <div className="create-pc-modal__overlay">
            <div className="create-pc-modal">
                <div className="create-pc-modal__header">
                    <h2 className="create-pc-modal__title">Register</h2>
                    <button className="create-pc-modal__close-button" onClick={onClose}>X</button>
                </div>
                <div className="create-pc-modal__body">
                    <div className="create-pc-modal__form-group">
                        <label className="create-pc-modal__label" htmlFor="register-email">Email</label>
                        <input
                            id="register-email"
                            className="create-pc-modal__input"
                            type="email"
                            value={email}
                            onChange={(e) => {
                                setEmail(e.target.value);
                                setEmailError('');
                            }}
                        />
                        {emailError && <span className="error-message">{emailError}</span>}
                    </div>
                    <div className="create-pc-modal__form-group">
                        <label className="create-pc-modal__label" htmlFor="register-password">Password</label>
                        <div className="password-input-container">
                            <input
                                id="register-password"
                                className="create-pc-modal__input"
                                type={showPassword ? 'text' : 'password'}
                                value={password}
                                onChange={(e) => {
                                    setPassword(e.target.value);
                                    setPasswordError('');
                                }}
                            />
                            <button
                                type="button"
                                className="password-toggle-button"
                                onClick={() => setShowPassword(!showPassword)}
                            />
                        </div>
                        {passwordError && <span className="error-message">{passwordError}</span>}
                    </div>
                </div>
                <div className="create-pc-modal__footer">
                    <button className="create-pc-modal__save-button" onClick={handleSave}>Register Me!</button>
                </div>
            </div>
        </div>
    );
};

export default RegisterModal;
