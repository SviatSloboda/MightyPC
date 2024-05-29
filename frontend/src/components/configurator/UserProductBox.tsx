import {Product} from "../../model/pc/Product.tsx";

interface ProductBoxProps {
    product: Product;
    imgSrc: string;
    toCharacteristicsPage: () => void;
    onAddToBasket: () => void;
}

export default function UserProductBox(props: Readonly<ProductBoxProps>) {
    return (<div className="product-box">
        <img src={props.imgSrc} alt={props.product.hardwareSpec.name} className="product-box__image"/>
        <h2 className="product-box__name">{props.product.hardwareSpec.name}</h2>
        <p className="product-box__description">{props.product.hardwareSpec.description}</p>
        <p className="product-box__price">${props.product.hardwareSpec.price}</p>
        <div className="product-box__actions">
            <button className="product-box__button" onClick={props.onAddToBasket}>Buy</button>
            <button className="product-box__button" onClick={props.toCharacteristicsPage}>Info</button>
        </div>
    </div>);
}

