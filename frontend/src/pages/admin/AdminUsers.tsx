import { useEffect, useState } from "react";
import type { User } from "../../types/user";
import { apiFetch } from "../../services/api";
import UserTable from "../../components/admin/UserTable";

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
        <div>
            <h1 className="mb-6 text-3xl font-bold text-primary">
                회원관리
            </h1>
            <form onSubmit={searchUser}>
                <input
                    className="rounded-lg border border-gray px-4 py-3 outline-none focus:ring-1"
                    type="text"
                    value={searchKeyword}
                    placeholder="아이디 혹은 이름 검색"
                    onChange={(e) => setSearchKeyword(e.target.value)}
                />
                <button
                    type="submit"
                    className="rounded-lg bg-primary px-4 text-white"
                >
                    검색
                </button>
            </form>
            <UserTable users={users} />
        </div>
    )
}