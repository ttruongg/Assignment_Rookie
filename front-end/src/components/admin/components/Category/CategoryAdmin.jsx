import React, { useState, useEffect } from "react";
import { FaPlus, FaEdit, FaTrashAlt, FaSearch } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import { fetchCategories, createCategory } from "../../../../store/actions"; // Assuming you have a createCategory action
import CreateCategoryModal from "./CreateCategoryModal";
import CategoryTable from "./CategoryTable";

const CategoryAdmin = () => {
  const dispatch = useDispatch();

  const { isLoading, errorMessage } = useSelector((state) => state.errors);
  const { categories } = useSelector((state) => state.products);

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [newCategory, setNewCategory] = useState({
    categoryName: "",
    description: "",
  });

  useEffect(() => {
    dispatch(fetchCategories());
  }, [dispatch]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewCategory((prev) => ({ ...prev, [name]: value }));
  };

  const openCreateModal = () => {
    setNewCategory({ name: "", description: "" }); // Reset form
    setIsCreateModalOpen(true);
  };

  const closeCreateModal = () => {
    setIsCreateModalOpen(false);
  };

  const handleCreateCategory = (e) => {
    e.preventDefault();
    dispatch(
      createCategory({
        categoryName: newCategory.name,
        description: newCategory.description,
      })
    );
    closeCreateModal();
  };

  const openEditModal = (category) => {
    console.log("Open edit modal for:", category, "(not implemented yet)");
  };
  const openDeleteModal = (category) => {
    console.log("Open delete modal for:", category, "(not implemented yet)");
  };

  if (isLoading) return <p>Loading categories...</p>;
  if (errorMessage)
    return <p className="text-red-500">Error: {errorMessage}</p>;

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6 text-gray-800">
        Category Management
      </h1>
      <div className="mb-6 flex flex-col sm:flex-row justify-between items-center gap-4">
        <button
          onClick={openCreateModal}
          className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-lg shadow-md flex items-center space-x-2 w-full sm:w-auto justify-center"
        >
          <FaPlus />
          <span>Add New Category</span>
        </button>
      </div>

      <CategoryTable
        categories={categories}
        openEditModal={openEditModal}
        openDeleteModal={openDeleteModal}
      />

      <CreateCategoryModal
        isOpen={isCreateModalOpen}
        newCategory={newCategory}
        handleInputChange={handleInputChange}
        handleCreateCategory={handleCreateCategory}
        closeCreateModal={closeCreateModal}
      />
    </div>
  );
};

export default CategoryAdmin;
