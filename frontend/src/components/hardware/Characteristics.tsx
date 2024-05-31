import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import Photo from "./utils/Photo.tsx";
import Rating from "./utils/Rating.tsx";
import {useAuth} from "../../contexts/AuthContext.tsx";
import useLoginModal from "../login/useLoginModal.ts";
import LoginModal from "../login/LoginModal.tsx";
import cpuPhoto from "../../assets/hardware/cpu.png";
import gpuPhoto from "../../assets/hardware/gpu.png";
import hddPhoto from "../../assets/hardware/hdd.png";
import motherboardPhoto from "../../assets/hardware/motherboard.png";
import pcCasePhoto from "../../assets/hardware/pcCase.png";
import psuPhoto from "../../assets/hardware/psu.png";
import ramPhoto from "../../assets/hardware/ram.png";
import ssdPhoto from "../../assets/hardware/ssd.png";
import useAxiosWithAuth from "../../contexts/useAxiosWithAuth.ts";

type HardwareType = 'cpu' | 'gpu' | 'hdd' | 'motherboard' | 'pc-case' | 'psu' | 'ram' | 'ssd';

interface HardwareSpec {
    name: string;
    description: string;
    price: string;
    rating: number;
}

interface HardwareItemBase {
    id: string;
    hardwareSpec: HardwareSpec;
    photos?: string[];
}

interface CPU extends HardwareItemBase {
    socket: string;
    energyConsumption: number;
    cpuPhotos?: string[];
}

interface GPU extends HardwareItemBase {
    energyConsumption: number;
    gpuPhotos?: string[];
}

interface HDD extends HardwareItemBase {
    capacity: number;
    energyConsumption: number;
    hddPhotos?: string[];
}

interface Motherboard extends HardwareItemBase {
    socket: string;
    energyConsumption: number;
    motherboardPhotos?: string[];
}

interface PcCase extends HardwareItemBase {
    dimensions: string;
    pcCasePhotos?: string[];
}

interface PSU extends HardwareItemBase {
    power: number;
    psuPhotos?: string[];
}

interface RAM extends HardwareItemBase {
    type: string;
    energyConsumption: number;
    memorySize: number;
    ramPhotos?: string[];
}

interface SSD extends HardwareItemBase {
    capacity: number;
    energyConsumption: number;
    ssdPhotos?: string[];
}

type HardwareItem = CPU | GPU | HDD | Motherboard | PcCase | PSU | RAM | SSD;

interface HardwarePageProps {
    type: HardwareType;
    apiPath: string;
    defaultPhoto: string;
}

