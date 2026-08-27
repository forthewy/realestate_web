import { useEffect, useState } from "react";
import KakaoMap, {
  type ApartmentMapItem,
} from "../components/KakaoMap";
import { getMapTransactions } from "../../services/api";

export default function TransactionMap() {
  const [minAmount, setMinAmount] = useState("");
  const [maxAmount, setMaxAmount] = useState("");
  const [apartments, setApartments] = useState<ApartmentMapItem[]>([]);
  const [bounds, setBounds] = useState<{
    minLat: number;
    maxLat: number;
    minLng: number;
    maxLng: number;
  } | null>(null);

  useEffect(() => {
    if (!bounds) return;

    getMapTransactions(
      bounds.minLat,
      bounds.maxLat,
      bounds.minLng,
      bounds.maxLng,
      minAmount,
      maxAmount
    )
      .then(setApartments)
      .catch((error) => {
        console.error("지도 데이터 조회 실패", error);
      });
  }, [bounds, minAmount, maxAmount]);

  const filteredApartments = apartments.filter((apartment) => {
    const price = apartment.price;

    if (minAmount && price < Number(minAmount)) {
      return false;
    }

    if (maxAmount && price > Number(maxAmount)) {
      return false;
    }

    return true;
  });

  return (
    <div className="p-6">
      <h1 className="mb-2 text-3xl font-bold text-primary">
        거래 지도
      </h1>

      <p className="mb-6 text-text-secondary">
        아파트별 실거래 가격과 거래량을 지도에서 확인합니다.
      </p>

      <div className="mb-4 flex flex-wrap items-end gap-4 rounded-lg border border-gray bg-surface p-4">
        <div className="w-48">
          <label className="mb-1 block text-sm text-text-secondary">
            최소 금액(만원)
          </label>

          <input
            type="number"
            value={minAmount}
            onChange={(e) => setMinAmount(e.target.value)}
            placeholder="예: 50000"
            className="w-full rounded-lg border border-gray px-3 py-2"
          />
        </div>

        <div className="w-48">
          <label className="mb-1 block text-sm text-text-secondary">
            최대 금액(만원)
          </label>

          <input
            type="number"
            value={maxAmount}
            onChange={(e) => setMaxAmount(e.target.value)}
            placeholder="예: 150000"
            className="w-full rounded-lg border border-gray px-3 py-2"
          />
        </div>
      </div>

      <div className="mb-3 flex items-center gap-3 text-sm text-text-secondary">
        <span>거래량 적음</span>

        <div className="flex gap-1">
          <div className="h-4 w-8 rounded bg-primary/20" />
          <div className="h-4 w-8 rounded bg-primary/50" />
          <div className="h-4 w-8 rounded bg-primary/80" />
          <div className="h-4 w-8 rounded bg-primary" />
        </div>

        <span>거래량 많음</span>
      </div>

      <KakaoMap
        apartments={apartments}
        onBoundsChange={setBounds}
      />
    </div>
  );
}