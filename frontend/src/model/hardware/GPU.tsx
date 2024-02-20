import {HardwareSpec} from "./HardwareSpec.tsx";

export interface GPU {
    id: string;
    hardwareSpec: HardwareSpec;
    performance: number;
    energyConsumption: number;
    gpuPhotos?: string[];
}