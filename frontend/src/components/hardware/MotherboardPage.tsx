import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import Modal, {useModal} from './Modal';
import {useNavigate} from "react-router-dom";
import motherboardPhoto from "../../assets/motherboard.png";
import {Motherboard} from "../../model/hardware/Motherboard.tsx";

export default function MotherboardPage() {
    const [Motherboards, setMotherboards] = useState<Motherboard[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const motherboardsPerPage = 8; // This value should match your backend Pageable size
    const [modalOpen, toggleModal] = useModal();

    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [price, setPrice] = useState("");
    const [rating, setRating] = useState(0);
    const [graphicCardCompatibility, setGraphicCardCompatibility] = useState<string[]>([]);
    const [processorCompatibility, setProcessorCompatibility] = useState<string[]>([]);

    const [energyConsumption, setEnergyConsumption] = useState(0);
    const navigate = useNavigate();

    useEffect(() => {
        async function fetchMotherboards() {
            try {
                const response = await axios.get(`/api/hardware/motherboard/page?page=${currentPage}&size=${motherboardsPerPage}`);
                setMotherboards(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Failed to fetch Motherboards:", error);
            }
        }

        fetchMotherboards();
    }, [currentPage, motherboardsPerPage]);

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
            energyConsumption,
            graphicCardCompatibility,
            processorCompatibility
        };

        axios.post('/api/hardware/motherboard', payload)
            .then(response => {
                console.log('Motherboard added:', response.data);
                setMotherboards(prevMotherboards => [...prevMotherboards, response.data]);
                toggleModal();
            })
            .catch(error => console.error('Failed to add Motherboard:', error));
    };

    return (
        <>
            <h1 className="body--product-page">Motherboard List</h1>
            <button className="default-button" onClick={toggleModal}>Add Motherboard</button>

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
                    <label htmlFor="graphicCardCompatibility" className="modal__form-label">Graphic Card
                        Compatibility:</label>
                    <input id="graphicCardCompatibility" className="modal__input"
                           value={graphicCardCompatibility.join(', ')}
                           onChange={(e) => setGraphicCardCompatibility(e.target.value.split(', '))} required/>
                </div>

                <div className="modal__form-group">
                    <label htmlFor="processorCompatibility" className="modal__form-label">Processor
                        Compatibility:</label>
                    <input id="processorCompatibility" className="modal__input"
                           value={processorCompatibility.join(', ')}
                           onChange={(e) => setProcessorCompatibility(e.target.value.split(', '))} required/>
                </div>

                <div className="modal__form-group">
                    <label htmlFor="energyConsumption" className="modal__form-label">Energy Consumption:</label>
                    <input id="energyConsumption" className="modal__input" type="number" value={energyConsumption}
                           onChange={(e) => setEnergyConsumption(parseInt(e.target.value, 10))} min="0" required/>
                </div>

            </Modal>

            <div className="product-list">
                {Motherboards.map(motherboard => (
                    <ProductBox
                        key={motherboard.id}
                        product={motherboard}
                        imgSrc={motherboard.motherboardPhotos && motherboard.motherboardPhotos.length > 0 ? motherboard.motherboardPhotos[0] : motherboardPhoto}
                        toCharacteristicsPage={() => navigate(`/hardware/motherboard/${motherboard.id}`)}
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
