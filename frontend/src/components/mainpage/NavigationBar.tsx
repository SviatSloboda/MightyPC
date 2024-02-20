import {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import logo from '../../assets/logo.jpg';
import axios from "axios";
import {User} from "../../model/shop/User.tsx";

export default function NavigationBar() {
    const [user, setUser] = useState<User | null>(null);

    useEffect(() => {
        axios.get<User>('/api/user', {withCredentials: true}).then((response) => {
            setUser(response.data || null);
        }).catch(() => {
            setUser(null);
        });
    }, []);

    const [isAccountMenuOpen, setAccountMenuOpen] = useState(false);

    const toggleAccountMenu = () => {
        setAccountMenuOpen(!isAccountMenuOpen);
    };

    const logout = async () => {
        try {
            await axios.post('/api/logout', {}, {withCredentials: true});
            setUser(null);
            window.location.href = '/';
        } catch (error) {
            console.error('Logout failed', error);
        }
    };

    const login = () => {
        const host =
            window.location.host === "localhost:5173"
                ? "http://localhost:8080"
                : window.location.origin;

        window.open(host + "/oauth2/authorization/google", "_self");
    };

    const handleProfileClick = () => {
        if (!user) {
            login();
        } else {
            window.location.href = '/user';
        }
    };

    return (
        <nav className="nav">
            <Link to="/" className="nav__link">
                <img className="nav__img-logo" src={logo} alt="logo"/>
            </Link>
            <Link to="/configurator" className="nav__link">Configurator</Link>
            <Link to="/gaming-pcs" className="nav__link">Gaming PCs</Link>
            <Link to="/workstations" className="nav__link">Workstations</Link>
            <Link to="/laptops" className="nav__link">Laptops</Link>
            <Link to="/hardware" className="nav__link">Hardware</Link>
            <Link to="/services" className="nav__link">Services</Link>

            <div className="nav__link nav__link--account" onClick={toggleAccountMenu}>Account</div>

            {isAccountMenuOpen && (
                <div className="nav__account-dropdown">
                    {!user && (
                        <Link to="/" className="nav__account-dropdown-link" onClick={login}>Login</Link>
                    )}
                    <Link to="/user" className="nav__account-dropdown-link" onClick={handleProfileClick}>Profile</Link>
                    <Link to="/orders" className="nav__account-dropdown-link">Orders</Link>
                    <Link to="/settings" className="nav__account-dropdown-link">Settings</Link>
                    {
                        user && (
                            <Link to="/" className="nav__account-dropdown-link" onClick={logout}>Logout</Link>
                        )
                    }
                </div>
            )}
        </nav>
    );
}


