import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import {Product} from "../../model/hardware/Product.tsx";
import motherboardPhoto from '../../assets/motherboard.png';

export default function MotherBoardPage() {
    const [MotherBoards, setMotherBoards] = useState<Product[]>([]);

    useEffect(() => {
        const getAllMotherBoards = () => {
            axios.get("/api/hardware/motherboard")
                .then(response => {
                    console.log("Fetched MotherBoards:", response.data);
                    if (Array.isArray(response.data)) {
                        setMotherBoards(response.data);
                    } else {
                        console.error("Response data is not an array:", response.data);
                    }
                })
                .catch(error => console.error("Failed to fetch MotherBoards:", error));
        };

        getAllMotherBoards();
    }, []);

    return (
        <>
            <h1 className="body--product-page">MotherBoard List</h1>
            <div className="product-list">
                {MotherBoards.map(motherboard => (
                    <ProductBox product={motherboard} key={motherboard.hardwareSpec.id} imgSrc={motherboardPhoto}/>
                ))}
            </div>
        </>
    );
}