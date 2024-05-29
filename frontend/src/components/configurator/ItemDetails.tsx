import React, {useEffect} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import {ConfiguratorItem} from "../../model/configurator/ConfiguratorItem.tsx";

import selectImage from "../../assets/shop/select.png";

interface SelectOption {
    id: string;
    displayValue: string;
}

interface ItemDetailsProps {
    title: string;
    selectedItem: ConfiguratorItem | null;
    components: SelectOption[];
    createSpecs: { [key: string]: string };
    handleSelectChange: (e: React.ChangeEvent<HTMLSelectElement>, componentType: string) => void;
    componentType: string;
    defaultImage: string;
    path: string;
}

export default function ItemDetails({
                                        title,
                                        selectedItem,
                                        components,
                                        createSpecs,
                                        handleSelectChange,
                                        componentType,
                                        defaultImage,
                                        path
                                    }: Readonly<ItemDetailsProps>) {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const hash = location.hash;
        if (hash) {
            const targetId = hash.slice(1);
            const targetElement = document.getElementById(targetId);
            if (targetElement) {
                const navHeight = document.querySelector('.nav')?.clientHeight ?? 0;
                const scrollToPosition = targetElement.offsetTop - navHeight;
                window.scrollTo({
                    top: scrollToPosition, behavior: 'smooth'
                });
            }
        }
    }, [location]);

    return (<div className="item-details" id={componentType}>
        <h1 className="item-details__title">{title}</h1>
        <div className="item-details__characteristics">
            <div className="item-details__image">
                {selectedItem ? (<img
                    className="item-details__image--img"
                    src={selectedItem.image ? selectedItem.image : defaultImage}
                    alt={selectedItem.name}
                />) : (<img
                    className="item-details__image--img"
                    src={selectImage}
                    alt="Select an option"
                />)}
            </div>
            <div className="item-details__details">
                <h2 className="item-details__name">
                    {selectedItem ? selectedItem.name : "Please select a hardware component!"}
                </h2>
                <h2 className="item-details__price">
                    {selectedItem ? `$${selectedItem.price}` : "Please select a hardware component!"}
                </h2>
                <select
                    className="item-details__dropdown"
                    onChange={(e) => handleSelectChange(e, componentType)}
                    value={createSpecs[componentType]}
                >
                    <option value="">Select an option</option>
                    {components.map((option) => (<option key={option.id} value={option.id}>
                        {option.displayValue}
                    </option>))}
                </select>
                <button
                    className="item-details__details-button"
                    onClick={() => navigate(`${path}/${createSpecs[componentType]}`)}
                    disabled={!selectedItem}
                >
                    Specific details
                </button>
            </div>
        </div>
    </div>);
}