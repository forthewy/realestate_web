import type { TransactionImport } from "../../services/api";

type AdminCalendarProps = {
    selectedMonth: string;
    onMonthChange: (month: string) => void;
    imports: TransactionImport[];
};

export default function AdminCalendar({
    selectedMonth,
    onMonthChange,
    imports,
}: AdminCalendarProps) {

    const [year, month] = selectedMonth
        .split("-")
        .map(Number);

    const days = ["일", "월", "화", "수", "목", "금", "토"];
    const today = new Date();

    // 달의 1일의 요일
    const firstDay = new Date(year, month - 1, 1).getDay();

    // 달의 마지막 날짜
    const lastDate = new Date(year, month, 0).getDate();

    // 달력에 표시할 날짜
    const calendarDays = [
        ...Array(firstDay).fill(null),
        ...Array.from(
            { length: lastDate },
            (_, index) => index + 1
        ),
    ];

    const isToday = (date: number | null) => {
        return (
            date === today.getDate() &&
            month === today.getMonth() + 1 &&
            year === today.getFullYear()
        );
    };
    const isImported = (date: number | null) => {
        if (date === null) {
            return false;
        }

        const currentDate =
            `${year}-${String(month).padStart(2, "0")}-${String(date).padStart(2, "0")}`;

        return imports.some(
            (item) =>
                currentDate >= item.startDate &&
                currentDate <= item.endDate
        );
    };

    const changeMonth = (offset: number) => {
        const date = new Date(
            year,
            month - 1 + offset,
            1
        );

        const newMonth =
            `${date.getFullYear()}-${String(
                date.getMonth() + 1
            ).padStart(2, "0")}`;

        onMonthChange(newMonth);
    };

    const handlePrevMonth = () => {
        changeMonth(-1);
    };

    const handleNextMonth = () => {
        changeMonth(1);
    };

    return (
        <div className="mt-8">
            <div className="flex items-center justify-between">
                <h2 className="text-xl font-semibold">
                    {year}년 {month}월
                </h2>

                <div className="flex gap-2">
                    <button
                        type="button"
                        onClick={handlePrevMonth}
                        className="border border-gray px-3 py-2"
                    >
                        &lt;
                    </button>

                    <button
                        type="button"
                        onClick={handleNextMonth}
                        className="border border-gray px-3 py-2"
                    >
                        &gt;
                    </button>
                </div>
            </div>

            <div className="mt-6 grid grid-cols-7">
                {days.map((day) => (
                    <div
                        key={day}
                        className="py-3 text-center font-medium text-text-secondary"
                    >
                        {day}
                    </div>
                ))}
            </div>

            <div className="grid grid-cols-7">
                {calendarDays.map((date, index) => (
                    <div
                        key={index}
                        className={`min-h-24 border border-gray p-2 ${isToday(date)
                            ? "bg-primary/10"
                            : ""
                            }`}
                    >
                        <div className="flex">
                            {date}

                            {isToday(date) && (
                                <div className="ml-2 text-sm font-medium text-primary">
                                    오늘
                                </div>
                            )}
                        </div>

                        {date && (
                            <div className="mt-2">
                                {isImported(date) ? (
                                    <span className="text-sm font-medium">
                                        등록
                                    </span>
                                ) : (
                                    <span className="text-sm text-text-secondary">
                                        미등록
                                    </span>
                                )}
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}