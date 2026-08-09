import { useState } from "react"

export default function LogIn() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const login = async () => {

        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username,
                password
            })
        });


        console.log(response.status);

        const data = await response.json();

        console.log(data);
    }

    return (
        <div className="flex items-center justify-center gap-4 pt-20">
            <div className="w-full max-w-md flex-col flex gap-4 rounded-2xl border border-gray p-8">
                <h1 className="text-center text-3xl font-bold text-primary">
                    로그인
                </h1>
                <div className="flex flex-col gap-2">
                    <label className="text-xl font-medium text-text-secondary">
                        아이디
                    </label>
                    <input
                        className="rounded-lg border border-gray px-4 py-3 outline-none focus:ring-1"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>
                <div className="flex flex-col gap-2">
                    <label className="text-xl font-medium text-text-secondary">
                        비밀번호
                    </label>
                    <input
                        className="rounded-lg border border-gray  px-4 py-3 outline-none focus:ring-1"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                <button
                    className="rounded-lg bg-primary mt-6 text-center px-4 py-3 font-semibold text-white"
                    onClick={login}>로그인
                </button>

            </div>
        </div>
    );
}