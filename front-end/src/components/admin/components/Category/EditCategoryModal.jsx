import React from "react";

const EditCategoryModal = ({
  isOpen,
  currentCategory,
  handleInputChange,
  handleUpdateCategory,
  closeEditModal,
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50 flex justify-center items-center">
      <div className="bg-white p-8 rounded-lg shadow-xl w-full max-w-md">
        <h3 className="text-xl font-semibold mb-6 text-gray-800">
          Edit Category
        </h3>
        <form onSubmit={handleUpdateCategory}>
          <div className="mb-4">
            <label
              htmlFor="edit-categoryName"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Category Name
            </label>
            <input
              type="text"
              name="categoryName"
              id="edit-categoryName"
              value={currentCategory.categoryName || ""}
              onChange={handleInputChange}
              required
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
            />
          </div>
          <div className="mb-6">
            <label
              htmlFor="edit-description"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Description
            </label>
            <textarea
              name="description"
              id="edit-description"
              value={currentCategory.description || ""}
              onChange={handleInputChange}
              rows="3"
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
            ></textarea>
          </div>
          <div className="flex justify-end space-x-3">
            <button
              type="button"
              onClick={closeEditModal}
              className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors"
            >
              Update Category
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditCategoryModal;
