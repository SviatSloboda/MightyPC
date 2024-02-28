import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import Modal, {useModal} from './Modal';
import {useNavigate} from "react-router-dom";
import pcCasePhoto from "../../assets/pcCase.png";
import {PcCase} from "../../model/pc/hardware/PcCase.tsx";

export default function PcCasePage() {
    const [PcCases, setPcCases] = useState<PcCase[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pcCasesPerPage = 8; // This value should match your backend Pageable size
    const [modalOpen, toggleModal] = useModal();

    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [price, setPrice] = useState("");
    const [rating, setRating] = useState(0);
    const [dimensions, setDimensions] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        async function fetchPcCases() {
            try {
                const response = await axios.get(`/api/hardware/pc-case/page?page=${currentPage}&size=${pcCasesPerPage}`);
                setPcCases(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Failed to fetch PcCases:", error);
            }
        }

        fetchPcCases();
    }, [currentPage, pcCasesPerPage]);

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
            dimensions
        };

        axios.post('/api/hardware/pc-case', payload)
            .then(response => {
                console.log('PcCase added:', response.data);
                setPcCases(prevPcCases => [...prevPcCases, response.data]);
                toggleModal();
            })
            .catch(error => console.error('Failed to add PcCase:', error));
    };

    return (
        <>
            <h1 className="body--product-page">PcCase List</h1>
            <button className="default-button" onClick={toggleModal}>Add PcCase</button>

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
                    <label htmlFor="modal-dimensions" className="modal__form-label">Dimensions:</label>
                    <input id="modal-dimensions" className="modal__input" type="number" value={dimensions}
                           onChange={(e) => setDimensions(e.target.value)} required/>
                </div>
            </Modal>


            <div className="product-list">
                {PcCases.map(pcCase => (
                    <ProductBox
                        key={pcCase.id}
                        product={pcCase}
                        imgSrc={pcCase.pcCasePhotos && pcCase.pcCasePhotos.length > 0 ? pcCase.pcCasePhotos[0] : pcCasePhoto}
                        toCharacteristicsPage={() => navigate(`/hardware/pc-case/${pcCase.id}`)}
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
