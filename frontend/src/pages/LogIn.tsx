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

        <div>
            <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
            />
            <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <button onClick={login}>로그인</button>
        </div>
    );
}