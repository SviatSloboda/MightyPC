import {useState} from 'react';
import {login} from "../../../contexts/authUtils.ts";

export default function useLoginModal() {
    const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);

    const showLoginModal = () => setIsLoginModalOpen(true);
    const hideLoginModal = () => setIsLoginModalOpen(false);

    const handleLogin = () => {
        login();
        hideLoginModal();
    };

    return {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin};
}