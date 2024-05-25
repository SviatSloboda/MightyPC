import { useState } from 'react';

export default function useLoginModal() {
    const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);

    const showLoginModal = () => setIsLoginModalOpen(true);
    const hideLoginModal = () => setIsLoginModalOpen(false);

    const handleLogin = () => {
        hideLoginModal();
        window.location.href = "/user/login";
    };

    return { isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin };
}
