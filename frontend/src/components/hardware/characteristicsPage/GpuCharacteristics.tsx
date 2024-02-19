import {useEffect, useState} from "react";
import axios from "axios";
import {useNavigate, useParams} from "react-router-dom";
import {GPU} from "../../../model/hardware/GPU.tsx";
import gpuPhoto from "../../../assets/gpu.png";
import Photo from "../Photo.tsx";
import Rating from "./Rating.tsx";

export default function GpuCharacteristics() {
    const [gpu, setGpu] = useState<GPU>();
    const {id} = useParams<{ id: string }>();
    const [photos, setPhotos] = useState<string[]>([]);
    const [isModalOpen, setIsModalOpen] = useState(false);

    const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
    const [updatedName, setUpdatedName] = useState('');
    const [updatedDescription, setUpdatedDescription] = useState('');
    const [updatedPrice, setUpdatedPrice] = useState('');
    const [updatedRating, setUpdatedRating] = useState(0);

    const navigate = useNavigate();

    const [currentSlideIndex, setCurrentSlideIndex] = useState(0);

    useEffect(() => {
        if (id) {
            axios.get(`/api/hardware/gpu/${id}`)
                .then(response => {
                    setGpu(response.data);
                    setPhotos(response.data.gpuPhotos || []);
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
            const response = await axios.post(`/api/gpu/upload/image/${id}`, formData, {
                headers: {'Content-Type': 'multipart/form-data'}
            });
            setPhotos(prevPhotos => [response.data, ...prevPhotos]);
        } catch (error) {
            console.error('Error uploading image', error);
        }
    };

    const handleDelete = () => {
        axios.delete(`/api/hardware/gpu/${id}`)
            .then(() => {
                navigate('/hardware/gpu');
            })
            .catch(console.error);
    };

    const handleUpdate = () => {
        const payload = {
            id: gpu?.id,
            hardwareSpec: {
                name: updatedName,
                description: updatedDescription,
                price: updatedPrice,
                rating: updatedRating,
            },
            performance: gpu?.performance,
            energyConsumption: gpu?.energyConsumption,
            gpuPhotos: gpu?.gpuPhotos
        };
        axios.put(`/api/hardware/gpu`, payload)
            .then(response => {
                setGpu(response.data);
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
                            <img src={photo} alt="GPU" className="product-characteristics__photo-img"/>
                        </div>
                    ))}
                    {!photos.length && <img src={gpuPhoto} alt="GPU" className="product-characteristics__photo-img"/>}
                    {photos.length >= 2 && (
                        <>
                            <button className="product-characteristics__control product-characteristics__control--prev"
                                    onClick={() => plusSlides(-1)}>&#10094;</button>
                            <button className="product-characteristics__control product-characteristics__control--next"
                                    onClick={() => plusSlides(1)}>&#10095;</button>
                        </>
                    )}
                </div>
                <br/>
                <div className="product-characteristics__details">
                    <h1 className="product-characteristics__name">{gpu?.hardwareSpec.name}</h1>
                    <div className="product-characteristics__info">
                        <Rating rating={gpu?.hardwareSpec.rating ?? 0}/>
                        <span className="product-characteristics__rating">{gpu?.hardwareSpec.rating}/5</span>
                    </div>
                    <p className="product-characteristics__description">{gpu?.hardwareSpec.description}</p>
                    <span className="product-characteristics__price">{gpu?.hardwareSpec.price}$</span>
                    <button className="product-characteristics__buy-btn">Add to basket</button>
                </div>
            </div>
            <Photo savePhoto={savePhoto}/>

            <button className="upload-button item__delete" onClick={() => setIsUpdateModalOpen(true)}>Update
            </button>
            {isUpdateModalOpen && (
                <div className="modal-overlay">
                    <div className="modal">
                        <div className="modal__header">
                            <h3 className="modal__title">Update GPU</h3>
                            <button className="modal__close-btn" onClick={() => setIsUpdateModalOpen(false)}>×</button>
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
                                       onChange={(e) => setUpdatedRating(Number(e.target.value))} placeholder="Rating"
                                       min="0" max="5"/>
                            </div>
                        </div>
                        <div className="modal__footer">
                            <button className="modal__save-btn" onClick={handleUpdate}>Save Changes</button>
                            <button className="modal__close-btn" onClick={() => setIsUpdateModalOpen(false)}>Cancel
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <button className="upload-button item__delete" onClick={() => setIsModalOpen(true)}>Delete</button>
            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>Are you sure you want to delete this GPU?</h2>
                        <div className="modal__delete">
                            <button className="default-button modal__delete-button" onClick={handleDelete}>Delete
                            </button>
                            <button className="default-button modal__delete-button"
                                    onClick={() => setIsModalOpen(false)}>Close
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}
