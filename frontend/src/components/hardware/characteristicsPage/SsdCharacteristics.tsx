import {useEffect, useState} from "react";
import axios from "axios";
import {useParams} from "react-router-dom";
import {SSD} from "../../../model/hardware/SSD.tsx";
import ssdPhoto from "../../../assets/ssd.png";
import Photo from "../Photo.tsx";
import Rating from "./Rating.tsx";

export default function SsdCharacteristics() {
    const [ssd, setSsd] = useState<SSD>();
    const {id} = useParams<{ id: string }>();
    const [photos, setPhotos] = useState<string[]>([]);

    const [currentSlideIndex, setCurrentSlideIndex] = useState(0);

    useEffect(() => {
        if (id) {
            axios.get(`/api/hardware/ssd/${id}`)
                .then(response => {
                    setSsd(response.data);
                    setPhotos(response.data.ssdPhotos || []);
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
            const response = await axios.post(`/api/ssd/upload/image/${id}`, formData, {
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
                            <img src={photo} alt="SSD" className="product-characteristics__photo-img"/>
                        </div>
                    ))}
                    {!photos.length && <img src={ssdPhoto} alt="SSD" className="product-characteristics__photo-img"/>}
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
                    <h1 className="product-characteristics__name">{ssd?.hardwareSpec.name}</h1>
                    <div className="product-characteristics__info">
                        <Rating rating={ssd?.hardwareSpec.rating ?? 0}/>
                        <span className="product-characteristics__rating">{ssd?.hardwareSpec.rating}/5</span>
                    </div>
                    <p className="product-characteristics__description">{ssd?.hardwareSpec.description}</p>
                    <p className="product-characteristics__description">Capacity: {ssd?.capacity}GB</p>
                    <span className="product-characteristics__price">{ssd?.hardwareSpec.price}$</span>
                    <button className="product-characteristics__buy-btn">Add to basket</button>
                </div>
            </div>
            <Photo savePhoto={savePhoto}/>
            <br/>
        </>
    );
}
