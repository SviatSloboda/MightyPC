interface LoginModalProps {
    isOpen: boolean;
    onLogin: () => void;
    onClose: () => void;
}

export default function LoginModal(props: Readonly<LoginModalProps>) {
    if (!props.isOpen) return null;

    return (
        <div className="modal-overlay">
            <div className="modal">
                <h2 className="modal-header__message">You need to be logged in!</h2>
                <div className="modal__delete">
                    <button className="default-button modal__delete-button" onClick={props.onLogin}>Login</button>
                    <button className="default-button modal__delete-button" onClick={props.onClose}>Close</button>
                </div>
            </div>
        </div>
    );
}
