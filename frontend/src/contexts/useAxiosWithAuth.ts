import axios from 'axios';
import {useAuth} from './AuthContext';

const useAxiosWithAuth = () => {
    const {logout} = useAuth();

    const axiosInstance = axios.create({
        baseURL: '/api',
    });

    axiosInstance.interceptors.request.use(
        config => {
            const token = localStorage.getItem('authToken');
            if (token) {
                config.headers['Authorization'] = `Bearer ${token}`;
            }
            return config;
        },
        error => {
            return Promise.reject(error);
        }
    );

    axiosInstance.interceptors.response.use(
        response => response,
        async error => {
            if (error.response && error.response.status === 401) {
                await logout();
            }
            return Promise.reject(error);
        }
    );

    return axiosInstance;
};

export default useAxiosWithAuth;
