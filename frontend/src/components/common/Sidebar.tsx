import { Link } from "react-router-dom";

export default function Sidebar() {
  const linkStyle =
    "w-full px-4 py-3 hover:bg-primary/10 hover:text-primary";

  return (
    <aside className="w-40 shrink-0 border-r border-gray py-4">
      <nav className="text-xl flex flex-col">
        <Link
          to="/"
          className={linkStyle}
        >지도
        </Link>
        <Link
          to="/transaction"
          className={linkStyle}>
          실거래
        </Link>
        <Link
          to="/mypage"
          className={linkStyle}>
          내 정보
        </Link>
      </nav>
    </aside>
  );
}
