import { useDispatch, useSelector } from "react-redux";
import HeroBanner from "./HeroBanner";
import { useEffect } from "react";
import { fetchFeaturedProducts } from "../../store/actions";
import ProductCard from "../shared/ProductCard";
import Loader from "../shared/Loader";
import { FaExclamationTriangle } from "react-icons/fa";

const Home = () => {
  const dispatch = useDispatch();

  const { isLoading, errorMessage } = useSelector((state) => state.errors);

  const featuredProducts = useSelector(
    (state) => state.products.featuredProducts
  );

  useEffect(() => {
    dispatch(fetchFeaturedProducts());
  }, [dispatch]);

  

  return (
    <div className="lg:px-14 sm:px-8 px-4">
      <div className="py-6">
        <HeroBanner />
      </div>

      <div className="py-5">
        <div className="flex flex-col justify-center items-center space-y-2">
          <h1 className="text-slate-800 text-4xl font-bold">
            Featured Products
          </h1>
          <span className="text-slate-700">
            Discover our handpicked selection of top-rated items just for you!
          </span>
        </div>
      </div>

      {isLoading ? (
        <Loader />
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
          {featuredProducts &&
            featuredProducts.map((item) => (
              <ProductCard key={item.id} {...item} />
            ))}
        </div>
        </div>
      )}
    </div>
  );
};

export default Home;
