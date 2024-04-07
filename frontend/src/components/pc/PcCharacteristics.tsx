import {useEffect, useState} from "react";
import axios from "axios";
import {useLocation, useNavigate, useParams} from "react-router-dom";
import {useAuth} from "../../contexts/AuthContext.tsx";
import useLoginModal from "../hardware/utils/useLoginModal.ts";
import LoginModal from "../hardware/utils/LoginModal.tsx";
import Photo from "../hardware/utils/Photo.tsx";
import Rating from "../hardware/utils/Rating.tsx";
import pcPhoto from "../../assets/Pc.png"
import {PC} from "../../model/pc/PC.tsx";

export default function PcCharacteristics() {
    const [pc, setPc] = useState<PC | null>(null);
    const {id} = useParams<{ id: string }>();
    const [photos, setPhotos] = useState<string[]>([]);
    const {user, isSuperUser} = useAuth();

    const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
    const [updatedName, setUpdatedName] = useState('');
    const [updatedDescription, setUpdatedDescription] = useState('');
    const [updatedPrice, setUpdatedPrice] = useState('');
    const [updatedRating, setUpdatedRating] = useState(0);


    const [updatedCpuId, setUpdatedCpuId] = useState<string>("");
    const [updatedGpuId, setUpdatedGpuId] = useState<string>("");
    const [updatedMotherboardId, setUpdatedMotherboardId] = useState<string>("");
    const [updatedRamId, setUpdatedRamId] = useState<string>("");
    const [updatedSsdId, setUpdatedSsdId] = useState<string>("");
    const [updatedHddId, setUpdatedHddId] = useState<string>("");
    const [updatedPcCaseId, setUpdatedPcCaseId] = useState<string>("");
    const [updatedPsuId, setUpdatedPsuId] = useState<string>("");

    const location = useLocation();
    const {state} = location;
    const isUserPc = state?.isUserPc || false;

    const [isModalOpen, setIsModalOpen] = useState<boolean>(false)

    const navigate = useNavigate();

    const [currentSlideIndex, setCurrentSlideIndex] = useState(0);

    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();

    useEffect(() => {
        const fetchPcData = async () => {
            if (id) {
                try {
                    let url: string = `/api/pc/${id}`;
                    if (isUserPc && user) {
                        url = `/api/user-pcs/${user.id}/${id}`;
                    }

                    const response = await axios.get(url);
                    setPc(response.data);
                    setPhotos(response.data.photos || []);

                    setUpdatedName(response.data.hardwareSpec.name);
                    setUpdatedDescription(response.data.hardwareSpec.description);
                    setUpdatedPrice(response.data.hardwareSpec.price);
                    setUpdatedRating(response.data.hardwareSpec.rating);

                    setUpdatedCpuId(response.data.specsIds.cpuId);
                    setUpdatedGpuId(response.data.specsIds.gpuId);
                    setUpdatedMotherboardId(response.data.specsIds.motherboardId);
                    setUpdatedRamId(response.data.specsIds.ramId);
                    setUpdatedSsdId(response.data.specsIds.ssdId);
                    setUpdatedHddId(response.data.specsIds.hddId);
                    setUpdatedPcCaseId(response.data.specsIds.pcCaseId);
                    setUpdatedPsuId(response.data.specsIds.powerSupplyId);
                } catch (error) {
                    console.error('Error fetching PC data:', error);
                }
            }
        };
        fetchPcData();
    }, [id, isUpdateModalOpen]);


    const plusSlides = (n: number) => {
        setCurrentSlideIndex(prevIndex => (prevIndex + n + photos.length) % photos.length);
    };

    const savePhoto = async (file: File) => {
        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await axios.post(`/api/pc/upload/image/${id}`, formData, {
                headers: {'Content-Type': 'multipart/form-data'}
            });
            setPhotos(prevPhotos => [response.data, ...prevPhotos]);
        } catch (error) {
            console.error('Error uploading image', error);
        }
    };

    const handleDelete = () => {
        axios.delete(`/api/pc/${id}`)
            .then(() => {
                navigate('../pc');
            })
            .catch(console.error);
    };

    const handleUpdate = () => {
        const payload = {
            id: pc?.id, hardwareSpec: {
                name: updatedName, description: updatedDescription, price: updatedPrice, rating: updatedRating,
            }, specsIds: {
                cpuId: updatedCpuId,
                gpuId: updatedGpuId,
                motherboardId: updatedMotherboardId,
                ramId: updatedRamId,
                ssdId: updatedSsdId,
                hddId: updatedHddId,
                powerSupplyId: updatedPsuId,
                pcCaseId: updatedPcCaseId
            }, photos: pc?.photos
        };
        axios.put(`/api/pc`, payload)
            .then(response => {
                setPc(response.data);
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
            id: pc?.id,
            type: "pc",
            name: pc?.hardwareSpec.name,
            description: pc?.hardwareSpec.description,
            price: pc?.hardwareSpec.price,
            photos: pc && (pc.photos?.length ?? 0) > 0 ? pc.photos : [pcPhoto]
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
                    <img src={photo} alt="PC" className="product-characteristics__photo-img"/>
                </div>))}
                {!photos.length && <img src={pcPhoto} alt="PC" className="product-characteristics__photo-img"/>}
                {photos.length >= 2 && (<>
                    <button className="product-characteristics__control product-characteristics__control--prev"
                            onClick={() => plusSlides(-1)}>&#10094;</button>
                    <button className="product-characteristics__control product-characteristics__control--next"
                            onClick={() => plusSlides(1)}>&#10095;</button>
                </>)}
            </div>
            <br/>

            {pc && (<div className="product-characteristics__details pc-details">
                <div className={"product-characteristics--nameAndBuy"}>
                    <div className={"product-characteristics--nameAndRating"}>
                        <h1 className="product-characteristics__name pcCharacteristic__name">{pc?.hardwareSpec.name}</h1>
                        <div className="product-characteristics__info">
                            <Rating rating={pc?.hardwareSpec.rating ?? 0}/>
                            <span className="product-characteristics__rating">{pc?.hardwareSpec.rating}/5</span>
                        </div>
                    </div>

                    <div className="product-characteristics--Buy">
                        <span className="product-characteristics__price">{pc?.hardwareSpec.price}$</span>
                        <button className="product-characteristics__buy-btn pc-addToBasket-btn"
                                onClick={() => handleAddToBasket()}>Add to basket
                        </button>
                    </div>
                </div>

                <p className="product-characteristics__description">{pc?.hardwareSpec.description}</p>

                <div className="pc-details">
                    <p className="pc-specification" data-label="Cpu:"><span>{pc?.specsNames.cpuName}</span></p>
                    <p className="pc-specification" data-label="Gpu:"><span>{pc?.specsNames.gpuName}</span></p>
                    <p className="pc-specification" data-label="Motherboard:">
                        <span>{pc?.specsNames.motherboardName}</span>
                    </p>
                    <p className="pc-specification" data-label="Ram:"><span>{pc?.specsNames.ramName}</span></p>
                    <p className="pc-specification" data-label="Ssd:"><span>{pc?.specsNames.ssdName}</span></p>
                    <p className="pc-specification" data-label="Hdd:"><span>{pc?.specsNames.hddName}</span></p>
                    <p className="pc-specification" data-label="Pc case:"><span>{pc?.specsNames.pcCaseName}</span>
                    </p>
                    <p className="pc-specification" data-label="Psu:"><span>{pc?.specsNames.powerSupplyName}</span>
                    </p>
                </div>
            </div>)}

        </div>

        <LoginModal isOpen={isLoginModalOpen} onLogin={handleLogin} onClose={hideLoginModal}/>

        {(isSuperUser() || isUserPc) && (<>
            <Photo savePhoto={savePhoto}/>
            <button className="upload-button item__delete" onClick={() => setIsUpdateModalOpen(true)}>Update
            </button>

            {isUpdateModalOpen && (<div className="modal-overlay">
                <div className="modal">
                    <div className="modal__header">
                        <h3 className="modal__title">Update PC</h3>
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
                            <input className="modal__input" value={updatedCpuId}
                                   onChange={(e) => setUpdatedCpuId(e.target.value)} placeholder="CPU ID"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" value={updatedGpuId}
                                   onChange={(e) => setUpdatedGpuId(e.target.value)} placeholder="GPU ID"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" value={updatedMotherboardId}
                                   onChange={(e) => setUpdatedMotherboardId(e.target.value)}
                                   placeholder="Motherboard ID"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" value={updatedRamId}
                                   onChange={(e) => setUpdatedRamId(e.target.value)} placeholder="RAM ID"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" value={updatedSsdId}
                                   onChange={(e) => setUpdatedSsdId(e.target.value)} placeholder="SSD ID"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" value={updatedHddId}
                                   onChange={(e) => setUpdatedHddId(e.target.value)} placeholder="HDD ID"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" value={updatedPcCaseId}
                                   onChange={(e) => setUpdatedPcCaseId(e.target.value)}
                                   placeholder="PC Case ID"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" value={updatedPsuId}
                                   onChange={(e) => setUpdatedPsuId(e.target.value)} placeholder="PSU ID"/>
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
                    <h2>Are you sure you want to delete this PC?</h2>
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
