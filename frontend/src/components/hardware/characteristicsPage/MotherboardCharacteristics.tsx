import {useEffect, useState} from "react";
import axios from "axios";
import {useNavigate, useParams} from "react-router-dom";
import {Motherboard} from "../../../model/pc/hardware/Motherboard.tsx";
import Photo from "../utils/Photo.tsx";
import Rating from "../utils/Rating.tsx";
import {useAuth} from "../../../contexts/AuthContext.tsx";
import useLoginModal from "../utils/useLoginModal";
import LoginModal from "../utils/LoginModal";
import motherboardPhoto from "../../../assets/hardware/motherboard.png";

export default function MotherboardCharacteristics() {
    const {id} = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [motherboard, setMotherboard] = useState<Motherboard>();
    const [photos, setPhotos] = useState<string[]>([]);
    const [currentSlideIndex, setCurrentSlideIndex] = useState(0);
    const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
    const [updatedName, setUpdatedName] = useState('');
    const [updatedDescription, setUpdatedDescription] = useState('');
    const [updatedPrice, setUpdatedPrice] = useState('');
    const [updatedRating, setUpdatedRating] = useState(0);
    const [updatedSocket, setUpdatedSocket] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const {user, isSuperUser} = useAuth();
    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();

    useEffect(() => {
        if (id) {
            axios.get(`/api/hardware/motherboard/${id}`)
                .then(response => {
                    setMotherboard(response.data);
                    setPhotos(response.data.motherboardPhotos || []);
                    setUpdatedName(response.data.hardwareSpec.name);
                    setUpdatedDescription(response.data.hardwareSpec.description);
                    setUpdatedPrice(response.data.hardwareSpec.price);
                    setUpdatedRating(response.data.hardwareSpec.rating);
                    setUpdatedSocket(response.data.socket);
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
            const response = await axios.post(`/api/motherboard/upload/image/${id}`, formData, {
                headers: {'Content-Type': 'multipart/form-data'}
            });
            setPhotos(prevPhotos => [...prevPhotos, response.data.photo]);
        } catch (error) {
            console.error('Error uploading image', error);
        }
    };

    const handleUpdate = () => {
        const payload = {
            id: motherboard?.id,
            hardwareSpec: {
                name: updatedName, description: updatedDescription, price: updatedPrice, rating: updatedRating,
            },
            energyConsumption: motherboard?.energyConsumption,
            socket: updatedSocket,
            motherboardPhotos: photos
        };

        axios.put(`/api/hardware/motherboard/${id}`, payload)
            .then(() => {
                setIsUpdateModalOpen(false);
                navigate(`/hardware/motherboard/${id}`);
            })
            .catch(console.error);
    };


    const handleDelete = () => {
        axios.delete(`/api/hardware/motherboard/${id}`)
            .then(() => {
                navigate("/hardware/motherboard");
            })
            .catch(console.error);
    };

    const handleAddToBasket = () => {
        if (!user) {
            showLoginModal();
            return;
        }
        const payload = {
            id: motherboard?.id,
            name: motherboard?.hardwareSpec.name,
            description: motherboard?.hardwareSpec.description,
            price: motherboard?.hardwareSpec.price,
            photo: motherboard?.motherboardPhotos && motherboard.motherboardPhotos.length > 0 ? motherboard.motherboardPhotos[motherboard.motherboardPhotos.length - 1] : motherboardPhoto,
            pathToCharacteristicsPage: "/hardware/motherboard"
        };

        axios.post(`/api/basket/${user.id}`, payload)
            .then(() => navigate("/basket"))
            .catch(console.error);
    };

    return (<>
        <div className="product-characteristics">
            <div className="product-characteristics__slideshow-container">
                {photos.map((photo, index) => (<div
                    key={index}
                    className={`product-characteristics__slide ${index === currentSlideIndex ? "product-characteristics__slide--active" : ""}`}
                >
                    <div
                        className="product-characteristics__number-text">{`${index + 1} / ${photos.length}`}</div>
                    <img src={photo} alt="Motherboard" className="product-characteristics__photo-img"/>
                </div>))}
                {!photos.length && <img src={motherboardPhoto} alt="Motherboard default"
                                        className="product-characteristics__photo-img"/>}
                {photos.length > 1 && (<>
                    <button className="product-characteristics__control product-characteristics__control--prev"
                            onClick={() => plusSlides(-1)}>
                        &#10094;
                    </button>
                    <button className="product-characteristics__control product-characteristics__control--next"
                            onClick={() => plusSlides(1)}>
                        &#10095;
                    </button>
                </>)}
            </div>
            <br/>
            <div className="product-characteristics__details">
                <h1 className="product-characteristics__name">{motherboard?.hardwareSpec.name}</h1>
                <div className="product-characteristics__info">
                    <Rating rating={motherboard?.hardwareSpec.rating ?? 0}/>
                    <span
                        className="product-characteristics__rating">{`${motherboard?.hardwareSpec.rating ?? 0}/5`}</span>
                </div>
                <p className="product-characteristics__description">{motherboard?.hardwareSpec.description}</p>
                <p className="product-characteristics__description">Socket: {motherboard?.socket}</p>
                <span className="product-characteristics__price">{`${motherboard?.hardwareSpec.price}$`}</span>
                <button className="product-characteristics__buy-btn" onClick={handleAddToBasket}>
                    Add to basket
                </button>
            </div>
        </div>

        <Photo savePhoto={savePhoto}/>

        {isSuperUser() && (<>
            <button className="upload-button item__delete" onClick={() => setIsUpdateModalOpen(true)}>
                Update
            </button>
            <button className="upload-button item__delete" onClick={() => setIsModalOpen(true)}>
                Delete
            </button>
        </>)}

        <LoginModal isOpen={isLoginModalOpen} onLogin={handleLogin} onClose={hideLoginModal}/>

        {isUpdateModalOpen && (<div className="modal-overlay">
            <div className="modal">
                <div className="modal__header">
                    <h3 className="modal__title">Update Motherboard</h3>
                    <button className="modal__close-btn" onClick={() => setIsUpdateModalOpen(false)}>
                        ×
                    </button>
                </div>
                <div className="modal__body">
                    <input
                        className="modal__input"
                        value={updatedName}
                        onChange={(e) => setUpdatedName(e.target.value)}
                        placeholder="Name"
                    />
                    <input
                        className="modal__input"
                        value={updatedDescription}
                        onChange={(e) => setUpdatedDescription(e.target.value)}
                        placeholder="Description"
                    />
                    <input
                        className="modal__input"
                        type="number"
                        value={updatedPrice}
                        onChange={(e) => setUpdatedPrice(e.target.value)}
                        placeholder="Price"
                    />
                    <input
                        className="modal__input"
                        type="number"
                        value={updatedRating}
                        onChange={(e) => setUpdatedRating(Number(e.target.value))}
                        placeholder="Rating"
                        min="0"
                        max="5"
                    />
                    <input
                        className="modal__input"
                        value={updatedSocket}
                        onChange={(e) => setUpdatedSocket(e.target.value)}
                        placeholder="Socket"
                    />
                </div>
                <div className="modal__footer">
                    <button className="modal__save-btn" onClick={handleUpdate}>
                        Save Changes
                    </button>
                    <button className="modal__close-btn" onClick={() => setIsUpdateModalOpen(false)}>
                        Cancel
                    </button>
                </div>
            </div>
        </div>)}

        {isModalOpen && (<div className="modal-overlay">
            <div className="modal">
                <h2>Are you sure you want to delete this Motherboard?</h2>
                <div className="modal__delete">
                    <button className="default-button modal__delete-button" onClick={handleDelete}>
                        Delete
                    </button>
                    <button className="default-button modal__delete-button"
                            onClick={() => setIsModalOpen(false)}>
                        Close
                    </button>
                </div>
            </div>
        </div>)}
    </>);
}
