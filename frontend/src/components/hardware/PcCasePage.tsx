import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import {Product} from "../../model/hardware/Product.tsx";
import pcCasePhoto from '../../assets/pcCase.png';

export default function PcCasePage() {
    const [PcCases, setPcCases] = useState<Product[]>([]);

    useEffect(() => {
        const getAllPcCases = () => {
            axios.get("/api/hardware/pc-case")
                .then(response => {
                    console.log("Fetched PowerSupplies:", response.data);
                    if (Array.isArray(response.data)) {
                        setPcCases(response.data);
                    } else {
                        console.error("Response data is not an array:", response.data);
                    }
                })
                .catch(error => console.error("Failed to fetch PowerSupplies:", error));
        };

        getAllPcCases();
    }, []);

    return (
        <>
            <h1 className="body--product-page">PcCase List</h1>
            <div className="product-list">
                {PcCases.map(pcCase => (
                    <ProductBox product={pcCase} key={pcCase.hardwareSpec.id} imgSrc={pcCasePhoto}/>
                ))}
            </div>
        </>
    );
}