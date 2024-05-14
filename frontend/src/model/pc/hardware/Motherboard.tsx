import {HardwareSpec} from "./HardwareSpec.tsx";

export interface Motherboard {
    id: string;
    hardwareSpec: HardwareSpec;
    energyConsumption: number;
    socket: string;
    motherboardPhotos?: string[];
}
