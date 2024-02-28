interface LoginModalProps {
    isOpen: boolean;
    onLogin: () => void;
    onClose: () => void;
}

export default function LoginModal(props: LoginModalProps) {
    if (!props.isOpen) return null;

    return (<div className="modal-overlay">
        <div className="modal">
            <h2 className="modal-header__message">To buy a product you need to login!</h2>
            <div className="modal__delete">
                <button className="default-button modal__delete-button" onClick={props.onLogin}>Login</button>
                <button className="default-button modal__delete-button" onClick={props.onClose}>Close</button>
            </div>
        </div>
    </div>);
}

