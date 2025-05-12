import React, { useState } from "react";
import ProductViewModal from "./ProductViewModal";
import { LiaEdit } from "react-icons/lia";
import { Switch } from "@mui/material";
import { MdCheckCircleOutline } from "react-icons/md";
import { TbXboxX } from "react-icons/tb";
import EditProductModal from "./EditProductModal";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
} from "@mui/material";
import {
  fetchProducts,
  updateProductActiveStatus,
} from "../../../../store/actions";
import toast from "react-hot-toast";
import { useDispatch } from "react-redux";

const ProductTable = ({ products }) => {
  const dispatch = useDispatch();
  const [openProductViewModal, setOpenProductViewModal] = useState(false);
  const [selectedViewProduct, setSelectedViewProduct] = useState(null);

  const [openEditModal, setOpenEditModal] = useState(false);
  const [selectedEditProduct, setSelectedEditProduct] = useState(null);

  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [targetProduct, setTargetProduct] = useState(null);

  const handleToggleActive = (product) => {
    setTargetProduct(product);
    setConfirmDialogOpen(true);
  };

  const confirmToggleActive = async () => {
    if (!targetProduct) return;
    try {
      await dispatch(updateProductActiveStatus(targetProduct.id));

      dispatch(fetchProducts());

      setConfirmDialogOpen(false);
      setTargetProduct(null);
      toast.success("Active status updated successfully!");
    } catch (error) {
      console.error("Failed to update active status", error);
    }
  };

  const handleProductView = (product) => {
    setSelectedViewProduct(product);
    setOpenProductViewModal(true);
  };

  const handleEdit = (product) => {
    setSelectedEditProduct(product);
    setOpenEditModal(true);
  };

  const handleSaveEdit = (updatedProduct) => {
    console.log("Save product:", updatedProduct);
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
  console.log("product: ", products);

  return (
    <div className="max-w-5xl mx-auto space-y-6 mt-6">
      {products.map((product) => (
        <div
          key={product.id}
          className="bg-white shadow-md rounded-lg p-6 border cursor-pointer relative"
          onClick={() => handleProductView(product)}
        >
          <div
            className="absolute top-4 right-4 flex gap-2 z-10"
            onClick={(e) => e.stopPropagation()}
          >
            <button
              onClick={() => handleEdit(product)}
              className="text-blue-600 hover:text-blue-800"
            >
              <LiaEdit size={20} />
            </button>
            <Switch
              checked={product.active}
              onChange={() => handleToggleActive(product)}
            />
          </div>
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
            <div className="flex items-center gap-2">
              <span className="font-semibold">Active:</span>
              {product.active ? (
                <span className="text-green-600 flex items-center gap-1 font-medium">
                  <MdCheckCircleOutline />
                </span>
              ) : (
                <span className="text-red-600 flex items-center gap-1 font-medium">
                  <TbXboxX />
                </span>
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

      <EditProductModal
        open={openEditModal}
        onClose={() => setOpenEditModal(false)}
        product={selectedEditProduct}
        onSave={handleSaveEdit}
      />

      <Dialog
        open={confirmDialogOpen}
        onClose={() => setConfirmDialogOpen(false)}
      >
        <DialogTitle>Confirm Status Change</DialogTitle>
        <DialogContent>
          Are you sure you want to{" "}
          {targetProduct?.active ? "deactivate" : "activate"} this product?
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={confirmToggleActive}
            color="primary"
            variant="contained"
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default ProductTable;
