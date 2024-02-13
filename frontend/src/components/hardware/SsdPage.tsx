import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import {Product} from "../../model/hardware/Product.tsx";
import ssdPhoto from '../../assets/ssd.png';

export default function SsdPage() {
    const [Ssds, setSsds] = useState<Product[]>([]);

    useEffect(() => {
        const getAllSsds = () => {
            axios.get("/api/hardware/ssd")
                .then(response => {
                    console.log("Fetched Ssds:", response.data);
                    if (Array.isArray(response.data)) {
                        setSsds(response.data);
                    } else {
                        console.error("Response data is not an array:", response.data);
                    }
                })
                .catch(error => console.error("Failed to fetch Ssds:", error));
        };

        getAllSsds();
    }, []);

    return (
        <>
            <h1 className="body--product-page">Ssd List</h1>
            <div className="product-list">
                {Ssds.map(ssd => (
                    <ProductBox product={ssd} key={ssd.hardwareSpec.id} imgSrc={ssdPhoto}/>
                ))}
            </div>
        </>
    );
}