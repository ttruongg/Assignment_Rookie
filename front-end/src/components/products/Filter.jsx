import {
  Button,
  Checkbox,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  Tooltip,
} from "@mui/material";
import { useEffect, useState } from "react";
import { AiOutlineSearch } from "react-icons/ai";
import {
  LiaSortAmountUpSolid,
  LiaSortAmountDownAltSolid,
} from "react-icons/lia";
import { GrPowerReset } from "react-icons/gr";

import { useLocation, useNavigate, useSearchParams } from "react-router-dom";

const Filter = ({ categories }) => {
  const [searchParams] = useSearchParams();
  const pathName = useLocation().pathname;
  const params = new URLSearchParams(searchParams);
  const navigate = useNavigate();

  const [category, setCategory] = useState("all");
  const [sortOrder, setSortOrder] = useState("asc");
  const [search, setSearch] = useState("");
  const [featured, setFeatured] = useState(false);

  useEffect(() => {
    const currentCategory = searchParams.get("category") || "all";
    const currentSortOrder = searchParams.get("sortby") || "asc";
    const currentSearch = searchParams.get("keyword") || "";

    setCategory(currentCategory);
    setSortOrder(currentSortOrder);
    setSearch(currentSearch);
  }, [searchParams]);

  useEffect(() => {
    const handler = setTimeout(() => {
      if (search) {
        params.set("keyword", search);
      } else {
        params.delete("keyword");
      }

      if (featured) {
        params.set("featured", "true");
      } else {
        params.delete("featured");
      }
      navigate(`${pathName}?${params.toString()}`);
    }, 700);

    return () => {
      clearTimeout(handler);
    };
  }, [searchParams, search, featured, navigate, pathName]);

  const handleCategoryChange = (e) => {
    const selectedCategory = e.target.value;

    if (selectedCategory === "all") {
      params.delete("category");
    } else {
      params.set("category", selectedCategory);
    }

    navigate(`${pathName}?${params.toString()}`);
    setCategory(e.target.value);
  };

  const handleSortOrderChange = () => {
    setSortOrder((prev) => {
      const newSortOrder = prev === "asc" ? "desc" : "asc";
      params.set("sortby", newSortOrder);
      navigate(`${pathName}?${params.toString()}`);
      return newSortOrder;
    });
  };

  const handleClearFilter = () => {
    navigate({ pathName: window.location.pathname });
  };

  return (
    <div className="flex lg:flex-row flex-col-reverse lg:justify-between justify-center items-center gap-4">
      <div className="relative flex items-center 2xl:w-[450px] sm:w-[420px] w-full">
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          type="text"
          placeholder="Search..."
          className="border border-gray-400 text-slate-800 rounded-md py-2 pl-10 pr-4 w-full focus:outline-none focus:ring-2 focus:ring-[#1976d2]"
        />
        <AiOutlineSearch className="absolute left-3" />
      </div>

      <div className="flex sm:flex-row flex-col gap-4 items-center">
        <FormControl
          className="text-slate-800 border-slate-700"
          variant="outlined"
          size="small"
        >
          <InputLabel id="category-select-label">Category</InputLabel>

          <Select
            labelId="category-select-label"
            value={category}
            onChange={handleCategoryChange}
            label="Category"
            className="min-w-[120px] text-slate-800 border-slate-700"
          >
            <MenuItem value="all">All</MenuItem>
            {categories.map((category) => (
              <MenuItem key={category.categoryId} value={category.categoryName}>
                {category.categoryName}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <span className="text-slate-800 border-slate-700">Featured</span>
        <Checkbox
          checked={featured}
          onChange={(e) => setFeatured(e.target.checked)}
        />
        <Tooltip title="Sorted by price">
          <Button
            onClick={handleSortOrderChange}
            variant="contained"
            color="primary"
            className="flex items-center gap-2 h-10"
          >
            Sort By Price
            {sortOrder === "asc" ? (
              <LiaSortAmountUpSolid size={20} />
            ) : (
              <LiaSortAmountDownAltSolid size={20} />
            )}
          </Button>
        </Tooltip>
        <button
          onClick={handleClearFilter}
          className="flex items-center gap-2 bg-slate-600 text-white px-3 py-2 rounded-md transition duration-300 ease-in focus:outline-none"
        >
          <GrPowerReset size={20} />
          <span>Reset Filter</span>
        </button>
      </div>
    </div>
  );
};

export default Filter;
