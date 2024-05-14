import {Order} from "./Order.tsx";
import {PC} from "../pc/PC.tsx";

export type User = {
    id: string,
    email: string,
    userPcs: PC[],
    orders: Order[],
    role: string,
    dateOfAccountCreation: string,
    photo: string
} | null;
