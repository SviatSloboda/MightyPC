import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import Modal, {useModal} from './Modal';
import {useNavigate} from "react-router-dom";
import ramPhoto from "../../assets/ram.png";
import {RAM} from "../../model/pc/hardware/RAM.tsx";


export default function RamPage() {
    const [RAMs, setRAMs] = useState<RAM[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const ramsPerPage = 8; // This value should match your backend Pageable size
    const [modalOpen, toggleModal] = useModal();

    const [name, setName] = useState<string>("");
    const [description, setDescription] = useState<string>("");
    const [type, setType] = useState<string>("");
    const [price, setPrice] = useState("");
    const [rating, setRating] = useState(0);
    const [memorySize, setMemorySize] = useState(0);
    const [energyConsumption, setEnergyConsumption] = useState(0);
    const navigate = useNavigate();

    useEffect(() => {
        async function fetchRams() {
            try {
                const response = await axios.get(`/api/hardware/ram/page?page=${currentPage}&size=${ramsPerPage}`);
                setRAMs(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Failed to fetch RAMs:", error);
            }
        }

        fetchRams();
    }, [currentPage, ramsPerPage]);

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
            type,
            energyConsumption,
            memorySize
        };

        axios.post('/api/hardware/ram', payload)
            .then(response => {
                console.log('RAM added:', response.data);
                setRAMs(prevRAMs => [...prevRAMs, response.data]);
                toggleModal();
            })
            .catch(error => console.error('Failed to add RAM:', error));
    };

    return (
        <>
            <h1 className="body--product-page">RAM List</h1>
            <button className="default-button" onClick={toggleModal}>Add RAM</button>

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
                    <input id="modal-performance" className="modal__input" type="number" value={memorySize}
                           onChange={(e) => setMemorySize(parseInt(e.target.value, 10))} min="0" required/>
                </div>
                <div className="modal__form-group">
                    <label htmlFor="modal-energy-consumption" className="modal__form-label">Energy Consumption:</label>
                    <input id="modal-energy-consumption" className="modal__input" type="number"
                           value={energyConsumption}
                           onChange={(e) => setEnergyConsumption(parseInt(e.target.value, 10))} min="0" required/>
                </div>
                <div className="modal__form-group">
                    <label htmlFor="modal-ddr-type" className="modal__form-label">Type of DDR:</label>
                    <input id="modal-ddr-type" className="modal__input" type="number" value={type}
                           onChange={(e) => setType(e.target.value)} min="0" required/>
                </div>
            </Modal>


            <div className="product-list">
                {RAMs.map(ram => (
                    <ProductBox
                        key={ram.id}
                        product={ram}
                        imgSrc={ram.ramPhotos && ram.ramPhotos.length > 0 ? ram.ramPhotos[0] : ramPhoto}
                        toCharacteristicsPage={() => navigate(`/hardware/ram/${ram.id}`)}
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
