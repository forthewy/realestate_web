import { register, checkUsername as checkUsernameRequest, checkPhone as checkPhoneRequest } from "../../services/api"
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

export default function Register() {
    const [name, setName] = useState("");
    const [phone, setPhone] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [passwordConfirm, setPasswordConfirm] = useState("");
    const [usernameMessage, setUsernameMessage] = useState("");
    const [isUsernameChecked, setIsUsernameChecked] = useState(false);
    const [phoneMessage, setPhoneMessage] = useState("");
    const [isPhoneChecked, setIsPhoneChecked] = useState(false);

    const { login } = useAuth();

    // 이동
    const navigate = useNavigate();


    // 회원가입
    const registerHandler = async () => {
        if (!isUsernameChecked) {
            alert("아이디 중복확인을 해주세요.");
            return;
        }
        if (password !== passwordConfirm) {
            alert("비밀번호가 일치하지 않습니다.");
            return;
        }
        if (!isPhoneChecked) {
            alert("휴대폰 번호 중복확인을 해주세요.");
            return;
        }

        const response = await register({
            name,
            phone,
            username,
            password,
        });

        if (!response.ok) {
            alert("회원가입에 실패했습니다.");
            return;
        }

        const data = await response.json();

        login(data);

        navigate("/");
    }

    // 아이디 중복 확인
    const checkUsername = async () => {
        if (!username.trim()) {
            setUsernameMessage("아이디를 입력해주세요.");
            setIsUsernameChecked(false);
            return;
        }
        const response = await checkUsernameRequest(username);

        const available: boolean = await response.json();

        if (available) {
            setUsernameMessage("사용 가능한 아이디입니다.");
            setIsUsernameChecked(true);
        } else {
            setUsernameMessage("이미 사용 중인 아이디입니다.");
            setIsUsernameChecked(false);
        }
    };

    // 핸드폰 번호 중복 확인
    const checkPhone = async () => {
        if (!phone.trim()) {
            setPhoneMessage("휴대폰 번호를 입력해주세요.");
            setIsPhoneChecked(false);
            return;
        }

        const response = await checkPhoneRequest(phone);

        const available: boolean = await response.json();

        if (available) {
            setPhoneMessage("사용 가능한 휴대폰 번호입니다.");
            setIsPhoneChecked(true);
        } else {
            setPhoneMessage("이미 가입된 휴대폰 번호입니다.");
            setIsPhoneChecked(false);
        }
    };

    return (
        <div className="flex items-center justify-center gap-4 pt-20">
            <div className="w-full max-w-md flex-col flex gap-4 rounded-2xl border border-gray p-8">
                <h1 className="text-center text-3xl font-bold text-primary">
                    회원가입
                </h1>
                <form
                    onSubmit={(e) => {
                        e.preventDefault();
                        registerHandler();
                    }}
                >
                    <div className="flex flex-col gap-2">
                        <label className="text-xl font-medium text-text-secondary">
                            아이디
                        </label>
                        <div className="flex gap-2">
                            <input
                                className="flex-1 rounded-lg border border-gray px-4 py-3 outline-none focus:ring-1"
                                type="text"
                                value={username}
                                onChange={(e) => {
                                    setUsername(e.target.value);
                                    setUsernameMessage("");
                                    setIsUsernameChecked(false);
                                }}
                            />
                            <button
                                type="button"
                                onClick={checkUsername}
                                className="rounded-lg bg-primary px-4 text-white"
                            >
                                중복확인
                            </button>
                        </div>
                        {usernameMessage && (
                            <p
                                className={`text-sm ${isUsernameChecked
                                    ? "text-green-600"
                                    : "text-red-500"
                                    }`}
                            >
                                {usernameMessage}
                            </p>
                        )}
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
                        <div className="flex flex-col gap-2">
                            <label className="text-xl font-medium text-text-secondary">
                                비밀번호확인
                            </label>
                            <input
                                className="rounded-lg border border-gray  px-4 py-3 outline-none focus:ring-1"
                                type="password"
                                value={passwordConfirm}
                                onChange={(e) => setPasswordConfirm(e.target.value)}
                            />
                        </div>
                        <div className="flex flex-col gap-2">
                            <label className="text-xl font-medium text-text-secondary">
                                이름
                            </label>
                            <input
                                className="rounded-lg border border-gray px-4 py-3 outline-none focus:ring-1"
                                type="text"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                            />
                        </div>
                        <div className="flex flex-col gap-2">
                            <label className="text-xl font-medium text-text-secondary">
                                휴대폰 번호
                            </label>
                            <div className="flex gap-2">
                                <input
                                    className="rounded-lg border border-gray px-4 py-3 outline-none focus:ring-1"
                                    type="tel"
                                    value={phone}
                                    onChange={(e) => {
                                        setPhone(e.target.value);
                                        setPhoneMessage("");
                                        setIsPhoneChecked(false);
                                    }}
                                    placeholder="01012345678"
                                />
                                <button
                                    type="button"
                                    onClick={checkPhone}
                                    className="rounded-lg bg-primary px-4 text-white"
                                >
                                    중복확인
                                </button>
                            </div>
                        </div>
                        {phoneMessage && (
                            <p
                                className={`text-sm ${isPhoneChecked
                                    ? "text-green-600"
                                    : "text-red-500"
                                    }`}
                            >
                                {phoneMessage}
                            </p>
                        )}
                        {/* 회원가입 버튼 */}
                        <button
                            type="submit"
                            className="rounded-lg bg-primary mt-6 px-4 py-3 font-semibold text-white"
                        >
                            회원가입
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}