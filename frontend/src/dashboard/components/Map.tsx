import { useState } from "react";
import KakaoMap from "./KakaoMap";

const mockApartments = [
  {
    id: 1,
    aptName: "래미안 크레시티",
    price: 12.8,
    transactionCount: 8,
    area: 84.96,
    floor: 12,
    left: "22%",
    top: "35%",
  },
  {
    id: 2,
    aptName: "청량리 롯데캐슬",
    price: 14.5,
    transactionCount: 20,
    area: 84.99,
    floor: 18,
    left: "52%",
    top: "42%",
  },
  {
    id: 3,
    aptName: "래미안 위브",
    price: 9.7,
    transactionCount: 3,
    area: 59.98,
    floor: 9,
    left: "70%",
    top: "25%",
  },
  {
    id: 4,
    aptName: "힐스테이트",
    price: 11.2,
    transactionCount: 13,
    area: 74.2,
    floor: 15,
    left: "38%",
    top: "63%",
  },
];

function getMarkerColor(transactionCount: number) {
  if (transactionCount >= 16) {
    return "bg-primary text-white";
  }

  if (transactionCount >= 10) {
    return "bg-primary/80 text-white";
  }

  if (transactionCount >= 5) {
    return "bg-primary/50 text-white";
  }

  return "bg-primary/20 text-primary";
}

export default function MapPage() {
  const [minAmount, setMinAmount] = useState("");
  const [maxAmount, setMaxAmount] = useState("");
  const [selectedApartmentId, setSelectedApartmentId] =
    useState<number | null>(null);

  const selectedApartment = mockApartments.find(
    (apartment) => apartment.id === selectedApartmentId
  );

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

        <button
          type="button"
          className="rounded-lg bg-primary px-6 py-2 text-white hover:bg-primary-light"
        >
          필터 적용
        </button>
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

      <div className="relative h-[600px] overflow-hidden rounded-lg border border-gray bg-gray-100">
        <KakaoMap />
        {mockApartments.map((apartment) => {
          const isSelected = selectedApartmentId === apartment.id;

          return (
            <div
              key={apartment.id}
              className="absolute"
              style={{
                left: apartment.left,
                top: apartment.top,
              }}
            >
              {isSelected && (
                <div
                  className="
              absolute bottom-full left-1/2 z-20 mb-3
              w-64 -translate-x-1/2
              rounded-lg border border-gray
              bg-white p-4 shadow-lg
            "
                >
                  <div className="flex items-start justify-between">
                    <div>
                      <h3 className="font-bold text-primary">
                        {apartment.aptName}
                      </h3>

                      <p className="mt-1 text-xs text-text-secondary">
                        최근 실거래 정보
                      </p>
                    </div>

                    <button
                      type="button"
                      onClick={() => setSelectedApartmentId(null)}
                      className="text-text-secondary"
                    >
                      ×
                    </button>
                  </div>

                  <div className="mt-3 space-y-1 text-sm">
                    <div className="flex justify-between">
                      <span className="text-text-secondary">
                        최근 거래가
                      </span>
                      <strong>{apartment.price}억</strong>
                    </div>

                    <div className="flex justify-between">
                      <span className="text-text-secondary">
                        전용면적
                      </span>
                      <span>{apartment.area}㎡</span>
                    </div>

                    <div className="flex justify-between">
                      <span className="text-text-secondary">
                        층
                      </span>
                      <span>{apartment.floor}층</span>
                    </div>

                    <div className="flex justify-between">
                      <span className="text-text-secondary">
                        거래
                      </span>
                      <span>{apartment.transactionCount}건</span>
                    </div>
                  </div>

                  <div
                    className="
                absolute left-1/2 top-full
                -translate-x-1/2
                border-x-8 border-t-8
                border-x-transparent border-t-white
              "
                  />
                </div>
              )}

              <button
                type="button"
                onClick={() => setSelectedApartmentId(apartment.id)}
                className={`
            rounded-full px-4 py-2
            text-sm font-bold shadow
            transition-transform hover:scale-105
            ${getMarkerColor(apartment.transactionCount)}
          `}
              >
                {apartment.price}억
              </button>
            </div>
          );
        })}
      </div>

      {selectedApartment && (
        <div className="mt-4 rounded-lg border border-gray bg-surface p-5">
          <div className="flex items-start justify-between">
            <div>
              <h2 className="text-xl font-bold text-primary">
                {selectedApartment.aptName}
              </h2>

              <p className="mt-1 text-sm text-text-secondary">
                최근 실거래 정보
              </p>
            </div>

            <button
              type="button"
              onClick={() => setSelectedApartmentId(null)}
              className="text-sm text-text-secondary hover:text-primary"
            >
              닫기
            </button>
          </div>

          <div className="mt-4 grid grid-cols-4 gap-4">
            <div>
              <span className="text-sm text-text-secondary">
                최근 거래가
              </span>

              <p className="mt-1 text-lg font-bold">
                {selectedApartment.price}억
              </p>
            </div>

            <div>
              <span className="text-sm text-text-secondary">
                전용면적
              </span>

              <p className="mt-1 text-lg font-bold">
                {selectedApartment.area}㎡
              </p>
            </div>

            <div>
              <span className="text-sm text-text-secondary">
                층
              </span>

              <p className="mt-1 text-lg font-bold">
                {selectedApartment.floor}층
              </p>
            </div>

            <div>
              <span className="text-sm text-text-secondary">
                거래 건수
              </span>

              <p className="mt-1 text-lg font-bold">
                {selectedApartment.transactionCount}건
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}