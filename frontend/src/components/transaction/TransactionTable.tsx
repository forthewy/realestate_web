import type { Transaction } from "../../types/transaction";

type TransactionTableProps = {
  transactions: Transaction[];
  loading: boolean;
};

export default function TransactionTable({ transactions, loading }: TransactionTableProps) {
  if (loading) {
    return <p className="py-8 text-center text-text-secondary">데이
      터를 불러오는 중...</p>;
  }

  if (transactions.length === 0) {
    return <p className="py-8 text-center text-text-secondary">조건에 맞는 거래 데이터가 없습니다.</p>;
  }

  return (
    <div className="overflow-x-auto rounded-lg border border-gray">
      <table className="min-w-full text-left text-sm">
        <thead className="bg-primary/10 text-primary">
          <tr>
            <th className="px-4 py-3">거래일</th>
            <th className="px-4 py-3">아파트명</th>
            <th className="px-4 py-3">지역</th>
            <th className="px-4 py-3">거래금액</th>
            <th className="px-4 py-3">면적(㎡)</th>
            <th className="px-4 py-3">층</th>
            <th className="px-4 py-3">건축년도</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((tx, index) => (
            <tr
              key={index}
              className="border-t border-gray hover:bg-primary/5"
            >
              <td className="px-4 py-3">
                {tx.dealDate}
              </td>
              <td className="px-4 py-3 font-medium">
                {tx.aptName}
              </td>
              <td className="px-4 py-3">
                {tx.umdNm}
              </td>
              <td className="px-4 py-3 font-semibold text-primary">
                {tx.dealAmount.toLocaleString()}만원
              </td>
              <td className="px-4 py-3">
                {tx.area ?? "-"}
              </td>
              <td className="px-4 py-3">
                {tx.floor ?? "-"}
              </td>
              <td className="px-4 py-3">
                {tx.buildYear ?? "-"}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
