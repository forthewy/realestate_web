import { useEffect, useRef } from "react";

declare global {
  interface Window {
    kakao: any;
  }
}

export default function KakaoMap() {
  const mapRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const appKey = import.meta.env.VITE_KAKAO_MAP_KEY;

    if (!appKey) {
      console.error("카카오 JavaScript 키가 없습니다.");
      return;
    }

    const initMap = () => {
      window.kakao.maps.load(() => {
        if (!mapRef.current) return;

        console.log("kakao.maps:", window.kakao.maps);
        console.log("LatLng:", window.kakao.maps.LatLng);

        const options = {
          center: new window.kakao.maps.LatLng(
            37.5665,
            126.978
          ),
          level: 7,
        };

        new window.kakao.maps.Map(
          mapRef.current,
          options
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

    script.onerror = () => {
      console.error("카카오 지도 SDK 로드 실패");
    };

    document.head.appendChild(script);
  }, []);

  return (
    <div
      ref={mapRef}
      className="h-[600px] w-full rounded-lg border border-gray"
    />
  );
}