import React, { useState } from "react";
import ProductViewModal from "./ProductViewModal";

const ProductTable = ({ products }) => {
  const [openProductViewModal, setOpenProductViewModal] = useState(false);
  const [selectedViewProduct, setSelectedViewProduct] = useState(null);

  const handleProductView = (product) => {
    setSelectedViewProduct(product);
    setOpenProductViewModal(true);
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString();
  };

  if (!products) {
    return (
      <div className="p-6 text-center text-gray-500">
        Product not found or loading...
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto space-y-6 mt-6">
      {products.map((product) => (
        <div
          key={product.id}
          className="bg-white shadow-md rounded-lg p-6 border cursor-pointer"
          onClick={() => handleProductView(product)}
        >
          <h2 className="text-xl font-bold mb-4 text-gray-800">
            {product.productName}
          </h2>

          <div className="space-y-2 text-gray-700">
            <div>
              <span className="font-semibold">Brand:</span> {product.brand}
            </div>
            <div>
              <span className="font-semibold">Description:</span>{" "}
              {product.description}
            </div>
            <div>
              <span className="font-semibold">Quantity:</span>{" "}
              {product.quantity}
            </div>
            <div>
              <span className="font-semibold">Price:</span> $
              {product.price.toFixed(2)}
            </div>
            <div>
              <span className="font-semibold">Discount:</span>{" "}
              {product.discount}%
            </div>
            <div>
              <span className="font-semibold">Special Price:</span> $
              {product.specialPrice ? product.specialPrice.toFixed(2) : "0.00"}
            </div>
            <div>
              <span className="font-semibold">Featured:</span>{" "}
              {product.featured ? (
                <span className="text-green-600 font-medium">Yes</span>
              ) : (
                <span className="text-red-600 font-medium">No</span>
              )}
            </div>
            <div>
              <span className="font-semibold">Created On:</span>{" "}
              {formatDate(product.createdOn)}
            </div>
            <div>
              <span className="font-semibold">Last Updated:</span>{" "}
              {formatDate(product.lastUpdatedOn)}
            </div>
          </div>
        </div>
      ))}
      <ProductViewModal
        open={openProductViewModal}
        setOpen={setOpenProductViewModal}
        product={selectedViewProduct}
      />
    </div>
  );
};

export default ProductTable;
