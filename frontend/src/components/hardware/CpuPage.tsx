import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './utils/ProductBox.tsx';
import Modal, {useModal} from './utils/Modal.tsx';
import {useNavigate} from "react-router-dom";
import cpuPhoto from "../../assets/hardware/cpu.png";
import {CPU} from "../../model/pc/hardware/CPU.tsx";
import {useAuth} from "../../contexts/AuthContext.tsx";
import useLoginModal from "./utils/useLoginModal";
import LoginModal from "./utils/LoginModal";
import {login} from "../../contexts/authUtils.ts";

export default function CpuPage() {
    const [cpus, setCpus] = useState<CPU[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const cpusPerPage = 8;
    const [modalOpen, toggleModal] = useModal();
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [socket, setSocket] = useState("");
    const [price, setPrice] = useState("");
    const [rating, setRating] = useState(0);
    const [energyConsumption, setEnergyConsumption] = useState(0);
    const navigate = useNavigate();
    const {user, isSuperUser} = useAuth();
    const {isLoginModalOpen, showLoginModal, hideLoginModal} = useLoginModal();

    useEffect(() => {
        async function fetchCpus() {
            try {
                const response = await axios.get(`/api/hardware/cpu/page?page=${currentPage}&size=${cpusPerPage}`);
                setCpus(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Failed to fetch CPUs:", error);
            }
        }

        fetchCpus();
    }, [currentPage, cpusPerPage]);

    function paginate(pageNumber: number) {
        setCurrentPage(pageNumber - 1);
    }

    function saveValues() {
        const payload = {
            hardwareSpec: {
                name, description, price, rating,
            }, energyConsumption, socket
        };
        axios.post('/api/hardware/cpu', payload)
            .then(response => {
                console.log('CPU added:', response.data);
                setCpus(prevCPUs => [...prevCPUs, response.data]);
                toggleModal();
            })
            .catch(error => console.error('Failed to add CPU:', error));
    }

    const handleAddToBasket = (cpu: CPU) => {
        if (!user) {
            showLoginModal();
            return;
        }
        const payload = {
            id: cpu.id,
            name: cpu.hardwareSpec.name,
            description: cpu.hardwareSpec.description,
            price: cpu.hardwareSpec.price,
            photo: cpu.cpuPhotos && cpu.cpuPhotos.length > 0 ? cpu.cpuPhotos[cpu.cpuPhotos.length - 1] : cpuPhoto,
            pathToCharacteristicsPage: "/hardware/cpu"
        };

        axios.post<void>(`/api/basket/${user.id}`, payload)
            .then(() => {
                navigate("../basket/");
            })
            .catch(console.error);
    };

    return (<>
        <h1 className="body--product-page">CPU List</h1>
        {isSuperUser() && <button className="default-button" onClick={toggleModal}>Add CPU</button>}

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
                <label htmlFor="modal-energyConsumption" className="modal__form-label">Energy Consumption:</label>
                <input id="modal-energyConsumption" className="modal__input" type="number" value={energyConsumption}
                       onChange={(e) => setEnergyConsumption(parseInt(e.target.value, 10))} min="0" required/>
            </div>
            <div className="modal__form-group">
                <label htmlFor="modal-description" className="modal__form-label">Socket:</label>
                <input id="modal-description" className="modal__input" value={socket}
                       onChange={(e) => setSocket(e.target.value)}
                       required/>
            </div>
        </Modal>

        <div className="product-list">
            {cpus.map(cpu => (<ProductBox
                key={cpu.id}
                product={cpu}
                imgSrc={cpu.cpuPhotos && cpu.cpuPhotos.length > 0 ? cpu.cpuPhotos[0] : cpuPhoto}
                toCharacteristicsPage={() => navigate(`/hardware/cpu/${cpu.id}`)}
                onAddToBasket={() => handleAddToBasket(cpu)}
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
