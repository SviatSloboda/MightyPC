import {BrowserRouter, Route, Routes} from "react-router-dom";
import NavigationBar from "./components/mainpage/NavigationBar.tsx";
import FooterBar from "./components/mainpage/FooterBar.tsx";
import GamingPCsPage from "./components/pc/PcsPage.tsx";
import HardwarePage from "./components/hardware/HardwarePage.tsx";
import ProfilePage from "./components/shop/ProfilePage.tsx";
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
import BasketPage from "./components/shop/BasketPage.tsx";
import OrderPage from "./components/shop/OrderPage.tsx";
import PcCharacteristics from "./components/pc/PcCharacteristics.tsx";
import WorkstationsPage from "./components/workstation/WorkstationsPage.tsx";
import WorkstationsCharacteristicsPage from "./components/workstation/WorkstationCharacteristicsPage.tsx";
import UserPcsPage from "./components/configurator/UserPcsPage.tsx";
import Confi from "./components/configurator/Confi.tsx";
import LoginPage from "./components/login/LoginPage.tsx";


export default function App() {

    return (<BrowserRouter>
        <NavigationBar/>

        <Routes>
            <Route path="/" element={<GamingPCsPage/>}/>
            <Route path="/*" element={<NoMatch/>}/>

            <Route path="/configurator" element={<Confi/>}/>

            <Route path="/pc" element={<GamingPCsPage/>}/>
            <Route path="/pc/:id" element={<PcCharacteristics/>}/>

            <Route path="/workstation" element={<WorkstationsPage/>}/>
            <Route path="/workstation/:id" element={<WorkstationsCharacteristicsPage/>}/>

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

            <Route path="/basket" element={<BasketPage/>}/>
            <Route path="/order" element={<OrderPage/>}/>

            <Route path="/user" element={<ProfilePage/>}/>
            <Route path="/user/login" element={<LoginPage/>}/>

            <Route path="/user-pcs" element={<UserPcsPage/>}/>

        </Routes>
        <FooterBar/>
    </BrowserRouter>)
}