import {HardwareSpec} from "./HardwareSpec.tsx";

export interface CPU {
    id: string;
    hardwareSpec: HardwareSpec;
    performance: number;
    energyConsumption: number;
    cpuPhotos?: string[];
}
