import { BrowserRouter, Routes, Route } from "react-router-dom";

import Dashboard from "../pages/Dashboard";
import Map from "../pages/Map";
// import Transaction from "../pages/Transaction";
import Settings from "../pages/Settings";

export default function Router() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/map" element={<Map />} />
                <Route path="/settings" element={<Settings />} />
                {/* 이후 
                    <Route path="/transaction" element={<Transaction />} />
                    <Route path="/about" element={<About />} />
                */}
            </Routes>
        </BrowserRouter>
    );
}