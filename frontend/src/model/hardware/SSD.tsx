import {HardwareSpec} from "./HardwareSpec.tsx";

export interface SSD {
    id: string;
    hardwareSpec: HardwareSpec;
    capacity: number;
    energyConsumption: number;
    ssdPhotos?: string[];
}
