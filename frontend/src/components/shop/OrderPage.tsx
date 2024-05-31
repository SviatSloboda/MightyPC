import {useCallback, useEffect, useState} from 'react';
import {useAuth} from '../../contexts/AuthContext';
import {Order} from '../../model/shop/Order';
import OrderInfoModal from './OrderInfoModal';
import empty_box from "../../assets/shop/empty_box.png";
import useAxiosWithAuth from "../../contexts/useAxiosWithAuth.ts";

export default function OrderPage() {
    const [orders, setOrders] = useState<Order[]>([]);
    const [showModal, setShowModal] = useState<boolean>(false);
    const [selectedOrderId, setSelectedOrderId] = useState<string>('');
    const {user} = useAuth();
    const axiosInstance = useAxiosWithAuth();

    useEffect(() => {
        if (user?.id) {
            fetchOrders(user.id);
        }
    }, [user]);

    const fetchOrders = useCallback(async (userId: string) => {
        try {
            const response = await axiosInstance.get<Order[]>(`/order/${userId}`);
            setOrders(response.data);
        } catch (error) {
            console.error('Failed to fetch orders', error);
        }
    }, []);

    const handleOpenInfoModal = (orderId: string) => {
        setSelectedOrderId(orderId);
        setShowModal(true);
    };

    return (
        <div className="order-page">
            {orders.length === 0 ? (
                <div className="order-page__basket-empty">
                    <img src={empty_box} alt="Empty Basket" className="order-page__image"/>
                    <p className="order-page__basket-empty-message">There are no orders!</p>
                </div>
            ) : (
                orders.map((order: Order) => (
                    <div key={order.id} className="order-page__item">
                        <div className="order-page__item-photos">
                            {order.items.slice(0, 4).map((item) => (
                                <img key={item.id} src={item.photo} alt={`Item ${item.id}`}
                                     className="order-page__item-photo"/>
                            ))}
                        </div>
                        <div className="order-page__item-info">
                            <h2 className="order-page__item-id">Order ID: {order.id.slice(0, 8)}</h2>
                            <p className="order-page__item-total">Total: {order.completePrice} €</p>
                        </div>
                        <div className="order-page__item-actions">
                            <button className="order-page__item-button"
                                    onClick={() => handleOpenInfoModal(order.id)}>Info
                            </button>
                            {order.orderStatus === 'PENDING' &&
                                <button className="order-page__item-button">Pay</button>}
                        </div>
                    </div>
                ))
            )}
            {showModal && (
                <OrderInfoModal
                    showModal={showModal}
                    setShowModal={setShowModal}
                    orderId={selectedOrderId}
                    userId={user?.id ?? ''}
                />
            )}
        </div>
    );
}
