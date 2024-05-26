import React, { createContext, ReactNode, useContext, useEffect, useState, useMemo } from 'react';
import axios from 'axios';
import { User } from '../model/shop/User.tsx';
import { isSuperUser } from './isSuperUser';

interface AuthContextType {
    user: User | null;
    updateUser: (userData: User | null) => void;
    isSuperUser: () => boolean;
    logout: () => Promise<void>;
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

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        axios.get('/api/user', { withCredentials: true })
            .then((response) => {
                if (response.headers['content-type']?.includes('application/json')) {
                    setUser(response.data ?? null);
                } else {
                    setUser(null);
                }
            })
            .catch(() => {
                setUser(null);
            })
            .finally(() => {
                setIsLoading(false);
            });
    }, []);

    const updateUser = (userData: User | null) => {
        setUser(userData);
    };

    const logout = async () => {
        try {
            await axios.post('/api/logout', {}, { withCredentials: true });
            setUser(null);
        } catch (error) {
            console.error('Logout failed', error);
        }
    };

    const value = useMemo(() => ({
        user,
        updateUser,
        isSuperUser: () => isSuperUser(user),
        logout,
        isLoading,
    }), [user, isLoading]);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
