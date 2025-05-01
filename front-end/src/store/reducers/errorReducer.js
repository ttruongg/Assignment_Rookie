const initialState = {
    isLoading: false,
    errorMessage: null,
    categoryLoader: false,
    categoryError: null,
};

export const errorReducer = (state = initialState, action) => {
    switch (action.type) {
        case "IS_FETCHING":
            return {
                ...state,
                isLoading: true,
                errorMessage: null,
            };
        case "IS_FETCHED":
            return {
                ...state,
                isLoading: false,
                errorMessage: null,
            }
        case "IS_ERROR":
            return {
                ...state,
                isLoading: false,
                errorMessage: action.payload,
            };

        case "SUCCESS_CATEGORY":
            return {
                ...state,
                categoryLoader: false,
                categoryError: null,
            }
        case "LOADING_CATEGORY":
            return {
                ...state,
                categoryLoader: true,
                categoryError: null,
                errorMessage: null,
            };
        default:
            return state;
    };






}