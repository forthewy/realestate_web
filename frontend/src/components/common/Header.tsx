import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

export default function Header() {
    //이동
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
        <header className="sticky top-0 z-50 flex h-16 w-full items-center justify-between border-b border-gray bg-surface">
            <div className="shrink-0 px-6 text-2xl font-bold text-primary">
                <Link to="/">RealEstate</Link>
            </div>
            <nav className="mx-auto ml-8 flex shrink-0 gap-8 text-2xl font-medium">
                    <Link
                        to="/"
                        className={linkStyle}>
                        지도
                    </Link>
                    <Link to="/transactions" className={linkStyle}>
                        실거래
                    </Link>
                    <Link to="/mypage" className={linkStyle}>
                        내 정보
                    </Link>
                    {/* 관리자 로그인시에만 */}
                    {role === "ADMIN" && (
                        <Link to="/admin" className={linkStyle}>
                            관리자
                        </Link>
                    )}
                    {/* 로그인 */}
                    {isLogin ? (
                        <button onClick={handleLogout} className={linkStyle}>로그아웃</button>
                    ) : (
                        <Link to="/login" className={linkStyle}>로그인</Link>
                    )}
            </nav>
        </header>
    );
}
