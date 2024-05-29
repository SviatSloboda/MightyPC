import {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import logo from '../../assets/logo.jpg';
import {useAuth} from "../../contexts/AuthContext.tsx";

export default function NavigationBar() {
    const {user, logout, isLoading} = useAuth();
    const navigate = useNavigate();

    const [accountMenuOpen, setAccountMenuOpen] = useState(false);

    const toggleAccountMenu = () => {
        setAccountMenuOpen(!accountMenuOpen);
    };

    const closeAccountMenu = () => {
        setAccountMenuOpen(false);
    };

    const handleLogout = async () => {
        await logout();
        navigate('/');
    };

    const renderAccountDropdown = () => {
        if (isLoading) {
            return <div>Loading...</div>;
        }

        if (user) {
            return (
                <>
                    <Link to="/user" className="nav__account-dropdown-link" onClick={closeAccountMenu}>
                        Profile
                    </Link>
                    <Link to="/user-pcs" className="nav__account-dropdown-link" onClick={closeAccountMenu}>
                        My PCs
                    </Link>
                    <Link to="/order" className="nav__account-dropdown-link" onClick={closeAccountMenu}>
                        Orders
                    </Link>
                    <Link to="/basket" className="nav__account-dropdown-link" onClick={closeAccountMenu}>
                        Basket
                    </Link>
                    <button
                        className="nav__account-dropdown-link"
                        onClick={() => {
                            handleLogout();
                            closeAccountMenu();
                        }}
                    >
                        Logout
                    </button>
                </>
            );
        }

        return (
            <button
                className="nav__account-dropdown-link"
                onClick={() => {
                    navigate("/user/login");
                    closeAccountMenu();
                }}
            >
                Login
            </button>
        );
    };

    return (
        <nav className="nav">
            <Link to="/" className="nav__link">
                <img className="nav__img-logo" src={logo} alt="logo"/>
            </Link>
            <Link to="/configurator" className="nav__link">Configurator</Link>
            <Link to="/pc" className="nav__link">Gaming PCs</Link>
            <Link to="/workstation" className="nav__link">Workstations</Link>
            <Link to="/hardware" className="nav__link">Hardware</Link>

            <input
                type="button"
                className="nav__link nav__link--account"
                onClick={toggleAccountMenu}
                value="Account"
                tabIndex={0}
            />

            {accountMenuOpen && (
                <div className="nav__account-dropdown">
                    {renderAccountDropdown()}
                </div>
            )}
        </nav>
    );
}
