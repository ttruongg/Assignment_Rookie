import { FaExclamationTriangle } from "react-icons/fa";
import ProductCard from "./ProductCard";
const Products = () => {
  const isLoading = false;
  const errorMessage = "";
  const products = [
    {
      productId: 652,
      productName: "Samsung Galaxy Z Flip6",
      image: "https://ecommerce-rookie.s3.amazonaws.com/iphone16_1.png",
      brand: "Samsung",
      description:
        "iPhone 16 Pro Max comes pre-installed with iOS 18, offering an intuitive interface, easy to use, and many useful features.",
      quantity: 30,
      price: 770,
      discount: 5,
      specialPrice: 731.5,
      featured: true,
    },

    {
      productId: 652,
      productName: "Samsung Galaxy Z Flip6",
      image: "https://ecommerce-rookie.s3.amazonaws.com/samsung_2.png",
      brand: "Samsung",
      description:
        "The 6.7-inch foldable Dynamic AMOLED 2X 120Hz display delivers an amazing visual experience with high resolution.",
      quantity: 0,
      price: 770,
      discount: 5,
      specialPrice: 731.5,
      featured: true,
    },
  ];


  return (
    <div className="lg:px-14 sm:px-8 px-4 py-14 2xl:w-[90%] 2xl:mx-auto">
      {isLoading ? (
        <p>It is loading...</p>
      ) : errorMessage ? (
        <div className="flex justify-center items-center h-[200px]">
          <FaExclamationTriangle className="text-slate-800 text-3xl mr-2" />
          <span className="text-slate-800 text-lg font-medium">
            {errorMessage}
          </span>
        </div>
      ) : (
        <div className="min-h-[700px]">
          <div className="pb-6 pt-14 grid 2xl:grid-cols-4 lg:grid-cols-3 sm:grid-cols-2 gap-y-6 gap-x-6">
            
            {products &&
              products.map((item) => (
                <ProductCard key={item.productId} {...item} />
              ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Products;
