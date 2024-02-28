import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './utils/ProductBox.tsx';
import Modal, {useModal} from './utils/Modal.tsx';
import {useNavigate} from "react-router-dom";
import hddPhoto from "../../assets/hdd.png";
import {HDD} from "../../model/pc/hardware/HDD.tsx";
import {useAuth} from "../../contexts/AuthContext.tsx";
import useLoginModal from "./utils/useLoginModal";
import LoginModal from "./utils/LoginModal";

export default function HddPage() {
    const [HDDs, setHDDs] = useState<HDD[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const hddsPerPage = 8;
    const [modalOpen, toggleModal] = useModal();
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [price, setPrice] = useState("");
    const [rating, setRating] = useState(0);
    const [capacity, setCapacity] = useState('');
    const [energyConsumption, setEnergyConsumption] = useState('');
    const navigate = useNavigate();
    const {user, isSuperUser} = useAuth();
    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();

    useEffect(() => {
        async function fetchHdds() {
            try {
                const response = await axios.get(`/api/hardware/hdd/page?page=${currentPage}&size=${hddsPerPage}`);
                setHDDs(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Failed to fetch HDDs:", error);
            }
        }

        fetchHdds();
    }, [currentPage, hddsPerPage]);

    function paginate(pageNumber: number) {
        setCurrentPage(pageNumber - 1);
    }

    const saveValues = () => {
        const payload = {
            hardwareSpec: {
                name, description, price, rating,
            }, capacity, energyConsumption,
        };

        axios.post('/api/hardware/hdd', payload)
            .then(response => {
                console.log('HDD added:', response.data);
                setHDDs(prevHDDs => [...prevHDDs, response.data]);
                toggleModal();
            })
            .catch(error => console.error('Failed to add HDD:', error));
    };

    const handleAddToBasket = (hdd: HDD) => {
        if (!user) {
            showLoginModal();
            return;
        }
        const payload = {
            id: hdd.id,
            type: "hdd",
            name: hdd.hardwareSpec.name,
            description: hdd.hardwareSpec.description,
            price: hdd.hardwareSpec.price,
            photos: hdd.hddPhotos && hdd.hddPhotos.length > 0 ? hdd.hddPhotos : [hddPhoto]
        };

        axios.post<void>(`/api/basket/${user.id}`, payload)
            .then(() => {
                navigate("../basket/");
            })
            .catch(console.error);
    };

    return (<>
        <h1 className="body--product-page">HDD List</h1>
        {isSuperUser() && <button className="default-button" onClick={toggleModal}>Add HDD</button>}

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
                <label htmlFor="capacity" className="modal__form-label">Capacity:</label>
                <input id="capacity" className="modal__input" type="text" value={capacity}
                       onChange={(e) => setCapacity(e.target.value)} required/>
            </div>
            <div className="modal__form-group">
                <label htmlFor="energyConsumption" className="modal__form-label">Energy Consumption:</label>
                <input id="energyConsumption" className="modal__input" type="text" value={energyConsumption}
                       onChange={(e) => setEnergyConsumption(e.target.value)} required/>
            </div>
        </Modal>

        <div className="product-list">
            {HDDs.map(hdd => (<ProductBox
                key={hdd.id}
                product={hdd}
                imgSrc={hdd.hddPhotos && hdd.hddPhotos.length > 0 ? hdd.hddPhotos[0] : hddPhoto}
                toCharacteristicsPage={() => navigate(`/hardware/hdd/${hdd.id}`)}
                onAddToBasket={() => handleAddToBasket(hdd)}
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
