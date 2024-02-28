import {PC} from "../pc/PC.tsx";
import {Order} from "./Order.tsx";

export type User = {
    id: string,
    email: string,
    userPcs: PC[],
    orders: Order[],
    role: string,
    dateOfAccountCreation: string,
    photo: string
} | null;
