import {useState} from 'react';
import axios from 'axios';
import {SpecsIds} from "../../model/pc/SpecsIds.tsx";

export default function TryPage() {
    const [specsIds, setSpecsIds] = useState<SpecsIds>({
        cpuId: '', gpuId: '', motherboardId: '', ramId: '', ssdId: '', hddId: '', powerSupplyId: '', pcCaseId: ''
    });

    const getValues = () => {
        const payload: string[] = ["gaming, 1234"]
        axios.post('/api/hardware/motherboard', payload)
            .then(response => {
                setSpecsIds(response.data);
            })
            .catch(error => console.error('Failed to add Motherboard:', error));
    };


    return (<>
            <h1 className={"notCompleted"}>{specsIds.powerSupplyId}</h1>
            <h1 className={"notCompleted"}>{specsIds.powerSupplyId}</h1>
            <button onClick={getValues}></button>
        </>)
}