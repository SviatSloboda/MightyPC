import React, {useState} from 'react';

interface ModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSave: () => void;
    children: React.ReactNode;
}

const Modal: React.FC<ModalProps> = ({isOpen, onClose, onSave, children}) => (isOpen && (<div className="modal-overlay">
    <div className="modal">
        <div className="modal__header">
            <h3 className="modal__title">Add Product</h3>
            <button className="modal__close-btn" onClick={onClose}>×</button>
        </div>
        <div className="modal__body">
            {children}
        </div>
        <div className="modal__footer">
            <button className="modal__save-btn" onClick={onSave}>Save</button>
            <button className="modal__close-btn" onClick={onClose}>Close</button>
        </div>
    </div>
</div>));

export default Modal;

export const useModal = (initialMode = false): [boolean, () => void] => {
    const [modalOpen, setModalOpen] = useState<boolean>(initialMode);
    const toggle = () => setModalOpen(!modalOpen);
    return [modalOpen, toggle];
};