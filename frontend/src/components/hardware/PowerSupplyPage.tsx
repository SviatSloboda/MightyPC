import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import {Product} from "../../model/hardware/Product.tsx";
import psuPhoto from '../../assets/psu.png';

export default function PowerSupplyPage() {
    const [PowerSupplys, setPowerSupplys] = useState<Product[]>([]);

    useEffect(() => {
        const getAllPowerSupplys = () => {
            axios.get("/api/hardware/psu")
                .then(response => {
                    console.log("Fetched PowerSupplies:", response.data);
                    if (Array.isArray(response.data)) {
                        setPowerSupplys(response.data);
                    } else {
                        console.error("Response data is not an array:", response.data);
                    }
                })
                .catch(error => console.error("Failed to fetch PowerSupplies:", error));
        };

        getAllPowerSupplys();
    }, []);

    return (
        <>
            <h1 className="body--product-page">PowerSupply List</h1>
            <div className="product-list">
                {PowerSupplys.map(psu => (
                    <ProductBox product={psu} key={psu.hardwareSpec.id} imgSrc={psuPhoto}/>
                ))}
            </div>
        </>
    );
}