import React, {createContext, ReactNode, useContext, useEffect, useMemo, useState} from 'react';
import axios from 'axios';
import {User} from '../model/shop/User';

interface AuthContextType {
    user: User | null;
    updateUser: (userData: User | null) => void;
    isSuperUser: () => boolean;
    logout: () => Promise<void>;
    login: (email: string, password: string) => Promise<void>;
    loginWithGoogle: () => void;
    isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({children}) => {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(false);

    const updateUser = (userData: User | null) => {
        setUser(userData);
    };

    const login = async (email: string, password: string) => {
        setIsLoading(true);
        try {
            const response = await axios.post('/api/user/login', {email, password}, {withCredentials: true});
            if (response.status === 200) {
                setUser(response.data);
            } else {
                throw new Error('Login failed');
            }
        } catch (error) {
            console.error('Login failed', error);
            throw error;
        } finally {
            setIsLoading(false);
        }
    };

    const loginWithGoogle = () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/google";
    };

    const logout = async () => {
        try {
            await axios.post('/api/user/logout', {}, {withCredentials: true});
            setUser(null);
        } catch (error) {
            console.error('Logout failed', error);
        }
    };

    const isSuperUser = () => {
        return user?.role === 'admin';
    };

    const value = useMemo(() => ({
        user,
        updateUser,
        isSuperUser,
        logout,
        login,
        loginWithGoogle,
        isLoading,
    }), [user, isLoading]);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await axios.get('/api/user/current', {withCredentials: true});
                if (response.status === 200) {
                    setUser(response.data);
                }
            } catch (error) {
                console.error('Failed to fetch user', error);
            }
        };

        const hasAuthCookies = document.cookie.includes('authToken');
        const urlParams = new URLSearchParams(window.location.search);
        if (hasAuthCookies || urlParams.get('oauth2Login') === 'true') {
            fetchUser();
        }
    }, []);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
