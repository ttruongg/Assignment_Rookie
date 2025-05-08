import React, { useState, useEffect } from "react";
import { FaPlus, FaEdit, FaTrashAlt, FaSearch } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchCategories,
  createCategory,
  updateCategory,
} from "../../../../store/actions";
import CreateCategoryModal from "./CreateCategoryModal";
import CategoryTable from "./CategoryTable";
import EditCategoryModal from "./EditCategoryModal";
import toast from "react-hot-toast";

const CategoryAdmin = () => {
  const dispatch = useDispatch();

  const { isLoading, errorMessage } = useSelector((state) => state.errors);
  const { categories } = useSelector((state) => state.products);

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [currentCategory, setCurrentCategory] = useState(null); // For editing
  const [newCategory, setNewCategory] = useState({
    categoryName: "",
    description: "",
  });

  useEffect(() => {
    dispatch(fetchCategories());
  }, [dispatch]);

  const handleNewCategoryInputChange = (e) => {
    const { name, value } = e.target;
    setNewCategory((prev) => ({ ...prev, [name]: value }));
  };

  const handleEditCategoryInputChange = (e) => {
    const { name, value } = e.target;
    setCurrentCategory((prev) => ({ ...prev, [name]: value }));
  };

  const openCreateModal = () => {
    setNewCategory({ categoryName: "", description: "" });
    setIsCreateModalOpen(true);
  };

  const closeCreateModal = () => {
    setIsCreateModalOpen(false);
  };

  const handleCreateCategory = (e) => {
    e.preventDefault();
    dispatch(
      createCategory({
        categoryName: newCategory.categoryName,
        description: newCategory.description,
      })
    );
    closeCreateModal();
  };

  const openEditModal = (category) => {
    setCurrentCategory({ ...category });
    setIsEditModalOpen(true);
  };

  const closeEditModal = () => {
    setIsEditModalOpen(false);
    setCurrentCategory(null); // Clear current category
  };

  const handleUpdateCategory = (e) => {
    e.preventDefault();
    if (currentCategory) {
      try {
        dispatch(updateCategory(currentCategory.id, currentCategory));
        toast.success("Category updated successfully!");
      } catch (error) {
        toast.error("Failed to update category");
      }
    }
    closeEditModal();
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
        handleInputChange={handleNewCategoryInputChange}
        handleCreateCategory={handleCreateCategory}
        closeCreateModal={closeCreateModal}
      />

      <EditCategoryModal
        isOpen={isEditModalOpen}
        currentCategory={currentCategory}
        handleInputChange={handleEditCategoryInputChange}
        handleUpdateCategory={handleUpdateCategory}
        closeEditModal={closeEditModal}
      />
    </div>
  );
};

export default CategoryAdmin;
