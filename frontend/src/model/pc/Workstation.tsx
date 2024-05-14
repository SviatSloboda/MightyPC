import {HardwareSpec} from "./hardware/HardwareSpec.tsx";
import {SpecsIds} from "./SpecsIds.tsx";
import {SpecsNames} from "./SpecsNames.tsx";

export interface Workstation {
    id: string;
    hardwareSpec: HardwareSpec;
    specsIds: SpecsIds;
    specsNames: SpecsNames;
    cpuNumber: number,
    gpuNumber: number,
    energyConsumption: number;
    photos?: string[];
}
