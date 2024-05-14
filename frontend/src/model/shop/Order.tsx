import {Item} from "./Item.tsx";
import {OrderStatus} from "./OrderStatus.tsx";

export interface Order {
    id: string;
    items: Item[];
    completePrice: number;
    orderStatus: OrderStatus;
}