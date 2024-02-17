import {useEffect, useState} from "react";
import axios from "axios";
import {useParams} from "react-router-dom";
import {Motherboard} from "../../../model/hardware/Motherboard.tsx";
import motherboardPhoto from "../../../assets/motherboard.png";
import Photo from "../Photo.tsx";
import Rating from "./Rating.tsx";

export default function MotherboardCharacteristics() {
    const [motherboard, setMotherboard] = useState<Motherboard>();
    const {id} = useParams<{ id: string }>();
    const [photos, setPhotos] = useState<string[]>([]);

    const [currentSlideIndex, setCurrentSlideIndex] = useState(0);

    useEffect(() => {
        if (id) {
            axios.get(`/api/hardware/motherboard/${id}`)
                .then(response => {
                    setMotherboard(response.data);
                    setPhotos(response.data.motherboardPhotos || []);
                })
                .catch(console.error);
        }
    }, [id]);

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
            setPhotos(prevPhotos => [response.data, ...prevPhotos]);
        } catch (error) {
            console.error('Error uploading image', error);
        }
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
                            <img src={photo} alt="Motherboard" className="product-characteristics__photo-img"/>
                        </div>
                    ))}
                    {!photos.length &&
                        <img src={motherboardPhoto} alt="Motherboard" className="product-characteristics__photo-img"/>}
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
                    <h1 className="product-characteristics__name">{motherboard?.hardwareSpec.name}</h1>
                    <div className="product-characteristics__info">
                        <Rating rating={motherboard?.hardwareSpec.rating ?? 0}/>
                        <span className="product-characteristics__rating">{motherboard?.hardwareSpec.rating}/5</span>
                    </div>
                    <p className="product-characteristics__description">{motherboard?.hardwareSpec.description}</p>
                    <span className="product-characteristics__price">{motherboard?.hardwareSpec.price}$</span>
                    <button className="product-characteristics__buy-btn">Add to basket</button>
                </div>
            </div>
            <Photo savePhoto={savePhoto}/>
            <br/>
        </>
    );
}
