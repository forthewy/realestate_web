import { NavLink } from "react-router-dom";

export default function AdminSidebar() {

const linkStyle = ({ isActive }: { isActive: boolean }) =>
    `w-full px-4 py-3 hover:bg-primary/10 hover:text-primary ${
        isActive ? "bg-primary text-white" : ""
    }`;

    return (
        <aside className="w-40 shrink-0 border-r border-gray py-4">
            <h2 className="px-4 mb-4 text-lg font-bold text-primary">
                관리자
            </h2>
            <nav className="text-xl flex flex-col">
                <NavLink
                    to="/admin"
                    end
                    className={linkStyle}
                >
                    관리자 홈
                </NavLink>
                <NavLink
                    to="/admin/users"
                    className={linkStyle}
                >
                    회원 관리
                </NavLink>
                <NavLink
                    to="/admin/import"
                    className={linkStyle}
                >
                    데이터 Import
                </NavLink>
                <NavLink
                    to="/admin/settings"
                    className={linkStyle}>
                    시스템 설정
                </NavLink>
            </nav>
        </aside>
    );
}