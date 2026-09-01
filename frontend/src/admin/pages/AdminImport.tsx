import { useEffect, useState } from "react";
import {
    approveStoredMonth,
    cancelStoredMonth,
    getTransactionImports,
    getTransactionImportStatus,
    importExcel,
    geocodeApartments,
    getGeocodeStatus,
    type TransactionImport,
} from "../../services/api";
import AdminCalendar from "../components/AdminCalendar";

export default function AdminImport() {
    const [file, setFile] = useState<File | null>(null);
    const [imports, setImports] = useState<TransactionImport[]>([]);
    // 현재 선택된 월
    const [selectedMonth, setSelectedMonth] = useState(() => {
        const now = new Date();

        return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
    });

    const [approvable, setApprovable] = useState(false);
    const [approved, setApproved] = useState(false);
    const [missingCoordinateCount, setMissingCoordinateCount] = useState(0);

    const handleApprove = async () => {
        const response = await approveStoredMonth(selectedMonth);

        if (!response.ok) {
            alert("승인에 실패했습니다.");
            return;
        }

        setApproved(true);
    };
    const handleCancel = async () => {
        const response = await cancelStoredMonth(selectedMonth);

        if (!response.ok) {
            alert("승인 취소에 실패했습니다.");
            return;
        }

        setApproved(false);
    };
    const handleGeocode = async () => {
        const response = await geocodeApartments();

        if (!response.ok) {
            alert("좌표 변환에 실패했습니다.");
            return;
        }

        alert("좌표 변환이 완료되었습니다.");
    };
    useEffect(() => {
        const loadImports = async () => {
            try {
                const data = await getTransactionImports(selectedMonth);
                setImports(data);
            } catch (error) {
                console.error("Import 이력 조회 실패:", error);
                setImports([]);
            }
        };

        loadImports();
    }, [selectedMonth]);

    useEffect(() => {
        const loadStatus = async () => {
            try {
                const status =
                    await getTransactionImportStatus(selectedMonth);

                setApprovable(status.approvable);
                setApproved(status.approved);
            } catch (error) {
                console.error("Import 상태 조회 실패:", error);
            }
        };

        loadStatus();
    }, [selectedMonth]);
    useEffect(() => {
        const loadGeocodeStatus = async () => {
            try {
                const count = await getGeocodeStatus();
                setMissingCoordinateCount(count);
            } catch (error) {
                console.error("좌표 미등록 개수 조회 실패:", error);
            }
        };

        loadGeocodeStatus();
    }, []);

    const uploadSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);

        const response = await importExcel(formData);

        if (!response.ok) {
            alert("업로드에 실패했습니다.");
            return;
        }

        alert("업로드가 완료되었습니다.");
        setFile(null);
        window.location.reload();
    };

    return (
        <div className="p-8">
            {/* 페이지 상단 */}
            <div className="flex items-start justify-between">
                <div>
                    <h1 className="text-3xl font-bold">
                        데이터 관리
                    </h1>

                    <p className="mt-2 text-text-secondary">
                        실거래 데이터의 등록 현황을 관리합니다.
                    </p>
                </div>

                <form
                    onSubmit={uploadSubmit}
                    className="flex items-center gap-3"
                >
                    <label
                        htmlFor="excelFile"
                        className="cursor-pointer rounded-lg bg-primary px-5 py-3 font-semibold text-white"
                    >
                        Excel 선택
                    </label>

                    <input
                        id="excelFile"
                        type="file"
                        accept=".xlsx,.xls"
                        className="hidden"
                        onChange={(e) => {
                            const selectedFile = e.target.files?.[0];

                            if (selectedFile) {
                                setFile(selectedFile);
                            }
                        }}
                    />

                    {file && (
                        <span className="text-sm text-text-secondary">
                            {file.name}
                        </span>
                    )}

                    <button
                        type="submit"
                        disabled={!file}
                        className="rounded-lg bg-secondary px-5 py-3 font-semibold disabled:cursor-not-allowed disabled:opacity-40"
                    >
                        업로드
                    </button>
                </form>
            </div>
            {/* 아파트 좌표 관리 */}
            <div className="mt-8 rounded-xl border border-gray p-6">
                <div className="flex items-center justify-between">
                    <div>
                        <p className="text-sm text-text-secondary">
                            아파트 좌표 상태
                        </p>

                        <p className="mt-1 text-xl font-semibold">
                            미등록 {missingCoordinateCount.toLocaleString()}개
                        </p>
                    </div>

                    <button
                        type="button"
                        onClick={handleGeocode}
                        disabled={missingCoordinateCount === 0}
                        className="rounded-lg bg-primary px-5 py-2 font-semibold text-white disabled:cursor-not-allowed disabled:opacity-40"
                    >
                        좌표 변환
                    </button>
                </div>
            </div>
            {/* 선택 월 데이터 상태 */}
            <div className="mt-8 rounded-xl border border-gray p-6">
                <div className="flex items-center justify-between">
                    <div>
                        <p className="text-sm text-text-secondary">
                            DB 조회 상태
                        </p>

                        <p className="mt-1 text-xl font-semibold">
                            {selectedMonth}
                        </p>
                    </div>

                    <div>
                        {approved ? (
                            <button
                                type="button"
                                onClick={handleCancel}
                                className="rounded-lg border border-gray px-5 py-2"
                            >
                                승인 취소
                            </button>
                        ) : (
                            <button
                                type="button"
                                disabled={!approvable}
                                onClick={handleApprove}
                                className="rounded-lg bg-primary px-5 py-2 font-semibold text-white disabled:cursor-not-allowed disabled:opacity-40"
                            >
                                DB 조회 승인
                            </button>
                        )}
                    </div>
                </div>

                <div className="mt-5 border-t border-gray pt-5">
                    {approved ? (
                        <p className="font-semibold">
                            DB 조회 사용 중
                        </p>
                    ) : approvable ? (
                        <>
                            <p className="font-semibold">
                                승인 가능
                            </p>
                            <p className="mt-1 text-sm text-text-secondary">
                                해당 월의 데이터가 모두 등록되어 있습니다.
                            </p>
                        </>
                    ) : (
                        <>
                            <p className="font-semibold">
                                승인 불가
                            </p>
                            <p className="mt-1 text-sm text-text-secondary">
                                해당 월의 데이터가 모두 등록되지 않았습니다.
                            </p>
                        </>
                    )}
                </div>
            </div>

            {/* 달력 */}
            <AdminCalendar
                selectedMonth={selectedMonth}
                onMonthChange={setSelectedMonth}
                imports={imports}
            />
        </div>
    );
}