import {HardwareSpec} from "./HardwareSpec.tsx";

export interface CPU {
    hardwareSpec: HardwareSpec;
    performance: number;
    energyConsumption: number;
}
