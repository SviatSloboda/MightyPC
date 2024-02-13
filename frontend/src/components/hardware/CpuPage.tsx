import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import {Product} from "../../model/hardware/Product.tsx";
import cpuPhoto from '../../assets/cpu.png';

export default function CpuPage() {
    const [CPUs, setCPUs] = useState<Product[]>([]);

    useEffect(() => {
        const getAllCpus = () => {
            axios.get("/api/hardware/cpu")
                .then(response => {
                    console.log("Fetched CPUs:", response.data);
                    if (Array.isArray(response.data)) {
                        setCPUs(response.data);
                    } else {
                        console.error("Response data is not an array:", response.data);
                    }
                })
                .catch(error => console.error("Failed to fetch CPUs:", error));
        };

        getAllCpus();
    }, []);

    return (
        <>
            <h1 className="body--product-page">CPU List</h1>
            <div className="product-list">
                {CPUs.map(cpu => (
                    <ProductBox product={cpu} key={cpu.hardwareSpec.id} imgSrc={cpuPhoto}/>
                ))}
            </div>
        </>
    );
}