import type { LoginRequest, RegisterRequest } from "../auth/types/auth";
import type { PageResponse, Transaction } from "../transaction/types/transaction";

export async function apiFetch(
    url: string,
    options: RequestInit = {}
) {
    const token = localStorage.getItem("accessToken");
    const isFormData = options.body instanceof FormData;

    return fetch(url, {
        ...options,
        headers: {
            ...(!isFormData && { "Content-Type": "application/json" }),
            ...(token && { Authorization: `Bearer ${token}` }),
            ...options.headers,
        }
    })
}

export async function apiJson<T>(url: string, options: RequestInit = {}): Promise<T> {
    const response = await apiFetch(url, options);
    if (!response.ok) {
        throw new Error(`API 요청 실패 (${response.status})`);
    }
    if (response.status === 204) {
        return undefined as T;
    }
    return response.json();
}

export function buildQuery(params: Record<string, string | number | undefined | null>): string {
    const search = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== "") {
            search.set(key, String(value));
        }
    });
    const query = search.toString();
    return query ? `?${query}` : "";
}

export function login(body: LoginRequest) {
    return apiFetch("/api/auth/login", {
        method: "POST",
        body: JSON.stringify(body),
    });
}

export function register(body: RegisterRequest) {
    return apiFetch("/api/auth/register", {
        method: "POST",
        body: JSON.stringify(body),
    });
}

export function checkUsername(username: string) {
    return apiFetch(`/api/auth/check-username${buildQuery({ username })}`);
}

export function checkPhone(phone: string) {
    return apiFetch(`/api/auth/check-phone${buildQuery({ phone })}`);
}

export function getTransactions(sggCd: string, dealYmd: string, pageNo: number) {
    return apiJson<PageResponse<Transaction>>(
        `/api/transactions/getTransactions${buildQuery({ sggCd, dealYmd, pageNo })}`
    );
}

export function getAdminUsers() {
    return apiFetch("/api/admin/users");
}

export function importExcel(formData: FormData) {
    return apiFetch("/api/admin/import", {
        method: "POST",
        body: formData,
    });
}
