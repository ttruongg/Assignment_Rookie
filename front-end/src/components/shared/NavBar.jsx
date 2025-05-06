import { Link, useLocation } from "react-router-dom";
import { FaStore } from "react-icons/fa";
import { Badge } from "@mui/material";
import { MdShoppingCart } from "react-icons/md";
import { IoIosLogIn } from "react-icons/io";
import { RxCrossCircled } from "react-icons/rx";
import { IoMdMenu } from "react-icons/io";

import { useState } from "react";
const NavBar = () => {
  const path = useLocation().pathname;
  const [NavBarOpen, setNavbarOpen] = useState(false);
  return (
    <div className="h-[70px] bg-custom-gradient text-white z-50 flex items-center sticky top-0">
      <div className="lg:px-14 sm:px-8 px-4 w-full flex justify-between">
        <Link to="/" className="flex items-center text-2xl font-bold">
          <FaStore className="mr-2 text-3xl" />
          <span className="font-[Poppins]">E-Shop</span>
        </Link>

        <ul
          className={`flex sm:gap-10 gap-4 sm:items-center  text-slate-800 sm:static absolute left-0 top-[70px] sm:shadow-none shadow-md ${
            NavBarOpen ? "h-fit sm:pb-0 pb-5" : "h-0 overflow-hidden"
          }  transition-all duration-100 sm:h-fit sm:bg-none bg-custom-gradient   text-white sm:w-fit w-full sm:flex-row flex-col px-4 sm:px-0`}
        >
          <li className="font-[500] transition-all duration-150">
            <Link
              className={`${
                path === "/" ? "text-white font-semibold" : "text-gray-200"
              }`}
              to="/"
            >
              Home
            </Link>
          </li>
          <li className="font-[500] transition-all duration-150">
            <Link
              className={`${
                path === "/products"
                  ? "text-white font-semibold"
                  : "text-gray-200"
              }`}
              to="/products"
            >
              Products
            </Link>
          </li>

          <li className="font-[500] transition-all duration-150">
            <Link
              className={`${
                path === "/about" ? "text-white font-semibold" : "text-gray-200"
              }`}
              to="/about"
            >
              About
            </Link>
          </li>

          <li className="font-[500] transition-all duration-150">
            <Link
              className={`${
                path === "/contact"
                  ? "text-white font-semibold"
                  : "text-gray-200"
              }`}
              to="/contact"
            >
              Contact
            </Link>
          </li>

          <li className="font-[500] transition-all duration-150">
            <Link
              className={`${
                path === "/cart" ? "text-white font-semibold" : "text-gray-100"
              }`}
              to="/cart"
            >
              <Badge
                showZero
                badgeContent={0}
                color="primary"
                anchorOrigin={{
                  vertical: "top",
                  horizontal: "right",
                }}
              >
                <MdShoppingCart size={22} />
              </Badge>
            </Link>
          </li>

          <li className="font-[500] transition-all duration-150">
            <Link
              className="flex items-center space-x-2 px-4 py-[6px] 
                bg-gradient-to-r from-purple-600 to-red-500 
                text-white font-semibold rounded-md
                hover:from-purple-500 hover:to-red-400 
                duration-300 "
              to="/login"
            >
              <IoIosLogIn size={25} />
              <span>Login</span>
            </Link>
          </li>
        </ul>

        <button
          onClick={() => setNavbarOpen(!NavBarOpen)}
          className="sm:hidden flex items-center sm:mt-0 mt-2"
        >
          {NavBarOpen ? (
            <RxCrossCircled className="text-white text-3xl" />
          ) : (
            <IoMdMenu className="text-white text-3xl" />
          )}
        </button>
      </div>
    </div>
  );
};

export default NavBar;
