import axios from 'axios';
import {useAuth} from "./AuthContext.tsx";

const useAxiosWithAuth = () => {
    const { logout } = useAuth();

    const axiosInstance = axios.create({
        baseURL: '/api',
        withCredentials: true,
    });

    axiosInstance.interceptors.response.use(
        response => response,
        error => {
            if (error.response.status === 401) {
                logout();
            }
            return Promise.reject(error);
        }
    );

    return axiosInstance;
};

export default useAxiosWithAuth;
