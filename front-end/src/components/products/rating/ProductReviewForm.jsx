import React, { useState } from "react";
import { Rating } from "@mui/material";
import { TextField, Button, Typography, Box } from "@mui/material";
import { toast } from "react-hot-toast";
import { useDispatch } from "react-redux";
import { ratingProduct } from "../../../store/actions";

const ProductReviewForm = ({ productId, userId }) => {
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState("");
  const dispatch = useDispatch();
  const [loading, setLoading] = useState(false);

  const resetForm = () => {
    setRating(0);
    setComment("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!rating || !comment.trim()) {
      toast.error("Please enter both rating and comment.");
      return;
    }

    const reviewData = {
      userId,
      productId,
      rating,
      comment,
    };

    dispatch(ratingProduct(reviewData, toast, resetForm, setLoading));
  };

  return (
    <Box
      component="form"
      onSubmit={handleSubmit}
      className="bg-white p-6 rounded-xl shadow-md w-full max-w-2xl mx-auto"
    >
      <Typography variant="h6" className="text-gray-800 mb-4 font-semibold">
        Write Your Review
      </Typography>

      <div className="mb-4">
        <Rating
          name="product-rating"
          value={rating}
          precision={1}
          onChange={(event, newValue) => {
            setRating(newValue);
          }}
        />
      </div>

      <TextField
        label="Your Comment"
        multiline
        rows={4}
        fullWidth
        value={comment}
        onChange={(e) => setComment(e.target.value)}
        variant="outlined"
        className="mb-4"
      />

      <Button
        type="submit"
        variant="contained"
        className="bg-blue-600 hover:bg-blue-700 text-white"
        fullWidth
      >
        Submit Review
      </Button>
    </Box>
  );
};

export default ProductReviewForm;
