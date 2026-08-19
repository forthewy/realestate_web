export default function AdminImport() {
    return (
        <div className="p-8">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold">
                        데이터 관리
                    </h1>

                    <p className="mt-2 text-text-secondary">
                        실거래 데이터의 등록 현황을 관리합니다.
                    </p>
                </div>

                <button
                    type="button"
                    className="rounded-lg bg-primary px-5 py-3 font-semibold text-white"
                >
                    Excel 업로드
                </button>
            </div>
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
            <div className="mt-6 rounded-xl border border-gray p-6">
                <div className="flex items-center justify-between">
                    <h2 className="text-xl font-semibold">
                        2026년 8월
                    </h2>

                    <div className="flex gap-2">
                        <button
                            type="button"
                            className="rounded-lg border border-gray px-3 py-2"
                        >
                            &lt;
                        </button>

                        <button
                            type="button"
                            className="rounded-lg border border-gray px-3 py-2"
                        >
                            &gt;
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}