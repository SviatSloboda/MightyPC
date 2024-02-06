import {Link} from "react-router-dom";
import logo from "../../assets/logo.jpg";

export default function NavigationBar() {
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
            <Link to="/account" className="nav__link nav__link--account">Account</Link>
        </nav>
    );
}
