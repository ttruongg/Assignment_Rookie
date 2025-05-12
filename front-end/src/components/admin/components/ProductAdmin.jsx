import React, { useState, useEffect } from "react";
import { FaPlus, FaSearch } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";

import {
  createProduct,
  fetchCategories,
  fetchProducts,
  uploadProductImages,
} from "../../../store/actions";

import CreateProductModal from "./Product/CreatProductModal";
import EditProductModal from "./Product/EditProductModal";
import ProductTable from "./Product/ProductTable";
import toast from "react-hot-toast";

const ProductAdmin = () => {
  const dispatch = useDispatch();

  const { isLoading, errorMessage } = useSelector((state) => state.errors);

  const { products, categories } = useSelector((state) => state.products);

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);

  const [search, setSearch] = useState("");

  console.log("categories:", categories);
  useEffect(() => {
    const params = new URLSearchParams();

    if (search.trim()) {
      params.set("keyword", search.trim());
    }

    const queryString = params;
    dispatch(fetchProducts(queryString));
  }, [search, dispatch]);

  const openCreateModal = () => setIsCreateModalOpen(true);
  const closeCreateModal = () => setIsCreateModalOpen(false);

  const openEditModal = (product) => {
    setIsEditModalOpen(true);
    console.log("Editing product:", product);
  };
  const closeEditModal = () => setIsEditModalOpen(false);

  const [newProduct, setNewProduct] = useState({
    productName: "",
    description: "",
    brand: "",
    quantity: 0,
    price: 0,
    discount: 0,
    featured: false,
    images: [],
    categoryIds: [],
  });

  useEffect(() => {
    dispatch(fetchCategories());
  }, [dispatch]);

  const categoryOptions = categories.map((c) => ({
    value: c.id,
    label: c.categoryName,
  }));

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewProduct((prev) => ({ ...prev, [name]: value }));
  };

  const handleCheckboxChange = (e) => {
    const { name, checked } = e.target;
    setNewProduct((prev) => ({ ...prev, [name]: checked }));
  };

  const handleFileChange = (e) => {
    setNewProduct((prev) => ({
      ...prev,
      images: Array.from(e.target.files),
    }));
  };

  const handleSelectChange = (selectedOptions) => {
    const ids = selectedOptions.map((opt) => opt.value);
    setNewProduct((prev) => ({ ...prev, categoryIds: ids }));
  };

  const handleCreateProduct = async (e) => {
    e.preventDefault();

    try {
      const imageUrls = await dispatch(uploadProductImages(newProduct.images));

      const payload = {
        ...newProduct,
        images: imageUrls.map((url) => ({ imageUrl: url })),
      };

      dispatch(createProduct(payload));

      toast.success("Product created successfully");
      closeCreateModal();
      dispatch(fetchProducts());
    } catch (err) {
      console.error("Error:", err);
    }
  };

  if (isLoading && !products) return <p className="p-4">Loading products...</p>;
  if (errorMessage)
    return <p className="p-4 text-red-500">Error: {errorMessage}</p>;

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6 text-gray-800">
        Product Management
      </h1>

      <div className="mb-6 flex flex-col sm:flex-row justify-between items-center gap-4">
        <div className="relative w-full sm:w-auto">
          <input
            type="text"
            placeholder="Search products..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent w-full"
          />
          <FaSearch className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
        </div>
        <button
          onClick={openCreateModal}
          className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-lg shadow-md flex items-center space-x-2 w-full sm:w-auto justify-center"
        >
          <FaPlus />
          <span>Add New Product</span>
        </button>
      </div>

      <ProductTable products={products} openEditModal={openEditModal} />

      <CreateProductModal
        isOpen={isCreateModalOpen}
        newProduct={newProduct}
        handleInputChange={handleInputChange}
        handleFileChange={handleFileChange}
        handleSelectChange={handleSelectChange}
        handleCheckboxChange={handleCheckboxChange}
        handleCreateProduct={handleCreateProduct}
        closeCreateModal={closeCreateModal}
        categoryOptions={categoryOptions}
      />
      <EditProductModal
        isOpen={isEditModalOpen}
        closeEditModal={closeEditModal}
      />
    </div>
  );
};

export default ProductAdmin;
