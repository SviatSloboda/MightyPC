import {useCallback, useEffect, useState} from 'react';
import axios from 'axios';
import {useAuth} from "../../contexts/AuthContext.tsx";
import {Item} from "../../model/shop/Item.tsx";
import empty_basket from "../../assets/empty_basket.png";
import {useNavigate} from "react-router-dom";

export default function BasketPage() {
    const [items, setItems] = useState([]);
    const [totalPrice, setTotalPrice] = useState("0");
    const {user} = useAuth();
    const navigate = useNavigate();

    const fetchBasketData = useCallback(async (userId: string) => {
        try {
            const itemsResponse = await axios.get(`/api/basket/${userId}`, {withCredentials: true});
            setItems(itemsResponse.data ?? []);

            const priceResponse = await axios.get(`/api/basket/${userId}/price`);
            setTotalPrice(priceResponse.data.toString());
        } catch (error) {
            console.error("Failed to fetch basket data", error);
        }
    }, []);

    useEffect(() => {
        if (user?.id) {
            fetchBasketData(user.id);
        }
    }, [user, fetchBasketData]);

    const deleteItem = async (itemId: string) => {
        try {
            await axios.delete(`/api/basket/${user?.id}/${itemId}`, {withCredentials: true});
            if (user?.id) {
                fetchBasketData(user?.id);
            }
        } catch (error) {
            console.error("Failed to delete item", error);
        }
    };

    const placeOrder = async () => {
        try {
            await axios.post(`/api/order/${user?.id}`, items, {withCredentials: true});
            await axios.delete(`/api/basket/${user?.id}/all`, {withCredentials: true});
            navigate("../order");
        } catch (error) {
            console.error("Failed to place order", error);
        }
    };

    return (<div className="basket-page">
        {items.length === 0 ? (<div className="basket-empty">
            <img src={empty_basket} alt="Empty Basket" className="basket-empty__image"/>
            <p className="basket-empty__message">Basket is empty!</p>
        </div>) : (<div className="basket-items">
            {items.map((item: Item) => (<div key={item.id} className="basket-item">
                <img src={item.photos[0]} alt={item.name} className="basket-item__image"/>
                <div className="basket-item__details">
                    <h2 className="basket-item__name">{item.name}</h2>
                    <p className="basket-item__description">{item.description}</p>
                    <p className="basket-item__price">${item.price}</p>
                    <button onClick={() => deleteItem(item.id)} className="basket-item__button">Remove
                    </button>
                </div>
            </div>))}
        </div>)}
        {user && items.length > 0 && (<div className="basket-page__checkout">
            <p className="basket-page__total-price">Total: ${totalPrice}</p>
            <button onClick={placeOrder} className="basket-page__place-order-button">Place Order</button>
        </div>)}
    </div>);
}
