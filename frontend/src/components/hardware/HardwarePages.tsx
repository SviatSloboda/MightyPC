import React, {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './utils/ProductBox.tsx';
import Modal, {useModal} from './utils/Modal.tsx';
import {useNavigate} from 'react-router-dom';
import {useAuth} from '../../contexts/AuthContext.tsx';
import useLoginModal from '../login/useLoginModal.ts';
import LoginModal from '../login/LoginModal.tsx';
import {Range, getTrackBackground} from 'react-range';
import cpuPhoto from '../../assets/hardware/cpu.png';
import gpuPhoto from '../../assets/hardware/gpu.png';
import hddPhoto from '../../assets/hardware/hdd.png';
import motherboardPhoto from '../../assets/hardware/motherboard.png';
import pcCasePhoto from '../../assets/hardware/pcCase.png';
import psuPhoto from '../../assets/hardware/psu.png';
import ramPhoto from '../../assets/hardware/ram.png';
import ssdPhoto from '../../assets/hardware/ssd.png';
import {IThumbProps, ITrackProps} from "react-range/lib/types";

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
    apiPath: string;
    addApiPath: string;
    photo: string;
    additionalFields: Partial<T>;
    filterFields: FilterField[];
}

type FilterField = {
    label: string; field: string; type: 'text' | 'number'; placeholder: string;
};

const hardwareConfig: Record<HardwareType, HardwareConfig<HardwareItem>> = {
    cpu: {
        apiPath: '/api/hardware/cpu/filtered',
        addApiPath: '/api/hardware/cpu',
        photo: cpuPhoto,
        additionalFields: {socket: ''} as Partial<CPU>,
        filterFields: [
            {label: 'Socket', field: 'socket', type: 'text', placeholder: 'Socket'},
            {label: 'Min Price', field: 'lowestPrice', type: 'number', placeholder: 'Min Price'},
            {label: 'Max Price', field: 'highestPrice', type: 'number', placeholder: 'Max Price'}
        ]
    },
    gpu: {
        apiPath: '/api/hardware/gpu/filtered',
        addApiPath: '/api/hardware/gpu',
        photo: gpuPhoto,
        additionalFields: {} as Partial<GPU>,
        filterFields: [
            {label: 'Min Price', field: 'lowestPrice', type: 'number', placeholder: 'Min Price'},
            {label: 'Max Price', field: 'highestPrice', type: 'number', placeholder: 'Max Price'}
        ]
    },
    hdd: {
        apiPath: '/api/hardware/hdd/filtered',
        addApiPath: '/api/hardware/hdd',
        photo: hddPhoto,
        additionalFields: {capacity: ''} as Partial<HDD>,
        filterFields: [
            {label: 'Capacity', field: 'capacity', type: 'text', placeholder: 'Capacity'},
            {label: 'Min Price', field: 'lowestPrice', type: 'number', placeholder: 'Min Price'},
            {label: 'Max Price', field: 'highestPrice', type: 'number', placeholder: 'Max Price'}
        ]
    },
    motherboard: {
        apiPath: '/api/hardware/motherboard/filtered',
        addApiPath: '/api/hardware/motherboard',
        photo: motherboardPhoto,
        additionalFields: {socket: ''} as Partial<Motherboard>,
        filterFields: [
            {label: 'Socket', field: 'socket', type: 'text', placeholder: 'Socket'},
            {label: 'Min Price', field: 'lowestPrice', type: 'number', placeholder: 'Min Price'},
            {label: 'Max Price', field: 'highestPrice', type: 'number', placeholder: 'Max Price'}
        ]
    },
    'pc-case': {
        apiPath: '/api/hardware/pc-case/filtered',
        addApiPath: '/api/hardware/pc-case',
        photo: pcCasePhoto,
        additionalFields: {dimensions: ''} as Partial<PcCase>,
        filterFields: [
            {label: 'Dimensions', field: 'dimensions', type: 'text', placeholder: 'Dimensions'},
            {label: 'Min Price', field: 'lowestPrice', type: 'number', placeholder: 'Min Price'},
            {label: 'Max Price', field: 'highestPrice', type: 'number', placeholder: 'Max Price'}
        ]
    },
    psu: {
        apiPath: '/api/hardware/psu/filtered',
        addApiPath: '/api/hardware/psu',
        photo: psuPhoto,
        additionalFields: {power: 0} as Partial<PSU>,
        filterFields: [
            {label: 'Power', field: 'power', type: 'number', placeholder: 'Power'},
            {label: 'Min Price', field: 'lowestPrice', type: 'number', placeholder: 'Min Price'},
            {label: 'Max Price', field: 'highestPrice', type: 'number', placeholder: 'Max Price'}
        ]
    },
    ram: {
        apiPath: '/api/hardware/ram/filtered',
        addApiPath: '/api/hardware/ram',
        photo: ramPhoto,
        additionalFields: {type: '', memorySize: 0} as Partial<RAM>,
        filterFields: [
            {label: 'Type', field: 'type', type: 'text', placeholder: 'Type'},
            {label: 'Memory Size', field: 'memorySize', type: 'number', placeholder: 'Memory Size'},
            {label: 'Min Price', field: 'lowestPrice', type: 'number', placeholder: 'Min Price'},
            {label: 'Max Price', field: 'highestPrice', type: 'number', placeholder: 'Max Price'}
        ]
    },
    ssd: {
        apiPath: '/api/hardware/ssd/filtered',
        addApiPath: '/api/hardware/ssd',
        photo: ssdPhoto,
        additionalFields: {capacity: 0} as Partial<SSD>,
        filterFields: [
            {label: 'Capacity', field: 'capacity', type: 'number', placeholder: 'Capacity'},
            {label: 'Min Price', field: 'lowestPrice', type: 'number', placeholder: 'Min Price'},
            {label: 'Max Price', field: 'highestPrice', type: 'number', placeholder: 'Max Price'}
        ]
    }
};

