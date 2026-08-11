import type { User } from "../../types/user";


type UserTableProps = {
    users: User[];
};


export default function UserTable({ users }: UserTableProps) {
    return (
        <table>
            <thead>
                <tr>
                    <th>이름</th>
                    <th>아이디</th>
                    <th>권한</th>
                    <th>가입일</th>
                </tr>
            </thead>
            <tbody>
                {users.map((user) => (
                    <tr key={user.id}>
                        <td>{user.name}</td>
                        <td>{user.username}</td>
                        <td>{user.role}</td>
                        <td>{user.createdAt}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    )
}