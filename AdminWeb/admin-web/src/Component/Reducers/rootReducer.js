import {combineReducers} from "redux"

const DEFAULT_REDUCER = (initstate,action)=>{
    return {
        key: "HELLO WORD",
    };
};

const rootReducer = combineReducers({
    DEFAULE: DEFAULT_REDUCER,
});

export default rootReducer;