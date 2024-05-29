import {HardwareSpec} from "./HardwareSpec.tsx";

export interface HDD {
    id: string;
    hardwareSpec: HardwareSpec;
    capacity: number;
    energyConsumption: number;
    hddPhotos?: string[];
}
