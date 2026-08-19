import { useState } from "react";

export default function AdminCalendar() {
    const [currentDate, setCurrentDate] = useState(new Date());

    const year = currentDate.getFullYear();
    const month = currentDate.getMonth() + 1;
    const days = ["일", "월", "화", "수", "목", "금", "토"];
    const today = new Date();

    // 달의 1일의 요일
    const firstDay = new Date(year, month - 1, 1).getDay();

    // 달의 마지막 날짜
    const lastDate = new Date(year, month, 0).getDate();

    // 달력에 표시할 날짜.요일
    const calendarDays = [
        ...Array(firstDay).fill(null),
        ...Array.from({ length: lastDate }, (_, index) => index + 1),
    ];

    // 오늘 날짜 확인
    const isToday = (date: number | null) => {
        return (
            date === today.getDate() &&
            month === today.getMonth() + 1 &&
            year === today.getFullYear()
        );
    };

    const handlePrevMonth = () => {
        setCurrentDate(new Date(year, month - 2, 1));
    };
    const handleNextMonth = () => {
        setCurrentDate(new Date(year, month, 1));
    };

    return (
        <div>
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
                        className={`min-h-24 border border-gray p-2 ${isToday(date) ? "bg-primary/10" : ""
                            }`}
                    >
                        <div className="flex">
                            {date}
                            {isToday(date) && (
                                <div className="mt-2 text-sm font-medium text-primary">
                                    오늘
                                </div>
                            )}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}