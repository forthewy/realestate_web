import { useAuth } from "../../contexts/AuthContext";
import { Navigate } from "react-router-dom";
import type { ReactNode } from "react";


export default function AdminRoute({
    children,
}: {
    children: React.ReactNode
}) {
    // 로그인, 관리자 
    const { isLogin, role } = useAuth();

    if (!isLogin) {
        return <Navigate to="/login" replace />
    }
    if (role !== "ADMIN") {
        return <Navigate to="/" replace />
    }
    return children;
}