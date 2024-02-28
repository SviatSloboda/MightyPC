import {HardwareSpec} from "./HardwareSpec.tsx";

export interface PSU {
    id: string;
    hardwareSpec: HardwareSpec;
    power: number;
    psuPhotos?: string[];
}
