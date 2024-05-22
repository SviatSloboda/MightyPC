import {useEffect, useState} from 'react';
import axios from 'axios';
import {useAuth} from "../../contexts/AuthContext.tsx";
import Photo from "../hardware/utils/Photo.tsx";
import user_image from "../../assets/shop/user_image.png";

export default function ProfilePage() {
    const {user, isSuperUser} = useAuth();
    const [userImage, setUserImage] = useState<string>("");

    useEffect(() => {
        if (user?.photo) {
            setUserImage(user.photo);
        }
    }, [user]);

    const handleImageUpload = async (file: File) => {
        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await axios.post(`/api/user/upload/image/${user?.id}`, formData, {
                headers: {'Content-Type': 'multipart/form-data'}
            });
            setUserImage(response.data);
        } catch (error) {
            console.error('Error uploading image', error);
        }
    };

    const handleDeleteImage = async () => {
        try {
            await axios.delete(`/api/user/upload/image/${user?.id}`);
            setUserImage("");
        } catch (error) {
            console.error('Error deleting image', error);
        }
    };

    const handleDeleteAccount = async () => {
        setIsModalOpen(true);
    }

    const [isModalOpen, setIsModalOpen] = useState(false);

    const confirmDeleteAccount = async () => {
        try {
            await axios.delete(`/api/user/${user?.id}`);
            window.location.href = '/';
        } catch (error) {
            console.error('error deleting account: ' + error)
        }
    };

    return (<div className={"profile-page"}>
        <div className="profile__image-container">
            {userImage ? (<img src={userImage} alt="Profile" className="profile__image"/>) : (
                <img src={user_image} alt="Empty Basket" className="basket-empty__image"/>)}
            <div>
                <div className={"profile__image--savePhoto"}>
                    <Photo savePhoto={handleImageUpload}/>
                </div>
                <button className="upload-button item__delete" onClick={handleDeleteImage}>
                    Delete Image
                </button>
                <button className="upload-button item__delete delete-account" onClick={handleDeleteAccount}>
                    Delete Account
                </button>
            </div>
        </div>
        <div className={"profile__info"}>
            <h1 className="profile__email">Email: </h1>
            <span className="profile__creation-date">{user?.email}</span>

            <h1 className="profile__email">Account created at: </h1>
            <span className="profile__creation-date">{user?.dateOfAccountCreation}</span>

            {isSuperUser() &&
                <>
                    <h1 className="profile__email">You are super user! </h1>
                </>}

        </div>

        {isModalOpen && (<div className="modal-overlay">
            <div className="modal">
                <h2>Are you sure you want to delete your account?</h2>
                <div className="modal__delete">
                    <button className="default-button modal__delete-button"
                            onClick={confirmDeleteAccount}>Delete
                    </button>
                    <button className="default-button modal__delete-button"
                            onClick={() => setIsModalOpen(false)}>Close
                    </button>
                </div>
            </div>
        </div>)}
    </div>);
}