function Characteristics<T extends HardwareItem>({type, apiPath, defaultPhoto}: Readonly<HardwarePageProps>) {
    const [item, setItem] = useState<T | null>(null);
    const {id} = useParams<{ id: string }>();
    const [photos, setPhotos] = useState<string[]>([]);
    const {user, isSuperUser} = useAuth();
    const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
    const [updatedFields, setUpdatedFields] = useState<Partial<T>>({});
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
    const [currentSlideIndex, setCurrentSlideIndex] = useState(0);
    const navigate = useNavigate();
    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();

    const axiosInstance = useAxiosWithAuth();

    useEffect(() => {
        if (id) {
            axiosInstance.get(`${apiPath}/${id}`)
                .then(response => {
                    setItem(response.data);
                    setPhotos(response.data.photos ?? []);
                    setUpdatedFields(response.data);
                })
                .catch(console.error);
        }
    }, [id, isUpdateModalOpen, apiPath]);

    const plusSlides = (n: number) => {
        setCurrentSlideIndex(prevIndex => (prevIndex + n + photos.length) % photos.length);
    };

    const savePhoto = async (file: File) => {
        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await axiosInstance.post(`${apiPath}/upload/image/${id}`, formData, {
                headers: {'Content-Type': 'multipart/form-data'}
            });
            setPhotos(prevPhotos => [response.data, ...prevPhotos]);
        } catch (error) {
            console.error('Error uploading image', error);
        }
    };

    const handleDelete = () => {
        axiosInstance.delete(`${apiPath}/${id}`)
            .then(() => {
                navigate(`/hardware/${type}`);
            })
            .catch(console.error);
    };

    const handleUpdate = () => {
        axiosInstance.put(`${apiPath}`, updatedFields)
            .then(response => {
                setItem(response.data);
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
            id: item?.id,
            name: item?.hardwareSpec.name,
            description: item?.hardwareSpec.description,
            price: item?.hardwareSpec.price,
            photo: photos.length > 0 ? photos[photos.length - 1] : defaultPhoto,
            pathToCharacteristicsPage: `/hardware/${type}`
        };

        axiosInstance.post<void>(`/basket/${user.id}`, payload)
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
                    key={photo}
                >
                    <div className="product-characteristics__number-text">{index + 1} / {photos.length}</div>
                    <img src={photo} alt={type} className="product-characteristics__photo-img"/>
                </div>))}
                {!photos.length && <img src={defaultPhoto} alt={type} className="product-characteristics__photo-img"/>}
                {photos.length >= 2 && (<>
                    <button className="product-characteristics__control product-characteristics__control--prev"
                            onClick={() => plusSlides(-1)}>&#10094;</button>
                    <button className="product-characteristics__control product-characteristics__control--next"
                            onClick={() => plusSlides(1)}>&#10095;</button>
                </>)}
            </div>
            <br/>
            <div className="product-characteristics__details">
                <h1 className="product-characteristics__name">{item?.hardwareSpec.name}</h1>
                <div className="product-characteristics__info">
                    <Rating rating={item?.hardwareSpec.rating ?? 0}/>
                    <span className="product-characteristics__rating">{item?.hardwareSpec.rating}/5</span>
                </div>
                <p className="product-characteristics__description">{item?.hardwareSpec.description}</p>
                {type === "cpu" && item &&
                    <p className="product-characteristics__description">Socket: {(item as CPU).socket}</p>}
                {type === "hdd" && item &&
                    <p className="product-characteristics__description">Capacity: {(item as HDD).capacity}GB</p>}
                {type === "psu" && item &&
                    <p className="product-characteristics__description">Power: {(item as PSU).power}W</p>}
                {type === "ram" && item && <>
                    <p className="product-characteristics__description">Type: {(item as RAM).type}</p>
                    <p className="product-characteristics__description">Memory
                        Size: {(item as RAM).memorySize}GB</p>
                </>}
                {type === "ssd" && item &&
                    <p className="product-characteristics__description">Capacity: {(item as SSD).capacity}GB</p>}
                {type === "pc-case" && item &&
                    <p className="product-characteristics__description">Dimensions: {(item as PcCase).dimensions}</p>}
                <span className="product-characteristics__price">{item?.hardwareSpec.price}$</span>
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
                        <h3 className="modal__title">Update {type}</h3>
                        <button className="modal__close-btn" onClick={() => setIsUpdateModalOpen(false)}>×
                        </button>
                    </div>
                    <div className="modal__body">
                        <div className="modal__form-group">
                            <input className="modal__input" value={updatedFields.hardwareSpec?.name ?? ""}
                                   onChange={(e) => setUpdatedFields({
                                       ...updatedFields, hardwareSpec: {
                                           ...updatedFields.hardwareSpec, name: e.target.value,
                                       },
                                   })} placeholder="Name"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input"
                                   value={updatedFields.hardwareSpec?.description ?? ""}
                                   onChange={(e) => setUpdatedFields({
                                       ...updatedFields, hardwareSpec: {
                                           ...updatedFields.hardwareSpec, description: e.target.value,
                                       },
                                   })} placeholder="Description"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" type="number"
                                   value={updatedFields.hardwareSpec?.price ?? ""}
                                   onChange={(e) => setUpdatedFields({
                                       ...updatedFields, hardwareSpec: {
                                           ...updatedFields.hardwareSpec, price: e.target.value,
                                       },
                                   })} placeholder="Price"/>
                        </div>
                        <div className="modal__form-group">
                            <input className="modal__input" type="number"
                                   value={updatedFields.hardwareSpec?.rating ?? ""}
                                   onChange={(e) => setUpdatedFields({
                                       ...updatedFields, hardwareSpec: {
                                           ...updatedFields.hardwareSpec, rating: Number(e.target.value),
                                       },
                                   })} placeholder="Rating" min="0" max="5"/>
                        </div>
                        {type === "cpu" && (<div className="modal__form-group">
                            <input className="modal__input"
                                   value={(updatedFields as Partial<CPU>).socket ?? ""}
                                   onChange={(e) => setUpdatedFields({
                                       ...updatedFields, socket: e.target.value
                                   })} placeholder="Socket"/>
                        </div>)}
                        {type === "hdd" && (<div className="modal__form-group">
                            <input className="modal__input"
                                   value={(updatedFields as Partial<HDD>).capacity ?? ""}
                                   onChange={(e) => setUpdatedFields({
                                       ...updatedFields, capacity: Number(e.target.value)
                                   })} placeholder="Capacity"/>
                        </div>)}
                        {type === "psu" && (<div className="modal__form-group">
                            <input className="modal__input"
                                   value={(updatedFields as Partial<PSU>).power ?? ""}
                                   onChange={(e) => setUpdatedFields({
                                       ...updatedFields, power: Number(e.target.value)
                                   })} placeholder="Power"/>
                        </div>)}
                        {type === "ram" && (<>
                            <div className="modal__form-group">
                                <input className="modal__input"
                                       value={(updatedFields as Partial<RAM>).type ?? ""}
                                       onChange={(e) => setUpdatedFields({
                                           ...updatedFields, type: e.target.value
                                       })} placeholder="Type"/>
                            </div>
                            <div className="modal__form-group">
                                <input className="modal__input" type="number"
                                       value={(updatedFields as Partial<RAM>).memorySize ?? ""}
                                       onChange={(e) => setUpdatedFields({
                                           ...updatedFields, memorySize: Number(e.target.value)
                                       })} placeholder="Memory Size"/>
                            </div>
                        </>)}
                        {type === "ssd" && (<div className="modal__form-group">
                            <input className="modal__input"
                                   value={(updatedFields as Partial<SSD>).capacity ?? ""}
                                   onChange={(e) => setUpdatedFields({
                                       ...updatedFields, capacity: Number(e.target.value)
                                   })} placeholder="Capacity"/>
                        </div>)}
                        {type === "pc-case" && (<div className="modal__form-group">
                            <input className="modal__input"
                                   value={(updatedFields as Partial<PcCase>).dimensions ?? ""}
                                   onChange={(e) => setUpdatedFields({
                                       ...updatedFields, dimensions: e.target.value
                                   })} placeholder="Dimensions"/>
                        </div>)}
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
                    <h2>Are you sure you want to delete this {type}?</h2>
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

