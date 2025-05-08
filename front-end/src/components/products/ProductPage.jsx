import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { fetchProductById, productReview } from "../../store/actions";
import Loader from "../shared/Loader";
import ReviewCard from "./rating/ReviewCard";
import ProductReviewForm from "./rating/ProductReviewForm";

const ProductPage = () => {
  const { id } = useParams();
  const { user } = useSelector((state) => state.auth);
  const dispatch = useDispatch();

  const { product, isLoading, errorMessage } = useSelector(
    (state) => state.products.productDetails
  );

  const { reviews } = useSelector((state) => state.products);

  const [activeImg, setActiveImage] = useState("");
  const [amount, setAmount] = useState(1);

  useEffect(() => {
    dispatch(fetchProductById(id));
  }, [dispatch, id]);

  useEffect(() => {
    if (product?.images?.length > 0) {
      setActiveImage(product.images[0].imageUrl);
    }
  }, [product]);

  useEffect(() => {
    dispatch(productReview(id));
  }, [dispatch, id]);

  if (isLoading) return <Loader message="Loading Product Details..." />;
  if (errorMessage) return <p className="text-red-500">{errorMessage}</p>;

  return (
    <div>
      <div className="flex flex-col justify-between lg:flex-row gap-10 lg:items-center">
        <div className="flex flex-col gap-3 lg:w-2/4">
          <div className="relative w-full aspect-[4/3] overflow-hidden rounded-md">
            <img
              src={activeImg}
              alt={product?.productName}
              className="absolute inset-0 w-full h-full object-contain"
            />
          </div>

          <div
            className={`flex flex-row ${
              product?.images?.length >= 4
                ? "justify-between"
                : "justify-center gap-4"
            } h-24`}
          >
            {product?.images?.map((img, index) => (
              <img
                key={index}
                src={img.imageUrl}
                alt={`Product Image ${index + 1}`}
                className="w-24 h-24 rounded-md cursor-pointer object-cover"
                onClick={() => setActiveImage(img.imageUrl)}
              />
            ))}
          </div>
        </div>

        {/* ABOUT */}
        <div className="flex flex-col gap-4 lg:w-2/4">
          <div>
            <span className="text-violet-600 font-semibold">
              {product?.brand}
            </span>
            <h1 className="text-3xl font-bold">{product?.productName}</h1>
          </div>
          <p className="text-gray-700">{product?.description}</p>
          <h6 className="text-2xl font-semibold">
            ${product?.specialPrice || product?.price}
          </h6>
          <div className="flex flex-row items-center gap-12">
            <div className="flex flex-row items-center">
              <button
                className="bg-gray-200 py-2 px-5 rounded-lg text-violet-800 text-3xl"
                onClick={() => setAmount((prev) => Math.max(prev - 1, 1))}
              >
                -
              </button>
              <span className="py-4 px-6 rounded-lg">{amount}</span>
              <button
                className="bg-gray-200 py-2 px-4 rounded-lg text-violet-800 text-3xl"
                onClick={() => setAmount((prev) => prev + 1)}
              >
                +
              </button>
            </div>
            <button className="bg-violet-800 text-white font-semibold py-3 px-16 rounded-xl h-full">
              Add to Cart
            </button>
          </div>
        </div>
      </div>

      {user ? (
        <ProductReviewForm productId={id} userId={user.id} />
      ) : (
        <p className="text-center text-gray-500 mt-6">
          Please{" "}
          <Link to={"/login"} className="text-blue-500 font-semibold">
            log in
          </Link>{" "}
          to leave a review.
        </p>
      )}

      <div className="max-w-3xl mx-auto py-8 px-4">
        <h1 className="text-3xl font-bold text-gray-900 mb-6 text-center">
          Product Reviews
        </h1>
        {reviews.data.length === 0 ? (
          <h1 className="text-2xl font-bold text-gray-500 mb-6 text-center">
            No reviews yet for this product.
          </h1>
        ) : (
          <div className="space-y-4">
            {reviews.isLoading ? (
              <p className="text-center text-gray-500">Loading reviews...</p>
            ) : reviews.errorMessage ? (
              <p className="text-red-500 text-center">{reviews.errorMessage}</p>
            ) : (
              reviews.data.map((review) => (
                <ReviewCard
                  key={review.id}
                  name={review.firstName + " " + review.lastName}
                  rating={review.rating}
                  comment={review.comment}
                />
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default ProductPage;
