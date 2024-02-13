import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import {Product} from "../../model/hardware/Product.tsx";
import ramPhoto from '../../assets/ram.png';

export default function RamPage() {
    const [Rams, setRams] = useState<Product[]>([]);

    useEffect(() => {
        const getAllRams = () => {
            axios.get("/api/hardware/ram")
                .then(response => {
                    console.log("Fetched Rams:", response.data);
                    if (Array.isArray(response.data)) {
                        setRams(response.data);
                    } else {
                        console.error("Response data is not an array:", response.data);
                    }
                })
                .catch(error => console.error("Failed to fetch Rams:", error));
        };

        getAllRams();
    }, []);

    return (
        <>
            <h1 className="body--product-page">Ram List</h1>
            <div className="product-list">
                {Rams.map(ram => (
                    <ProductBox product={ram} key={ram.hardwareSpec.id} imgSrc={ramPhoto}/>
                ))}
            </div>
        </>
    );
}