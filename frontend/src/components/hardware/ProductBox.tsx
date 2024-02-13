import React from 'react';
import { Product } from "../../model/hardware/Product.tsx";

interface ProductBoxProps {
    product: Product;
    imgSrc: string;
}

const ProductBox: React.FC<ProductBoxProps> = ({ product, imgSrc }) => (
    <div className="product-box">
        <img src={imgSrc} alt={product.hardwareSpec.name} className="product-box__image" />
        <h2 className="product-box__name">{product.hardwareSpec.name}</h2>
        <p className="product-box__description">{product.hardwareSpec.description}</p>
        <p className="product-box__price">${product.hardwareSpec.price}</p>
        <p className="product-box__rating">Rating: {product.hardwareSpec.rating} / 5</p>
        <div className="product-box__actions">
            <button className="product-box__button">Buy</button>
            <button className="product-box__button">Info</button>
        </div>
    </div>
);

export default ProductBox;
