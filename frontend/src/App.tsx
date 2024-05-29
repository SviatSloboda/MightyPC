import {BrowserRouter, Route, Routes} from "react-router-dom";
import NavigationBar from "./components/mainpage/NavigationBar.tsx";
import FooterBar from "./components/mainpage/FooterBar.tsx";
import HardwareNavigationPage from "./components/hardware/HardwareNavigationPage.tsx";
import ProfilePage from "./components/shop/ProfilePage.tsx";
import NoMatch from "./components/mainpage/NoMatch.tsx";
import BasketPage from "./components/shop/BasketPage.tsx";
import OrderPage from "./components/shop/OrderPage.tsx";
import PcCharacteristics from "./components/pc/PcCharacteristics.tsx";
import WorkstationsCharacteristicsPage from "./components/pc/WorkstationCharacteristicsPage.tsx";
import UserPcsPage from "./components/configurator/UserPcsPage.tsx";
import ConfiguratorPage from "./components/configurator/ConfiguratorPage.tsx";
import LoginPage from "./components/login/LoginPage.tsx";
import {
    CpuPage,
    GpuPage,
    HddPage,
    MotherboardPage, PcCasePage, PcsPage,
    PowerSupplyPage,
    RamPage,
    SsdPage, WorkstationsPage
} from "./components/hardware/HardwarePages.tsx";

import {
    CpuCharacteristics,
    GpuCharacteristics, HddCharacteristics,
    MotherboardCharacteristics, PcCaseCharacteristics, PsuCharacteristics, RamCharacteristics, SsdCharacteristics
} from "./components/hardware/Characteristics.tsx";


export default function App() {

    return (<BrowserRouter>
        <NavigationBar/>

        <Routes>
            <Route path="/" element={<PcsPage/>}/>
            <Route path="/*" element={<NoMatch/>}/>

            <Route path="/configurator" element={<ConfiguratorPage/>}/>

            <Route path="/pc" element={<PcsPage/>}/>
            <Route path="/pc/:id" element={<PcCharacteristics/>}/>

            <Route path="/workstation" element={<WorkstationsPage/>}/>
            <Route path="/workstation/:id" element={<WorkstationsCharacteristicsPage/>}/>

            <Route path="/hardware" element={<HardwareNavigationPage/>}/>

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