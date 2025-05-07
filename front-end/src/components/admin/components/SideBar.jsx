import { FaFolder, FaBoxOpen, FaUsers, FaBars, FaTimes } from "react-icons/fa";
import { Link } from "react-router-dom";

const navItems = [
  { title: "Categories", icon: <FaFolder />, path: "/admin/categories" },
  { title: "Products", icon: <FaBoxOpen />, path: "/admin/products" },
  { title: "Users", icon: <FaUsers />, path: "/users" },
];

const Sidebar = ({ isOpen, toggleSidebar }) => {
  return (
    <>
      {/* Sidebar for larger screens */}
      <div
        className={`bg-gray-800 text-white transition-all duration-300 hidden md:flex md:flex-col ${
          isOpen ? "w-64" : "w-20"
        } flex flex-col`}
      >
        <div className="flex items-center justify-between p-4 border-b border-gray-700">
          <div
            className={`flex items-center gap-3 ${
              !isOpen && "justify-center w-full"
            }`}
          >
            <div className="flex h-8 w-8 items-center justify-center rounded-md bg-blue-600">
              <FaBoxOpen className="text-white" />
            </div>
            {isOpen && <span className="font-semibold">Inventory System</span>}
          </div>
          {isOpen && (
            <button
              onClick={toggleSidebar}
              className="text-gray-400 hover:text-white"
            >
              <FaTimes />
            </button>
          )}
        </div>
        <div className="flex-1 overflow-y-auto p-4">
          <ul className="space-y-2">
            {navItems.map((item) => (
              <li key={item.title}>
                <Link
                  to={item.path}
                  className="flex items-center gap-3 p-2 rounded-md hover:bg-gray-700 transition-colors"
                >
                  {item.icon}
                  {isOpen && <span>{item.title}</span>}
                </Link>
              </li>
            ))}
          </ul>
        </div>
      </div>

      {/* Mobile Sidebar (Overlay) */}
      {isOpen && (
        <div className="md:hidden fixed inset-0 z-30">
          {/* Overlay */}
          <div
            className="absolute inset-0 bg-black opacity-50"
            onClick={toggleSidebar}
          ></div>
          {/* Sidebar Content */}
          <div
            className={`bg-gray-800 text-white w-64 h-full fixed left-0 top-0 z-40 flex flex-col transition-transform duration-300 ease-in-out transform ${
              isOpen ? "translate-x-0" : "-translate-x-full"
            }`}
          >
            <div className="flex items-center justify-between p-4 border-b border-gray-700">
              <div className="flex items-center gap-3">
                <div className="flex h-8 w-8 items-center justify-center rounded-md bg-blue-600">
                  <FaBoxOpen className="text-white" />
                </div>
                <span className="font-semibold">Inventory System</span>
              </div>
              <button
                onClick={toggleSidebar}
                className="text-gray-400 hover:text-white"
              >
                <FaTimes />
              </button>
            </div>
            <div className="flex-1 overflow-y-auto p-4">
              <ul className="space-y-2">
                {navItems.map((item) => (
                  <li key={item.title}>
                    <Link
                      to={item.path}
                      onClick={toggleSidebar} // Close sidebar on navigation
                      className="flex items-center gap-3 p-2 rounded-md hover:bg-gray-700 transition-colors"
                    >
                      {item.icon}
                      <span>{item.title}</span>
                    </Link>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default Sidebar;
