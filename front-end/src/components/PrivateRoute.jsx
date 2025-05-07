import React from "react";
import { useSelector } from "react-redux";
import { Navigate, Outlet } from "react-router-dom";

const PrivateRoute = ({ publicPage = false, adminOnly = false }) => {
  const { user } = useSelector((state) => state.auth);

  if (publicPage) {
    return user ? <Navigate to="/" /> : <Outlet />;
  }

  if (!user) {
    return <Navigate to="/login" />;
  }

  if (adminOnly && !user.roles?.includes("ADMIN")) {
    return <Navigate to="/" />;
  }

  return <Outlet />;
};

export default PrivateRoute;
