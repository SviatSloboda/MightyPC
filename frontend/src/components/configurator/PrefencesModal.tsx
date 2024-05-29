import {useState} from 'react';

interface PreferencesModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSave: (name: string, description: string) => void;
}

export default function PreferencesModal({isOpen, onClose, onSave}: Readonly<PreferencesModalProps>) {
    const [pcName, setPcName] = useState('');
    const [pcDescription, setPcDescription] = useState('');

    if (!isOpen) return null;

    const handleSave = () => {
        onSave(pcName, pcDescription);
        onClose();
    };

    return (
        <div className="create-pc-modal__overlay">
            <div className="create-pc-modal">
                <div className="create-pc-modal__header">
                    <h2 className="create-pc-modal__title">Enter your PC Preferences</h2>
                    <button className="create-pc-modal__close-button" onClick={onClose}>X</button>
                </div>
                <div className="create-pc-modal__body">
                    <div className="create-pc-modal__form-group">
                        <label className="create-pc-modal__label" htmlFor="pc-name">Type of PC: </label>
                        <input
                            id="pc-name"
                            className="create-pc-modal__input"
                            type="text"
                            value={pcName}
                            onChange={(e) => setPcName(e.target.value)}
                        />
                    </div>
                    <div className="create-pc-modal__form-group">
                        <label className="create-pc-modal__label" htmlFor="pc-description">Price without taxes and shop
                            commission: </label>
                        <textarea
                            id="pc-description"
                            className="create-pc-modal__input"
                            value={pcDescription}
                            onChange={(e) => setPcDescription(e.target.value)}
                        />
                    </div>
                </div>
                <div className="create-pc-modal__footer">
                    <button className="create-pc-modal__save-button" onClick={handleSave}>Save</button>
                </div>
            </div>
        </div>
    );
}