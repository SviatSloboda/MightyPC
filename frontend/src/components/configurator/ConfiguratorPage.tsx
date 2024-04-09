import {useState, useEffect, useCallback, ChangeEvent, FormEvent} from 'react';
import axios from 'axios';
import {useAuth} from "../../contexts/AuthContext";
import {HardwareSpec} from "../../model/pc/hardware/HardwareSpec";
import {SpecsIds} from "../../model/pc/SpecsIds";
import useLoginModal from "../hardware/utils/useLoginModal.ts";
import LoginModal from "../hardware/utils/LoginModal.tsx";
import {login} from "../../contexts/authUtils.ts";

import {ToastContainer, toast} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import {useNavigate} from "react-router-dom";
import {SpecsIdsForEnergyConsumption} from "../../model/pc/SpecsIdsForEnergyConsumption.tsx";

interface ConfiguratorComponents {
    componentIdsAndNames: Array<Map<string, string>>;
}

interface SelectOption {
    id: string;
    displayValue: string;
}

export default function ConfiguratorPage() {
    const [components, setComponents] = useState<SelectOption[][]>([]);
    const [hardwareSpec, setHardwareSpec] = useState<HardwareSpec>({name: '', description: '', price: '0', rating: 0});
    const [createSpecs, setCreateSpecs] = useState<SpecsIds>({
        cpuId: '', gpuId: '', motherboardId: '', ramId: '', ssdId: '', hddId: '', powerSupplyId: '', pcCaseId: ''
    });
    const {user} = useAuth();
    const {isLoginModalOpen, showLoginModal, hideLoginModal} = useLoginModal();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchComponents = async () => {
            try {
                const response = await axios.get<ConfiguratorComponents>('/api/configurator');
                const fetchedComponents = response.data.componentIdsAndNames.map(componentCategory => Object.entries(componentCategory).map(([id, namePrice]): SelectOption => {
                    const [name, price] = namePrice.split(' ($');
                    return {id, displayValue: `${name} ($${price}`};
                }));
                setComponents(fetchedComponents);
            } catch (error) {
                console.error('Failed to fetch components:', error);
            }
        };

        fetchComponents();
    }, []);

    const handleSelectChange = useCallback((e: ChangeEvent<HTMLSelectElement>, componentType: keyof SpecsIds) => {
        const {value} = e.target;
        setCreateSpecs(prev => ({...prev, [componentType]: value}));
    }, []);

    const handleInputChange = useCallback((e: ChangeEvent<HTMLInputElement>, field: keyof HardwareSpec) => {
        const {value, type} = e.target;
        setHardwareSpec(prev => ({
            ...prev, [field]: type === 'number' ? parseFloat(value) || 0 : value
        }));
    }, []);

    const saveValues = useCallback(async () => {
        if (!user) {
            showLoginModal();
            return;
        }

        const allValuesSelected = Object.values(createSpecs).every(value => value !== '');
        if (!user || !allValuesSelected) {
            toast.error('Please choose all values before saving!', {
                position: 'top-center'
            });
            return;
        }

        const payload = {
            hardwareSpec, specsIds: createSpecs,
        };
        if (user) {
            try {
                await axios.post(`/api/user-pcs/${user.id}`, payload).then(() => {
                    navigate('../user-pcs')
                });
            } catch (error) {
                console.error('Failed to add PC:', error);
            }
        }
    }, [hardwareSpec, createSpecs, user]);

    const handleSubmit = useCallback((e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        saveValues();
    }, [saveValues]);

    useEffect(() => {
        const fetchMotherboards = async () => {
            if (createSpecs.cpuId) {
                try {
                    const cpuSocketResponse = await axios.get<string>(`/api/hardware/cpu/socket/${createSpecs.cpuId}`);
                    const cpuSocket = cpuSocketResponse.data;
                    const response = await axios.get<{
                        [key: string]: string
                    }>(`/api/configurator/motherboard/socket/${cpuSocket}`);
                    const motherboardOptions: SelectOption[] = Object.entries(response.data).map(([id, name]) => ({
                        id, displayValue: name,
                    }));
                    setComponents((prevComponents) => prevComponents.map((componentCategory, index) => Object.keys(createSpecs)[index] === 'motherboardId' ? motherboardOptions : componentCategory,),);
                } catch (error) {
                    console.error('Failed to fetch motherboards:', error);
                }
            }
        };

        fetchMotherboards();
    }, [createSpecs.cpuId]);

    useEffect(() => {
        const fetchPowerSupplies = async () => {
            if (createSpecs.powerSupplyId) {
                try {
                    const payload: SpecsIdsForEnergyConsumption = {
                        cpuId: createSpecs.cpuId,
                        gpuId: createSpecs.gpuId,
                        motherboardId: createSpecs.motherboardId,
                        ramId: createSpecs.ramId,
                        ssdId: createSpecs.hddId,
                        hddId: createSpecs.hddId,
                    }

                    const response = await axios.post<{
                        [key: string]: string
                    }>(`/api/pc/configuration/calculate-energy-consumption`, payload);

                    const powerSupplyOptions: SelectOption[] = Object.entries(response.data).map(([id, name]) => ({
                        id, displayValue: name,
                    }));

                    setComponents((prevComponents) => prevComponents.map((componentCategory, index) => Object.keys(createSpecs)[index] === 'powerSupplyId' ? powerSupplyOptions : componentCategory,),);
                } catch (error) {
                    console.error('Failed to fetch power supplies!:', error);
                }
            }
        };

        fetchPowerSupplies();
    }, [createSpecs.cpuId, createSpecs, createSpecs.hddId, hardwareSpec, user]);

    return (<>
        <div className="pc-configurator">
            <h2 className="pc-configurator__title">Configure Your PC</h2>
            <form className="pc-configurator__form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="name">Name:</label>
                    <input type="text" id="name" value={hardwareSpec.name}
                           onChange={(e) => handleInputChange(e, 'name')}/>
                </div>
                <div className="form-group">
                    <label htmlFor="description">Description:</label>
                    <input type="text" id="description" value={hardwareSpec.description}
                           onChange={(e) => handleInputChange(e, 'description')}/>
                </div>
                {components.map((componentCategory, index) => (<div className="form-group" key={index}>
                    <label htmlFor={`component-${index}`}>{Object.keys(createSpecs)[index].replace(/Id$/, '')}:</label>
                    <select id={`component-${index}`}
                            onChange={(e) => handleSelectChange(e, Object.keys(createSpecs)[index] as keyof SpecsIds)}>
                        <option value="">Select an option</option>
                        {componentCategory.map(option => (<option key={option.id} value={option.id}>
                            {option.displayValue}
                        </option>))}
                    </select>
                </div>))}
                <button className="pc-configurator__submit" type="submit">Save Configuration</button>
            </form>
        </div>

        <LoginModal isOpen={isLoginModalOpen} onLogin={login} onClose={hideLoginModal}/>
        <ToastContainer position="top-center"/>
    </>);
}