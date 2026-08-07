import { BrowserRouter, Routes, Route } from "react-router-dom";

import Layout from "../components/layout/Layout";
import Dashboard from "../pages/Dashboard";
import Transaction from "../pages/Transaction";
import Settings from "../pages/Settings";
import LogIn from "../pages/LogIn";

export default function Router() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<Layout />}>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/login" element={<LogIn />} />
                    <Route path="/transaction" element={<Transaction />} />
                    <Route path="/settings" element={<Settings />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}