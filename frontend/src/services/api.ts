import type { LoginRequest, RegisterRequest } from "../auth/types/auth";
import type { ApartmentMapItem } from "../map/components/KakaoMap";
import type { PageResponse, Transaction } from "../transaction/types/transaction";

// 경로 연결
export async function apiFetch(
    url: string,
    options: RequestInit = {}
) {
    const token = localStorage.getItem("accessToken");
    const isFormData = options.body instanceof FormData;
    const isAuthApi = url.startsWith("/api/auth/");

    const response = await fetch(url, {
        ...options,
        headers: {
            ...(!isFormData && { "Content-Type": "application/json" }),
            ...(!isAuthApi && token && {
                Authorization: `Bearer ${token}`,
            }),
            ...options.headers,
        },
    });

    // 로그인,회원가입 api는 Refresh 대상에서 제외
    if (response.status !== 401 || isAuthApi) {
        return response;
    }

    const refreshToken = localStorage.getItem("refreshToken");

    if (!refreshToken) {
        return response;
    }

    // Access Token 재발급
    const refreshResponse = await fetch("/api/auth/refresh", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ refreshToken }),
    });

    if (!refreshResponse.ok) {
        return response;
    }

    const data: { accessToken: string } = await refreshResponse.json();

    localStorage.setItem("accessToken", data.accessToken);

    // 실패했던 원래 요청 재시도
    return fetch(url, {
        ...options,
        headers: {
            ...(!isFormData && { "Content-Type": "application/json" }),
            ...options.headers,
            Authorization: `Bearer ${data.accessToken}`,
        },
    });
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

// 로그인
export function login(body: LoginRequest) {
    return apiFetch("/api/auth/login", {
        method: "POST",
        body: JSON.stringify(body),
    });
}

// ------ 회원가입 ------------
export function register(body: RegisterRequest) {
    return apiFetch("/api/auth/register", {
        method: "POST",
        body: JSON.stringify(body),
    });
}

// 아이디 중복
export function checkUsername(username: string) {
    return apiFetch(`/api/auth/check-username${buildQuery({ username })}`);
}

// 전화번호 중복
export function checkPhone(phone: string) {
    return apiFetch(`/api/auth/check-phone${buildQuery({ phone })}`);
}

// ---------- 마이 페이지 ----------
// 마이페이지 유저 정보
export type UserResponse = {
    id: number;
    username: string;
    name: string;
    phone: string;
    role: string;
};

// 마이페이지
export function getMyPage() {
    return apiJson<UserResponse>("/api/users/me");
}

// 회원정보 수정 타입
export type UpdateUserRequest = {
    phone: string;
    password: string;
};

// 회원정보 수정
export function updateMyPage(body: UpdateUserRequest) {
    return apiFetch("/api/users/me", {
        method: "PATCH",
        body: JSON.stringify(body),
    });
}

export function getTransactions(sggCd: string, dealYmd: string, pageNo: number) {
    return apiJson<PageResponse<Transaction>>(
        `/api/transactions/getTransactions${buildQuery({ sggCd, dealYmd, pageNo })}`
    );
}

export function getMapTransactions(
    minLat: number,
    maxLat: number,
    minLng: number,
    maxLng: number,
    minAmount?: string,
    maxAmount?: string
) {
    return apiJson<ApartmentMapItem[]>(
        `/api/transactions/map${buildQuery({
            minLat,
            maxLat,
            minLng,
            maxLng,
            minAmount,
            maxAmount,
        })}`
    );
}

export function getAdminUsers() {
    return apiFetch("/api/admin/users");
}
// --------- Excel Import --------------
// Excel 업로드
export function importExcel(formData: FormData) {
    return apiFetch("/api/admin/import", {
        method: "POST",
        body: formData,
    });
}

// Import 이력
export type TransactionImport = {
    id: number;
    startDate: string;
    endDate: string;
    transactionCount: number;
    skippedCount: number;
    importedAt: string;
};

export function getTransactionImports(yearMonth: string) {
    return apiJson<TransactionImport[]>(
        `/api/admin/import${buildQuery({ yearMonth })}`
    );
}

// 월 승인 상태
export type TransactionImportStatus = {
    yearMonth: string;
    approvable: boolean;
    approved: boolean;
};
// DB 조회 승인
export function approveStoredMonth(yearMonth: string) {
    return apiFetch(
        `/api/admin/import/approve${buildQuery({ yearMonth })}`,
        {
            method: "POST",
        }
    );
}

// DB 조회 승인취소
export function cancelStoredMonth(yearMonth: string) {
    return apiFetch(
        `/api/admin/import/approve${buildQuery({ yearMonth })}`,
        {
            method: "DELETE",
        }
    );
}
export function getTransactionImportStatus(yearMonth: string) {
    return apiJson<TransactionImportStatus>(
        `/api/admin/import/status${buildQuery({ yearMonth })}`
    );
}

// 아파트 위도경도 추가
export async function geocodeApartments() {
    return apiFetch(
        "/api/admin/apartments/geocode",
        {
            method: "POST",
        }
    );
}

export function getGeocodeStatus() {
    return apiJson<number>(
        "/api/admin/apartments/geocode/status",
        {
            method: "GET",
        }
    )
}
