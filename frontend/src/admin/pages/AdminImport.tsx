import { useEffect, useState } from "react";
import {
    importExcel,
    geocodeApartments,
    getGeocodeStatus,
} from "../../services/api";

export default function AdminImport() {
    const [file, setFile] = useState<File | null>(null);
    const [missingCoordinateCount, setMissingCoordinateCount] = useState(0);
    const [isExcelModalOpen, setIsExcelModalOpen] = useState(false);

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
            <div className="flex flex-col gap-4 border-b border-slate-200 pb-6 sm:flex-row sm:items-center sm:justify-between">
                <div>
                    <h1 className="text-2xl font-bold tracking-tight text-slate-900">
                        데이터 관리
                    </h1>
                    <p className="mt-1 text-sm text-slate-500">
                        실거래 데이터 자동 수집 및 관리
                    </p>
                </div>

                <button
                    type="button"
                    onClick={() => setIsExcelModalOpen(true)}
                    className="rounded-lg bg-primary px-4 py-2.5 text-sm font-medium text-white hover:opacity-90"
                >
                    Excel 업로드
                </button>
            </div>
            {isExcelModalOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
                    <div className="w-full max-w-lg rounded-xl bg-white shadow-xl">
                        <div className="flex items-center justify-between border-b border-slate-200 px-6 py-4">
                            <h2 className="font-bold text-slate-900">
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
                                <p className="mb-4 text-sm text-slate-500">
                                    초기 데이터 등록 또는 API 장애 시 수동 복구를 위한 기능입니다.
                                </p>

                                <input
                                    type="file"
                                    accept=".xlsx,.xls"
                                    onChange={(e) => setFile(e.target.files?.[0] ?? null)}
                                    className="w-full rounded-lg border border-slate-300 p-3 text-sm"
                                />

                                {file && (
                                    <p className="mt-3 text-sm text-slate-600">
                                        선택한 파일: {file.name}
                                    </p>
                                )}
                            </div>

                            <div className="flex justify-end gap-3 border-t border-slate-200 bg-slate-50 px-6 py-4">
                                <button
                                    type="button"
                                    onClick={() => setIsExcelModalOpen(false)}
                                    className="rounded-lg border border-slate-300 px-4 py-2 text-sm"
                                >
                                    취소
                                </button>
                                <button
                                    type="submit"
                                    disabled={!file}
                                    className="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-white disabled:opacity-40"
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
                <div className="flex h-[162px] flex-col justify-between rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
                    <div>
                        <p className="text-xs font-semibold tracking-wider text-slate-500">
                            최근 자동 수집
                        </p>

                        <div className="mt-3 flex flex-wrap items-baseline gap-2">
                            <span className="text-xl font-bold text-emerald-600">
                                정상 완료
                            </span>
                            <span className="text-xs text-slate-500">
                                2026.11.16 16:30
                            </span>
                        </div>
                    </div>

                    <div className="flex items-center justify-between border-t border-slate-100 pt-2 text-xs text-slate-500">
                        <span>배치 스케줄</span>
                        <span className="rounded bg-slate-100 px-2 py-1 font-medium text-slate-700">
                            매주 월요일 03:00
                        </span>
                    </div>
                </div>

                {/* 미해결 수집 실패 */}
                <div className="flex h-[162px] flex-col justify-between rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
                    <div>
                        <p className="text-xs font-semibold tracking-wider text-slate-500">
                            미해결 수집 실패
                        </p>

                        <div className="mt-3 flex flex-wrap items-baseline gap-2">
                            <span className="text-2xl font-bold text-slate-900">
                                0건
                            </span>
                            <span className="text-xs text-slate-500">
                                복구되지 않은 지역·월
                            </span>
                        </div>
                    </div>

                    <div className="border-t border-slate-100 pt-2">
                        <span className="text-xs text-slate-500">
                            현재 미해결 수집 실패가 없습니다.
                        </span>
                    </div>
                </div>

                {/* 미등록 좌표 */}
                <div className="flex h-[162px] flex-col justify-between rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
                    <div>
                        <p className="text-xs font-semibold tracking-wider text-slate-500">
                            미등록 좌표
                        </p>

                        <div className="mt-3 flex flex-wrap items-baseline gap-2">
                            <span className="text-2xl font-bold text-slate-900">
                                {missingCoordinateCount.toLocaleString()}개
                            </span>
                            <span className="text-xs text-slate-500">
                                좌표 변환 필요 아파트
                            </span>
                        </div>
                    </div>

                    <button
                        type="button"
                        onClick={handleGeocode}
                        disabled={missingCoordinateCount === 0}
                        className="w-full rounded-lg border border-slate-200 bg-slate-100 px-3 py-2 text-xs font-medium text-slate-700 transition-colors hover:bg-slate-200 disabled:cursor-not-allowed disabled:opacity-40"
                    >
                        ↻ 좌표 변환
                    </button>
                </div>
            </div>
        </div >
    );
}