import {Link} from "react-router-dom";

export default function FooterBar() {
    return (<footer className="footer">
            <div className="footer__info">
                <div className="footer__info-title">Contact Us</div>
                <div className="footer__info-content">+1 (555) 123-4567</div>
                <div className="footer__info-content">mightypc@gmail.com</div>
                <div className="footer__info-content">Lessingstr. 17, Frankfurt, DE</div>
            </div>
            <nav className="footer__nav">
                <Link to="/" className="footer__nav-link">Main</Link>
                <Link to="/configurator" className="footer__nav-link">Configurator</Link>
                <Link to="/gaming-pcs" className="footer__nav-link">PCs</Link>
                <Link to="/workstations" className="footer__nav-link">Workstations</Link>
                <Link to="/hardware" className="footer__nav-link">Hardware</Link>
                <Link to="/account" className="footer__nav-link footer__nav-link--account">Account</Link>
            </nav>
        </footer>

    )
}