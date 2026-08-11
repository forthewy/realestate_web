export type User = {
    id: number;
    name: string;
    username: string;
    phone: string;
    role: "USER" | "ADMIN";
    createdAt: string;
};