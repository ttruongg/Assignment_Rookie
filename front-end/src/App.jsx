import Home from "./components/home/Home";
import Products from "./components/products/Products";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/auth/Login";
import SignUp from "./components/auth/SignUp";
import About from "./components/About";
import Contact from "./components/Contact";
import PrivateRoute from "./components/PrivateRoute";
import ProductPage from "./components/products/ProductPage";
import UserLayout from "./components/layouts/UserLayout";
import AdminLayout from "./components/layouts/AdminLayout";
import Admin from "./components/admin/Admin";

import { Toaster } from "react-hot-toast";
import CategoryAdmin from "./components/admin/components/Category/CategoryAdmin";
import ProductAdmin from "./components/admin/components/ProductAdmin";
function App() {
  return (
    <Router>
      <Toaster position="top-center" reverseOrder={false} />
      <Routes>
        <Route element={<UserLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/products" element={<Products />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/about" element={<About />} />
          <Route path="/contact" element={<Contact />} />
          <Route path="/product/:id" element={<ProductPage />} />

          <Route element={<PrivateRoute publicPage />}>
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<SignUp />} />
          </Route>
        </Route>

        <Route element={<PrivateRoute adminOnly />}>
          <Route element={<AdminLayout />}>
            <Route path="/admin/categories" element={<CategoryAdmin />} />
            <Route path="/admin/products" element={<ProductAdmin />} />

            <Route path="/admin" element={<Admin />} />
          </Route>
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
