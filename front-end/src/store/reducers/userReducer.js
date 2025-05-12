const initialState = {
    users: [],
    pagination: {},
};

export const userReducer = (state = initialState, action) => {
    switch (action.type) {
        case "FETCH_USERS":
            return {
                ...state,
                users: action.payload,
                pagination: {},
            };

        default:
            return state;
    }
};
