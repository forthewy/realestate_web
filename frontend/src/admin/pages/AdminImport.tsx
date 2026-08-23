import { useState } from "react";
import AdminCalendar from "../components/AdminCalendar";
import { importExcel } from "../../services/api";

export default function AdminImport() {
    const [file, setFile] = useState<File | null>(null);
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
    };
    return (
        <div className="p-8">
            {/* 페이지 상단 */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold">
                        데이터 관리
                    </h1>

                    <p className="mt-2 text-text-secondary">
                        실거래 데이터의 등록 현황을 관리합니다.
                    </p>
                </div>
                <form onSubmit={uploadSubmit}>
                    <label
                        htmlFor="excelFile"
                        className="cursor-pointer rounded-lg bg-primary px-5 py-3 font-semibold text-white"
                    >
                        Excel 업로드
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
                        <p>
                            선택한 파일: {file.name}
                        </p>
                    )}
                    <button type="submit" className="bg-secondary">
                        업로드
                    </button>
                </form>
            </div>

            {/* 데이터 현황 */}
            <div className="mt-8 rounded-xl border border-gray p-6">
                <p className="text-sm text-text-secondary">
                    데이터 현황
                </p>

                <div className="mt-3 flex gap-12">
                    <div>
                        <p className="text-sm text-text-secondary">
                            최근 데이터
                        </p>
                        <p className="mt-1 text-xl font-semibold">
                            2026.08.10
                        </p>
                    </div>

                    <div>
                        <p className="text-sm text-text-secondary">
                            총 데이터
                        </p>
                        <p className="mt-1 text-xl font-semibold">
                            128,420건
                        </p>
                    </div>
                </div>
            </div>

            {/* 달력 */}
            <AdminCalendar />
        </div>
    );
}