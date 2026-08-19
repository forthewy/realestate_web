import { useState } from "react";
import TransactionTable from "../components/TransactionTable";
import type { Transaction, PageResponse } from "../types/transaction";
import { regions, type Sido } from "../data/regions";


// 거래 년월 초기화 (이번달)
const getCurrentYearMonth = () => {
  const today = new Date();

  return `${today.getFullYear()}-${String(
    today.getMonth() + 1
  ).padStart(2, "0")}`;
};

export default function Transaction() {
  // 실거래건
  const [transactions, setTransactions] =
    useState<PageResponse<Transaction>>({
      items: [],
      pageNo: 1,
      pageSize: 0,
      totalCount: 0,
    });
  // 거래 년월
  const [dealYmd, setDealYmd] = useState(getCurrentYearMonth());

  // 시군구
  const [sido, setSido] = useState<Sido>("서울특별시");
  const [sggCd, setSggCd] = useState("11110");

  // 로딩
  const [loading, setLoading] = useState(false);
  // 오류
  const [error, setError] = useState("");

  // 페이지
  const [pageNo, setPageNo] = useState(1);


  // 실거래 조회
  const getTransactions = async (page = pageNo) => {
    setLoading(true);
    setError("");

    try {
      const formattedDealYmd = dealYmd.replace("-", "");
      const params = new URLSearchParams({
        sggCd,
        dealYmd: formattedDealYmd,
        pageNo: page.toString(),
      });

      const response = await fetch(
        `/api/transactions/getTransactions?${params.toString()}`
      );

      if (!response.ok) {
        throw new Error("실거래 조회 실패");
      }

      const data: PageResponse<Transaction> = await response.json();

      setTransactions(data);

    } catch (error) {
      console.error(error);
      setError("실거래 데이터를 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  };
  // 페이지 변경
  const handlePageChange = (newPage: number) => {
    if (newPage < 1) return;

    setPageNo(newPage);
    getTransactions(newPage);
  };

  // 조회시 서브밋
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    setPageNo(1);
    getTransactions(1);
  };

  // 초기화
  const handleReset = () => {
    setSido("서울특별시");
    setSggCd("11110");
    setDealYmd(getCurrentYearMonth());
    setTransactions({
      items: [],
      pageNo: 1,
      pageSize: 0,
      totalCount: 0,
    });
    setPageNo(1);
    setError("");
  };

  // 총 페이지
  const totalPages =
    transactions.pageSize > 0
      ? Math.ceil(transactions.totalCount / transactions.pageSize)
      : 0;

  const pageCount = 5;

  let startPage = Math.max(1, pageNo - 2);
  let endPage = Math.min(totalPages, startPage + pageCount - 1);

  startPage = Math.max(1, endPage - pageCount + 1);

  const pageNumbers = Array.from(
    { length: endPage - startPage + 1 },
    (_, index) => startPage + index
  );

  return (
    <div className="p-6">
      <h1 className="mb-6 text-3xl font-bold text-primary">실거래 조회</h1>
      <form
        onSubmit={handleSubmit}
        className="mb-6 grid grid-cols-3 gap-4"
      >
        <div>
          <label className="mb-1 block text-sm text-text-secondary">
            시/도
          </label>
          <select
            value={sido}
            onChange={(e) => {
              const selectedSido = e.target.value as Sido;

              setSido(selectedSido);

              // 시/도가 바뀌면 첫 번째 시군구를 기본 선택
              setSggCd(regions[selectedSido][0].code);
            }}
            className="w-full rounded-lg border border-gray px-3 py-2 outline-none focus:ring-1 focus:ring-primary"
          >
            {(Object.keys(regions) as Sido[])
              .sort((a, b) => a.localeCompare(b, "ko"))
              .map((region) => (
                <option key={region} value={region}>
                  {region}
                </option>
              ))}
          </select>
        </div>
        <div>
          <label className="mb-1 block text-sm text-text-secondary">
            시/군/구
          </label>
          <select
            value={sggCd}
            onChange={(e) => setSggCd(e.target.value)}
            className="w-full rounded-lg border border-gray px-3 py-2 outline-none focus:ring-1 focus:ring-primary"
          >
            {[...regions[sido]]
              .sort((a, b) => a.name.localeCompare(b.name, "ko"))
              .map((region) => (
                <option key={region.code} value={region.code}>
                  {region.name}
                </option>
              ))}
          </select>
        </div>
        <div>
          <label className="mb-1 block text-sm text-text-secondary">
            거래년월
          </label>
          <input
            type="month"
            value={dealYmd}
            onChange={(e) => setDealYmd(e.target.value)}
            className="w-full rounded-lg border border-gray px-3 py-2 outline-none focus:ring-1 focus:ring-primary"
          />
        </div>
        <div className="flex items-end gap-2">
          <button
            type="submit"
            className="rounded-lg bg-primary px-6 py-2 text-white hover:bg-primary-light"
          >
            조회
          </button>

          <button
            type="button"
            onClick={handleReset}
            className="rounded-lg border border-gray px-6 py-2 hover:bg-primary/10"
          >
            초기화
          </button>
        </div>
      </form>
      {error && (
        <p className="mb-4 rounded-lg bg-red-50 px-4 py-3 text-red-600">
          {error}
        </p>
      )}
      <div className="mb-3 text-sm text-text-secondary">
        총 {transactions.totalCount.toLocaleString()}건
      </div>
      <TransactionTable
        transactions={transactions.items}
        loading={loading}
      />
      <div className="mt-4 flex items-center justify-center gap-2">
        <button
          type="button"
          onClick={() => handlePageChange(pageNo - 1)}
          disabled={pageNo === 1 || loading}
          className="rounded-lg border border-gray px-4 py-2 disabled:cursor-not-allowed disabled:opacity-40"
        >
          이전
        </button>

        {pageNumbers.map((page) => (
          <button
            key={page}
            type="button"
            onClick={() => handlePageChange(page)}
            disabled={loading}
            className={`rounded-lg border px-4 py-2 ${page === pageNo
              ? "bg-primary text-white"
              : "border-gray hover:bg-primary/10"
              }`}
          >
            {page}
          </button>
        ))}

        <button
          type="button"
          onClick={() => handlePageChange(pageNo + 1)}
          disabled={pageNo >= totalPages || loading}
          className="rounded-lg border border-gray px-4 py-2 disabled:cursor-not-allowed disabled:opacity-40"
        >
          다음
        </button>
      </div>
    </div>
  );
}
