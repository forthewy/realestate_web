import { useState } from "react";
import { Link } from "react-router-dom";

export default function Header() {
    const [searchWord, setSearchWord] = useState("");

    return (
        <header className="sticky justify-between items-center w-full top-0 left-0 right-0 h-16 flex">
            <div className="px-6 text-xl">
                logo
            </div>
            <nav className="flex gap-8 mx-auto ml-8 text-xl">
                <Link to="/">대시보드</Link>
                <Link to="/map">지도</Link>
                <Link to="/transaction">실거래</Link>
                <Link to="/settings">설정</Link>
                <button>About</button>
            </nav>
            {/* 검색 */}
            <div className="px-6">
                <input 
                    type="search" 
                    value={searchWord} 
                    onChange={(e) => setSearchWord(e.target.value)}
                    placeholder="지역을 검색하세요" 
                    className="border"
                />
                <button>검색</button>
            </div>
        </header>
    )
}