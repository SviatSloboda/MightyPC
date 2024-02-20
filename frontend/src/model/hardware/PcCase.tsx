import {HardwareSpec} from "./HardwareSpec.tsx";

export interface PcCase {
    id: string;
    hardwareSpec: HardwareSpec;
    dimensions: string;
    pcCasePhotos?: string[];
}
