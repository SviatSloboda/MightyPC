import {HardwareSpec} from "./HardwareSpec.tsx";

export interface CPU {
    id: string;
    hardwareSpec: HardwareSpec;
    energyConsumption: number;
    socket: string;
    cpuPhotos?: string[];
}
