# Real Estate Transaction Dashboard

아파트 실거래 데이터를 수집·관리하고, 지도 기반으로 거래 현황을 조회할 수 있는 부동산 실거래 데이터 시각화 서비스입니다.

공공데이터 API 및 Excel 데이터를 활용하여 실거래 데이터를 구축하고,
Spring Boot 기반 REST API와 React 지도 화면을 통해 지역별 거래 현황을 제공합니다.

## 주요 기능

### 실거래 데이터 관리

* 공공데이터 API를 통한 아파트 실거래 데이터 조회
* Excel 데이터를 파싱하여 DB에 저장
* 지역명을 기반으로 `sggCd` 지역 코드 매핑
* 데이터 중복 입력 방지
* 업로드 데이터 관리

### 관리자 기능

* 관리자용 국토교통부 실거래 Excel 데이터 업로드
* 업로드 데이터 날짜별 관리 및 현황 관리

### 지도 기반 실거래 조회

* Kakao Maps API를 이용한 지도 표시
* 현재 지도 영역을 기준으로 실거래 데이터 조회
* 지도 이동/확대 시 조회 범위 자동 갱신
* 거래량에 따른 지역 시각화
* 아파트 위치 마커 표시
* 아파트별 거래 금액 및 거래 정보 확인
* 최소/최대 거래금액 필터

## Tech Stack

### Backend

* Java
* Spring Boot
* JWT
* MySQL
* Apache POI

### Frontend

* React
* TypeScript
* Vite
* Tailwind CSS

## Architecture

```text
Frontend (React)
        │
        │ REST API
        ▼
Spring Boot
        │
        ├── Controller
        │
        ├── Service
        │
        ├── Repository
        │
        ▼
      MySQL
```

## Excel Import

국토교통부 실거래 Excel 파일을 관리자 페이지에서 업로드하면 서버에서 Apache POI를 이용하여 데이터를 읽습니다.

```text
Excel Upload
     ↓
MultipartFile
     ↓
Apache POI
     ↓
데이터 파싱
     ↓
지역 코드 매핑
     ↓
데이터 검증
     ↓
MySQL 저장
```

Excel의 주소 데이터에서 시군구와 법정동을 분리하고 내부 지역 코드 테이블을 이용하여 `sggCd`를 매핑합니다.

매핑할 수 없는 지역 데이터는 잘못된 지역 코드가 저장되지 않도록 제외합니다.

## Map Data Loading

지도에서는 전체 실거래 데이터를 한 번에 조회하지 않고 현재 화면에 보이는 영역을 기준으로 데이터를 요청합니다.

```text
사용자 지도 이동
       ↓
Kakao Maps bounds 계산
       ↓
minLat / maxLat
minLng / maxLng
       ↓
Backend API 요청
       ↓
현재 영역의 아파트 조회
       ↓
지도 Marker 갱신
```

예시:

```http
GET /api/transactions/map
    ?minLat=37.48
    &maxLat=37.65
    &minLng=126.77
    &maxLng=126.98
```

이를 통해 지도 이동 시 필요한 범위의 데이터만 조회하도록 구성했습니다.

## Authentication

Spring Security와 JWT를 이용하여 인증 구조를 구현했습니다.

```text
Login
  ↓
ID / Password 검증
  ↓
JWT 발급
  ↓
Client Token 저장
  ↓
API 요청 시 Authorization Header 전달
  ↓
JwtFilter 인증
```

공개 실거래 조회 API와 관리자 API를 분리하고 관리자 기능은 인증된 사용자만 접근할 수 있도록 구성했습니다.

## Project Structure

```text
backend
└── src/main/java/com/jane/realestate
    ├── config
    ├── controller
    ├── dto
    ├── entity
    ├── enums
    ├── repository
    ├── security
    └── service

frontend
└── src
    ├── components
    ├── pages
    ├── router
    ├── api
    └── types
```

## 개발 목적

단순 CRUD 프로젝트를 넘어 실제 공공데이터를 수집하고 가공하여 서비스에서 사용할 수 있는 형태로 관리하는 과정을 구현하는 것을 목표로 했습니다.

특히 다음 내용을 직접 구현하고 경험하는 데 중점을 두었습니다.

* 외부 공공데이터 처리
* 대용량 Excel 데이터 파싱
* 데이터 정제 및 지역 코드 매핑
* 중복 데이터 관리
* REST API 설계
* Spring Security / JWT 인증
* JPA 기반 데이터 조회
* 지도 영역 기반 데이터 조회
* React와 Spring Boot 간 API 연동

## 개선 예정

* 지도 조회 API 성능 최적화
* 대량 데이터 Import 성능 개선
* 실거래 데이터 통계 API 추가
* 지역별 거래량 시각화 개선
* 관리자 데이터 관리 기능 개선
* 누락된 지역 코드 (2026년 개편된 지역코드) 반영
