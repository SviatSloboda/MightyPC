import {useCallback, useEffect, useState} from 'react';
import axios from 'axios';
import {useAuth} from '../../contexts/AuthContext';
import {Order} from '../../model/shop/Order';
import OrderInfoModal from './OrderInfoModal'; // Adjust the import path as necessary
import empty_box from "../../assets/empty_box.png";

export default function OrderPage() {
    const [orders, setOrders] = useState<Order[]>([]);
    const [showModal, setShowModal] = useState<boolean>(false);
    const [selectedOrderId, setSelectedOrderId] = useState<string>('');
    const {user} = useAuth();

    useEffect(() => {
        if (user?.id) {
            fetchOrders(user.id);
        }
    }, [user]);

    const fetchOrders = useCallback(async (userId: string) => {
        try {
            const response = await axios.get<Order[]>(`/api/order/${userId}`);
            setOrders(response.data);
        } catch (error) {
            console.error('Failed to fetch orders', error);
        }
    }, []);

    const handleOpenInfoModal = (orderId: string) => {
        setSelectedOrderId(orderId);
        setShowModal(true);
    };

    return (<div className="order-page">
        {orders.length === 0 ? (<div className="basket-empty">
            <img src={empty_box} alt="Empty Basket" className="basket-empty__image"/>
            <p className="basket-empty__message">There are no orders!</p>
        </div>) : (orders.map((order: Order) => (<div key={order.id} className="order-item">
            <div className="order-item__details">
                <h2 className="order-item__id">Order ID: {order.id}</h2>
                <p className="order-item__status">Status: {order.orderStatus}</p>
                <p className="order-item__total">Total: {order.completePrice} €</p>
                <div className="order-item__actions">
                    <button className="order-item__button"
                            onClick={() => handleOpenInfoModal(order.id)}>Info
                    </button>
                    {order.orderStatus === 'PENDING' && <button className="order-item__button">Pay</button>}
                </div>
            </div>
        </div>)))}
        {showModal && (<OrderInfoModal
            showModal={showModal}
            setShowModal={setShowModal}
            orderId={selectedOrderId}
            userId={user?.id || ''}
        />)}
    </div>);
}