interface HardwarePageProps {
    type: HardwareType;
}

interface Filter {
    [key: string]: string | number;
}

function HardwarePage<T extends HardwareItem>({type}: Readonly<HardwarePageProps>) {
    const [items, setItems] = useState<T[]>([]);
    const [currentPage, setCurrentPage] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);
    const itemsPerPage = 8;
    const [modalOpen, toggleModal] = useModal();
    const [hardwareSpec, setHardwareSpec] = useState<HardwareSpec>({name: "", description: "", price: "", rating: 0});
    const [additionalFields, setAdditionalFields] = useState<Partial<T>>(hardwareConfig[type].additionalFields as Partial<T>);
    const [sortType, setSortType] = useState<string>("desc");
    const [filter, setFilter] = useState<Filter>({});
    const navigate = useNavigate();
    const {user, isSuperUser} = useAuth();
    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();

    useEffect(() => {
        const fetchItems = () => {
            axios.get(`${hardwareConfig[type].apiPath}?page=${currentPage}&size=${itemsPerPage}`, {
                params: {
                    ...filter, sortType
                }
            })
                .then(response => {
                    setItems(response.data.content);
                    setTotalPages(response.data.totalPages);
                })
                .catch(error => {
                    console.error(`Failed to fetch ${type}s:`, error);
                });
        };

        fetchItems();
    }, [currentPage, itemsPerPage, type, sortType, filter]);

    function paginate(pageNumber: number) {
        setCurrentPage(pageNumber);
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

    const handleSort = (sortBy: string) => {
        setSortType(sortBy);
        setCurrentPage(0);
    };

    const handleFilter = (field: string, value: string | number) => {
        setFilter(prevFilter => ({...prevFilter, [field]: value}));
        setCurrentPage(0);
    };

    return (
        <div className="hardware-page">
            <div className="filter-section">
                <h3 className={"filter-section__header"}>Filter By</h3>
                {hardwareConfig[type].filterFields.map(field => (
                    <div key={field.field} className="filter-input">
                        <label>{field.label}</label>
                        <input
                            type={field.type}
                            placeholder={field.placeholder}
                            onChange={(e) => handleFilter(field.field, field.type === 'number' ? Number(e.target.value) : e.target.value)}
                        />
                    </div>
                ))}
                <div className="price-slider">
                    <label htmlFor="price-range-slider">Price Range</label>
                    <div id="price-range-slider">
                        <Range
                            step={50}
                            min={0}
                            max={5000}
                            values={[filter.lowestPrice as number || 0, filter.highestPrice as number || 5000]}
                            onChange={(values: number[]) => {
                                handleFilter("lowestPrice", values[0]);
                                handleFilter("highestPrice", values[1]);
                            }}
                            renderTrack={({props, children}: { props: ITrackProps; children: React.ReactNode }) => (
                                <div
                                    {...props}
                                    style={{
                                        ...props.style,
                                        height: '6px',
                                        width: '100%',
                                        background: getTrackBackground({
                                            values: [filter.lowestPrice as number || 0, filter.highestPrice as number || 5000],
                                            colors: ['#ccc', '#d0ff00', '#ccc'],
                                            min: 0,
                                            max: 5000
                                        })
                                    }}
                                >
                                    {children}
                                </div>
                            )}
                            renderThumb={({props}: { props: IThumbProps }) => (
                                <div
                                    {...props}
                                    style={{
                                        ...props.style,
                                        height: '25px',
                                        width: '25px',
                                        backgroundColor: '#d0ff00',
                                        borderRadius: '50%'
                                    }}
                                />
                            )}
                        />
                    </div>
                    <div className="price-values">
                        <span>{filter.lowestPrice || 0}</span>
                        <span>{filter.highestPrice || 5000}</span>
                    </div>
                </div>
                <h3>Sort By</h3>
                <div className="sort-buttons">
                    <button onClick={() => handleSort("price-asc")}>Price Ascending</button>
                    <button onClick={() => handleSort("price-desc")}>Price Descending</button>
                    <button onClick={() => handleSort("rating-asc")}>Rating Ascending</button>
                    <button onClick={() => handleSort("rating-desc")}>Rating Descending</button>
                </div>
                <button className="add-item-button" onClick={toggleModal}>Add {type}</button>
            </div>
            <div className="content-section">
                <div className="product-list">
                    {items.map(item => (
                        <ProductBox
                            key={item.id}
                            product={item}
                            imgSrc={item.photos && item.photos.length > 0 ? item.photos[0] : hardwareConfig[type].photo}
                            toCharacteristicsPage={() => navigate(`/hardware/${type}/${item.id}`)}
                            onAddToBasket={() => handleAddToBasket(item)}
                        />
                    ))}
                </div>
                <div className="product-list__pagination">
                    {Array.from({length: totalPages}, (_, i) => (
                        <button
                            key={i}
                            onClick={() => paginate(i)}
                            className={`pagination__button ${currentPage === i ? 'pagination__button--active' : ''}`}
                        >
                            {i + 1}
                        </button>
                    ))}
                </div>
                <LoginModal isOpen={isLoginModalOpen} onLogin={handleLogin} onClose={hideLoginModal}/>
            </div>
        </div>
    );
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