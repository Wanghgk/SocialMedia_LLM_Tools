import React from "react";
import Home from "../pages/Home/Home";
import BinaryShow from "../pages/BinaryShow/BinaryShow";
import Tools from "../pages/Tools/Tools";

export default [
    {
        path: '/',
        element: <Home/>
    },
    {
      path: 'tools',
      element: <Tools/>
    },
    {
        path: '/BinaryShow',
        element: <BinaryShow/>
    }

]