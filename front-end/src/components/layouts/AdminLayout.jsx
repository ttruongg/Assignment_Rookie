import { Outlet } from "react-router-dom";
import Sidebar from "../admin/components/SideBar";

export default function AdminLayout() {
  return (
    <div className="flex h-screen bg-gray-100">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <header className="bg-white shadow-md p-4">
          <div className="flex items-center">
            <h1 className="text-xl font-semibold text-gray-800">Admin Panel</h1>
          </div>
        </header>
        <main className="flex-1 overflow-x-hidden overflow-y-auto bg-gray-100 p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
