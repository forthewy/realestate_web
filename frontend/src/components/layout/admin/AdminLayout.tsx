import { Outlet } from "react-router-dom";
import AdminSidebar from "./AdminSidebar";
import AdminHeader from "./AdminHeader";

export default function AdminLayout() {
    return (
        <>
            <AdminHeader />
            <div className="flex">
                <AdminSidebar  />
                <main className="flex-1">
                    <Outlet />
                </main>
            </div>
        </>
    );
}