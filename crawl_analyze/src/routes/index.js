import React from "react";
import Home from "../pages/Home/Home";
import ZhiHuBinaryShow from "../pages/ZhiHuBinaryShow/ZhiHuBinaryShow";
import Tools from "../pages/Tools/Tools";
import ZhiHuClassifyShow from "../pages/ZhiHuClassifyShow/ZhiHuClassifyShow";

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
        element: <ZhiHuBinaryShow/>
    },
    {
        path:'/ClassifyShow',
        element: <ZhiHuClassifyShow/>
    }


]