import {Link} from "react-router-dom";
import cpu from "../../assets/hardware/cpu.png";
import gpu from "../../assets/hardware/gpu.png";
import motherboard from "../../assets/hardware/motherboard.png";
import hdd from "../../assets/hardware/hdd.png";
import ram from "../../assets/hardware/ram.png";
import psu from "../../assets/hardware/psu.png";
import ssd from "../../assets/hardware/ssd.png";
import pcCase from "../../assets/hardware/pcCase.png";

const hardwareItems = [{name: 'CPU', image: cpu, path: '/hardware/cpu'}, {
    name: 'GPU', image: gpu, path: '/hardware/gpu'
}, {name: 'Motherboard', image: motherboard, path: '/hardware/motherboard'}, {
    name: 'RAM', image: ram, path: '/hardware/ram'
}, {name: 'SSD', image: ssd, path: '/hardware/ssd'}, {
    name: 'HDD', image: hdd, path: '/hardware/hdd'
}, {name: 'Power Supply', image: psu, path: '/hardware/psu'}, {
    name: 'PC Case', image: pcCase, path: '/hardware/pc-case'
},];

export default function HardwareNavigationPage() {
    return (<section className="hardware-section">
            {hardwareItems.map((item) => (<Link to={item.path} className="hardware-item" key={item.name}>
                <img src={`${item.image}`} alt={`${item.name}`} className="hardware-item__image"/>
                <span className="hardware-item__name">{item.name}</span>
            </Link>))}
        </section>

    );
}
