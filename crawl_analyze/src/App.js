import logo from './logo.svg';
import {NavLink,useRoutes } from "react-router-dom";
import './App.css';
// import LeftBar from "./pages/LeftBar/LeftBar";
import routes from "./routes";
import LeftBar from "./pages/LeftBar/LeftBar";
import LeftBarV2 from "./pages/LeftBarV2/LeftBarV2";

function App() {
  const element = useRoutes(routes)

  return (
    <div className="App">
      {/*<div className="App-header">*/}
      {/*    <div className="header">*/}
      {/*      文本分析平台*/}
      {/*    </div>*/}
      {/*</div>*/}

      <div className="App-main">
        <div className="left-bar">
            <LeftBarV2/>
        </div>
        <div className="main-page">
            {element}
        </div>
      </div>

      <div className="App-footer">

      </div>
    </div>
  );
}

export default App;
