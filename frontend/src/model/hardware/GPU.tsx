import {HardwareSpec} from "./HardwareSpec.tsx";

export interface GPU {
    id: string;
    hardwareSpec: HardwareSpec;
    energyConsumption: number;
    gpuPhotos?: string[];
}
