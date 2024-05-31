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

interface DecodedToken {
    exp: number;
    iat: number;
}

const decodeJWT = (token: string): DecodedToken => {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
};

const setLogoutTimer = (expirationTime: number, logout: () => Promise<void>) => {
    setTimeout(() => {
        logout();
    }, expirationTime);
};

export const AuthProvider: React.FC<AuthProviderProps> = ({children}) => {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(false);

    const updateUser = (userData: User | null) => {
        setUser(userData);
    };

    const login = async (email: string, password: string) => {
        setIsLoading(true);
        try {
            const response = await axios.post('/user/login', {email, password});
            if (response.status === 200) {
                const token = response.headers['authorization'].split(' ')[1];
                localStorage.setItem('authToken', token);
                const decodedToken: DecodedToken = decodeJWT(token);
                const expirationTime = decodedToken.exp * 1000 - new Date().getTime();
                setLogoutTimer(expirationTime, logout);
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
            await axios.post('/api/user/logout');
            localStorage.removeItem('authToken');
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
                const token = localStorage.getItem('authToken');
                if (token) {
                    const decodedToken: DecodedToken = decodeJWT(token);
                    if (decodedToken.exp * 1000 < new Date().getTime()) {
                        logout();
                    } else {
                        const response = await axios.get('/api/user/current', {
                            headers: {
                                'Authorization': `Bearer ${token}`
                            }
                        });
                        if (response.status === 200) {
                            setUser(response.data);
                            const expirationTime = decodedToken.exp * 1000 - new Date().getTime();
                            setLogoutTimer(expirationTime, logout);
                        }
                    }
                }
            } catch (error) {
                console.error('Failed to fetch user', error);
            }
        };

        fetchUser();
    }, []);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
