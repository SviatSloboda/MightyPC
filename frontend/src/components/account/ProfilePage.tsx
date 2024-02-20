import {useEffect, useState} from "react";
import {User} from "../../model/shop/User.tsx";
import axios from "axios";

export default function ProfilePage(){
    const[user, setUser] = useState<User>(null)

    useEffect(() => {
        axios.get<User>('/api/user', { withCredentials: true }).then((response) => {
            setUser(response.data || null);
        }).catch(() => {
            setUser(null);
        });
    }, []);

    return(
        <h1 className={"notCompleted"}>{user?.email}</h1>
    )
}