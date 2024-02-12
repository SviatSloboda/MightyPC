import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import {Product} from "../../model/hardware/Product.tsx";
import hddPhoto from '../../assets/hdd.png';

export default function HddPage() {
    const [Hdds, setHdds] = useState<Product[]>([]);

    useEffect(() => {
        const getAllHdds = () => {
            axios.get("/api/hardware/hdd")
                .then(response => {
                    console.log("Fetched Hdds:", response.data);
                    if (Array.isArray(response.data)) {
                        setHdds(response.data);
                    } else {
                        console.error("Response data is not an array:", response.data);
                    }
                })
                .catch(error => console.error("Failed to fetch Hdds:", error));
        };

        getAllHdds();
    }, []);

    return (
        <>
            <h1 className="body--product-page">Hdd List</h1>
            <div className="product-list">
                {Hdds.map(hdd => (
                    <ProductBox product={hdd} key={hdd.hardwareSpec.id} imgSrc={hddPhoto}/>
                ))}
            </div>
        </>
    );
}