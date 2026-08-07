import { Link } from "react-router-dom";

export default function Sidebar() {
  return (
    <aside>
      <nav className="text-xl flex flex-col">
        <Link to="/">대시보드</Link>
        <Link to="/transaction">실거래</Link>
        <Link to="/transaction">사용자</Link>
        <Link to="/settings">설정</Link>
        <Link to="">About</Link>
      </nav>
    </aside>
  );
}