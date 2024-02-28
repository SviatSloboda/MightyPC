import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './utils/ProductBox.tsx';
import Modal, {useModal} from './utils/Modal.tsx';
import {useNavigate} from "react-router-dom";
import gpuPhoto from "../../assets/gpu.png";
import {GPU} from "../../model/pc/hardware/GPU.tsx";
import {useAuth} from "../../contexts/AuthContext.tsx";
import useLoginModal from "./utils/useLoginModal";
import LoginModal from "./utils/LoginModal";
import {login} from "../../contexts/authUtils.ts";

export default function GpuPage() {
    const [GPUs, setGPUs] = useState<GPU[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const gpusPerPage = 8;
    const [modalOpen, toggleModal] = useModal();
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [price, setPrice] = useState("");
    const [rating, setRating] = useState(0);
    const [performance, setPerformance] = useState(0);
    const [energyConsumption, setEnergyConsumption] = useState(0);
    const navigate = useNavigate();
    const {user, isSuperUser} = useAuth();
    const {isLoginModalOpen, showLoginModal, hideLoginModal} = useLoginModal();

    useEffect(() => {
        async function fetchGpus() {
            try {
                const response = await axios.get(`/api/hardware/gpu/page?page=${currentPage}&size=${gpusPerPage}`);
                setGPUs(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Failed to fetch GPUs:", error);
            }
        }

        fetchGpus();
    }, [currentPage, gpusPerPage]);

    function paginate(pageNumber: number) {
        setCurrentPage(pageNumber - 1);
    }

    function saveValues() {
        const payload = {
            hardwareSpec: {
                name, description, price, rating,
            }, performance, energyConsumption,
        };
        axios.post('/api/hardware/gpu', payload)
            .then(response => {
                console.log('GPU added:', response.data);
                setGPUs(prevGPUs => [...prevGPUs, response.data]);
                toggleModal();
            })
            .catch(error => console.error('Failed to add GPU:', error));
    }

    const handleAddToBasket = (gpu: GPU) => {
        if (!user) {
            showLoginModal();
            return;
        }
        const payload = {
            id: gpu.id,
            type: "gpu",
            name: gpu.hardwareSpec.name,
            description: gpu.hardwareSpec.description,
            price: gpu.hardwareSpec.price,
            photos: gpu.gpuPhotos && gpu.gpuPhotos.length > 0 ? gpu.gpuPhotos : [gpuPhoto]
        };

        axios.post<void>(`/api/basket/${user.id}`, payload)
            .then(() => {
                navigate("../basket/");
            })
            .catch(console.error);
    };

    return (<>
        <h1 className="body--product-page">GPU List</h1>
        {isSuperUser() && <button className="default-button" onClick={toggleModal}>Add GPU</button>}

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
                <label htmlFor="modal-performance" className="modal__form-label">Performance:</label>
                <input id="modal-performance" className="modal__input" type="number" value={performance}
                       onChange={(e) => setPerformance(parseInt(e.target.value, 10))} min="0" required/>
            </div>
            <div className="modal__form-group">
                <label htmlFor="modal-energyConsumption" className="modal__form-label">Energy Consumption:</label>
                <input id="modal-energyConsumption" className="modal__input" type="number" value={energyConsumption}
                       onChange={(e) => setEnergyConsumption(parseInt(e.target.value, 10))} min="0" required/>
            </div>
        </Modal>

        <div className="product-list">
            {GPUs.map(gpu => (<ProductBox
                key={gpu.id}
                product={gpu}
                imgSrc={gpu.gpuPhotos && gpu.gpuPhotos.length > 0 ? gpu.gpuPhotos[0] : gpuPhoto}
                toCharacteristicsPage={() => navigate(`/hardware/gpu/${gpu.id}`)}
                onAddToBasket={() => handleAddToBasket(gpu)}
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

        <LoginModal isOpen={isLoginModalOpen} onLogin={login} onClose={() => hideLoginModal()}/>
    </>);
}
