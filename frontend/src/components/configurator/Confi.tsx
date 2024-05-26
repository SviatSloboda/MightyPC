import {ChangeEvent, useCallback, useEffect, useState} from 'react';
import axios from 'axios';
import {useAuth} from "../../contexts/AuthContext";
import {HardwareSpec} from "../../model/pc/hardware/HardwareSpec";
import {SpecsIds} from "../../model/pc/SpecsIds";
import useLoginModal from "../login/useLoginModal.ts";
import LoginModal from "../login/LoginModal.tsx";
import chatGptIcon from "../../assets/icon/chatGpt-icon-link.png";

import {toast, ToastContainer} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import {useNavigate} from "react-router-dom";
import {SpecsIdsForEnergyConsumption} from "../../model/pc/SpecsIdsForEnergyConsumption.tsx";
import ItemDetails from "./ItemDetails.tsx";

import gpuIcon from "../../assets/icon/gpu-icon-link.png";
import cpuIcon from "../../assets/icon/cpu-icon-link.png";
import motherboardIcon from "../../assets/icon/motherboard-icon-link.png";
import ramIcon from "../../assets/icon/ram-icon-link.png";
import ssdIcon from "../../assets/icon/ssd-icon-link.png";
import hddIcon from "../../assets/icon/hdd-icon-link.png";
import psuIcon from "../../assets/icon/psu-icon-link.png";
import pcCaseIcon from "../../assets/icon/pc-case-icon-link.png";

import gpuPhoto from "../../assets/hardware/gpu.png";
import cpuPhoto from "../../assets/hardware/cpu.png";
import motherboardPhoto from "../../assets/hardware/motherboard.png";
import ramPhoto from "../../assets/hardware/ram.png";
import ssdPhoto from "../../assets/hardware/ssd.png";
import hddPhoto from "../../assets/hardware/hdd.png";
import psuPhoto from "../../assets/hardware/psu.png";

import pcCasePhoto from "../../assets/hardware/pcCase.png";
import CreatePCModal from "./CreatePcModal.tsx";
import PreferencesModal from "./PrefencesModal.tsx";

interface ConfiguratorItem {
    id: string;
    name: string;
    price: string;
    image: string;
    pathNameForItemDetailsPage: string;
}

interface ConfiguratorItems {
    itemsForConfigurator: ConfiguratorItem[][];
}

interface SelectOption {
    id: string;
    displayValue: string;
    name: string;
    price: string;
    image: string;
    pathNameForItemDetailsPage: string;
}

const componentTypes = [{
    key: "cpuId", icon: cpuIcon, text: "CPU", defaultImage: cpuPhoto, path: "/hardware/cpu"
}, {key: "gpuId", icon: gpuIcon, text: "GPU", defaultImage: gpuPhoto, path: "/hardware/gpu"}, {
    key: "motherboardId",
    icon: motherboardIcon,
    text: "Motherboard",
    defaultImage: motherboardPhoto,
    path: "/hardware/motherboard"
}, {key: "ramId", icon: ramIcon, text: "RAM", defaultImage: ramPhoto, path: "/hardware/ram"}, {
    key: "ssdId", icon: ssdIcon, text: "SSD", defaultImage: ssdPhoto, path: "/hardware/ssd"
}, {key: "hddId", icon: hddIcon, text: "HDD", defaultImage: hddPhoto, path: "/hardware/hdd"}, {
    key: "powerSupplyId", icon: psuIcon, text: "Power Supply", defaultImage: psuPhoto, path: "/hardware/psu"
}, {key: "pcCaseId", icon: pcCaseIcon, text: "PC Case", defaultImage: pcCasePhoto, path: "/hardware/pc-case"}];

