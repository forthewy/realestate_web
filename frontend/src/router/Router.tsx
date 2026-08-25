import { BrowserRouter, Routes, Route } from "react-router-dom";

import Layout from "../components/common/Layout";
import Dashboard from "../dashboard/pages/Dashboard";
import LogIn from "../auth/pages/LogIn";
import Register from "../auth/pages/Register";
import ProtectedRoute from "../auth/components/ProtectedRoute";
import MyPage from "../mypage/pages/MyPage";
import AdminRoute from "../auth/components/AdminRoute";
import AdminLayout from "../admin/components/AdminLayout";
import AdminHome from "../admin/pages/AdminHome";
import AdminUsers from "../admin/pages/AdminUsers";
import AdminImport from "../admin/pages/AdminImport";
import TransactionMap from "../dashboard/pages/TransactionMap";

export default function Router() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<Layout />}>
                    {/* 누구나 접근 */}
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/login" element={<LogIn />} />
                    <Route path="/transaction" element={<TransactionMap />} />
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
                        <Route path="import" element={<AdminImport />} />
                        {/* <Route path="settings" element={<Settings />} /> */}
                    </Route>    
            </Routes>
        </BrowserRouter>
    );
}
