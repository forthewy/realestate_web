import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

export default function AdminHeader() {
    // 이동
    const navigate = useNavigate();

    // 로그인
    const { isLogin, logout, role } = useAuth();

    // 로그 아웃
    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    const linkStyle =
        "px-3 py-2 rounded-lg transition-colors";

    return (
        <header className="z-50  border-b border-gray bg-surface sticky justify-between items-center w-full top-0 left-0 right-0 h-16 flex">
            <div className="shrink-0 px-6 text-2xl font-bold text-primary">
                <Link to="/">RealEstate</Link>
            </div>
            <nav className="flex shrink-0 gap-8 mx-auto ml-8 text-2xl font-medium">
                <Link
                    to="/"
                    className={linkStyle}>
                    관리자 홈
                </Link>
                <Link
                     to="/admin/users"
                    className={linkStyle}>
                    회원관리
                </Link>
                <Link
                    to="/admin/import"
                    className={linkStyle}>
                    데이터 Import
                </Link>
                {/* 로그인 */}
                {isLogin ? (
                    <button onClick={handleLogout}>로그아웃</button>
                ) : (
                    <Link to="/login" className={linkStyle}>로그인</Link>
                )}
            </nav>
        </header>
    )
}