import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './utils/ProductBox.tsx';
import Modal, {useModal} from './utils/Modal.tsx';
import {useNavigate} from "react-router-dom";
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
    cpuPhotos?: string[];
}

interface GPU extends HardwareItemBase {
    gpuPhotos?: string[];
}

interface HDD extends HardwareItemBase {
    capacity: string;
    hddPhotos?: string[];
}

interface Motherboard extends HardwareItemBase {
    socket: string;
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
    memorySize: number;
    ramPhotos?: string[];
}

interface SSD extends HardwareItemBase {
    capacity: number;
    ssdPhotos?: string[];
}

type HardwareItem = CPU | GPU | HDD | Motherboard | PcCase | PSU | RAM | SSD;

interface HardwareConfig<T extends HardwareItem> {
    title: string;
    apiPath: string;
    addApiPath: string;
    photo: string;
    additionalFields: Partial<T>;
}

const hardwareConfig: Record<HardwareType, HardwareConfig<HardwareItem>> = {
    cpu: {
        title: 'CPU List',
        apiPath: '/api/hardware/cpu/page',
        addApiPath: '/api/hardware/cpu',
        photo: cpuPhoto,
        additionalFields: {socket: ''} as Partial<CPU>
    }, gpu: {
        title: 'GPU List',
        apiPath: '/api/hardware/gpu/page',
        addApiPath: '/api/hardware/gpu',
        photo: gpuPhoto,
        additionalFields: {} as Partial<GPU>
    }, hdd: {
        title: 'HDD List',
        apiPath: '/api/hardware/hdd/page',
        addApiPath: '/api/hardware/hdd',
        photo: hddPhoto,
        additionalFields: {capacity: ''} as Partial<HDD>
    }, motherboard: {
        title: 'Motherboard List',
        apiPath: '/api/hardware/motherboard/page',
        addApiPath: '/api/hardware/motherboard',
        photo: motherboardPhoto,
        additionalFields: {socket: ''} as Partial<Motherboard>
    }, "pc-case": {
        title: 'PcCase List',
        apiPath: '/api/hardware/pc-case/page',
        addApiPath: '/api/hardware/pc-case',
        photo: pcCasePhoto,
        additionalFields: {dimensions: ''} as Partial<PcCase>
    }, psu: {
        title: 'PSU List',
        apiPath: '/api/hardware/psu/page',
        addApiPath: '/api/hardware/psu',
        photo: psuPhoto,
        additionalFields: {power: 0} as Partial<PSU>
    }, ram: {
        title: 'RAM List',
        apiPath: '/api/hardware/ram/page',
        addApiPath: '/api/hardware/ram',
        photo: ramPhoto,
        additionalFields: {type: '', memorySize: 0} as Partial<RAM>
    }, ssd: {
        title: 'SSD List',
        apiPath: '/api/hardware/ssd/page',
        addApiPath: '/api/hardware/ssd',
        photo: ssdPhoto,
        additionalFields: {capacity: 0} as Partial<SSD>
    }
};

interface HardwarePageProps {
    type: HardwareType;
}

