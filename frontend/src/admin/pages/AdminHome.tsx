import { BoardCard } from "../../dashboard/components/BoardCard";

export default function AdminHome() {
  return (
    <>
        <div className="grid grid-cols-4 gap-6">
            <BoardCard
                title="회원 수"
                value="153명"
            />

            <BoardCard
                title="오늘 가입"
                value="4명"
            />

            <BoardCard
                title="실거래 데이터"
                value="24,356건"
            />

            <BoardCard
                title="Import 대기"
                value="12건"
            />
        </div>
    </>
  );
}