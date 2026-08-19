import type { User } from "../types/user";


type UserTableProps = {
    users: User[];
};
const thStyle = "px-6 py-4 text-left text-m font-semibold";
const tdStyle = "px-6 py-4 text-m";

export default function UserTable({ users }: UserTableProps) {
    return (
        <table className="w-full">
            <thead className="bg-secondary/10">
                <tr>
                    <th className={thStyle}>이름</th>
                    <th className={thStyle}>아이디</th>
                    <th className={thStyle}>권한</th>
                    <th className={thStyle}>가입일</th>
                </tr>
            </thead>
            <tbody className="divide-y divide-gray/30">
                {users.map((user) => (
                    <tr key={user.id}>
                        <td className={`${tdStyle} font-medium`}>
                            {user.name}
                        </td>
                        <td className={tdStyle}>{user.username}</td>
                        <td className={tdStyle}>
                            <span className="rounded-full font-semibold">
                                {user.role}
                            </span>
                        </td>
                        <td className={tdStyle}>
                            {user.createdAt?.split("T")[0]}
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    )
}