function HardwarePage<T extends HardwareItem>({type}: Readonly<HardwarePageProps>) {
    const [items, setItems] = useState<T[]>([]);
    const [currentPage, setCurrentPage] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);
    const itemsPerPage = 8;
    const [modalOpen, toggleModal] = useModal();
    const [hardwareSpec, setHardwareSpec] = useState<HardwareSpec>({name: "", description: "", price: "", rating: 0});
    const [additionalFields, setAdditionalFields] = useState<Partial<T>>(hardwareConfig[type].additionalFields as Partial<T>);
    const navigate = useNavigate();
    const {user, isSuperUser} = useAuth();
    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();

    useEffect(() => {
        async function fetchItems() {
            try {
                const response = await axios.get(`${hardwareConfig[type].apiPath}?page=${currentPage}&size=${itemsPerPage}`);
                setItems(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error(`Failed to fetch ${type}s:`, error);
            }
        }

        fetchItems();
    }, [currentPage, itemsPerPage, type]);

    function paginate(pageNumber: number) {
        setCurrentPage(pageNumber - 1);
    }

    function saveValues() {
        const payload = {
            hardwareSpec, ...additionalFields
        };
        axios.post(hardwareConfig[type].addApiPath, payload)
            .then(response => {
                console.log(`${type} added:`, response.data);
                setItems(prevItems => [...prevItems, response.data]);
                toggleModal();
            })
            .catch(error => console.error(`Failed to add ${type}:`, error));
    }

    const handleAddToBasket = (item: T) => {
        if (!user) {
            showLoginModal();
            return;
        }
        const photoKey = `${type}Photos` as keyof T;
        const photos = item[photoKey] as unknown as string[] | undefined;
        const payload = {
            id: item.id,
            name: item.hardwareSpec.name,
            description: item.hardwareSpec.description,
            price: item.hardwareSpec.price,
            photo: photos && photos.length > 0 ? photos[0] : hardwareConfig[type].photo,
            pathToCharacteristicsPage: `/hardware/${type}`
        };

        axios.post(`/api/basket/${user.id}`, payload)
            .then(() => {
                navigate("../basket/");
            })
            .catch(console.error);
    };

    return (<>
        <h1 className="body--product-page">{hardwareConfig[type].title}</h1>
        {isSuperUser() && <button className="default-button" onClick={toggleModal}>Add {type}</button>}

        <Modal isOpen={modalOpen} onClose={toggleModal} onSave={saveValues}>
            {Object.keys(hardwareSpec).map((key) => (<div className="modal__form-group" key={key}>
                <label htmlFor={`modal-${key}`}
                       className="modal__form-label">{key.charAt(0).toUpperCase() + key.slice(1)}:</label>
                <input id={`modal-${key}`} className="modal__input"
                       value={hardwareSpec[key as keyof HardwareSpec]}
                       onChange={(e) => setHardwareSpec({...hardwareSpec, [key]: e.target.value})} required/>
            </div>))}
            {Object.keys(additionalFields).map((key) => (<div className="modal__form-group" key={key}>
                <label htmlFor={`modal-${key}`}
                       className="modal__form-label">{key.charAt(0).toUpperCase() + key.slice(1)}:</label>
                <input id={`modal-${key}`} className="modal__input"
                       value={additionalFields[key as keyof T] as unknown as string | number}
                       onChange={(e) => setAdditionalFields({...additionalFields, [key]: e.target.value})}
                       required/>
            </div>))}
        </Modal>

        <div className="product-list">
            {items.map(item => (<ProductBox
                key={item.id}
                product={item}
                imgSrc={item.photos && item.photos.length > 0 ? item.photos[0] : hardwareConfig[type].photo}
                toCharacteristicsPage={() => navigate(`/hardware/${type}/${item.id}`)}
                onAddToBasket={() => handleAddToBasket(item)}
            />))}
        </div>

        <div className="product-list__pagination">
            {Array.from({length: totalPages}, (_, i) => (<button
                key={i}
                onClick={() => paginate(i + 1)}
                className={`pagination__button ${currentPage === i ? 'pagination__button--active' : ''}`}
            >
                {i + 1}
            </button>))}
        </div>

        <LoginModal isOpen={isLoginModalOpen} onLogin={handleLogin} onClose={hideLoginModal}/>
    </>);
}

const CpuPage = () => <HardwarePage<CPU> type="cpu"/>;
const GpuPage = () => <HardwarePage<GPU> type="gpu"/>;
const HddPage = () => <HardwarePage<HDD> type="hdd"/>;
const MotherboardPage = () => <HardwarePage<Motherboard> type="motherboard"/>;
const PcCasePage = () => <HardwarePage<PcCase> type="pc-case"/>;
const PowerSupplyPage = () => <HardwarePage<PSU> type="psu"/>;
const RamPage = () => <HardwarePage<RAM> type="ram"/>;
const SsdPage = () => <HardwarePage<SSD> type="ssd"/>;

export {
    CpuPage, GpuPage, HddPage, MotherboardPage, PcCasePage, PowerSupplyPage, RamPage, SsdPage
};
