
import Style from "./Home.module.css";
import TurnTable from "../TurnTable/TurnTable";

export default function Home() {

    return (
        <div className={Style["home"]}>
            <TurnTable changeStick={()=> {}} flushStick={()=>{}}/>
        </div>
    )
}