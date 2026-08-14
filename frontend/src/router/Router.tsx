import { BrowserRouter, Routes, Route } from "react-router-dom";

import Layout from "../components/layout/Layout";
import Dashboard from "../pages/Dashboard";
import Transaction from "../pages/Transaction";
import LogIn from "../pages/LogIn";
import Register from "../pages/Register";
import ProtectedRoute from "../components/auth/ProtectedRoute";
import MyPage from "../pages/MyPage";
import AdminRoute from "../components/auth/AdminRoute";
import AdminLayout from "../components/layout/admin/AdminLayout";
import AdminHome from "../pages/admin/AdminHome";
import AdminUsers from "../pages/admin/AdminUsers";

export default function Router() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<Layout />}>
                    {/* 누구나 접근 */}
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/login" element={<LogIn />} />
                    <Route path="/transaction" element={<Transaction />} />
                    {/* 로그인 사용자 */}Z
                    <Route
                        path="/mypage"
                        element={
                            <ProtectedRoute>
                                <MyPage />
                            </ProtectedRoute>
                        }
                    />
                </Route>
                 {/* 관리자 */}
                    <Route
                        path="/admin"
                        element={
                            <AdminRoute>
                                <AdminLayout />
                            </AdminRoute>
                        }
                    >
                        <Route index element={<AdminHome />} />
                        <Route path="users" element={<AdminUsers />} />
                        {/* <Route path="import" element={<Import />} />
                        <Route path="settings" element={<Settings />} /> */}
                    </Route>    
            </Routes>
        </BrowserRouter>
    );
}
