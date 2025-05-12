import { FaFolder, FaBoxOpen, FaUsers } from "react-icons/fa";
import { Link } from "react-router-dom";

const navItems = [
  { title: "Categories", icon: <FaFolder />, path: "/admin/categories" },
  { title: "Products", icon: <FaBoxOpen />, path: "/admin/products" },
  { title: "Users", icon: <FaUsers />, path: "/admin/users" },
];

const Sidebar = () => {
  return (
    <div className="w-64 bg-gray-800 text-white flex flex-col">
      <div className="flex items-center justify-between p-4 border-b border-gray-700">
        <div className="flex items-center gap-3">
          <div className="flex h-8 w-8 items-center justify-center rounded-md bg-blue-600">
            <FaBoxOpen className="text-white" />
          </div>
          <span className="font-semibold">Eshop Management</span>
        </div>
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
                <span>{item.title}</span>
              </Link>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Sidebar;
