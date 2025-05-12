import React, { useEffect, useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  FormControlLabel,
  Switch,
} from "@mui/material";

const EditProductModal = ({ open, onClose, product, onSave }) => {
  const [editedProduct, setEditedProduct] = useState({ ...product });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    setEditedProduct({ ...product });
    setErrors({});
  }, [product]);

  const handleChange = (field) => (e) => {
    const value =
      e.target.type === "checkbox" ? e.target.checked : e.target.value;

    setEditedProduct((prev) => ({
      ...prev,
      [field]:
        field === "quantity" ||
        field === "discount" ||
        field === "price" ||
        field === "specialPrice"
          ? Number(value)
          : value,
    }));
  };

  const validate = () => {
    const newErrors = {};
    if (!editedProduct.productName || editedProduct.productName.length < 3) {
      newErrors.productName = "Name must be at least 3 characters";
    }
    if (!editedProduct.description || editedProduct.description.length < 10) {
      newErrors.description = "Description must be at least 10 characters";
    }
    if (editedProduct.quantity < 0) {
      newErrors.quantity = "Quantity cannot be negative";
    }
    if (editedProduct.discount < 0) {
      newErrors.discount = "Discount cannot be negative";
    }
    if (editedProduct.price < 0) {
      newErrors.price = "Price cannot be negative";
    }
    if (editedProduct.specialPrice < 0) {
      newErrors.specialPrice = "Special Price cannot be negative";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSave = () => {
    if (validate()) {
      onSave(editedProduct);
      onClose();
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Edit Product</DialogTitle>
      <DialogContent>
        <TextField
          margin="normal"
          label="Product Name"
          fullWidth
          value={editedProduct.productName || ""}
          onChange={handleChange("productName")}
          error={!!errors.productName}
          helperText={errors.productName}
        />
        <TextField
          margin="normal"
          label="Description"
          fullWidth
          multiline
          minRows={3}
          value={editedProduct.description || ""}
          onChange={handleChange("description")}
          error={!!errors.description}
          helperText={errors.description}
        />
        <TextField
          margin="normal"
          label="Quantity"
          type="number"
          fullWidth
          value={editedProduct.quantity || ""}
          onChange={handleChange("quantity")}
          error={!!errors.quantity}
          helperText={errors.quantity}
          inputProps={{ min: 0 }}
        />
        <TextField
          margin="normal"
          label="Price"
          type="number"
          fullWidth
          value={editedProduct.price || ""}
          onChange={handleChange("price")}
          error={!!errors.price}
          helperText={errors.price}
          inputProps={{ min: 0, step: 0.01 }}
        />
        <TextField
          margin="normal"
          label="Discount"
          type="number"
          fullWidth
          value={editedProduct.discount || ""}
          onChange={handleChange("discount")}
          error={!!errors.discount}
          helperText={errors.discount}
          inputProps={{ min: 0 }}
        />
        <TextField
          margin="normal"
          label="Special Price"
          type="number"
          fullWidth
          value={editedProduct.specialPrice || ""}
          onChange={handleChange("specialPrice")}
          error={!!errors.specialPrice}
          helperText={errors.specialPrice}
          inputProps={{ min: 0, step: 0.01 }}
        />
        <FormControlLabel
          control={
            <Switch
              checked={editedProduct.active || false}
              onChange={handleChange("active")}
            />
          }
          label="Active"
        />
        <FormControlLabel
          control={
            <Switch
              checked={editedProduct.featured || false}
              onChange={handleChange("featured")}
            />
          }
          label="Featured"
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} variant="outlined">
          Cancel
        </Button>
        <Button onClick={handleSave} variant="contained" color="primary">
          Save
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default EditProductModal;
