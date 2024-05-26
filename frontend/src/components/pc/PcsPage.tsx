import {useCallback, useEffect, useState} from 'react';
import axios from 'axios';
import {useNavigate} from "react-router-dom";
import pcPhoto from "../../assets/pc/Pc.png";
import {useAuth} from "../../contexts/AuthContext";
import {Modal} from "react-bootstrap";
import ProductBox from "../hardware/utils/ProductBox";
import useLoginModal from "../login/useLoginModal.ts";
import LoginModal from "../login/LoginModal.tsx";
import {useModal} from "../hardware/utils/Modal";
import {HardwareSpec} from "../../model/pc/hardware/HardwareSpec.tsx";
import {SpecsIds} from "../../model/pc/SpecsIds.tsx";
import {PC} from "../../model/pc/PC.tsx";

export default function PcsPage() {
    const [pcs, setPcs] = useState<PC[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pcsPerPage = 8;
    const [modalOpen, toggleModal] = useModal();
    const [hardwareSpec, setHardwareSpec] = useState<HardwareSpec>({name: '', description: '', price: '', rating: 0});
    const [createSpecs, setCreateSpecs] = useState<SpecsIds>({
        cpuId: '', gpuId: '', motherboardId: '', ramId: '', ssdId: '', hddId: '', powerSupplyId: '', pcCaseId: ''
    });
    const navigate = useNavigate();
    const {user, isSuperUser} = useAuth();
    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();

    useEffect(() => {
        const fetchpcs = async () => {
            try {
                const response = await axios.get(`/api/pc/page`, {
                    params: {page: currentPage, size: pcsPerPage}
                });
                setPcs(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Failed to fetch pcs:", error);
            }
        };
        fetchpcs();
    }, [currentPage, pcsPerPage]);

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
            }
        };
        axios.post('/api/pc', payload)
            .then(response => {
                setPcs(prevpcs => [...prevpcs, response.data]);
                toggleModal();
            })
            .catch(error => console.error('Failed to add PC:', error));
    }, [hardwareSpec, createSpecs, toggleModal]);

    const handleAddToBasket = (pc: PC) => {
        if (!user) {
            showLoginModal();
            return;
        }
        const payload = {
            id: pc.id,
            name: pc.hardwareSpec.name,
            description: pc.hardwareSpec.description,
            price: pc.hardwareSpec.price,
            photo: pc?.photos && pc.photos.length > 0 ? pc.photos[pc.photos.length - 1] : pcPhoto,
            pathToCharacteristicsPage: "/pc"
        };

        axios.post<void>(`/api/basket/${user?.id}`, payload)
            .then(() => {
                navigate("../basket/");
            })
            .catch(console.error);
    };

    return (
        <>
            <h1 className="body--product-page">pcs List</h1>
            {isSuperUser() && <button className="default-button" onClick={toggleModal}>Add PC</button>}

            <Modal show={modalOpen} onHide={toggleModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Add PC</Modal.Title>
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
                </Modal.Body>
                <Modal.Footer>
                    <button onClick={toggleModal}>Close</button>
                    <button onClick={saveValues}>Save Changes</button>
                </Modal.Footer>
            </Modal>

            <div className="product-list">
                {pcs.map(pc => (
                    <ProductBox
                        key={pc.id}
                        product={pc}
                        imgSrc={pc.photos && pc.photos.length > 0 ? pc.photos[0] : pcPhoto}
                        toCharacteristicsPage={() => navigate(`/pc/${pc.id}`, {state: {isUserPc: false}})}
                        onAddToBasket={() => handleAddToBasket(pc)}
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
