import { FaExclamationTriangle } from "react-icons/fa";

const Products = () => {
  const isLoading = false;
  const errorMessage = "Error fetching products";
  const products = [
    
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
            <p>Products</p>
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
