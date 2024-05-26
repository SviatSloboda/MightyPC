import React, {useEffect, useState} from 'react';
import axios from 'axios';
import {Order} from "../../model/shop/Order.tsx";
import {useNavigate} from "react-router-dom";

interface OrderInfoModalProps {
    showModal: boolean;
    setShowModal: (show: boolean) => void;
    orderId: string;
    userId: string;
}

const OrderInfoModal: React.FC<OrderInfoModalProps> = ({showModal, setShowModal, orderId, userId}) => {
    const [orderDetails, setOrderDetails] = useState<Order | null>(null);
    const navigate = useNavigate();

    const fetchOrderDetails = async () => {
        try {
            const response = await axios.get<Order>(`/api/order/${userId}/${orderId}`);
            setOrderDetails(response.data);
        } catch (error) {
            console.error("Error fetching order details:", error);
        }
    };

    useEffect(() => {
        if (showModal) {
            fetchOrderDetails();
        }
    }, [showModal, orderId, userId]);

    const handleClose = () => setShowModal(false);

    if (!showModal) return null;

    return (
        <div className="order-info-modal__overlay">
            <div className="order-info-modal">
                <div className="order-info-modal__header">
                    <h2 className="order-info-modal__title">Order Details</h2>
                    <button className="order-info-modal__close-button" onClick={handleClose}>X</button>
                </div>
                <div className="order-info-modal__body">
                    {orderDetails?.items.map((item) => (
                        <div key={item.id} className="order-info-modal__item">
                            <img src={item.photo} alt={item.name} className="order-info-modal__item-photo"/>
                            <div className="order-info-modal__item-details">
                                <h4 className="order-info-modal__item-name">{item.name}</h4>
                                <p className="order-info-modal__item-description">{item.description}</p>
                                <p className="order-info-modal__item-price">Price: {item.price} €</p>
                            </div>
                            <button
                                className="order-info-modal__item-button"
                                onClick={() => navigate(`${item.pathToCharacteristicsPage}/${item.id}`)}>
                                Detailed Information
                            </button>
                        </div>
                    ))}
                    <div className="order-info-modal__total">
                        <h4>Total Price: {orderDetails?.completePrice} €</h4>
                    </div>

                    <button onClick={handleClose}
                            className="order-info-modal__button--close">Close
                    </button>
                </div>
            </div>
        </div>
    );
}

export default OrderInfoModal;
