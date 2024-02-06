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
                <Route path="/hardware/power-supply" element={<PowerSupplyPage/>}/>
                <Route path="/hardware/pc-case" element={<PcCasePage/>}/>

                <Route path="/services" element={<ServicesPage/>}/>
                <Route path="/account" element={<AccountPage/>}/>
                <Route path="/*" element={<NoMatch/>}/>
            </Routes>
            <FooterBar/>
        </BrowserRouter>
    )
}