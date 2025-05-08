import api from "../../api/api";
export const fetchProducts = (queryString) => async (dispatch) => {
    try {

        dispatch({ type: "IS_FETCHING" })
        const { data } = await api.get(`/products?${queryString}`);
        dispatch({
            type: "FETCH_PRODUCTS",
            payload: data.products,
            pageNumber: data.pageNumber,
            pageSize: data.pageSize,
            totalElements: data.totalElements,
            totalPages: data.totalPages,
            lastPage: data.lastPage,
        });
        dispatch({ type: "IS_FETCHED" })

    } catch (error) {
        console.log(error);
        dispatch({
            type: "IS_ERROR",
            payload: error?.response?.data?.message || "Failed to fetch products",
        })
    }
}


export const fetchCategories = () => async (dispatch) => {
    try {

        dispatch({ type: "LOADING_CATEGORY" })
        const { data } = await api.get(`/categories`);
        dispatch({
            type: "FETCH_CATEGORIES",
            payload: data.categories,
            pageNumber: data.pageNumber,
            pageSize: data.pageSize,
            totalElements: data.totalElements,
            totalPages: data.totalPages,
            lastPage: data.lastPage,
        });
        dispatch({ type: "SUCCESS_CATEGORY" })

    } catch (error) {
        console.log(error);
        dispatch({
            type: "IS_ERROR",
            payload: error?.response?.data?.message || "Failed to fetch categories",
        })
    }
}

export const fetchFeaturedProducts = () => async (dispatch) => {
    try {
        dispatch({ type: "IS_FETCHING" });
        const { data } = await api.get(`/products?pageNumber=0&pageSize=8&featured=true`);
        dispatch({
            type: "FETCH_FEATURED_PRODUCTS",
            payload: data.products,
            pageNumber: data.pageNumber,
            pageSize: data.pageSize,
            totalElements: data.totalElements,
            totalPages: data.totalPages,
            lastPage: data.lastPage,
        });
        dispatch({ type: "IS_FETCHED" });
    } catch (error) {
        console.log(error);
        dispatch({
            type: "IS_ERROR",
            payload: error?.response?.data?.message || "Failed to fetch featured products",
        });
    }
};


export const authenticateSignInUser
    = (sendData, toast, reset, navigate, setLoader) => async (dispatch) => {
        try {
            setLoader(true);
            const { data } = await api.post("/auth/signin", sendData);
            dispatch({ type: "LOGIN_USER", payload: data });
            localStorage.setItem("auth", JSON.stringify(data));
            reset();
            toast.success("Login Success");
        } catch (error) {
            console.log(error);
            toast.error(error?.response?.data?.message || "Internal Server Error");
        } finally {
            setLoader(false);
        }
    }


export const fetchProductById = (id) => async (dispatch) => {
    dispatch({ type: "FETCH_PRODUCT_REQUEST" });

    try {
        const { data } = await api.get(`/products/${id}`);
        dispatch({ type: "FETCH_PRODUCT_SUCCESS", payload: data });
    } catch (error) {
        dispatch({
            type: "FETCH_PRODUCT_FAILURE",
            payload: error.response?.data?.message || "Failed to fetch product details",
        });
    }
};

export const createCategory = (categoryData, toast) => async (dispatch) => {
    try {
        const { data } = await api.post("/admin/categories", categoryData, {
            withCredentials: true,
        });
        dispatch({ type: "CREATE_CATEGORY", payload: data });
        toast.success("Category created successfully!");
    } catch (error) {
        console.log(error);
        toast.error(error?.response?.data?.message || "Failed to create category");
    }
};


export const SignUpUser
    = (sendData, toast, reset, navigate, setLoader) => async (dispatch) => {
        try {
            setLoader(true);
            const { data } = await api.post("/auth/signup", sendData);
            dispatch({ type: "LOGIN_USER", payload: data });
            reset();
            toast.success(data?.message || "User Registered Successfully");
            navigate("/login");
        } catch (error) {
            console.log(error);
            toast.error(error?.response?.data?.message || error?.response?.data?.password || "Internal Server Error");
        } finally {
            setLoader(false);
        }
    }

export const logOutUser = (navigate) => (dispatch) => {
    dispatch({ type: "LOG_OUT" });
    localStorage.removeItem("auth");
    navigate("/login");
}


export const ratingProduct = (reviewData, toast, resetForm, setLoader) => async (dispatch) => {

    try {
        setLoader(true);
        const { data } = await api.post("/ratings", reviewData);
        dispatch({ type: "ADD_REVIEW", payload: data });
        resetForm();
        toast.success(data?.message || "Thank you for your feedback!");
    } catch (error) {
        console.log(error);
        toast.error(error?.response?.data?.message || "Failed to submit review");
    } finally {
        setLoader(false);
    }
}

export const productReview = (productId) => async (dispatch) => {
    dispatch({ type: "FETCH_PRODUCT_REVIEW_REQUEST" });
    try {
        const { data } = await api.get(`/ratings/${productId}`);
        dispatch({ type: "FETCH_PRODUCT_REVIEW_SUCCESS", payload: data });
    } catch (error) {
        dispatch({
            type: "FETCH_PRODUCT_REVIEW_FAILURE",
            payload: error?.response?.data?.message || "Failed to fetch product reviews",
        });
    }
};

export const updateCategory = (categoryId, categoryData) => async (dispatch) => {
    dispatch({ type: "UPDATE_CATEGORY_REQUEST" });
    try {
        const { data } = await api.put(`/admin/categories/${categoryId}`, categoryData, {
            withCredentials: true,
        });

        dispatch({ type: "UPDATE_CATEGORY_SUCCESS", payload: data });
        dispatch(fetchCategories());
    } catch (error) {
        dispatch({
            type: "UPDATE_CATEGORY_FAILURE",
            payload: error?.response?.data?.message || "Failed to update category",
        });
    }
};