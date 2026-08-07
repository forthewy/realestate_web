import { BoardCard } from "../components/dashboard/BoardCard";
import { RecentSchedule } from "../components/dashboard/RecentSchedule";

export default function Dashboard() {
  return (
    <>
      <div className="grid grid-cols-4 gap-6">
        <BoardCard />
        <BoardCard />
        <BoardCard />
        <BoardCard />

      </div>
      <RecentSchedule />
    </>
  );
}