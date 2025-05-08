const initialState = {
    products: null,
    categories: null,
    featuredProducts: null,
    featuredPagination: {},
    pagination: {},
    productDetails: {
        product: null,
        isLoading: false,
        errorMessage: null,
    },
    reviews: {
        data: [],
        isLoading: false,
        errorMessage: null,
    },
};

export const productReducer = (state = initialState, action) => {
    switch (action.type) {
        case "FETCH_PRODUCTS":
            return {
                ...state,
                products: action.payload,
                pagination: {
                    ...state.pagination,
                    pageNumber: action.pageNumber,
                    pageSize: action.pageSize,
                    totalElements: action.totalElements,
                    totalPages: action.totalPages,
                    lastPage: action.lastPage,
                },
            };
        case "FETCH_FEATURED_PRODUCTS":
            return {
                ...state,
                featuredProducts: action.payload,
                featuredPagination: {
                    pageNumber: action.pageNumber,
                    pageSize: action.pageSize,
                    totalElements: action.totalElements,
                    totalPages: action.totalPages,
                    lastPage: action.lastPage,
                },
            };

        case "FETCH_CATEGORIES":
            return {
                ...state,
                categories: action.payload,
                pagination: {
                    ...state.pagination,
                    pageNumber: action.pageNumber,
                    pageSize: action.pageSize,
                    totalElements: action.totalElements,
                    totalPages: action.totalPages,
                    lastPage: action.lastPage,
                },
            }
        case "FETCH_PRODUCT_REQUEST":
            return {
                ...state,
                productDetails: {
                    ...state.productDetails,
                    isLoading: true,
                    errorMessage: null,
                },
            };
        case "FETCH_PRODUCT_SUCCESS":
            return {
                ...state,
                productDetails: {
                    product: action.payload,
                    isLoading: false,
                    errorMessage: null,
                },
            };
        case "FETCH_PRODUCT_FAILURE":
            return {
                ...state,
                productDetails: {
                    product: null,
                    isLoading: false,
                    errorMessage: action.payload,
                },
            };
        case "CREATE_CATEGORY":
            return {
                ...state,
                categories: [...state.categories, action.payload],
            };
        case "UPDATE_CATEGORY":
            return {
                ...state,
                categories: state.categories.map((category) =>
                    category.id === action.payload.id ? action.payload : category
                ),
            };
        case "ADD_REVIEW":
            return {
                ...state,
                productDetails: {
                    ...state.productDetails,
                    product: {
                        ...state.productDetails.product,
                        reviews: [
                            ...(state.productDetails.product?.reviews || []),
                            action.payload,
                        ],
                    },
                },
            };
        case "FETCH_PRODUCT_REVIEW_REQUEST":
            return {
                ...state,
                reviews: {
                    data: [],
                    isLoading: true,
                    errorMessage: null,
                },
            };

        case "FETCH_PRODUCT_REVIEW_SUCCESS":
            return {
                ...state,
                reviews: {
                    data: action.payload,
                    isLoading: false,
                    errorMessage: null,
                },
            };

        case "FETCH_PRODUCT_REVIEW_FAILURE":
            return {
                ...state,
                reviews: {
                    data: [],
                    isLoading: false,
                    errorMessage: action.payload,
                },
            };
        default:
            return state;

    };
}