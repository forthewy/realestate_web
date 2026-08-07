import { useState, useEffect } from "react";
import { Link } from "react-router-dom";

export default function Header() {
    const [searchWord, setSearchWord] = useState("");

    return (
        <header className="sticky justify-between items-center w-full top-0 left-0 right-0 h-16 flex">
            <div className="px-6 text-xl">
                logo
            </div>
            <nav className="flex gap-8 mx-auto ml-8 text-2xl font-medium">
                <Link
                    to="/"
                    className="transition-colors hover:text-primary"
                >
                    대시보드
                </Link>

                <Link
                    to="/transaction"
                    className="transition-colors hover:text-primary"
                >
                    실거래
                </Link>

                <Link
                    to="/user"
                    className="transition-colors hover:text-primary"
                >
                    사용자
                </Link>

                <Link
                    to="/settings"
                    className="transition-colors hover:text-primary"
                >
                    설정
                </Link>

                <Link
                    to="/about"
                    className="transition-colors hover:text-primary"
                >
                    About
                </Link>
                 <Link
                    to="/login"
                    className="transition-colors hover:text-primary"
                >
                    로그인
                </Link>
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