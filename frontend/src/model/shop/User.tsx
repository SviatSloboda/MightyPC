import {PC} from "../PC.tsx";
import {Order} from "./Order.tsx";

export type User = {
    id: string,
    email: string,
    userPcs: PC[],
    orders: Order[],
} | null;
