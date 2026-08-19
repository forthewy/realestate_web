import { useAuth } from "../../contexts/AuthContext";
import { Navigate } from "react-router-dom";
import type { ReactNode } from "react";     


export default function ProtectedRoute({
    children,
}:{
    children: React.ReactNode
}) {
    // 로그인
    const { isLogin }  = useAuth();

    if (!isLogin) {
        return <Navigate to="/login" replace />
    }

    return children;
}