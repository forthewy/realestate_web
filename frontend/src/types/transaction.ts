export type Transaction = {
  id: number;
  aptName: string;
  dealAmount: number;
  dealDate: string;
  area: number | null;
  floor: number | null;
  buildYear: number | null;
  dong: string | null;
  umdNm: string;
  sggCd: string;
  sggName: string;
  roadName: string | null;
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
