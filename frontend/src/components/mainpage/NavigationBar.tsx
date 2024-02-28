import {useState} from 'react';
import {Link} from 'react-router-dom';
import logo from '../../assets/logo.jpg';
import axios from "axios";
import {useAuth} from "../../contexts/AuthContext.tsx";
import {login} from "../../contexts/authUtils.ts";

export default function NavigationBar() {
    const {user, updateUser} = useAuth();

    const [accountMenuOpen, setAccountMenuOpen] = useState(false);

    const toggleAccountMenu = () => {
        setAccountMenuOpen(!accountMenuOpen);
    };

    const closeAccountMenu = () => {
        setAccountMenuOpen(false);
    };

    const logout = async () => {
        try {
            await axios.post('/api/logout', {}, {withCredentials: true});
            updateUser(null);
            window.location.href = '/';
        } catch (error) {
            console.error('Logout failed', error);
        }
    };

    const handleProfileClick = () => {
        if (!user) {
            login();
        } else {
            window.location.href = '/user';
        }
        closeAccountMenu();
    };

    return (<nav className="nav">
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

        {accountMenuOpen && (<div className="nav__account-dropdown">
            {!user && (<Link to="/" className="nav__account-dropdown-link" onClick={() => {
                login();
                closeAccountMenu();
            }}>Login</Link>)}
            {user && (<>
                <Link to="/user" className="nav__account-dropdown-link"
                      onClick={handleProfileClick}>Profile</Link>
                <Link to="/order" className="nav__account-dropdown-link"
                      onClick={closeAccountMenu}>Orders</Link>
                <Link to="/basket" className="nav__account-dropdown-link"
                      onClick={closeAccountMenu}>Basket</Link>
                <Link to="/" className="nav__account-dropdown-link" onClick={() => {
                    logout();
                    closeAccountMenu();
                }}>Logout</Link>
            </>)}
        </div>)}
    </nav>);
}
