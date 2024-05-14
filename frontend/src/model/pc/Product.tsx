import {HardwareSpec} from "./hardware/HardwareSpec.tsx";

export interface Product {
    id: string;
    hardwareSpec: HardwareSpec;
}