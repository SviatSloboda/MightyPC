import {useEffect, useState} from "react";
import axios from "axios";
import {CPU} from "../../model/hardware/CPU.tsx";

export default function CpuPage() {
    const [CPUs, setCPUs] = useState<CPU[]>([]);

    useEffect(() => {
        const getAllWorkouts = () => {
            axios.get("/api/hardware/cpu")
                .then(response => {
                    setCPUs(response.data);
                })
                .catch(error => console.error("Failed to fetch CPUs:", error));
        };

        getAllWorkouts();
    }, []);


    return (
        <>
            <h1 className="notCompleted">CPU List</h1>
            {CPUs.length > 0 ? (
                <div className="cpuList">
                    {CPUs.map(cpu => (
                        <h2 className="notCompleted" key={cpu.id}>{cpu.name}</h2>
                    ))}
                </div>
            ) : (
                <p>No CPUs found.</p>
            )}
        </>
    );
}
