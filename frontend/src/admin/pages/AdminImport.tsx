import { Fragment, useEffect, useState } from "react";
import {
    importExcel,
    geocodeApartments,
    getGeocodeStatus,
    type ApiImportRunResponse,
    getApiImportHistory,
    type ApiImportFailureResponse,
    getApiImportFailures,
    getUnresolvedFailureCount,
} from "../../services/api";

export default function AdminImport() {
    const importStatusMap = {
        RUNNING: {
            label: "수집 중",
            className: "text-blue-700",
        },
        SUCCESS: {
            label: "정상 완료",
            className: " text-emerald-700",
        },
        PARTIAL_SUCCESS: {
            label: "일부 실패",
            className: "text-amber-700",
        },
        FAILED: {
            label: "수집 실패",
            className: "text-red-700",
        },
    };
    const [file, setFile] = useState<File | null>(null);
    const [missingCoordinateCount, setMissingCoordinateCount] = useState(0);
    const [isExcelModalOpen, setIsExcelModalOpen] = useState(false);
    const [importHistory, setImportHistory] =
        useState<ApiImportRunResponse[]>([]);
    const [expandedRunId, setExpandedRunId] = useState<number | null>(null);

    const [failureDetails, setFailureDetails] =
        useState<Record<number, ApiImportFailureResponse[]>>({});

    const [loadingRunId, setLoadingRunId] = useState<number | null>(null);

    const [failureErrorRunId, setFailureErrorRunId] = useState<number | null>(null);
    const latestRun = importHistory[0];

    const [unresolvedFailureCount, setUnresolvedFailureCount] =
        useState<number | null>(null);

    const handleGeocode = async () => {
        if (!window.confirm("미등록 아파트의 좌표 변환을 실행하시겠습니까?")) {
            return;
        }

        try {
            const response = await geocodeApartments();

            if (!response.ok) {
                alert("좌표 변환에 실패했습니다.");
                return;
            }


            const count = await getGeocodeStatus();
            setMissingCoordinateCount(count);

            alert("좌표 변환이 완료되었습니다.");
        } catch (error) {
            console.error("좌표 변환 오류:", error);
            alert("좌표 변환 중 오류가 발생했습니다.");
        }
    };

    const handleRunClick = async (runId: number) => {
        if (expandedRunId === runId) {
            setExpandedRunId(null);
            return;
        }

        setExpandedRunId(runId);

        if (failureDetails[runId]) return;

        setLoadingRunId(runId);
        setFailureErrorRunId(null);

        try {
            const failures = await getApiImportFailures(runId);

            setFailureDetails(prev => ({
                ...prev,
                [runId]: failures
            }));
        } catch (error) {
            console.error("실패 상세 조회 실패:", error);
            setFailureErrorRunId(runId);
        } finally {
            setLoadingRunId(null);
        }
    };
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
    useEffect(() => {
        getApiImportHistory()
            .then(setImportHistory)
            .catch(error => {
                console.error("자동 수집 이력 조회 실패:", error);
            });
    }, []);
    useEffect(() => {
        getUnresolvedFailureCount()
            .then(setUnresolvedFailureCount)
            .catch(error => {
                console.error("미해결 수집 실패 건수 조회 실패:", error);
            });
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
            <div className="flex flex-col gap-4 border-b border-secondary pb-6 sm:flex-row sm:items-center sm:justify-between">
                <div>
                    <h1 className="text-3xl font-bold">
                        데이터 Import 관리
                    </h1>
                </div>
                <button
                    type="button"
                    onClick={() => setIsExcelModalOpen(true)}
                    className=" bg-primary px-4 py-2.5 text-lg font-medium text-white cursor-pointer"
                >
                    Excel 업로드
                </button>
            </div>
            {isExcelModalOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div className="w-full max-w-lg border bg-white">
                        <div className="flex items-center justify-between border-b border-slate-200 px-6 py-4">
                            <h2 className="font-bold text-2xl">
                                Excel 수동 데이터 업로드
                            </h2>
                            <button
                                type="button"
                                onClick={() => setIsExcelModalOpen(false)}
                                className="text-slate-500"
                            >
                                ✕
                            </button>
                        </div>

                        <form onSubmit={uploadSubmit}>
                            <div className="p-6">
                                <p className="mb-4 text-sm text-secondary">
                                    초기 데이터 등록 또는 API 장애 시 수동 복구를 위한 기능입니다.
                                </p>

                                <input
                                    type="file"
                                    accept=".xlsx,.xls"
                                    onChange={(e) => setFile(e.target.files?.[0] ?? null)}
                                    className="w-full rounded-lg border border-gray p-3 text-lg"
                                />

                                {file && (
                                    <p className="mt-3 text-sm text-secondary">
                                        선택한 파일: {file.name}
                                    </p>
                                )}
                            </div>

                            <div className="flex justify-end gap-3 border-t border-slate-200 bg-slate-50 px-6 py-4">
                                <button
                                    type="button"
                                    onClick={() => setIsExcelModalOpen(false)}
                                    className=" border border-slate-300 px-4 py-2 text-sm"
                                >
                                    취소
                                </button>
                                <button
                                    type="submit"
                                    disabled={!file}
                                    className=" bg-primary px-4 py-2 text-sm font-medium text-white disabled:opacity-40"
                                >
                                    업로드 시작
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
            {/* 상단 요약 카드 */}
            <div className="mt-6 mb-8 grid grid-cols-1 gap-5 md:grid-cols-3">

                {/* 최근 자동 수집 */}
                <div className="flex h-[162px] flex-col justify-between border border-slate-200 bg-white p-5 shadow-sm">
                    <div>
                        <p className="text-medium font-semibold tracking-wider text-slate-500">
                            최근 자동 수집
                        </p>

                        <div className="mt-3 flex flex-wrap items-baseline gap-2">
                            {latestRun ? (
                                <>
                                    <span
                                        className={`text-xl font-bold ${importStatusMap[latestRun.status].className
                                            }`}
                                    >
                                        {importStatusMap[latestRun.status].label}
                                    </span>

                                    <span className="text-medium text-slate-500">
                                        {new Date(latestRun.startedAt).toLocaleString("ko-KR")}
                                    </span>
                                </>
                            ) : (
                                <span className="text-sm text-slate-500">
                                    수집 이력 없음
                                </span>
                            )}
                        </div>
                    </div>

                    <div className="flex items-center  pt-2 text-medium">
                        <span className=" px-2 py-1 text-secondary">
                            자동 업로드 날짜 :  매주 월요일 03:00
                        </span>
                    </div>
                </div>

                {/* 미해결 수집 실패 */}
                <div className="flex h-[162px] flex-col justify-between border border-slate-200 bg-white p-5 shadow-sm">
                    <div>
                        <p className="text-medium font-semibold tracking-wider text-slate-500">
                            미해결 수집 실패
                        </p>

                        <div className="mt-3 flex flex-wrap items-baseline gap-2">
                            <span className={`text-2xl font-bold ${unresolvedFailureCount !== null && unresolvedFailureCount > 0
                                ? "text-red-600"
                                : "text-slate-900"
                                }`}>
                                {unresolvedFailureCount === null
                                    ? "—"
                                    : `${unresolvedFailureCount.toLocaleString()}건`}
                            </span>
                        </div>
                    </div>

                    <div className="border-t border-slate-100 pt-2">
                        <span className="text-xs text-slate-500">
                            {unresolvedFailureCount === null
                                ? "수집 실패 현황을 확인할 수 없습니다."
                                : unresolvedFailureCount === 0
                                    ? "현재 미해결 수집 실패가 없습니다."
                                    : "복구가 필요한 수집 실패가 있습니다."}
                        </span>
                    </div>
                </div>

                {/* 미등록 좌표 */}
                <div className="flex h-[162px] flex-col justify-between border border-slate-200 bg-white p-5 shadow-sm">
                    <div>
                        <p className="text-medium font-semibold tracking-wider text-slate-500">
                            미등록 좌표
                        </p>

                        <div className="mt-3 flex flex-wrap items-baseline gap-2">
                            <span className="text-2xl font-bold text-slate-900">
                                {missingCoordinateCount.toLocaleString()}개
                            </span>
                            <span className="text-medium text-slate-500">
                                좌표 변환 필요 아파트
                            </span>
                        </div>
                    </div>

                    <button
                        type="button"
                        onClick={handleGeocode}
                        disabled={missingCoordinateCount === 0}
                        className="w-full rounded-lg border border-slate-200 bg-slate-100 px-3 py-2 text-medium font-medium text-slate-700 transition-colors hover:bg-slate-200 disabled:cursor-not-allowed disabled:opacity-40"
                    >
                        ↻ 좌표 변환
                    </button>
                </div>
            </div>
            <section className="mt-6 border border-slate-200 bg-white p-6">
                <h2 className="mb-4 text-lg font-semibold">
                    자동 수집 이력
                </h2>

                <table className="w-full text-sm">
                    <thead className="border-b bg-slate-50 text-slate-600">
                        <tr>
                            <th className="p-3 text-left">실행 시각</th>
                            <th className="p-3 text-left">상태</th>
                            <th className="p-3 text-left">성공 / 실패</th>
                        </tr>
                    </thead>
                    <tbody>
                        {importHistory.map(run => (
                            <Fragment key={run.id}>
                                <tr
                                    onClick={() => {
                                        if (run.failureCount > 0) {
                                            handleRunClick(run.id);
                                        }
                                    }}
                                    className={`border-b transition-colors ${run.status === "FAILED"
                                        ? "bg-red-300 hover:bg-red-400"
                                        : run.status === "PARTIAL_SUCCESS"
                                            ? "bg-primary-lightest hover:bg-primary-light"
                                            : ""
                                        } ${run.failureCount > 0 ? "cursor-pointer" : ""}`}
                                >
                                    <td className="p-3">
                                        {new Date(run.startedAt).toLocaleString("ko-KR")}
                                    </td>

                                    <td className="p-3">
                                        <span
                                            className={`inline-flex px-3 py-1 text-medium font-semibold
                                              ${importStatusMap[run.status].className}`}
                                        >
                                            {importStatusMap[run.status].label}
                                        </span>
                                    </td>

                                    <td className="p-3">
                                        <div className="flex items-center justify-between">
                                            <span>
                                                {run.successCount} / {run.failureCount}
                                            </span>

                                            {run.failureCount > 0 && (
                                                <span className="text-slate-500">
                                                    {expandedRunId === run.id ? "▲" : "▼"}
                                                </span>
                                            )}
                                        </div>
                                    </td>
                                </tr>

                                {expandedRunId === run.id && (
                                    <tr className="bg-slate-50">
                                        <td colSpan={3} className="p-4">
                                            {loadingRunId === run.id ? (
                                                <p>실패 내역 조회 중...</p>
                                            ) : failureErrorRunId === run.id ? (
                                                <p className="text-red-600">
                                                    실패 내역을 불러오지 못했습니다.
                                                </p>
                                            ) : (failureDetails[run.id]?.length ?? 0) === 0 ? (
                                                <p className="text-slate-500">
                                                    실패 내역이 없습니다.
                                                </p>
                                            ) : (
                                                <div className="space-y-3">
                                                    {failureDetails[run.id]?.map(failure => (
                                                        <div
                                                            key={failure.id}
                                                            className="rounded-lg border border-slate-200 bg-white p-4"
                                                        >
                                                            <p className="font-medium">
                                                                지역 코드: {failure.sggCd}
                                                                {" / "}
                                                                거래 월: {failure.dealMonth}
                                                            </p>

                                                            <p className="mt-2 text-sm text-red-600">
                                                                {failure.errorMessage || "오류 메시지 없음"}
                                                            </p>

                                                            <p className="mt-2 text-medium text-slate-500">
                                                                실패 시각: {new Date(failure.failedAt).toLocaleString("ko-KR")}
                                                            </p>

                                                            <p className="mt-1 text-medium">
                                                                {failure.resolvedAt
                                                                    ? `복구 완료: ${new Date(failure.resolvedAt).toLocaleString("ko-KR")}`
                                                                    : "미해결"}
                                                            </p>
                                                        </div>
                                                    ))}
                                                </div>
                                            )}
                                        </td>
                                    </tr>
                                )}
                            </Fragment>
                        ))}
                    </tbody>
                </table>
            </section>
        </div >
    );
}