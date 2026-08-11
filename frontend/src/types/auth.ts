export type LoginResponse = {
    accessToken: string;
    refreshToken: string;
    username: string;
    role: string;
};

export type LoginRequest = {
    username: string;
    password: string;
};

export type RegisterRequest = {
    name: string;
    phone: string;
    username: string;
    password: string;
};