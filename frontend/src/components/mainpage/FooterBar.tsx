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
                <Link to="/pc" className="footer__nav-link">PCs</Link>
                <Link to={"/privacy-policy"} className={"footer__nav-link"}>Privacy Policy</Link>
                <Link to="/user" className="footer__nav-link footer__nav-link--account">Account</Link>
                <Link to="/workstation" className="footer__nav-link">Workstations</Link>
                <Link to="/configurator" className="footer__nav-link">Configurator</Link>
                <Link to="/hardware" className="footer__nav-link">Hardware</Link>
            </nav>
        </footer>
    )
}