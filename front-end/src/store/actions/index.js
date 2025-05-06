import axios from "axios";
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



