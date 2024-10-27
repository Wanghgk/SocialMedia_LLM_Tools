import {useState} from "react";


import Style from './LeftBarV2.module.css';
import {NavLink} from "react-router-dom";
export default function LeftBarV2() {
    const [activeLi, setActiveLi] = useState(0);

    return (
        <div className={Style["left-bar-v2"]}>
            <div className={Style["shell"]}>
                <ul className={Style["nav"]}>
                    <li className={activeLi===0 ? Style["active"]:Style[""]} id="logo"
                        onClick={()=> {
                            setActiveLi(0)
                        }}
                    >
                        <NavLink  to={""}>
                            <div className={Style["icon"]}>
                                <div className={Style["imageBox"]}>
                                    <img src="../image/11.gif" alt=""/>
                                </div>
                            </div>
                            <div className={Style["text"]}>工具库</div>
                        </NavLink>
                    </li>
                    <li className={activeLi===1 ? Style["active"]:Style[""]}
                        onClick={()=> {
                            setActiveLi(1)
                        }}
                    >
                        <NavLink to={"tools"}>
                            <div className={Style["icon"]}>
                                <i className={["iconfont", "icon-gongju"].join(' ')}></i>
                            </div>
                            <div className={Style["text"]}>Tools</div>
                        </NavLink>
                    </li>
                    <li className={activeLi===2 ? Style["active"]:Style[""]}
                        onClick={()=> {
                            setActiveLi(2)
                        }}
                    >
                        <NavLink to={""}>
                            <div className={Style["icon"]}>
                                <i className={["iconfont", "icon-Profile"].join(' ')}></i>
                            </div>
                            <div className={Style["text"]}>Profile</div>
                        </NavLink>
                    </li>
                </ul>
            </div>
        </div>
    )
}