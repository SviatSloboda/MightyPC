import {BrowserRouter, Route, Routes} from "react-router-dom";
import MainPage from "./components/mainpage/MainPage.tsx";
import ConfiguratorPage from "./components/configurator/ConfiguratorPage.tsx";
import NavigationBar from "./components/mainpage/NavigationBar.tsx";
import FooterBar from "./components/mainpage/FooterBar.tsx";
import GamingPCsPage from "./components/gamingpcs/GamingPCsPage.tsx";
import WorkstationsPage from "./components/workstations/WorkstationsPage.tsx";
import LaptopsPage from "./components/laptops/LaptopsPage.tsx";
import HardwarePage from "./components/hardware/HardwarePage.tsx";
import ServicesPage from "./components/services/ServicesPage.tsx";
import AccountPage from "./components/account/AccountPage.tsx";
import CpuPage from "./components/hardware/CpuPage.tsx";
import NoMatch from "./components/mainpage/NoMatch.tsx";
import GpuPage from "./components/hardware/GpuPage.tsx";
import MotherboardPage from "./components/hardware/MotherboardPage.tsx";
import RamPage from "./components/hardware/RamPage.tsx";
import SsdPage from "./components/hardware/SsdPage.tsx";
import HddPage from "./components/hardware/HddPage.tsx";
import PowerSupplyPage from "./components/hardware/PowerSupplyPage.tsx";
import PcCasePage from "./components/hardware/PcCasePage.tsx";
import CpuCharacteristics from "./components/hardware/characteristicsPage/CpuCharacteristics.tsx";
import MotherboardCharacteristics from "./components/hardware/characteristicsPage/MotherboardCharacteristics.tsx";
import GpuCharacteristics from "./components/hardware/characteristicsPage/GpuCharacteristics.tsx";
import RamCharacteristics from "./components/hardware/characteristicsPage/RamCharacteristics.tsx";
import SsdCharacteristics from "./components/hardware/characteristicsPage/SsdCharacteristics.tsx";
import HddCharacteristics from "./components/hardware/characteristicsPage/HddCharacteristics.tsx";
import PsuCharacteristics from "./components/hardware/characteristicsPage/PsuCharacteristics.tsx";
import PcCaseCharacteristics from "./components/hardware/characteristicsPage/PcCaseCharacteristics.tsx";

export default function App() {
    return (
        <BrowserRouter>
            <NavigationBar/>

            <Routes>
                <Route path="/" element={<MainPage/>}/>
                <Route path="/configurator" element={<ConfiguratorPage/>}/>
                <Route path="/gaming-pcs" element={<GamingPCsPage/>}/>
                <Route path="/workstations" element={<WorkstationsPage/>}/>
                <Route path="/laptops" element={<LaptopsPage/>}/>
                <Route path="/hardware" element={<HardwarePage/>}/>

                <Route path="/hardware/cpu" element={<CpuPage/>}/>
                <Route path="/hardware/gpu" element={<GpuPage/>}/>
                <Route path="/hardware/motherboard" element={<MotherboardPage/>}/>
                <Route path="/hardware/ram" element={<RamPage/>}/>
                <Route path="/hardware/ssd" element={<SsdPage/>}/>
                <Route path="/hardware/hdd" element={<HddPage/>}/>
                <Route path="/hardware/psu" element={<PowerSupplyPage/>}/>
                <Route path="/hardware/pc-case" element={<PcCasePage/>}/>

                <Route path="/hardware/cpu/:id" element={<CpuCharacteristics/>}/>
                <Route path="/hardware/gpu/:id" element={<GpuCharacteristics/>}/>
                <Route path="/hardware/motherboard/:id" element={<MotherboardCharacteristics/>}/>
                <Route path="/hardware/ram/:id" element={<RamCharacteristics/>}/>
                <Route path="/hardware/ssd/:id" element={<SsdCharacteristics/>}/>
                <Route path="/hardware/hdd/:id" element={<HddCharacteristics/>}/>
                <Route path="/hardware/psu/:id" element={<PsuCharacteristics/>}/>
                <Route path="/hardware/pc-case/:id" element={<PcCaseCharacteristics/>}/>

                <Route path="/services" element={<ServicesPage/>}/>
                <Route path="/account" element={<AccountPage/>}/>
                <Route path="/*" element={<NoMatch/>}/>
            </Routes>
            <FooterBar/>
        </BrowserRouter>
    )
}