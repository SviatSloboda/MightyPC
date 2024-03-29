import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import Modal, {useModal} from './Modal';
import {useNavigate} from "react-router-dom";
import psuPhoto from "../../assets/psu.png";
import {PSU} from "../../model/hardware/PSU.tsx";


export default function PsuPage() {
    const [PSUs, setPSUs] = useState<PSU[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const psusPerPage = 8; // This value should match your backend Pageable size
    const [modalOpen, toggleModal] = useModal();

    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [price, setPrice] = useState("");
    const [rating, setRating] = useState(0);
    const [power, setPower] = useState(0);
    const navigate = useNavigate();

    useEffect(() => {
        async function fetchPsus() {
            try {
                const response = await axios.get(`/api/hardware/psu/page?page=${currentPage}&size=${psusPerPage}`);
                setPSUs(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Failed to fetch PSUs:", error);
            }
        }

        fetchPsus();
    }, [currentPage, psusPerPage]);

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
            power
        };

        axios.post('/api/hardware/psu', payload)
            .then(response => {
                console.log('PSU added:', response.data);
                setPSUs(prevPSUs => [...prevPSUs, response.data]);
                toggleModal();
            })
            .catch(error => console.error('Failed to add PSU:', error));
    };

    return (
        <>
            <h1 className="body--product-page">PSU List</h1>
            <button className="default-button" onClick={toggleModal}>Add PSU</button>

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
                    <label htmlFor="modal-power" className="modal__form-label">Power:</label>
                    <input id="modal-power" className="modal__input" type="number" value={power}
                           onChange={(e) => setPower(parseInt(e.target.value, 10))} min="0" required/>
                </div>
            </Modal>


            <div className="product-list">
                {PSUs.map(psu => (
                    <ProductBox
                        key={psu.id}
                        product={psu}
                        imgSrc={psu.psuPhotos && psu.psuPhotos.length > 0 ? psu.psuPhotos[0] : psuPhoto}
                        toCharacteristicsPage={() => navigate(`/hardware/psu/${psu.id}`)}
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
