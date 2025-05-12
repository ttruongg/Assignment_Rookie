import { configureStore } from "@reduxjs/toolkit";

import { productReducer } from "./ProductReducer";
import { errorReducer } from "./errorReducer";
import { authReducer } from "./authReducer";
import { userReducer } from "./userReducer";

const user = localStorage.getItem("auth")
    ? JSON.parse(localStorage.getItem("auth"))
    : [];

export const store = configureStore({
    reducer: {
        products: productReducer,
        errors: errorReducer,
        auth: authReducer,
        users: userReducer
    },
    preloadedState: {},
});

export default store;