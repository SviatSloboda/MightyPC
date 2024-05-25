import React, { createContext, ReactNode, useContext, useEffect, useState } from 'react';
import axios from 'axios';
import { User } from '../model/shop/User.tsx';

interface AuthContextType {
    user: User | null;
    updateUser: (userData: User | null) => void;
    isSuperUser: () => boolean;
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

    useEffect(() => {
        axios.get<User>('/api/user', { withCredentials: true })
            .then((response) => setUser(response.data ?? null))
            .catch(() => setUser(null));
    }, []);

    const updateUser = (userData: User | null) => setUser(userData);

    const isSuperUser = (): boolean => {
        return user?.role === 'superuser';
    };

    return (
        <AuthContext.Provider value={{ user, updateUser, isSuperUser }}>
            {children}
        </AuthContext.Provider>
    );
};