const CpuCharacteristics = () => <Characteristics type="cpu" apiPath="/hardware/cpu" defaultPhoto={cpuPhoto}/>;
const GpuCharacteristics = () => <Characteristics type="gpu" apiPath="/hardware/gpu" defaultPhoto={gpuPhoto}/>;
const HddCharacteristics = () => <Characteristics type="hdd" apiPath="/hardware/hdd" defaultPhoto={hddPhoto}/>;
const MotherboardCharacteristics = () => <Characteristics type="motherboard" apiPath="/hardware/motherboard"
                                                          defaultPhoto={motherboardPhoto}/>;
const PcCaseCharacteristics = () => <Characteristics type="pc-case" apiPath="/hardware/pc-case"
                                                     defaultPhoto={pcCasePhoto}/>;
const PsuCharacteristics = () => <Characteristics type="psu" apiPath="/hardware/psu" defaultPhoto={psuPhoto}/>;
const RamCharacteristics = () => <Characteristics type="ram" apiPath="/hardware/ram" defaultPhoto={ramPhoto}/>;
const SsdCharacteristics = () => <Characteristics type="ssd" apiPath="/hardware/ssd" defaultPhoto={ssdPhoto}/>;

export {
    CpuCharacteristics,
    GpuCharacteristics,
    HddCharacteristics,
    MotherboardCharacteristics,
    PcCaseCharacteristics,
    PsuCharacteristics,
    RamCharacteristics,
    SsdCharacteristics
};

