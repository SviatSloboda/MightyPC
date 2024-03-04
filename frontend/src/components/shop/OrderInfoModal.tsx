import React, {useEffect, useState} from 'react';
import Modal from 'react-bootstrap/Modal';
import axios from 'axios';
import {Order} from "../../model/shop/Order.tsx";

interface OrderInfoModalProps {
    showModal: boolean;
    setShowModal: (show: boolean) => void;
    orderId: string;
    userId: string;
}

const OrderInfoModal: React.FC<OrderInfoModalProps> = ({showModal, setShowModal, orderId, userId}) => {
    const [orderDetails, setOrderDetails] = useState<Order | null>(null);

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

    return (<Modal show={showModal} onHide={handleClose} className="order-info-modal">
        <Modal.Header closeButton className="order-info-modal__header">
            <Modal.Title className="order-info-modal__title">Order Details</Modal.Title>
        </Modal.Header>
        <Modal.Body className="order-info-modal__body">
            {orderDetails?.items.map((item) => (<div key={item.id} className="order-info-modal__group">
                <label className="order-info-modal__label">{item.name}:</label>
                <input
                    className="order-info-modal__input"
                    type="text"
                    value={item.price}
                    readOnly
                />
            </div>))}
            <div className="order-info-modal__group">
                <label className="order-info-modal__label">Total Price:</label>
                <input
                    className="order-info-modal__input"
                    type="text"
                    value={orderDetails?.completePrice || ''}
                    readOnly
                />
            </div>
        </Modal.Body>
        <Modal.Footer className="order-info-modal__footer">
            <button onClick={handleClose}
                    className="order-info-modal__button order-info-modal__button--close">Close
            </button>
        </Modal.Footer>
    </Modal>);
}

export default OrderInfoModal;
