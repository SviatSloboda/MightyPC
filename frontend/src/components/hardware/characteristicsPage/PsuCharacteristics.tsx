import {useEffect, useState} from "react";
import axios from "axios";
import {useNavigate, useParams} from "react-router-dom";
import {PSU} from "../../../model/hardware/PSU.tsx";
import Photo from "../utils/Photo.tsx";
import Rating from "../utils/Rating.tsx";
import {useAuth} from "../../../contexts/AuthContext.tsx";
import useLoginModal from "../../login/useLoginModal.ts";
import LoginModal from "../../login/LoginModal.tsx";
import psuPhoto from "../../../assets/hardware/psu.png";

export default function PsuCharacteristics() {
    const [psu, setPsu] = useState<PSU>();
    const {id} = useParams<{ id: string }>();
    const [photos, setPhotos] = useState<string[]>([]);
    const [currentSlideIndex, setCurrentSlideIndex] = useState(0);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
    const [updatedName, setUpdatedName] = useState('');
    const [updatedDescription, setUpdatedDescription] = useState('');
    const [updatedPrice, setUpdatedPrice] = useState('');
    const [updatedRating, setUpdatedRating] = useState(0);
    const [updatedPower, setUpdatedPower] = useState('');
    const {user, isSuperUser} = useAuth();
    const navigate = useNavigate();
    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();

    useEffect(() => {
        if (id) {
            axios.get(`/api/hardware/psu/${id}`)
                .then(response => {
                    setPsu(response.data);
                    setPhotos(response.data.psuPhotos || []);
                    setUpdatedName(response.data.hardwareSpec.name);
                    setUpdatedDescription(response.data.hardwareSpec.description);
                    setUpdatedPrice(response.data.hardwareSpec.price);
                    setUpdatedRating(response.data.hardwareSpec.rating);
                    setUpdatedPower(response.data.power);
                })
                .catch(console.error);
        }
    }, [id, isUpdateModalOpen]);

    const plusSlides = (n: number) => {
        setCurrentSlideIndex(prevIndex => (prevIndex + n + photos.length) % photos.length);
    };

    const savePhoto = async (file: File) => {
        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await axios.post(`/api/psu/upload/image/${id}`, formData, {
                headers: {'Content-Type': 'multipart/form-data'}
            });
            setPhotos(prevPhotos => [response.data, ...prevPhotos]);
        } catch (error) {
            console.error('Error uploading image', error);
        }
    };

    const handleUpdate = () => {
        const payload = {
            id: psu?.id, hardwareSpec: {
                name: updatedName, description: updatedDescription, price: updatedPrice, rating: updatedRating,
            }, power: updatedPower, psuPhotos: psu?.psuPhotos
        };
        axios.put(`/api/hardware/psu`, payload)
            .then(response => {
                setPsu(response.data);
                setIsUpdateModalOpen(false);
            })
            .catch(console.error);
    };

    const handleDelete = () => {
        axios.delete(`/api/hardware/psu/${id}`)
            .then(() => {
                navigate('/hardware/psu');
            })
            .catch(console.error);
    };

    const handleAddToBasket = () => {
        if (!user) {
            showLoginModal();
            return;
        }

        const payload = {
            id: psu?.id,
            name: psu?.hardwareSpec.name,
            description: psu?.hardwareSpec.description,
            price: psu?.hardwareSpec.price,
            photo: psu?.psuPhotos && psu.psuPhotos.length > 0 ? psu.psuPhotos[psu.psuPhotos.length - 1] : psuPhoto,
            pathToCharacteristicsPage: "/hardware/psu"
        };

        axios.post<void>(`/api/basket/${user?.id}`, payload)
            .then(() => {
                navigate("../basket/");
            })
            .catch(console.error);
    };

    return (<>
        <div className="product-characteristics">
            <div className="product-characteristics__slideshow-container">
                {photos.map((photo) => (
                    <div
                        className={`product-characteristics__slide ${photos.indexOf(photo) === currentSlideIndex ? 'product-characteristics__slide--active' : ''}`}
                        key={photo}
                    >
                        <div
                            className="product-characteristics__number-text">{photos.indexOf(photo) + 1} / {photos.length}</div>
                        <img src={photo} alt="PSU" className="product-characteristics__photo-img"/>
                    </div>
                ))}
                {!photos.length && <img src={psuPhoto} alt="PSU" className="product-characteristics__photo-img"/>}
                {photos.length >= 2 && (<>
                    <button className="product-characteristics__control product-characteristics__control--prev"
                            onClick={() => plusSlides(-1)}>&#10094;</button>
                    <button className="product-characteristics__control product-characteristics__control--next"
                            onClick={() => plusSlides(1)}>&#10095;</button>
                </>)}
            </div>
            <br/>
            <div className="product-characteristics__details">
                <h1 className="product-characteristics__name">{psu?.hardwareSpec.name}</h1>
                <div className="product-characteristics__info">
                    <Rating rating={psu?.hardwareSpec.rating ?? 0}/>
                    <span className="product-characteristics__rating">{psu?.hardwareSpec.rating}/5</span>
                </div>
                <p className="product-characteristics__description">{psu?.hardwareSpec.description}</p>
                <span className="product-characteristics__price">{psu?.hardwareSpec.price}$</span>
                <button className="product-characteristics__buy-btn" onClick={handleAddToBasket}>Add to basket
                </button>
            </div>
        </div>

        <LoginModal isOpen={isLoginModalOpen} onLogin={handleLogin} onClose={hideLoginModal}/>

        {isSuperUser() && (<>
            <Photo savePhoto={savePhoto}/>

            <button className="upload-button item__delete" onClick={() => setIsUpdateModalOpen(true)}>Update
            </button>
            {isUpdateModalOpen && (<div className="modal-overlay">
                <div className="modal">
                    <div className="modal__header">
                        <h3 className="modal__title">Update PSU</h3>
                        <button className="modal__close-btn" onClick={() => setIsUpdateModalOpen(false)}>×
                        </button>
                    </div>
                    <div className="modal__body">
                        <div className="modal__form-group">
                            <input className="modal__input" value={updatedName}
                                   onChange={(e) => setUpdatedName(e.target.value)} placeholder="Name"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" value={updatedDescription}
                                   onChange={(e) => setUpdatedDescription(e.target.value)}
                                   placeholder="Description"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" type="number" value={updatedPrice}
                                   onChange={(e) => setUpdatedPrice(e.target.value)} placeholder="Price"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" type="number" value={updatedRating}
                                   onChange={(e) => setUpdatedRating(Number(e.target.value))}
                                   placeholder="Rating" min="0" max="5"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" value={updatedPower}
                                   onChange={(e) => setUpdatedPower(e.target.value)} placeholder="Power"/>
                        </div>
                    </div>
                    <div className="modal__footer">
                        <button className="modal__save-btn" onClick={handleUpdate}>Save Changes</button>
                        <button className="modal__close-btn"
                                onClick={() => setIsUpdateModalOpen(false)}>Cancel
                        </button>
                    </div>
                </div>
            </div>)}

            <button className="upload-button item__delete" onClick={() => setIsModalOpen(true)}>Delete</button>
            {isModalOpen && (<div className="modal-overlay">
                <div className="modal">
                    <h2>Are you sure you want to delete this PSU?</h2>
                    <div className="modal__delete">
                        <button className="default-button modal__delete-button"
                                onClick={handleDelete}>Delete
                        </button>
                        <button className="default-button modal__delete-button"
                                onClick={() => setIsModalOpen(false)}>Close
                        </button>
                    </div>
                </div>
            </div>)}
        </>)}
    </>);
}
