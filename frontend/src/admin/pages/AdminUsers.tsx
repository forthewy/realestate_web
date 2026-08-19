import { useEffect, useState } from "react";
import type { User } from "../types/user";
import { apiFetch } from "../../services/api";
import UserTable from "../components/UserTable";

export default function AdminUsers() {
    const [searchKeyword, setSearchKeyword] = useState("");
    const [users, setUsers] = useState<User[]>([]);


    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async () => {

        const response = await apiFetch("/api/admin/users");

        if (!response.ok) {
            alert("회원 정보를 불러오지 못했습니다.");
            return;
        }

        const data = await response.json();

        setUsers(data);
    };

    const searchUser = async (
        e: React.FormEvent<HTMLFormElement>
    ) => {
        e.preventDefault();

        // API 호출
    };

    return (
        <div className="p-8">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-primary">
                    회원관리
                </h1>
            </div>
            <form onSubmit={searchUser} className="mb-8 flex">
                <input
                    className="w-full max-w-md border border-gray px-4 py-3"
                    type="text"
                    value={searchKeyword}
                    placeholder="아이디 혹은 이름 검색"
                    onChange={(e) => setSearchKeyword(e.target.value)}
                />
                <button
                    type="submit"
                    className="bg-primary px-4 text-white text-m font-semibold"
                >
                    검색
                </button>
            </form>
            <UserTable users={users} />
        </div>
    )
}