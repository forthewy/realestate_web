import { useState } from "react";
import TransactionTable from "../components/transaction/TransactionTable";
import type { TransactionApiItem } from "../types/transaction";
import { regions, type Sido } from "../data/regions";


// 거래 년월 초기화 (이번달)
const getCurrentYearMonth = () => {
  const today = new Date();

  return `${today.getFullYear()}-${String(
    today.getMonth() + 1
  ).padStart(2, "0")}`;
};

// 거래 금액
const formatAmountInput = (value: string) => {
  if (!value) return "";

  return Number(value).toLocaleString();
};

export default function Transaction() {
  // 실거래건
  const [transactions, setTransactions] =
    useState<TransactionApiItem[]>([]);
  // 거래 년월
  const [dealYmd, setDealYmd] = useState(getCurrentYearMonth());

  // 시군구
  const [sido, setSido] = useState<Sido>("서울특별시");
  const [sggCd, setSggCd] = useState("11110");

  //  거래금액
  const [minAmount, setMinAmount] = useState("");
  const [maxAmount, setMaxAmount] = useState("");

  // 로딩
  const [loading, setLoading] = useState(false);
  // 오류
  const [error, setError] = useState("");

  // 실거래 조회
  const getTransactions = async () => {
    setLoading(true);
    setError("");

    if (
      minAmount !== "" &&
      maxAmount !== "" &&
      Number(minAmount) > Number(maxAmount)
    ) {
      setError("최소 거래금액은 최대 거래금액보다 클 수 없습니다.");
      setLoading(false);
      return;
    }

    try {
      const formattedDealYmd = dealYmd.replace("-", "");
      const params = new URLSearchParams({
        sggCd,
        dealYmd: formattedDealYmd,
      });

      if (minAmount) {
        params.append("minAmount", minAmount);
      }

      if (maxAmount) {
        params.append("maxAmount", maxAmount);
      }


      const response = await fetch(
        `/api/transactions/getTransactions?${params.toString()}`
      );

      if (!response.ok) {
        throw new Error("실거래 조회 실패");
      }

      const data: TransactionApiItem[] = await response.json();

      setTransactions(data);

    } catch (error) {
      console.error(error);
      setError("실거래 데이터를 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  };

  // 조회시 서브밋
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    getTransactions();
  };

  // 초기화
  const handleReset = () => {
    setSido("서울특별시");
    setSggCd("11110");
    setDealYmd(getCurrentYearMonth());
    setMinAmount("");
    setMaxAmount("");
    setTransactions([]);
    setError("");
  };

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
            {Object.keys(regions).map((region) => (
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
            {regions[sido].map((region) => (
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
        <div>
          <label className="mb-1 block text-sm text-text-secondary">
            최소 거래금액 (만원)
          </label>
          <input
            type="text"
            inputMode="numeric"
            value={formatAmountInput(minAmount)}
            onChange={(e) => {
              const value = e.target.value.replace(/[^0-9]/g, "");
              setMinAmount(value);
            }}
            placeholder="예: 50,000"
            className="w-full rounded-lg border border-gray px-3 py-2 outline-none focus:ring-1 focus:ring-primary"
          />
        </div>
        <div>
          <label className="mb-1 block text-sm text-text-secondary">
            최대 거래금액 (만원)
          </label>

          <input
            type="text"
            inputMode="numeric"
            value={formatAmountInput(maxAmount)}
            onChange={(e) => {
              const value = e.target.value.replace(/[^0-9]/g, "");
              setMaxAmount(value);
            }}
            placeholder="예: 150,000"
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
        총 {transactions.length}건
      </div>
      <TransactionTable
        transactions={transactions}
        loading={loading}
      />
    </div>
  );
}
