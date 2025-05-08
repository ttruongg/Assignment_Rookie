import React, { useState, useEffect } from "react";
import { FaPlus, FaSearch } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";

import { fetchProducts } from "../../../store/actions";

import CreateProductModal from "./Product/CreatProductModal";
import EditProductModal from "./Product/EditProductModal";
import ProductTable from "./Product/ProductTable";
// import DeleteProductModal from "./DeleteProductModal"; // Placeholder for delete

const ProductAdmin = () => {
  const dispatch = useDispatch();

  const { isLoading, errorMessage } = useSelector((state) => state.errors);

  const { products } = useSelector((state) => state.products);

  console.log("products:", products);

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  // const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

  // Form/Data States
  // const [newProduct, setNewProduct] = useState({}); // For create form
  // const [currentProduct, setCurrentProduct] = useState(null); // For edit/delete

  // Search/Filter States
  // const [searchTerm, setSearchTerm] = useState("");
  // const [filteredProducts, setFilteredProducts] = useState([]);

  useEffect(() => {
    // Dispatch action to fetch products.
    // You might need to pass page number and page size if your API supports pagination
    dispatch(fetchProducts()); // Example: fetching first page, 20 items
  }, [dispatch]);

  // Placeholder handlers - to be implemented
  const openCreateModal = () => setIsCreateModalOpen(true);
  const closeCreateModal = () => setIsCreateModalOpen(false);
  // const handleCreateProduct = (productData) => { /* ...dispatch create action... */ };

  const openEditModal = (product) => {
    // setCurrentProduct(product);
    setIsEditModalOpen(true);
    console.log("Editing product:", product);
  };
  const closeEditModal = () => setIsEditModalOpen(false);
  // const handleUpdateProduct = (productId, productData) => { /* ...dispatch update action... */ };

  const openDeleteModal = (product) => {
    // setCurrentProduct(product);
    // setIsDeleteModalOpen(true);
    console.log("Deleting product:", product);
  };
  // const closeDeleteModal = () => setIsDeleteModalOpen(false);
  // const handleDeleteProduct = (productId) => { /* ...dispatch delete action... */ };

  if (isLoading && !products)
    return <p className="p-4">Loading products...</p>; // Show loading only if no data yet
  if (errorMessage)
    return <p className="p-4 text-red-500">Error: {errorMessage}</p>;

  const productsToDisplay = products || []; // Fallback to empty array if productData is null/undefined

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6 text-gray-800">
        Product Management
      </h1>

      <div className="mb-6 flex flex-col sm:flex-row justify-between items-center gap-4">
        <div className="relative w-full sm:w-auto">
          {/* Search input will go here */}
          <input
            type="text"
            placeholder="Search products..."
            // value={searchTerm}
            // onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent w-full"
            disabled // Disabled for now
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

      <ProductTable
        products={products}
        openEditModal={openEditModal}
        openDeleteModal={openDeleteModal}
      />

      <CreateProductModal
        isOpen={isCreateModalOpen}
        closeCreateModal={closeCreateModal}
      />
      <EditProductModal
        isOpen={isEditModalOpen}
        closeEditModal={closeEditModal}
      />
      {/* <DeleteProductModal isOpen={isDeleteModalOpen} closeDeleteModal={closeDeleteModal} currentProduct={currentProduct} handleDelete={handleDeleteProduct} /> */}
    </div>
  );
};

export default ProductAdmin;
