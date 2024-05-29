import axios from 'axios';
import { useAuth } from "./AuthContext.tsx";

const useAxiosWithAuth = () => {
    const { logout } = useAuth();

    const axiosInstance = axios.create({
        baseURL: '/api',
        withCredentials: true,
    });

    axiosInstance.interceptors.response.use(
        response => response,
        error => {
            if (error.response && error.response.status === 401) {
                logout();
            }
            const rejectionError = error instanceof Error ? error : new Error('An unknown error occurred');
            return Promise.reject(rejectionError);
        }
    );

    return axiosInstance;
};

export default useAxiosWithAuth;
