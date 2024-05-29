import React from 'react';

const PrivacyPolicy: React.FC = () => {
    return (
        <div className="privacy-policy">
            <h1 className="privacy-policy__title">Privacy Policy</h1>

            <h2 className="privacy-policy__section-title">1. Introduction</h2>
            <p className="privacy-policy__text">
                This Privacy Policy outlines how we handle your personal information when you use our services. Your privacy is important to us, and we are committed to protecting it.
            </p>

            <h2 className="privacy-policy__section-title">2. Information We Collect</h2>
            <p className="privacy-policy__text">
                We collect only your email address and password.
            </p>

            <h2 className="privacy-policy__section-title">3. How We Use Your Information</h2>
            <ul className="privacy-policy__list">
                <li className="privacy-policy__list-item"><strong>Email Address:</strong> Your email address is used solely for login purposes. It is not shared with any third parties.</li>
                <li className="privacy-policy__list-item"><strong>Password:</strong> When you create an account, your password is securely encoded using bcrypt. This ensures that your password is protected and not stored in plain text.</li>
            </ul>

            <h2 className="privacy-policy__section-title">4. Security</h2>
            <p className="privacy-policy__text">
                We take the security of your personal information seriously. We use industry-standard measures, including encryption and secure protocols, to protect your data from unauthorized access, use, or disclosure.
            </p>

            <h2 className="privacy-policy__section-title">5. Changes to This Privacy Policy</h2>
            <p className="privacy-policy__text">
                We may update this Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on our website. You are advised to review this Privacy Policy periodically for any changes.
            </p>

            <h2 className="privacy-policy__section-title">6. Contact Us</h2>
            <p className="privacy-policy__text">
                If you have any questions about this Privacy Policy, please contact us at:
            </p>
            <ul className="privacy-policy__list">
                <li className="privacy-policy__list-item">Email: slsvyatko@gmail.com</li>
            </ul>

            <p className="privacy-policy__effective-date">Effective Date: 29.05.2024</p>
        </div>
    );
};

export default PrivacyPolicy;
