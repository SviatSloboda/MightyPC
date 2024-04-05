import { useState, useCallback, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from "react-router-dom";
import pcPhoto from "../../assets/Pc.png";
import nothingImage from "../../assets/noPC.png";
import { useAuth } from "../../contexts/AuthContext";
import useLoginModal from "../hardware/utils/useLoginModal";
import LoginModal from "../hardware/utils/LoginModal";
import { PC } from "../../model/pc/PC";
import {login} from "../../contexts/authUtils.ts";
import UserProductBox from "../hardware/utils/UserProductBox.tsx";

export default function UserPcsPage() {
    const [PCs, setPCs] = useState<PC[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pcsPerPage = 8;
    const { user } = useAuth();
    const { isLoginModalOpen, showLoginModal, hideLoginModal } = useLoginModal();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchPCs = async () => {
            const response = await axios.get(`/api/user-pcs/${user?.id}/page`, { params: { page: currentPage, size: pcsPerPage } });
            setPCs(response.data.content);
            setTotalPages(response.data.totalPages);
        };
        fetchPCs();
    }, [currentPage]);

    const paginate = useCallback((pageNumber: number) => setCurrentPage(pageNumber - 1), []);

    const handleAddToBasket = (pc: PC) => {
        if (!user) {
            showLoginModal();
            return;
        }
        const payload = {
            id: pc.id,
            type: "pc",
            name: pc.hardwareSpec.name,
            description: pc.hardwareSpec.description,
            price: pc.hardwareSpec.price,
            photos: pc.photos?.length ? pc.photos : [pcPhoto]
        };
        axios.post(`/api/basket/${user.id}`, payload).then(() => navigate("../basket/"));
    };

    return (
        <>
            {PCs.length === 0 ? (
                <div className="basket-empty">
                    <img className={"basket-empty__image"} src={nothingImage} alt="No PCs found" />
                    <p className={"basket-empty__message"}>You have not configured your own PC yet!</p>
                </div>
            ) : (
                <>
                    <div className="product-list">
                        {PCs.map(pc => (
                            <UserProductBox
                                key={pc.id}
                                product={pc}
                                imgSrc={pc.photos?.[0] ?? pcPhoto}
                                toCharacteristicsPage={() => navigate(`/pc/${pc.id}`, { state: { isUserPc: true } })}
                                onAddToBasket={() => handleAddToBasket(pc)}
                            />
                        ))}
                    </div>
                    <div className="product-list__pagination">
                        {Array.from({ length: totalPages }, (_, i) => (
                            <button key={i} onClick={() => paginate(i + 1)} className={`pagination__button ${currentPage === i ? 'pagination__button--active' : ''}`}>{i + 1}</button>
                        ))}
                    </div>
                </>
            )}
            <LoginModal isOpen={isLoginModalOpen} onLogin={login} onClose={hideLoginModal} />
        </>
    );
}
