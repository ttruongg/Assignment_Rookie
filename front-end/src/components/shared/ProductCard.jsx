import { useState } from "react";
import { FaCartShopping } from "react-icons/fa6";
import { useNavigate } from "react-router-dom";
const ProductCard = ({
  id,
  productName,
  images,
  quantity,
  brand,
  description,
  price,
  discount,
  specialPrice,
}) => {
  const isAvailable = quantity && Number(quantity) > 0;

  const [openProductViewModal, setOpenProductViewModal] = useState(false);
  const [selectedViewProduct, setSelectedViewProduct] = useState("");

  const btnLoader = false;

  const productImage = images && images.length > 0 ? images[0].imageUrl : "";

  const handleProductView = (product) => {
    setSelectedViewProduct(product);
    setOpenProductViewModal(true);
  };

  const navigate = useNavigate();

  return (
    <div className="border rounded-lg shadow-xl overflow-hidden transition-shadow duration-300">
      <div
        onClick={() => navigate(`/product/${id}`)}
        className="w-full overflow-hidden aspect-[3/2]"
      >
        <img
          className="w-full h-full cursor-pointer transition-transform duration-300 transform hover:scale-105"
          src={productImage}
          alt={productName}
        />
      </div>

      <div className="p-4">
        <h2
          onClick={() => navigate(`/product/${id}`)}
          className="text-lg font-semibold mb-2 cursor-pointer"
        >
          {productName}
        </h2>

        <div className="min-h-20 max-h-20">
          <p className="text-gray-600 text-sm">{description}</p>
        </div>

        <div className="flex items-center justify-between">
          {specialPrice ? (
            <div className="flex flex-col">
              <span className="text-gray-400 line-through">
                ${Number(price).toFixed(2)}
              </span>
              <span className="text-xl font-bold text-slate-700">
                ${Number(specialPrice).toFixed(2)}
              </span>
            </div>
          ) : (
            <span className="text-gray-800">${Number(price).toFixed(2)}</span>
          )}

          <button
            disabled={!isAvailable || btnLoader}
            onClick={() => {}}
            className={`bg-blue-600 ${
              isAvailable ? "opacity-100 hover:bg-blue-800" : "opacity-70"
            }
            text-white py-2 px-3 rounded-lg items-center transition-colors duration-300 w-30 flex justify-center`}
          >
            <FaCartShopping className="mr-2" />
            {isAvailable ? "Add to Cart" : "Stock Out"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductCard;
