import React from 'react';
import {Product} from "../../model/pc/Product.tsx";
import Rating from "./characteristicsPage/Rating.tsx";

interface ProductBoxProps {
    product: Product;
    imgSrc: string;
    toCharacteristicsPage: () => void;
    onAddToBasket: () => void;
}

const ProductBox: React.FC<ProductBoxProps> = ({product, imgSrc, toCharacteristicsPage, onAddToBasket}) => (
    <div className="product-box">
        <img src={imgSrc} alt={product.hardwareSpec.name} className="product-box__image"/>
        <h2 className="product-box__name">{product.hardwareSpec.name}</h2>
        <div className="product-characteristics__info">
            <Rating rating={product?.hardwareSpec.rating ?? 0}/>
            <span className="product-characteristics__rating">{product?.hardwareSpec.rating}/5</span>
        </div>
        <p className="product-box__description">{product.hardwareSpec.description}</p>
        <p className="product-box__price">${product.hardwareSpec.price}</p>
        <div className="product-box__actions">
            <button className="product-box__button" onClick={onAddToBasket}>Buy</button>
            {/* Use onAddToBasket here */}
            <button className="product-box__button" onClick={toCharacteristicsPage}>Info</button>
        </div>
    </div>
);

export default ProductBox;
