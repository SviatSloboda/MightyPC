import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import Modal, {useModal} from './Modal';
import {useNavigate} from "react-router-dom";
import hddPhoto from "../../assets/hdd.png";
import {HDD} from "../../model/hardware/HDD.tsx";

export default function HddPage() {
    const [HDDs, setHDDs] = useState<HDD[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const hddsPerPage = 8; // This value should match your backend Pageable size
    const [modalOpen, toggleModal] = useModal();
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [price, setPrice] = useState("");
    const [rating, setRating] = useState(0);
    const [capacity, setCapacity] = useState(0);
    const [energyConsumption, setEnergyConsumption] = useState(0);
    const navigate = useNavigate();

    useEffect(() => {
        async function fetchGpus() {
            try {
                const response = await axios.get(`/api/hardware/hdd/page?page=${currentPage}&size=${hddsPerPage}`);
                setHDDs(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Failed to fetch HDDs:", error);
            }
        }

        fetchGpus();
    }, [currentPage, hddsPerPage]);

    function paginate(pageNumber: number) {
        setCurrentPage(pageNumber - 1);
    }

    const saveValues = () => {
        const payload = {
            hardwareSpec: {
                name,
                description,
                price,
                rating,
            },
            capacity,
            energyConsumption,
        };

        axios.post('/api/hardware/hdd', payload)
            .then(response => {
                console.log('HDD added:', response.data);
                setHDDs(prevHDDs => [...prevHDDs, response.data]);
                toggleModal();
            })
            .catch(error => console.error('Failed to add HDD:', error));
    };

    return (
        <>
            <h1 className="body--product-page">HDD List</h1>
            <button className="default-button" onClick={toggleModal}>Add HDD</button>

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
                    <input id="capacity" className="modal__input" type="number" value={capacity}
                           onChange={(e) => setCapacity(parseInt(e.target.value, 10))} min="0" required/>
                </div>
                <div className="modal__form-group">
                    <label htmlFor="energyConsumption" className="modal__form-label">Energy Consumption:</label>
                    <input id="energyConsumption" className="modal__input" type="number" value={energyConsumption}
                           onChange={(e) => setEnergyConsumption(parseInt(e.target.value, 10))} min="0" required/>
                </div>
            </Modal>

            <div className="product-list">
                {HDDs.map(hdd => (
                    <ProductBox
                        key={hdd.id}
                        product={hdd}
                        imgSrc={hdd.hddPhotos && hdd.hddPhotos.length > 0 ? hdd.hddPhotos[0] : hddPhoto}
                        toCharacteristicsPage={() => navigate(`/hardware/hdd/${hdd.id}`)}
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
        </>
    );
}