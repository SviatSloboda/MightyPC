import {useEffect, useState} from 'react';
import axios from 'axios';
import ProductBox from './ProductBox';
import {Product} from "../../model/hardware/Product.tsx";
import gpuPhoto from '../../assets/gpu.png';

export default function GpuPage() {
    const [GPUs, setGPUs] = useState<Product[]>([]);
    useEffect(() => {
        const getAllGpus = () => {
            axios.get("/api/hardware/gpu")
                .then(response => {
                    console.log("Fetched GPUs:", response.data);
                    if (Array.isArray(response.data)) {
                        setGPUs(response.data);
                    } else {
                        console.error("Response data is not an array:", response.data);
                    }
                })
                .catch(error => console.error("Failed to fetch GPUs:", error));
        };

        getAllGpus();
    }, []);

    return (
        <>
            <h1 className="body--product-page">GPU List</h1>
            <div className="product-list">
                {GPUs.map(gpu => (
                    <ProductBox product={gpu} key={gpu.hardwareSpec.id} imgSrc={gpuPhoto}/>
                ))}
            </div>
        </>
    );
}