import { useEffect, useRef } from "react";

declare global {
  interface Window {
    kakao: any;
  }
}

export type ApartmentMapItem = {
  id: number;
  name: string;
  lat: number;
  lng: number;
  price: number;
  transactionCount: number;
  area: number;
  floor: number;
};

type MapBounds = {
  minLat: number;
  maxLat: number;
  minLng: number;
  maxLng: number;
};

type KakaoMapProps = {
  apartments: ApartmentMapItem[];
  onBoundsChange: (bounds: MapBounds) => void;
};

function getMarkerColor(count: number) {
  if (count >= 16) return "#2563eb";
  if (count >= 10) return "#60a5fa";
  if (count >= 5) return "#93c5fd";
  return "#dbeafe";
}

export default function KakaoMap({
  apartments,
  onBoundsChange,
}: KakaoMapProps) {
  const mapRef = useRef<HTMLDivElement>(null);

  const kakaoMapRef = useRef<any>(null);
  const overlaysRef = useRef<any[]>([]);
  const openedInfoOverlayRef = useRef<any>(null);

  // 지도 생성
  useEffect(() => {
    const appKey = import.meta.env.VITE_KAKAO_MAP_KEY;

    if (!appKey) return;

    const initMap = () => {
      window.kakao.maps.load(() => {
        if (!mapRef.current) return;

        // 이미 만들어졌으면 또 만들지 않음
        if (kakaoMapRef.current) return;

        const map = new window.kakao.maps.Map(mapRef.current, {
          center: new window.kakao.maps.LatLng(
            37.575,
            127.05
          ),
          level: 5,
        });

        kakaoMapRef.current = map;

        const sendBounds = () => {
          const bounds = map.getBounds();

          const sw = bounds.getSouthWest();
          const ne = bounds.getNorthEast();

          onBoundsChange({
            minLat: Number(sw.getLat().toFixed(6)),
            maxLat: Number(ne.getLat().toFixed(6)),
            minLng: Number(sw.getLng().toFixed(6)),
            maxLng: Number(ne.getLng().toFixed(6)),
          });
        };

        // 최초 조회
        sendBounds();

        // 지도 이동/확대축소가 끝났을 때 조회
        window.kakao.maps.event.addListener(
          map,
          "idle",
          sendBounds
        );
      });
    };

    if (window.kakao?.maps?.load) {
      initMap();
      return;
    }

    const script = document.createElement("script");

    script.src =
      `https://dapi.kakao.com/v2/maps/sdk.js` +
      `?appkey=${appKey}&autoload=false`;

    script.async = true;
    script.onload = initMap;

    document.head.appendChild(script);
  }, [onBoundsChange]);

  // apartments 변경 시 마커만 다시 그림
  useEffect(() => {
    const map = kakaoMapRef.current;

    if (!map || !window.kakao?.maps) {
      return;
    }

    // 기존 말풍선 제거
    if (openedInfoOverlayRef.current) {
      openedInfoOverlayRef.current.setMap(null);
      openedInfoOverlayRef.current = null;
    }

    // 기존 가격 마커 제거
    overlaysRef.current.forEach((overlay) => {
      overlay.setMap(null);
    });

    overlaysRef.current = [];

    apartments.forEach((apartment) => {
      const position = new window.kakao.maps.LatLng(
        apartment.lat,
        apartment.lng
      );

      const content = document.createElement("button");

      content.type = "button";
      content.innerText =
        `${(apartment.price / 10000).toFixed(1)}억`;

      content.style.background =
        getMarkerColor(apartment.transactionCount);

      content.style.border = "1px solid #ffffff";
      content.style.borderRadius = "18px";
      content.style.padding = "7px 12px";
      content.style.fontWeight = "700";
      content.style.cursor = "pointer";
      content.style.boxShadow =
        "0 2px 6px rgba(0,0,0,0.2)";

      const overlay =
        new window.kakao.maps.CustomOverlay({
          position,
          content,
          yAnchor: 1,
        });

      overlay.setMap(map);

      overlaysRef.current.push(overlay);

      content.addEventListener("click", () => {
        if (openedInfoOverlayRef.current) {
          openedInfoOverlayRef.current.setMap(null);
        }

        const info = document.createElement("div");

        info.style.width = "230px";
        info.style.background = "white";
        info.style.border = "1px solid #ddd";
        info.style.borderRadius = "10px";
        info.style.padding = "14px";
        info.style.boxShadow =
          "0 4px 12px rgba(0,0,0,0.18)";
        info.style.position = "relative";

        const title = document.createElement("div");

        title.innerText = apartment.name;
        title.style.fontWeight = "700";
        title.style.marginBottom = "10px";

        const price = document.createElement("div");

        price.innerText =
          `최근 거래가 ${(apartment.price / 10000).toFixed(1)}억`;

        price.style.fontWeight = "700";
        price.style.marginBottom = "6px";

        const detail = document.createElement("div");

        detail.innerText =
          `${apartment.area}㎡ · ${apartment.floor}층`;

        detail.style.fontSize = "13px";
        detail.style.marginBottom = "4px";

        const count = document.createElement("div");

        count.innerText =
          `최근 거래 ${apartment.transactionCount}건`;

        count.style.fontSize = "13px";

        const closeButton =
          document.createElement("button");

        closeButton.innerText = "×";
        closeButton.type = "button";
        closeButton.style.position = "absolute";
        closeButton.style.top = "7px";
        closeButton.style.right = "9px";
        closeButton.style.border = "none";
        closeButton.style.background = "transparent";
        closeButton.style.cursor = "pointer";
        closeButton.style.fontSize = "18px";

        info.appendChild(title);
        info.appendChild(price);
        info.appendChild(detail);
        info.appendChild(count);
        info.appendChild(closeButton);

        const infoOverlay =
          new window.kakao.maps.CustomOverlay({
            position,
            content: info,
            yAnchor: 1.35,
            zIndex: 10,
          });

        infoOverlay.setMap(map);

        openedInfoOverlayRef.current =
          infoOverlay;

        closeButton.addEventListener(
          "click",
          () => {
            infoOverlay.setMap(null);

            openedInfoOverlayRef.current =
              null;
          }
        );
      });
    });
  }, [apartments]);

  return (
    <div
      ref={mapRef}
      className="h-[600px] w-full rounded-lg border border-gray"
    />
  );
}