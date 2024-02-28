import {useEffect, useState} from "react";
import axios from "axios";
import {useNavigate, useParams} from "react-router-dom";
import {HDD} from "../../../model/pc/hardware/HDD.tsx";
import hddPhoto from "../../../assets/hdd.png";
import Photo from "../utils/Photo.tsx";
import Rating from "../utils/Rating.tsx";
import {useAuth} from "../../../contexts/AuthContext.tsx";
import useLoginModal from "../utils/useLoginModal";
import LoginModal from "../utils/LoginModal";

export default function HddCharacteristics() {
    const [hdd, setHdd] = useState<HDD>();
    const {id} = useParams<{ id: string }>();
    const [photos, setPhotos] = useState<string[]>([]);
    const {user, isSuperUser} = useAuth();
    const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
    const [updatedName, setUpdatedName] = useState('');
    const [updatedDescription, setUpdatedDescription] = useState('');
    const [updatedPrice, setUpdatedPrice] = useState('');
    const [updatedRating, setUpdatedRating] = useState(0);
    const [updatedCapacity, setUpdatedCapacity] = useState('');
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false)


    const navigate = useNavigate();

    const [currentSlideIndex, setCurrentSlideIndex] = useState(0);

    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();

    useEffect(() => {
        if (id) {
            axios.get(`/api/hardware/hdd/${id}`)
                .then(response => {
                    setHdd(response.data);
                    setPhotos(response.data.hddPhotos || []);
                    setUpdatedName(response.data.hardwareSpec.name);
                    setUpdatedDescription(response.data.hardwareSpec.description);
                    setUpdatedPrice(response.data.hardwareSpec.price);
                    setUpdatedRating(response.data.hardwareSpec.rating);
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
            const response = await axios.post(`/api/hdd/upload/image/${id}`, formData, {
                headers: {'Content-Type': 'multipart/form-data'}
            });
            setPhotos(prevPhotos => [response.data, ...prevPhotos]);
        } catch (error) {
            console.error('Error uploading image', error);
        }
    };

    const handleDelete = () => {
        axios.delete(`/api/hardware/hdd/${id}`)
            .then(() => {
                navigate('/hardware/hdd');
            })
            .catch(console.error);
    };

    const handleUpdate = () => {
        const payload = {
            id: hdd?.id, hardwareSpec: {
                name: updatedName, description: updatedDescription, price: updatedPrice, rating: updatedRating,
            }, capacity: hdd?.capacity, energyConsumption: hdd?.energyConsumption, hddPhotos: hdd?.hddPhotos
        };
        axios.put(`/api/hardware/hdd`, payload)
            .then(response => {
                setHdd(response.data);
                setIsUpdateModalOpen(false);
            })
            .catch(console.error);
    };

    const handleAddToBasket = () => {
        if (!user) {
            showLoginModal();
            return;
        }
        const payload = {
            id: hdd?.id,
            type: "hdd",
            name: hdd?.hardwareSpec.name,
            description: hdd?.hardwareSpec.description,
            price: hdd?.hardwareSpec.price,
            photos: hdd && (hdd.hddPhotos?.length ?? 0) > 0 ? hdd.hddPhotos : ['https://res.cloudinary.com/dmacmrhwq/image/upload/v1708536729/cloudinary_file_test/plain.png.png']
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
                {photos.map((photo, index) => (<div
                    className={`product-characteristics__slide ${index === currentSlideIndex ? 'product-characteristics__slide--active' : ''}`}
                    key={index}
                >
                    <div className="product-characteristics__number-text">{index + 1} / {photos.length}</div>
                    <img src={photo} alt="HDD" className="product-characteristics__photo-img"/>
                </div>))}
                {!photos.length && <img src={hddPhoto} alt="HDD" className="product-characteristics__photo-img"/>}
                {photos.length >= 2 && (<>
                    <button className="product-characteristics__control product-characteristics__control--prev"
                            onClick={() => plusSlides(-1)}>&#10094;</button>
                    <button className="product-characteristics__control product-characteristics__control--next"
                            onClick={() => plusSlides(1)}>&#10095;</button>
                </>)}
            </div>
            <br/>
            <div className="product-characteristics__details">
                <h1 className="product-characteristics__name">{hdd?.hardwareSpec.name}</h1>
                <div className="product-characteristics__info">
                    <Rating rating={hdd?.hardwareSpec.rating ?? 0}/>
                    <span className="product-characteristics__rating">{hdd?.hardwareSpec.rating}/5</span>
                </div>
                <p className="product-characteristics__description">{hdd?.hardwareSpec.description}</p>
                <p className="product-characteristics__description">Capacity: {hdd?.capacity}GB</p>
                <span className="product-characteristics__price">{hdd?.hardwareSpec.price}$</span>
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
                        <h3 className="modal__title">Update HDD</h3>
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
                                   placeholder="Rating"
                                   min="0" max="5"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" type="text" value={updatedCapacity}
                                   onChange={(e) => setUpdatedCapacity(e.target.value)}
                                   placeholder="Capacity"/>
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
                    <h2>Are you sure you want to delete this HDD?</h2>
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
