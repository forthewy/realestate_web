import { useEffect, useState } from "react";
import {
    getMyPage,
    updateMyPage,
    type UserResponse,
} from "../../services/api";


export default function MyPage() {
    const [user, setUser] = useState<UserResponse | null>(null);
    const [phone, setPhone] = useState("");
    const [password, setPassword] = useState("");
    const [passwordConfirm, setPasswordConfirm] = useState("");
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        getMyPage()
            .then((data) => {
                setUser(data);
                setPhone(data.phone || "");
            })
            .catch((error) => {
                console.error("회원정보 조회 실패", error);
            });
    }, []);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (password !== passwordConfirm) {
            alert("새 비밀번호가 일치하지 않습니다.");
            return;
        }

        try {
            setSaving(true);

            const response = await updateMyPage({
                phone,
                password,
            });

            if (!response.ok) {
                throw new Error(`회원정보 수정 실패 (${response.status})`);
            }

            setPassword("");
            setPasswordConfirm("");

            alert("회원정보가 수정되었습니다.");
        } catch (error) {
            console.error(error);
            alert("회원정보 수정에 실패했습니다.");
        } finally {
            setSaving(false);
        }
    };

    if (!user) {
        return (
            <main className="flex min-h-[calc(100vh-64px)] items-center justify-center">
                <p className="text-gray-500">
                    회원정보를 불러오는 중...
                </p>
            </main>
        );
    }

    return (
        <main className="flex min-h-[calc(100vh-64px)] items-center justify-center p-6">
            <div className="w-full max-w-xl rounded-2xl border border-gray-200 bg-white p-8 shadow-sm">

                <div className="mb-8">
                    <h1 className="text-2xl font-bold text-gray-900">
                        회원정보
                    </h1>

                    <p className="mt-2 text-sm text-gray-500">
                        전화번호와 비밀번호를 변경할 수 있습니다.
                    </p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-5">

                    <InfoField
                        label="회원 번호"
                        value={user.id}
                    />

                    <InfoField
                        label="아이디"
                        value={user.username}
                    />

                    <InfoField
                        label="이름"
                        value={user.name}
                    />

                    <div>
                        <label className="mb-2 block text-sm font-medium text-gray-600">
                            전화번호
                        </label>

                        <input
                            type="tel"
                            value={phone}
                            onChange={(e) => setPhone(e.target.value)}
                            className="w-full rounded-lg border border-gray-300 px-4 py-3 outline-none focus:border-primary"
                            placeholder="01012345678"
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-medium text-gray-600">
                            새 비밀번호
                        </label>

                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full rounded-lg border border-gray-300 px-4 py-3 outline-none focus:border-primary"
                            placeholder="변경할 경우에만 입력"
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-medium text-gray-600">
                            새 비밀번호 확인
                        </label>

                        <input
                            type="password"
                            value={passwordConfirm}
                            onChange={(e) => setPasswordConfirm(e.target.value)}
                            className={`w-full rounded-lg border px-4 py-3 outline-none ${passwordConfirm && password !== passwordConfirm
                                ? "border-red-400"
                                : "border-gray-300 focus:border-primary"
                                }`}
                            placeholder="새 비밀번호를 다시 입력"
                        />

                        {passwordConfirm && password !== passwordConfirm && (
                            <p className="mt-2 text-xs text-red-500">
                                비밀번호가 일치하지 않습니다.
                            </p>
                        )}

                        {passwordConfirm && password === passwordConfirm && (
                            <p className="mt-2 text-xs text-green-600">
                                비밀번호가 일치합니다.
                            </p>
                        )}

                        {!password && !passwordConfirm && (
                            <p className="mt-2 text-xs text-gray-400">
                                비밀번호를 변경하지 않으려면 비워두세요.
                            </p>
                        )}
                    </div>

                    <InfoField
                        label="권한"
                        value={user.role}
                    />

                    <div className="pt-3">
                        <button
                            type="submit"
                            disabled={
                                saving ||
                                (!!password || !!passwordConfirm) && password !== passwordConfirm
                            }
                            className="w-full rounded-lg bg-primary px-4 py-3 font-medium text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                            {saving ? "저장 중..." : "회원정보 수정"}
                        </button>
                    </div>

                </form>
            </div>
        </main>
    );
}

function InfoField({
    label,
    value,
}: {
    label: string;
    value: string | number;
}) {
    return (
        <div>
            <p className="mb-2 text-sm font-medium text-gray-600">
                {label}
            </p>

            <div className="rounded-lg border border-gray-200 bg-gray-50 px-4 py-3 text-gray-500">
                {value}
            </div>
        </div>
    );
}