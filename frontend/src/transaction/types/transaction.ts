export type Transaction = {
  id: number | null;
  aptName: string;
  dealAmount: number;
  dealDate: string;
  area: number | null;
  floor: number | null;
  buildYear: number | null;
  dong: string | null;
  umdNm: string;
  sggCd: string;
  sggName: string | null;
};

export type RegionStat = {
  sggCd: string;
  sggName: string;
  count: number;
  avgAmount: number;
  totalAmount: number;
};

export type RegionMap = Record<string, string>;

export type TransactionFilters = {
  sggCd: string;
  umdNm: string;
  minAmount: string;
  maxAmount: string;
  fromDate: string;
  toDate: string;
};

export type PageResponse<T> = {
  items: T[];
  pageNo: number;
  pageSize: number;
  totalCount: number;
};
