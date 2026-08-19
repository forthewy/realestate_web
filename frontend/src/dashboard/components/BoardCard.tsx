type BoardCardProps = {
    title: string;
    value: string;
    description?: string;
};

export function BoardCard({
    title,
    value,
    description,
}: BoardCardProps) {
    return (
        <div className="rounded-xl border border-gray p-6">
            <h3 className="text-lg font-medium">{title}</h3>

            <p className="mt-2 text-3xl font-bold">
                {value}
            </p>

            {description && (
                <p className="mt-2 text-sm text-gray-500">
                    {description}
                </p>
            )}
        </div>
    );
}