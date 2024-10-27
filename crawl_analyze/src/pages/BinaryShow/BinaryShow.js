import {useRef, useState, useEffect} from "react";

import axiosUtils from "../../utils/axiosUtils";
import axios from "axios";
import sleepUtils from "../../utils/sleepUtils";
import qs from "qs";

import Style from "./BinaryShow.module.css";
import {Box, Button, TextField} from "@mui/material";
import QuestionAnswerOutlinedIcon from '@mui/icons-material/QuestionAnswerOutlined';
import CustomActiveShapePieChart from "../CustomActiveShapePieChart/CustomActiveShapePieChart";
import LineChartWithXAxisPadding from "../LineChartWithXAxisPadding/LineChartWithXAxisPadding";

export default function BinaryShow() {

    let [data, setData] = useState([]);
    let [submitted, setSubmitted] = useState(false);
    let [showResults, setShowResults] = useState(false);
    const questionId = useRef("");
    const keyWords = useRef("")
    const isEnd = useRef(true);
    const show = useRef();
    const [largeItem, setLargeItem] = useState(0);
    const Items = [
        {
            id:0,
            text:"回答直接分布",
            component: <CustomActiveShapePieChart list={data} getPlus={(support, thanks) => {
                return 1
            }}/>
        },
        {
            id:1,
            text: "回答加权分布",
            component: <CustomActiveShapePieChart list={data} getPlus={(support, thanks) => {
                return Number(support + thanks)
            }}/>
        },
        {
            id:2,
            text: "时间直接分布",
            component: <LineChartWithXAxisPadding list={data} getPlus={(support, thanks) => {
                return 1
            }}/>
        },
        {
            id:3,
            text: "时间加权分布",
            component: <LineChartWithXAxisPadding list={data} getPlus={(support, thanks) => {
                return Number(support + thanks)
            }}/>
        }
    ]

    const runModel = () => {
        let sendData = {
            questionId: questionId.current,
            questionKeyWord: keyWords.current
        }
        console.log(sendData);
        axios.post("http://localhost:8080/anazhihu/support", sendData)
            .then((response) => {
                console.log(response)
                setSubmitted(false);
            })
            .catch((err)=>{
                console.log(err);
            })
    }

    const getResults = () =>{
        let sendData = {
            questionId: questionId.current
        }
        axios.post("http://localhost:8080/anazhihu/result", sendData)
            .then(
                (response) => {
                    console.log(response.data);
                    isEnd.current = response.data.end
                    setData(response.data.binaryResList);
                }
            )
            .catch(
                (err)=>{
                    console.log(err)
                }
            )
        console.log("isEnd:",isEnd.current);

    }

    const beginProcess = () => {
        setTimeout(async () => {
            while (!isEnd.current) {
                // console.log("查询结果")
                getResults()
                await sleepUtils(10000)
            }
        },10000)

        runModel()

    }

    const endProcess = () => {
        let sendData = {
            questionId: questionId.current
        }
        axios.post("http://localhost:8080/anazhihu/shutDownNow", sendData)
            .catch((response)=>{
                isEnd.current = true
                setSubmitted(false)
                setData(response.data.binaryResList);
            })
    }

    useEffect(()=>{
        console.log(data)
    },[data])

    return (
        <div className={Style["binary-show"]}>
            <div className={Style["title"]} style={{marginTop:showResults?"0":"30vh"}}><h1>知乎回答特定方面支持情况</h1></div>
            <div className={Style["description"]}></div>
            <div className={Style["information"]}>
                <Box sx={{display: 'flex', alignItems: 'flex-end'}}>
                    <QuestionAnswerOutlinedIcon sx={{mr: 1, my: 0.5}}/>
                    <TextField onChange={(e) => {
                        if(!submitted){
                            questionId.current = e.target.value
                        }else{
                            e.target.value = questionId.current
                        }
                    }} id="input-question-id" label="问题id" variant="standard" sx={{input: {color:"#cccff1"}, label: {color: "#ccece6"}}}/>
                </Box>
                <Box sx={{display: 'flex', alignItems: 'flex-end'}}>
                    <QuestionAnswerOutlinedIcon sx={{color: 'action.active', mr: 1, my: 0.5}}/>
                    <TextField onChange={(e) => {
                        if(!submitted){
                            keyWords.current = e.target.value
                        }else{
                            e.target.value = keyWords.current
                        }
                    }} id="input-question-topic" label="问题关键词" variant="standard" sx={{input: {color:"#cccff1"}, label: {color: "#ccece6"}}}/>
                </Box>
            </div>
            <div className={Style["submit"]}>
                <Button onClick={()=> {
                    if(!submitted) {
                        isEnd.current = false;
                        setSubmitted(true)
                        setShowResults(true)
                        beginProcess()
                    }else {
                        console.log("shut down!")
                        isEnd.current = true
                        endProcess()
                    }
                }} variant="contained" color="primary"
                        sx={{fontSize: "20px"}}>{!submitted?"开始分析":"停止分析"}</Button>
            </div>

            <div className={Style["show"]} ref={show} style={{display: showResults ? "block" : "none"}} >
                <div className={Style["result"]}>
                    <div className={Style["statistics"]}>
                        <h2>已处理数/总问题数 : 114514/1919810</h2>
                    </div>

                    <div className={Style["result-row"]}>
                        {Items.map((item)=>{
                            return (
                                <div className={Style["result-item"]} key={item.id}>
                                    <h2>{item.text}</h2>
                                    {item.component}
                                    <div className={Style["musk"]} onClick={()=> {
                                        setLargeItem(item.id)
                                    }}/>
                                </div>
                            )
                        })}
                    </div>

                    <div className={Style["result-item-large"]}>
                        <h2>{Items[largeItem].text}</h2>
                        {Items[largeItem].component}
                    </div>


                </div>
            </div>

        </div>
    )
}