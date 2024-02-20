import {HardwareSpec} from "./HardwareSpec.tsx";

export interface Motherboard {
    id: string;
    hardwareSpec: HardwareSpec;
    energyConsumption: number;
    graphicCardCompatibility: string[];
    processorCompatibility: string[];
    motherboardPhotos?: string[];
}