export default function ConfiguratorPage() {
    const [components, setComponents] = useState<SelectOption[][]>([]);
    const [fetchedComponents, setFetchedComponents] = useState<ConfiguratorItem[][]>([]);
    const [hardwareSpec, setHardwareSpec] = useState<HardwareSpec>({name: '', description: '', price: '0', rating: 0});
    const [createSpecs, setCreateSpecs] = useState<SpecsIds>({
        cpuId: '', gpuId: '', motherboardId: '', ramId: '', ssdId: '', hddId: '', powerSupplyId: '', pcCaseId: ''
    });
    const {user} = useAuth();
    const {isLoginModalOpen, showLoginModal, hideLoginModal, handleLogin} = useLoginModal();

    const navigate = useNavigate();

    const [selectedItems, setSelectedItems] = useState<{ [key in keyof SpecsIds]: ConfiguratorItem | null }>({
        cpuId: null,
        gpuId: null,
        motherboardId: null,
        ramId: null,
        ssdId: null,
        hddId: null,
        powerSupplyId: null,
        pcCaseId: null,
    });

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isPreferencesModalOpen, setIsPreferencesModalOpen] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [isGeneratedByGPT, setIsGeneratedByGPT] = useState(false);

    const totalPrice = Object.values(selectedItems).reduce((acc, item) => acc + (item ? parseFloat(item.price) : 0), 0);

    const fetchComponents = async () => {
        try {
            const response = await axios.get<ConfiguratorItems>('/api/configurator/items');
            const fetchedComponents = response.data.itemsForConfigurator.map(componentCategory => componentCategory.map(item => ({
                id: item.id,
                displayValue: `${item.name} ($${item.price})`,
                name: item.name,
                price: item.price,
                image: item.image,
                pathNameForItemDetailsPage: item.pathNameForItemDetailsPage
            })));
            setComponents(fetchedComponents.map(componentCategory =>
                componentCategory.map(({
                                           id,
                                           name,
                                           price,
                                           image,
                                           pathNameForItemDetailsPage
                                       }) => ({
                    id, displayValue: `${name} ($${price})`, name, price, image, pathNameForItemDetailsPage
                }))));
            setFetchedComponents(fetchedComponents as ConfiguratorItem[][]);

            const initialCreateSpecs = {} as SpecsIds;
            const initialSelectedItems = {} as { [key in keyof SpecsIds]: ConfiguratorItem | null };

            componentTypes.forEach(({key}) => {
                initialCreateSpecs[key] = '';
                initialSelectedItems[key] = null;
            });

            setCreateSpecs(initialCreateSpecs);
            setSelectedItems(initialSelectedItems);

        } catch (error) {
            console.error('Failed to fetch components:', error);
        }
    };

    useEffect(() => {
        fetchComponents();
    }, []);

    const handlePreferencesSave = async (type: string, price: string) => {
        if (!user) {
            showLoginModal();
            return;
        }

        setIsLoading(true);

        try {
            const response = await axios.post<SpecsIds>('/api/configurator/gpt', [type, price]);
            const specsIds = response.data;

            const updatedSelectedItems = {...selectedItems};
            componentTypes.forEach(({key}) => {
                const item = fetchedComponents.flat().find(item => item.id === specsIds[key as keyof SpecsIds]);
                if (item) {
                    updatedSelectedItems[key as keyof SpecsIds] = item;
                }
            });

            setCreateSpecs(specsIds);
            setSelectedItems(updatedSelectedItems);
            setIsLoading(false);
            setIsGeneratedByGPT(true);
            toast.success('PC generated successfully!');
        } catch (error) {
            console.error('Failed to generate PC:', error);
            toast.error('Failed to generate PC!');
            setIsLoading(false);
        }
    };

    const handleGeneratePC = useCallback(() => {
        if (!user) {
            showLoginModal();
            return;
        }
        setIsPreferencesModalOpen(true);
    }, [user, showLoginModal]);

    const saveValues = useCallback(() => {
        const allValuesSelected = Object.values(createSpecs).every(value => value !== '');
        if (!user) {
            showLoginModal();
            return;
        }
        if (!allValuesSelected) {
            toast.error('Please choose all values before saving!', {
                position: 'top-center'
            });
            return;
        }

        setIsModalOpen(true);
    }, [createSpecs, user, showLoginModal]);

    const handleModalSave = (name: string, description: string) => {
        setHardwareSpec(prev => ({
            ...prev, name, description,
        }));
        saveHardwareSpec(name, description);
    };

    const saveHardwareSpec = async (name: string, description: string) => {
        const updatedHardwareSpec = {
            ...hardwareSpec, name, description,
        };

        const payload = {
            hardwareSpec: updatedHardwareSpec, specsIds: createSpecs,
        };

        if (user) {
            try {
                await axios.post(`/api/user-pcs/${user.id}`, payload).then(() => {
                    navigate('../user-pcs');
                });
            } catch (error) {
                console.error('Failed to add PC:', error);
            }
        }
    };

    const fetchMotherboards = async () => {
        if (createSpecs.cpuId && !isGeneratedByGPT) {
            try {
                const cpuSocketResponse = await axios.get<string>(`/api/hardware/cpu/socket/${createSpecs.cpuId}`);
                const cpuSocket = cpuSocketResponse.data;
                const response = await axios.get<{
                    [key: string]: string
                }>(`/api/configurator/motherboard/socket/${cpuSocket}`);
                const motherboardOptions: SelectOption[] = Object.entries(response.data).map(([id, name]) => ({
                    id, displayValue: name, name, price: '', image: '', pathNameForItemDetailsPage: ''
                }));
                setComponents((prevComponents) => prevComponents.map((componentCategory, index) => Object.keys(createSpecs)[index] === 'motherboardId' ? motherboardOptions : componentCategory));
            } catch (error) {
                console.error('Failed to fetch motherboards:', error);
            }
        }
    };

    useEffect(() => {
        fetchMotherboards();
    }, [createSpecs.cpuId, isGeneratedByGPT]);

    const fetchPowerSupplies = async () => {
        if (createSpecs.powerSupplyId && !isGeneratedByGPT) {
            try {
                const payload: SpecsIdsForEnergyConsumption = {
                    cpuId: createSpecs.cpuId,
                    gpuId: createSpecs.gpuId,
                    motherboardId: createSpecs.motherboardId,
                    ramId: createSpecs.ramId,
                    ssdId: createSpecs.ssdId,
                    hddId: createSpecs.hddId,
                };

                const response = await axios.post<{
                    [key: string]: string
                }>(`/api/pc/configuration/calculate-energy-consumption`, payload);

                const powerSupplyOptions: SelectOption[] = Object.entries(response.data).map(([id, name]) => ({
                    id, displayValue: name, name, price: '', image: '', pathNameForItemDetailsPage: ''
                }));

                setComponents((prevComponents) => prevComponents.map((componentCategory, index) => Object.keys(createSpecs)[index] === 'powerSupplyId' ? powerSupplyOptions : componentCategory));
            } catch (error) {
                console.error('Failed to fetch power supplies!:', error);
            }
        }
    };

    useEffect(() => {
        fetchPowerSupplies();
    }, [createSpecs.cpuId, createSpecs.gpuId, createSpecs.motherboardId, createSpecs.ramId, createSpecs.ssdId, createSpecs.hddId, isGeneratedByGPT]);

    const handleSelectChange = useCallback((e: ChangeEvent<HTMLSelectElement>, componentType: keyof SpecsIds) => {
        const {value} = e.target;
        if (!user) {
            showLoginModal();
            return;
        }
        setCreateSpecs(prev => ({...prev, [componentType]: value}));
        const selectedComponentData = components[componentTypes.findIndex(ct => ct.key === componentType)]?.find(item => item.id === value);
        if (selectedComponentData) {
            const selectedComponentItem = fetchedComponents[componentTypes.findIndex(ct => ct.key === componentType)]?.find(item => item.id === value);
            if (selectedComponentItem) {
                setSelectedItems(prev => ({
                    ...prev, [componentType]: {
                        id: selectedComponentItem.id,
                        name: selectedComponentItem.name,
                        price: selectedComponentItem.price,
                        image: selectedComponentItem.image,
                        pathNameForItemDetailsPage: selectedComponentItem.pathNameForItemDetailsPage
                    }
                }));
            }
        }
    }, [components, fetchedComponents, user, showLoginModal]);

    const handleHashChange = useCallback(() => {
        const element = document.getElementById(window.location.hash.replace('#', ''));
        if (element) {
            const headerOffset = 80;
            const elementPosition = element.getBoundingClientRect().top;
            const offsetPosition = elementPosition + window.scrollY - headerOffset;

            window.scrollTo({
                top: offsetPosition, behavior: "smooth",
            });
        }
    }, []);

    useEffect(() => {
        window.addEventListener('hashchange', handleHashChange, false);

        return () => {
            window.removeEventListener('hashchange', handleHashChange, false);
        };
    }, [handleHashChange]);

    return (<>
        <main className="main-container">
            <section className="left-section">
                <h2>Hardware</h2>
                <ul>
                    {componentTypes.map(({key, icon, text}, index) => (
                        <li className={`left-section__item${index === 1 ? " left-section__item--active" : ""}`}
                            key={key}>
                            <a href={`#${key}`}>
                                <span className="left-section__icon">
                                    <img src={icon} alt={text} className="left-section__img"/>
                                </span>
                                <span className="left-section__text">{text}</span>
                            </a>
                        </li>))}
                </ul>
            </section>

            <article className="main-section">
                {componentTypes.map(({key, text, defaultImage, path}) => (<div id={key} key={key}>
                    <ItemDetails
                        title={text}
                        selectedItem={selectedItems[key]}
                        components={components[componentTypes.findIndex(ct => ct.key === key)] || []}
                        createSpecs={createSpecs}
                        handleSelectChange={handleSelectChange}
                        componentType={key}
                        defaultImage={defaultImage}
                        path={path}
                    />
                </div>))}

                <button className="pc-configurator__submit" onClick={saveValues}>
                    Submit Configuration
                </button>
            </article>

            <section className="right-section">
                <div className="right-section__price">
                    <h2>Total Price: ${totalPrice.toFixed(2)}</h2>
                </div>
                <h2 className="right-section__title">Do you have a problem creating your own PC?</h2>
                <p className="right-section__text">Ask ChatGPT to create the best PC for you.</p>
                <div className="right-section__icon">
                    <img src={chatGptIcon} alt="ChatGPT Icon" className="right-section__img"/>
                </div>
                <button className="right-section__button" onClick={handleGeneratePC} disabled={isLoading}>
                    {isLoading ? 'Generating...' : 'Generate PC for me!!!'}
                </button>
            </section>
        </main>

        <LoginModal isOpen={isLoginModalOpen} onLogin={handleLogin} onClose={hideLoginModal}/>
        <ToastContainer position="top-center"/>
        <CreatePCModal
            isOpen={isModalOpen}
            onClose={() => setIsModalOpen(false)}
            onSave={handleModalSave}
        />
        <PreferencesModal
            isOpen={isPreferencesModalOpen}
            onClose={() => setIsPreferencesModalOpen(false)}
            onSave={handlePreferencesSave}
        />
    </>);
}
