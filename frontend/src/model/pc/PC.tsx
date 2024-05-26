import {HardwareSpec} from "../hardware/HardwareSpec.tsx";
import {SpecsNames} from "./SpecsNames.tsx";
import {SpecsIds} from "./SpecsIds.tsx";

export interface PC {
    id: string;
    hardwareSpec: HardwareSpec;
    specsIds: SpecsIds;
    specsNames: SpecsNames;
    energyConsumption: number;
    photos?: string[];
}
