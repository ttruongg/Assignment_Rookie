import React from "react";

const CreateProductModal = ({
  isOpen,
  // newProduct,
  // handleInputChange,
  // handleCreateProduct,
  closeCreateModal,
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50 flex justify-center items-center">
      <div className="bg-white p-8 rounded-lg shadow-xl w-full max-w-lg">
        <h3 className="text-xl font-semibold mb-6 text-gray-800">
          Add New Product
        </h3>
        {/* Form will go here */}
        <p className="text-center text-gray-500">
          Product creation form will be here.
        </p>
        <div className="flex justify-end space-x-3 mt-6">
          <button
            type="button"
            onClick={closeCreateModal}
            className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit" // This will eventually be part of a form
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
          >
            Save Product
          </button>
        </div>
      </div>
    </div>
  );
};

export default CreateProductModal;
