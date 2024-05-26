import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './utils/ProductBox.tsx';
import Modal, {useModal} from './utils/Modal.tsx';
import {useNavigate} from "react-router-dom";
import {Motherboard} from "../../model/pc/hardware/Motherboard.tsx";
import {useAuth} from "../../contexts/AuthContext.tsx";
import useLoginModal from "../login/useLoginModal.ts";
import LoginModal from "../login/LoginModal.tsx";
import motherboardPhoto from "../../assets/hardware/motherboard.png";

export default function MotherboardPage() {
    const [motherboards, setMotherboards] = useState<Motherboard[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const motherboardsPerPage = 8;
    const [modalOpen, toggleModal] = useModal();
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [price, setPrice] = useState("");
    const [rating, setRating] = useState(0);
    const [energyConsumption, setEnergyConsumption] = useState('');
    const [socket, setSocket] = useState('');
    const navigate = useNavigate();
    const {user, isSuperUser} = useAuth();
    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();

    useEffect(() => {
        async function fetchmotherboards() {
            try {
                const response = await axios.get(`/api/hardware/motherboard/page?page=${currentPage}&size=${motherboardsPerPage}`);
                setMotherboards(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Failed to fetch motherboards:", error);
            }
        }

        fetchmotherboards();
    }, [currentPage, motherboardsPerPage]);

    function paginate(pageNumber: number) {
        setCurrentPage(pageNumber - 1);
    }

    const saveValues = () => {
        const payload = {
            hardwareSpec: {
                name, description, price, rating,
            }, energyConsumption, socket,
        };

        axios.post('/api/hardware/motherboard', payload)
            .then(response => {
                console.log('Motherboard added:', response.data);
                setMotherboards(prevmotherboards => [...prevmotherboards, response.data]);
                toggleModal();
            })
            .catch(error => console.error('Failed to add Motherboard:', error));
    };

    const handleAddToBasket = (motherboard: Motherboard) => {
        if (!user) {
            showLoginModal();
            return;
        }
        const payload = {
            id: motherboard.id,
            name: motherboard.hardwareSpec.name,
            description: motherboard.hardwareSpec.description,
            price: motherboard.hardwareSpec.price,
            photo: motherboard.motherboardPhotos && motherboard.motherboardPhotos.length > 0 ? motherboard.motherboardPhotos[motherboard.motherboardPhotos.length - 1] : motherboardPhoto,
            pathToCharacteristicsPage: "/hardware/motherboard"
        };

        axios.post<void>(`/api/basket/${user.id}`, payload)
            .then(() => {
                navigate("../basket/");
            })
            .catch(console.error);
    };

    return (<>
        <h1 className="body--product-page">Motherboard List</h1>
        {isSuperUser() && <button className="default-button" onClick={toggleModal}>Add Motherboard</button>}

        <Modal isOpen={modalOpen} onClose={toggleModal} onSave={saveValues}>
            <div className="modal__form-group">
                <label htmlFor="name" className="modal__form-label">Name:</label>
                <input id="name" className="modal__input" value={name} onChange={(e) => setName(e.target.value)}
                       required/>
            </div>
            <div className="modal__form-group">
                <label htmlFor="description" className="modal__form-label">Description:</label>
                <input id="description" className="modal__input" value={description}
                       onChange={(e) => setDescription(e.target.value)}
                       required/>
            </div>
            <div className="modal__form-group">
                <label htmlFor="price" className="modal__form-label">Price:</label>
                <input id="price" className="modal__input" value={price} onChange={(e) => setPrice(e.target.value)}
                       required/>
            </div>
            <div className="modal__form-group">
                <label htmlFor="rating" className="modal__form-label">Rating:</label>
                <input id="rating" className="modal__input" type="number" value={rating}
                       onChange={(e) => setRating(parseFloat(e.target.value))} min="0" max="5" step="0.1" required/>
            </div>

            <div className="modal__form-group">
                <label htmlFor="description" className="modal__form-label">Socket:</label>
                <input id="description" className="modal__input" value={socket}
                       onChange={(e) => setSocket(e.target.value)}
                       required/>
            </div>

            <div className="modal__form-group">
                <label htmlFor="energyConsumption" className="modal__form-label">Energy Consumption:</label>
                <input id="energyConsumption" className="modal__input" type="text" value={energyConsumption}
                       onChange={(e) => setEnergyConsumption(e.target.value)} required/>
            </div>
        </Modal>

        <div className="product-list">
            {motherboards.map(motherboard => (<ProductBox
                key={motherboard.id}
                product={motherboard}
                imgSrc={motherboard.motherboardPhotos && motherboard.motherboardPhotos.length > 0 ? motherboard.motherboardPhotos[0] : motherboardPhoto}
                toCharacteristicsPage={() => navigate(`/hardware/motherboard/${motherboard.id}`)}
                onAddToBasket={() => handleAddToBasket(motherboard)}
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
