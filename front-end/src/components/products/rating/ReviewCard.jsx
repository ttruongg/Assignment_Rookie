import { Rating } from "@mui/material";

const ReviewCard = ({ name, rating, comment }) => {
  return (
    <div className="bg-white p-6 rounded-lg shadow-md mb-4">
      <div className="flex justify-between items-center mb-2">
        <h3 className="text-lg font-semibold text-gray-800">{name}</h3>
        <Rating
          value={rating}
          readOnly
          precision={1}
          
        />
      </div>
      <p className="text-gray-600">{comment}</p>
    </div>
  );
};

export default ReviewCard;
