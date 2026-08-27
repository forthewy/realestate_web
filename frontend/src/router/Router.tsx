import { BrowserRouter, Route, Routes } from "react-router-dom";

import AdminLayout from "../admin/components/AdminLayout";
import AdminHome from "../admin/pages/AdminHome";
import AdminImport from "../admin/pages/AdminImport";
import AdminUsers from "../admin/pages/AdminUsers";
import AdminRoute from "../auth/components/AdminRoute";
import ProtectedRoute from "../auth/components/ProtectedRoute";
import LogIn from "../auth/pages/LogIn";
import Register from "../auth/pages/Register";
import Layout from "../components/common/Layout";
import TransactionMap from "../map/pages/TransactionMap";
import MyPage from "../mypage/pages/MyPage";
import TransactionsPage from "../transaction/pages/TransactionsPage";

export default function Router() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<Layout />}>
                    {/* 누구나 접근 */}
                    <Route path="/" element={<TransactionMap />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/login" element={<LogIn />} />
                    <Route path="/transactions" element={<TransactionsPage />} />
                    {/* 로그인 사용자 */}
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
                    </Route>    
            </Routes>
        </BrowserRouter>
    );
}
