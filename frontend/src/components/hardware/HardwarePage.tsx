import { Link } from "react-router-dom";

const hardwareItems = [
    { name: 'CPU', image: 'cpu.png', path: '/hardware/cpu' },
    { name: 'GPU', image: 'gpu.png', path: '/hardware/gpu' },
    { name: 'Motherboard', image: 'motherboard.png', path: '/hardware/motherboard' },
    { name: 'RAM', image: 'ram.png', path: '/hardware/ram' },
    { name: 'SSD', image: 'ssd.png', path: '/hardware/ssd' },
    { name: 'HDD', image: 'hdd.png', path: '/hardware/hdd' },
    { name: 'Power Supply', image: 'psu.png', path: '/hardware/power-supply' },
    { name: 'PC Case', image: 'pcCase.png', path: '/hardware/pc-case' },
];

export default function HardwarePage() {
    return (
        <section className="hardware-section">
            {hardwareItems.map((item) => (
                <Link to={item.path} className="hardware-item" key={item.name}>
                    <img src={`src/assets/${item.image}`} alt={`${item.name}`} className="hardware-item__image"/>
                    <span className="hardware-item__name">{item.name}</span>
                </Link>
            ))}
        </section>

    );
}
