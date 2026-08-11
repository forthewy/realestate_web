import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../../contexts/AuthContext";

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

    // 검색
    const [searchWord, setSearchWord] = useState("");

    const linkStyle =
        "px-3 py-2 rounded-lg transition-colors";

    return (
        <header className="z-50  border-b border-gray bg-surface sticky justify-between items-center w-full top-0 left-0 right-0 h-16 flex">
            <div className="shrink-0 px-6 text-2xl font-bold text-primary">
                logo
            </div>
            <nav className="flex shrink-0 gap-8 mx-auto ml-8 text-2xl font-medium">
                <Link
                    to="/"
                    className={linkStyle}>
                    대시보드
                </Link>
                <Link
                    to="/transaction"
                    className={linkStyle}>
                    실거래
                </Link>
                <Link
                    to="/about"
                    className={linkStyle}>
                    About
                </Link>
                <Link
                    to="/mypage"
                    className={linkStyle}>
                    내 정보
                </Link>
                {/* 관리자 로그인시에만 */}
                {role === "ADMIN" && (
                    <Link to="/admin">
                        관리자
                    </Link>
                )}
                {/* 로그인 */}
                {isLogin ? (
                    <button onClick={handleLogout}>로그아웃</button>
                ) : (
                    <Link to="/login" className={linkStyle}>로그인</Link>
                )}
            </nav>
            {/* 검색 */}
            <div className="flex items-center gap-2 px-6">
                <input
                    type="search"
                    value={searchWord}
                    onChange={(e) => setSearchWord(e.target.value)}
                    placeholder="지역을 검색하세요"
                    className="
                        w-48
                        rounded-lg
                        border border-gray
                        px-4 py-2
                        outline-none
                        focus:ring-1
                        focus:ring-grey
                    "
                />
                <button
                    className="
                        rounded-lg
                        bg-primary
                        px-4 py-2
                        font-medium
                        text-white
                    "
                >검색
                </button>
            </div>

        </header>
    )
}