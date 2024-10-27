import {Grid2} from "@mui/material";
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import ToolCard from "../ToolCard";
import data from "./data.json";

import Style from './Tools.module.css';

export default function Tools() {
    const {tool} = data

    return (
        <div className={Style["tools"]}>
            <div className={Style["card-list"]}>
                <Grid2 container direction="row" spacing={0} style={{width:'90vw', marginLeft:'auto',marginRight:'auto'}}>
                    <Grid2 size={4} item>
                        <div className={Style["tool-card"]}>
                            <ToolCard image={tool[0].picture} name={tool[0].name} description={tool[0].description}/>
                        </div>
                    </Grid2>
                    <Grid2 size={4} item>
                        <div className={Style["tool-card"]}>
                            <ToolCard image={tool[0].picture} name={tool[0].name} description={tool[0].description}/>
                        </div>
                    </Grid2>
                    <Grid2 size={4} item>
                        <div className={Style["tool-card"]}>
                            <ToolCard image={tool[0].picture} name={tool[0].name} description={tool[0].description}/>
                        </div>
                    </Grid2>
                </Grid2>
            </div>

        </div>
    )
}