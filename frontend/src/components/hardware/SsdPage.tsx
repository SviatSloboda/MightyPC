import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './utils/ProductBox.tsx';
import Modal, {useModal} from './utils/Modal.tsx';
import {useNavigate} from "react-router-dom";
import {SSD} from "../../model/hardware/SSD.tsx";
import {useAuth} from "../../contexts/AuthContext.tsx";
import useLoginModal from "../login/useLoginModal.ts";
import LoginModal from "../login/LoginModal.tsx";
import ssdPhoto from "../../assets/hardware/ssd.png";

export default function SsdPage() {
    const [ssds, setSsds] = useState<SSD[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const ssdsPerPage = 8;
    const [modalOpen, toggleModal] = useModal();
    const {user} = useAuth();
    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();
    const navigate = useNavigate();

    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [price, setPrice] = useState("");
    const [rating, setRating] = useState(0);
    const [capacity, setCapacity] = useState(0);
    const [energyConsumption, setEnergyConsumption] = useState(0);

    useEffect(() => {
        async function fetchSsds() {
            try {
                const response = await axios.get(`/api/hardware/ssd/page?page=${currentPage}&size=${ssdsPerPage}`);
                setSsds(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Failed to fetch SSDs:", error);
            }
        }

        fetchSsds();
    }, [currentPage, ssdsPerPage]);

    function paginate(pageNumber: number) {
        setCurrentPage(pageNumber - 1);
    }

    const saveValues = () => {
        const payload = {
            hardwareSpec: {
                name, description, price, rating,
            }, capacity, energyConsumption,
        };

        axios.post('/api/hardware/ssd', payload)
            .then(response => {
                setSsds([...ssds, response.data]);
                toggleModal();
            })
            .catch(error => console.error('Failed to add SSD:', error));
    };

    const handleAddToBasket = (ssd: SSD) => {
        if (!user) {
            showLoginModal();
            return;
        }
        const payload = {
            id: ssd.id,
            name: ssd.hardwareSpec.name,
            description: ssd.hardwareSpec.description,
            price: ssd.hardwareSpec.price,
            photo: ssd.ssdPhotos && ssd.ssdPhotos.length > 0 ? ssd.ssdPhotos[ssd.ssdPhotos.length - 1] : ssdPhoto,
            pathToCharacteristicsPage: "/hardware/ssd"
        };

        axios.post(`/api/basket/${user.id}`, payload)
            .then(() => {
                navigate("../basket/");
            })
            .catch(console.error);
    };

    return (<>
        <h1 className="body--product-page">SSD List</h1>
        <button className="default-button" onClick={toggleModal}>Add SSD</button>

        <Modal isOpen={modalOpen} onClose={toggleModal} onSave={saveValues}>
            <div className="modal__form-group">
                <label htmlFor="modal-name" className="modal__form-label">Name:</label>
                <input id="modal-name" className="modal__input" value={name}
                       onChange={(e) => setName(e.target.value)} required/>
            </div>
            <div className="modal__form-group">
                <label htmlFor="modal-description" className="modal__form-label">Description:</label>
                <input id="modal-description" className="modal__input" value={description}
                       onChange={(e) => setDescription(e.target.value)}
                       required/>
            </div>
            <div className="modal__form-group">
                <label htmlFor="modal-price" className="modal__form-label">Price:</label>
                <input id="modal-price" className="modal__input" value={price}
                       onChange={(e) => setPrice(e.target.value)} required/>
            </div>
            <div className="modal__form-group">
                <label htmlFor="modal-rating" className="modal__form-label">Rating:</label>
                <input id="modal-rating" className="modal__input" type="number" value={rating}
                       onChange={(e) => setRating(parseFloat(e.target.value))} min="0" max="5" step="0.1" required/>
            </div>
            <div className="modal__form-group">
                <label htmlFor="modal-capacity" className="modal__form-label">Capacity:</label>
                <input id="modal-capacity" className="modal__input" type="number" value={capacity}
                       onChange={(e) => setCapacity(parseInt(e.target.value, 10))} min="0" required/>
            </div>
            <div className="modal__form-group">
                <label htmlFor="modal-energyConsumption" className="modal__form-label">Energy Consumption:</label>
                <input id="modal-energyConsumption" className="modal__input" type="number" value={energyConsumption}
                       onChange={(e) => setEnergyConsumption(parseInt(e.target.value, 10))} min="0" required/>
            </div>
        </Modal>

        <div className="product-list">
            {ssds.map(ssd => (<ProductBox
                key={ssd.id}
                product={ssd}
                imgSrc={ssd.ssdPhotos && ssd.ssdPhotos.length > 0 ? ssd.ssdPhotos[0] : ssdPhoto}
                toCharacteristicsPage={() => navigate(`/hardware/ssd/${ssd.id}`)}
                onAddToBasket={() => handleAddToBasket(ssd)}
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
