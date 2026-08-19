import { useState, useEffect } from "react"
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { apiFetch } from "../../services/api"

export default function LogIn() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    // 로그인
    const { login, isLogin } = useAuth();

    // 이동
    const navigate = useNavigate();

    // 로그인 여부 확인
    useEffect(() => {
        if (isLogin) {
            navigate("/");
        }
    }, [isLogin, navigate]);


    // 로그인
    const loginSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const response = await apiFetch("/api/auth/login", {
            method: "POST",
            body: JSON.stringify({
                username,
                password,
            }),
        });

        if (!response.ok) {
            alert("아이디 또는 비밀번호가 올바르지 않습니다.");
            return;
        }

        const data = await response.json();
        login(data);
    };

    return (
        <div className="flex items-center justify-center gap-4 pt-20">
            <div className="w-full max-w-md flex-col flex gap-4 rounded-2xl border border-gray p-8">
                <div className="text-center mb-4">
                    <h1
                        className="text-6xl font-bold text-primary"
                    >
                        Real Estate
                    </h1>

                    <p className="mt-2 text-text-secondary">
                        Real Estate 실거래가 대시보드 웹
                    </p>
                </div>
                <form onSubmit={loginSubmit}>
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
                        type="submit"
                        className="w-full rounded-lg bg-primary mt-6 text-center px-4 py-3 font-semibold text-white"
                    >
                        로그인
                    </button>
                    <div className="py-4 text-center text-text-secondary">
                        아직 회원이 아니신가요?{" "}
                        <Link
                            to="/register"
                            className="font-medium text-primary hover:underline"
                        >
                            회원가입
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
}