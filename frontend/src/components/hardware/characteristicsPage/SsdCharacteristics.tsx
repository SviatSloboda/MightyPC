import { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";
import { SSD } from "../../../model/hardware/SSD.tsx";
import ssdPhoto from "../../../assets/ssd.png";
import Photo from "../Photo.tsx";
import Rating from "./Rating.tsx";

const SsdCharacteristics = () => {
    const [ssd, setSsd] = useState<SSD>();
    const { id } = useParams<{ id: string }>();
    const [photos, setPhotos] = useState<string[]>([]);
    const [currentSlideIndex, setCurrentSlideIndex] = useState(0);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
    const [updatedName, setUpdatedName] = useState('');
    const [updatedDescription, setUpdatedDescription] = useState('');
    const [updatedPrice, setUpdatedPrice] = useState('');
    const [updatedRating, setUpdatedRating] = useState(0);
    const [updatedCapacity, setUpdatedCapacity] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        if (id) {
            axios.get(`/api/hardware/ssd/${id}`)
                .then(response => {
                    setSsd(response.data);
                    setPhotos(response.data.ssdPhotos || []);
                    setUpdatedName(response.data.hardwareSpec.name);
                    setUpdatedDescription(response.data.hardwareSpec.description);
                    setUpdatedPrice(response.data.hardwareSpec.price);
                    setUpdatedRating(response.data.hardwareSpec.rating);
                    setUpdatedCapacity(response.data.capacity);
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
            const response = await axios.post(`/api/ssd/upload/image/${id}`, formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
            setPhotos(prevPhotos => [response.data, ...prevPhotos]);
        } catch (error) {
            console.error('Error uploading image', error);
        }
    };

    const handleDelete = () => {
        axios.delete(`/api/hardware/ssd/${id}`)
            .then(() => {
                navigate('/hardware/ssd');
            })
            .catch(console.error);
    };

    const handleUpdate = () => {
        const payload = {
            id: ssd?.id,
            hardwareSpec: {
                name: updatedName,
                description: updatedDescription,
                price: updatedPrice,
                rating: updatedRating,
            },
            capacity: updatedCapacity,
            energyConsumption: ssd?.energyConsumption,
            ssdPhotos: ssd?.ssdPhotos
        };
        axios.put(`/api/hardware/ssd`, payload)
            .then(response => {
                setSsd(response.data);
                setIsUpdateModalOpen(false);
            })
            .catch(console.error);
    };

    return (
        <>
            <div className="product-characteristics">
                <div className="product-characteristics__slideshow-container">
                    {photos.map((photo, index) => (
                        <div
                            className={`product-characteristics__slide ${index === currentSlideIndex ? 'product-characteristics__slide--active' : ''}`}
                            key={index}
                        >
                            <div className="product-characteristics__number-text">{index + 1} / {photos.length}</div>
                            <img src={photo} alt="SSD" className="product-characteristics__photo-img" />
                        </div>
                    ))}
                    {!photos.length && <img src={ssdPhoto} alt="SSD" className="product-characteristics__photo-img" />}
                    {photos.length >= 2 && (
                        <>
                            <button className="product-characteristics__control product-characteristics__control--prev"
                                    onClick={() => plusSlides(-1)}>&#10094;</button>
                            <button className="product-characteristics__control product-characteristics__control--next"
                                    onClick={() => plusSlides(1)}>&#10095;</button>
                        </>
                    )}
                </div>
                <br />
                <div className="product-characteristics__details">
                    <h1 className="product-characteristics__name">{ssd?.hardwareSpec.name}</h1>
                    <div className="product-characteristics__info">
                        <Rating rating={ssd?.hardwareSpec.rating ?? 0} />
                        <span className="product-characteristics__rating">{ssd?.hardwareSpec.rating}/5</span>
                    </div>
                    <p className="product-characteristics__description">{ssd?.hardwareSpec.description}</p>
                    <p className="product-characteristics__description">Capacity: {updatedCapacity}GB</p>
                    <span className="product-characteristics__price">{ssd?.hardwareSpec.price}$</span>
                    <button className="product-characteristics__buy-btn">Add to basket</button>
                </div>
            </div>
            <Photo savePhoto={savePhoto} />

            <button className="upload-button item__delete" onClick={() => setIsUpdateModalOpen(true)}>Update</button>
            {isUpdateModalOpen && (
                <div className="modal-overlay">
                    <div className="modal">
                        <div className="modal__header">
                            <h3 className="modal__title">Update SSD</h3>
                            <button className="modal__close-btn" onClick={() => setIsUpdateModalOpen(false)}>×</button>
                        </div>
                        <div className="modal__body">
                            <div className="modal__form-group">
                                <input className="modal__input" value={updatedName}
                                       onChange={(e) => setUpdatedName(e.target.value)} placeholder="Name" />
                            </div>
                            <div className="modal__form-group">
                                <input className="modal__input" value={updatedDescription}
                                       onChange={(e) => setUpdatedDescription(e.target.value)}
                                       placeholder="Description" />
                            </div>
                            <div className="modal__form-group">
                                <input className="modal__input" type="number" value={updatedPrice}
                                       onChange={(e) => setUpdatedPrice(e.target.value)} placeholder="Price" />
                            </div>
                            <div className="modal__form-group">
                                <input className="modal__input" type="number" value={updatedRating}
                                       onChange={(e) => setUpdatedRating(Number(e.target.value))} placeholder="Rating"
                                       min="0" max="5" />
                            </div>
                            <div className="modal__form-group">
                                <input className="modal__input" type="text" value={updatedCapacity}
                                       onChange={(e) => setUpdatedCapacity(e.target.value)} placeholder="Capacity" />
                            </div>
                        </div>
                        <div className="modal__footer">
                            <button className="modal__save-btn" onClick={handleUpdate}>Save Changes</button>
                            <button className="modal__close-btn" onClick={() => setIsUpdateModalOpen(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            <button className="upload-button item__delete" onClick={() => setIsModalOpen(true)}>Delete</button>
            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>Are you sure you want to delete this SSD?</h2>
                        <div className="modal__delete">
                            <button className="default-button modal__delete-button" onClick={handleDelete}>Delete</button>
                            <button className="default-button modal__delete-button"
                                    onClick={() => setIsModalOpen(false)}>Close</button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default SsdCharacteristics;
