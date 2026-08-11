import {
    createContext,
    useContext,
    useEffect,
    useState,
} from "react";

import type { LoginResponse } from "../types/auth";
import type { PropsWithChildren } from "react";

type AuthContextType = {
    accessToken: string | null;
    refreshToken: string | null;
    username: string | null;
    role: string | null;
    isLogin: boolean;

    login: (data: LoginResponse) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const useAuth = () => {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error("useAuth must be used within AuthProvider");
    }

    return context;
};

export function AuthProvider({ children }: PropsWithChildren) {

    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [refreshToken, setRefreshToken] = useState<string | null>(null);
    const [username, setUsername] = useState<string | null>(null);
    const [role, setRole] = useState<string | null>(null);
    const [isLogin, setIsLogin] = useState(false);

    useEffect(() => {
        const accessToken = localStorage.getItem("accessToken");
        const refreshToken = localStorage.getItem("refreshToken");
        const username = localStorage.getItem("username");
        const role = localStorage.getItem("role");

        if (accessToken) {
            setAccessToken(accessToken);
            setRefreshToken(refreshToken);
            setUsername(username);
            setRole(role);
            setIsLogin(true);
        }
    }, []);

    const login = (data: LoginResponse) => {

        setAccessToken(data.accessToken);
        setRefreshToken(data.refreshToken);
        setUsername(data.username);
        setRole(data.role);
        setIsLogin(true);

        localStorage.setItem("accessToken", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshToken);
        localStorage.setItem("username", data.username);
        localStorage.setItem("role", data.role);
    }

    const logout = () => {
        setAccessToken(null);
        setRefreshToken(null);
        setUsername(null);
        setRole(null);
        setIsLogin(false);

        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("username");
        localStorage.removeItem("role");
    }

    return (
        <AuthContext.Provider value={{
            accessToken,
            refreshToken,
            username,
            role,
            isLogin,
            login,
            logout
        }}
        >
            {children}
        </AuthContext.Provider >
    );
}
