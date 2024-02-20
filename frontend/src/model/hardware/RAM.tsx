import {HardwareSpec} from "./HardwareSpec.tsx";

export interface RAM {
    id: string;
    hardwareSpec: HardwareSpec;
    type: string;
    energyConsumption: number;
    memorySize: number;
    ramPhotos?: string[];
}
