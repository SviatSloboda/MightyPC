import {useCallback, useEffect, useState} from 'react';
import axios from 'axios';
import {useNavigate} from "react-router-dom";
import workstationPhoto from "../../assets/pc/Workstations.png";
import {useAuth} from "../../contexts/AuthContext";
import {Modal} from "react-bootstrap";
import ProductBox from "../hardware/utils/ProductBox";
import useLoginModal from "../hardware/utils/useLoginModal";
import LoginModal from "../hardware/utils/LoginModal";
import {useModal} from "../hardware/utils/Modal";
import {Workstation} from "../../model/pc/Workstation.tsx";
import {HardwareSpec} from "../../model/pc/hardware/HardwareSpec.tsx";
import {SpecsIds} from "../../model/pc/SpecsIds.tsx";

export default function WorkstationsPage() {
    const [workstations, setWorkstations] = useState<Workstation[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const workstationsPerPage = 8;
    const [modalOpen, toggleModal] = useModal();
    const [hardwareSpec, setHardwareSpec] = useState<HardwareSpec>({name: '', description: '', price: '', rating: 0});
    const [createSpecs, setCreateSpecs] = useState<SpecsIds>({
        cpuId: '', gpuId: '', motherboardId: '', ramId: '', ssdId: '', hddId: '', powerSupplyId: '', pcCaseId: ''
    });
    const [cpuNumber, setCpuNumber] = useState<number>(1);
    const [gpuNumber, setGpuNumber] = useState<number>(1);
    const navigate = useNavigate();
    const {user, isSuperUser} = useAuth();
    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();

    useEffect(() => {
        const fetchWorkstations = async () => {
            try {
                const response = await axios.get(`/api/workstation/page`, {
                    params: {page: currentPage, size: workstationsPerPage}
                });
                setWorkstations(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Failed to fetch Workstations:", error);
            }
        };
        fetchWorkstations();
    }, [currentPage, workstationsPerPage]);

    const paginate = useCallback((pageNumber: number) => {
        setCurrentPage(pageNumber - 1);
    }, []);

    const saveValues = useCallback(() => {
        const payload = {
            hardwareSpec: {
                name: hardwareSpec.name,
                description: hardwareSpec.description,
                price: hardwareSpec.price,
                rating: hardwareSpec.rating
            },
            specsIds: {
                cpuId: createSpecs.cpuId,
                gpuId: createSpecs.gpuId,
                motherboardId: createSpecs.motherboardId,
                ramId: createSpecs.ramId,
                ssdId: createSpecs.ssdId,
                hddId: createSpecs.hddId,
                powerSupplyId: createSpecs.powerSupplyId,
                pcCaseId: createSpecs.pcCaseId
            },
            cpuNumber: cpuNumber,
            gpuNumber: gpuNumber
        };
        axios.post('/api/workstation', payload)
            .then(response => {
                setWorkstations(prevWorkstations => [...prevWorkstations, response.data]);
                toggleModal();  // Moved toggleModal to be inside the 'then' block
            })
            .catch(error => console.error('Failed to add Workstation:', error));
    }, [hardwareSpec, createSpecs, cpuNumber, gpuNumber, toggleModal]);

    const handleAddToBasket = (workstation: Workstation) => {
        if (!user) {
            showLoginModal();
            return;
        }
        const payload = {
            id: workstation.id,
            name: workstation.hardwareSpec.name,
            description: workstation.hardwareSpec.description,
            price: workstation.hardwareSpec.price,
            photo: workstation?.photos && workstation.photos.length > 0 ? workstation.photos[workstation.photos.length - 1] : workstationPhoto,
            pathToCharacteristicsPage: "/workstation"
        };

        axios.post<void>(`/api/basket/${user?.id}`, payload)
            .then(() => {
                navigate("../basket/");
            })
            .catch(console.error);
    };

    return (
        <>
            <h1 className="body--product-page">Workstations List</h1>
            {isSuperUser() && <button className="default-button" onClick={toggleModal}>Add Workstation</button>}

            <Modal show={modalOpen} onHide={toggleModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Add Workstation</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <div className="modal__form-group">
                        <label htmlFor="modal-name">Name:</label>
                        <input
                            id="modal-name"
                            className="modal__input"
                            type="text"
                            value={hardwareSpec.name}
                            onChange={(e) => setHardwareSpec({...hardwareSpec, name: e.target.value})}
                            required
                        />
                    </div>
                    <div className="modal__form-group">
                        <label htmlFor="modal-description">Description:</label>
                        <input
                            id="modal-description"
                            className="modal__input"
                            type="text"
                            value={hardwareSpec.description}
                            onChange={(e) => setHardwareSpec({...hardwareSpec, description: e.target.value})}
                            required
                        />
                    </div>
                    <div className="modal__form-group">
                        <label htmlFor="modal-price">Price:</label>
                        <input
                            id="modal-price"
                            className="modal__input"
                            type="text"
                            value={hardwareSpec.price}
                            onChange={(e) => setHardwareSpec({...hardwareSpec, price: e.target.value})}
                            required
                        />
                    </div>
                    <div className="modal__form-group">
                        <label htmlFor="modal-rating">Rating:</label>
                        <input
                            id="modal-rating"
                            className="modal__input"
                            type="number"
                            value={hardwareSpec.rating}
                            onChange={(e) => setHardwareSpec({...hardwareSpec, rating: parseInt(e.target.value)})}
                            required
                        />
                    </div>
                    {Object.entries(createSpecs).map(([key, value]) => (
                        <div className="modal__form-group" key={key}>
                            <label
                                htmlFor={`modal-${key}`}>{key.charAt(0).toUpperCase() + key.slice(1).replace('Id', '')}:</label>
                            <input
                                id={`modal-${key}`}
                                className="modal__input"
                                type="text"
                                value={value}
                                onChange={(e) => setCreateSpecs({...createSpecs, [key]: e.target.value})}
                                required
                            />
                        </div>
                    ))}
                    <div className="modal__form-group">
                        <label htmlFor="modal-rating">Cpu number: </label>
                        <input
                            id="modal-rating"
                            className="modal__input"
                            type="number"
                            value={cpuNumber}
                            onChange={(e) => setCpuNumber(parseInt(e.target.value))}
                            required
                        />
                    </div>
                    <div className="modal__form-group">
                        <label htmlFor="modal-rating">Gpu number: </label>
                        <input
                            id="modal-rating"
                            className="modal__input"
                            type="number"
                            value={gpuNumber}
                            onChange={(e) => setGpuNumber(parseInt(e.target.value))}
                            required
                        />
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <button onClick={toggleModal}>Close</button>
                    <button onClick={saveValues}>Save Changes</button>
                </Modal.Footer>
            </Modal>

            <div className="product-list">
                {workstations.map(workstation => (
                    <ProductBox
                        key={workstation.id}
                        product={workstation}
                        imgSrc={workstation.photos && workstation.photos.length > 0 ? workstation.photos[0] : workstationPhoto}
                        toCharacteristicsPage={() => navigate(`/workstation/${workstation.id}`)}
                        onAddToBasket={() => handleAddToBasket(workstation)}
                    />
                ))}
            </div>

            <div className="product-list__pagination">
                {Array.from({length: totalPages}, (_, i) => (
                    <button
                        key={i}
                        onClick={() => paginate(i + 1)}
                        className={`pagination__button ${currentPage === i ? 'pagination__button--active' : ''}`}
                    >
                        {i + 1}
                    </button>
                ))}
            </div>

            <LoginModal isOpen={isLoginModalOpen} onLogin={handleLogin} onClose={hideLoginModal}/>
        </>
    );
}
