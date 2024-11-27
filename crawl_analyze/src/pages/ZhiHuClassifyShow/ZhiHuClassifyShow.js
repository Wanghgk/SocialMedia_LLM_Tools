import Style from './ZhiHuClassifyShow.module.css';
import {
    Avatar,
    Box,
    Button,
    IconButton,
    List,
    ListItem,
    ListItemAvatar, ListItemText,
    styled,
    TextField,
    Typography
} from "@mui/material";
import QuestionAnswerOutlinedIcon from "@mui/icons-material/QuestionAnswerOutlined";
import React, {useEffect, useRef, useState} from "react";
import CustomActiveShapePieChart from "../CustomActiveShapePieChart/CustomActiveShapePieChart";
import LineChartWithXAxisPadding from "../LineChartWithXAxisPadding/LineChartWithXAxisPadding";
import sleepUtils from "../../utils/sleepUtils";
import axios from "axios";
import FolderIcon from '@mui/icons-material/Folder';
import DeleteIcon from '@mui/icons-material/Delete';
import IP from "../../static/ip.json";

export default function ZhiHuClassifyShow() {

    let [data, setData] = useState([]);
    let [submitted, setSubmitted] = useState(false);
    let [showResults, setShowResults] = useState(false);
    const questionId = useRef("");
    const questionKeyWord = useRef("")
    const theme = useRef("")
    const isEnd = useRef(true);
    const show = useRef();
    const [largeItem, setLargeItem] = useState(0);
    const [themes, setThemes] = useState([]);

    let classifyNames = useRef([])

    useEffect(() => {
        let tmpData = data
        setData([])
        setTimeout(()=>{
            setData(tmpData)
        },500)
    }, [largeItem]);

    const Items = [
        {
            id: 0,
            text: "回答直接分布",
            component: <CustomActiveShapePieChart key={"回答直接分布" + data.length} list={data} width={100} height={100} getPlus={(support, thanks) => {
                return 1
            }} resultNames={classifyNames.current}/>,
            largeComponent: <CustomActiveShapePieChart key={"回答直接分布-large"} list={data} width={300} height={300} getPlus={(support, thanks) => {
                return 1
            }} resultNames={classifyNames.current}/>
        },
        {
            id: 1,
            text: "回答加权分布",
            component: <CustomActiveShapePieChart key={"回答加权分布" + data.length} list={data} width={100} height={100} getPlus={(support, thanks) => {
                return Number(support + thanks)
            }} resultNames={classifyNames.current}/>,
            largeComponent: <CustomActiveShapePieChart key={"回答加权分布-large"} list={data} width={300} height={300} getPlus={(support, thanks) => {
                return Number(support + thanks)
            }} resultNames={classifyNames.current}/>
        },
        {
            id: 2,
            text: "时间直接分布",
            component: <LineChartWithXAxisPadding key={"时间直接分布" + data.length} list={data} getPlus={(support, thanks) => {
                return 1
            }} resultNames={classifyNames.current}/>,
            largeComponent: <LineChartWithXAxisPadding key={"时间直接分布-large"} list={data} getPlus={(support, thanks) => {
                return 1
            }} resultNames={classifyNames.current}/>
        },
        {
            id: 3,
            text: "时间加权分布",
            component: <LineChartWithXAxisPadding key={"时间加权分布" + data.length} list={data} getPlus={(support, thanks) => {
                return Number(support + thanks)
            }} resultNames={classifyNames.current}/>,
            largeComponent: <LineChartWithXAxisPadding key={"时间加权分布-large"} list={data} getPlus={(support, thanks) => {
                return Number(support + thanks)
            }} resultNames={classifyNames.current}/>
        }
    ]

    const generateNames = () => {
        classifyNames.current.push({
            name: theme.current,
            value: 0
        })
        let newTheme = [...themes, theme.current]
        console.log(theme.current)
        setThemes(newTheme)
    }

    const deleteName = (index) => {
        classifyNames.current.splice(index, 1)
        let newTheme = []
        classifyNames.current.forEach((item) => {
            newTheme.push(item.name)
        })
        // console.log(newTheme)

        setThemes(newTheme)
    }

    const Demo = styled('div')(({theme}) => ({
        backgroundColor: "rgba(122,124,133,0.6)",
        color: "#fff",
        paddingRight: theme.spacing(2),
        borderRadius: "25px"
    }));

    const beginProcess = () => {
        setTimeout(async () => {
            while (!isEnd.current) {
                // console.log("查询结果")
                getResults()
                await sleepUtils(10000)
            }
        }, 10000)

        runModel()

    }

    const endProcess = () => {
        let sendData = {
            questionId: questionId.current
        }
        axios.post(IP.localhost+"/anazhihu/shutDownNow", sendData)
            .catch((response) => {
                isEnd.current = true
                setSubmitted(false)
                setData(response.data.classifyResList);
            })
    }

    const runModel = () => {
        let newTheme = []
        classifyNames.current.forEach((item) => {
            newTheme.push(item.name)
        })
        let sendData = {
            questionId: questionId.current,
            questionKeyWord: questionKeyWord.current,
            opinions: newTheme,
        }
        // console.log(sendData);
        axios.post(IP.localhost+"/anazhihu/classify", sendData)
            .then((response) => {
                console.log(response)
                setSubmitted(false);
            })
            .catch((err) => {
                console.log(err);
            })
    }

    const getResults = () => {
        let sendData = {
            questionId: questionId.current
        }
        axios.post(IP.localhost+"/anazhihu/classifyResult", sendData)
            .then(
                (response) => {
                    console.log(response.data);
                    isEnd.current = response.data.end
                    setData(response.data.classifyResList);
                }
            )
            .catch(
                (err) => {
                    console.log(err)
                }
            )
        console.log("isEnd:", isEnd.current);

    }


    return (
        <div className={Style["classify-show"]}>
            <div className={Style["title"]} style={{marginTop: showResults ? "0" : "30vh"}}>
                <h1>知乎回答特定方面支持情况</h1></div>
            <div className={Style["description"]}></div>
            <div className={Style["information"]}>
                <Box sx={{display: 'flex', alignItems: 'flex-end'}}>
                    <QuestionAnswerOutlinedIcon sx={{mr: 1, my: 0.5}}/>
                    <TextField onChange={(e) => {
                        if (!submitted) {
                            questionId.current = e.target.value
                        } else {
                            e.target.value = questionId.current
                        }
                    }} id="input-question-id" label="问题id" variant="standard"
                               sx={{input: {color: "#cccff1"}, label: {color: "#ccece6"}}}/>
                </Box>
                <Box sx={{display: 'flex', alignItems: 'flex-end'}}>
                    <QuestionAnswerOutlinedIcon sx={{color: 'action.active', mr: 1, my: 0.5}}/>
                    <TextField onChange={(e) => {
                        if (!submitted) {
                            questionKeyWord.current = e.target.value
                        } else {
                            e.target.value = questionKeyWord.current
                        }
                    }} id="input-question-topic" label="分析方面" variant="standard"
                               sx={{input: {color: "#cccff1"}, label: {color: "#ccece6"}}}/>
                </Box>
                <Box sx={{display: 'flex', alignItems: 'flex-end'}}>
                    <QuestionAnswerOutlinedIcon sx={{color: 'action.active', mr: 1, my: 0.5}}/>
                    <TextField onChange={(e) => {
                        if (!submitted) {
                            theme.current = e.target.value
                        } else {
                            e.target.value = theme.current
                        }
                    }} id="input-question-topic" label="输入待匹配观点" variant="standard"
                               sx={{input: {color: "#cccff1"}, label: {color: "#ccece6"}}}/>
                    <Button onClick={generateNames}
                            sx={{backgroundColor: "#1976d2", color: "#fff", marginLeft: "20px"}}>添加</Button>
                </Box>
            </div>
            <div className={Style["submit"]}>
                <Button onClick={() => {
                    if (!submitted) {
                        isEnd.current = false;
                        setSubmitted(true)
                        setShowResults(true)
                        beginProcess()
                    } else {
                        console.log("shut down!")
                        isEnd.current = true
                        endProcess()
                    }
                }} variant="contained" color="primary"
                        sx={{fontSize: "20px"}}>{!submitted ? "开始分析" : "停止分析"}</Button>
            </div>

            <div className={Style["names"]}>
                <h2>待匹配观点</h2>
                <Demo>
                    {
                        themes.map((item, index) => {
                            return (
                                <List key={index + "" + item} dense={true}>
                                    <ListItem
                                        secondaryAction={
                                            <IconButton edge="end" aria-label="delete" onClick={()=>{deleteName(index)}}>
                                                <DeleteIcon sx={{color: "#5d7ddc"}}/>
                                            </IconButton>
                                        }
                                    >
                                        <ListItemText
                                            primary={item}
                                            secondary={null}
                                            sx={{background: "transparent"}}
                                        />
                                    </ListItem>,
                                </List>
                            )
                        })
                    }
                </Demo>
            </div>

            <div className={Style["show"]} ref={show} style={{display: showResults ? "block" : "none"}}>
                <div className={Style["result"]}>
                    <div className={Style["statistics"]}>
                        <h2>已分析回答数: {data.length}</h2>
                    </div>

                    <div className={Style["result-row"]}>
                        {Items.map((item) => {
                            return (
                                <div className={Style["result-item"]} key={item.id}>
                                    <h2>{item.text}</h2>
                                    {item.component}
                                    <div className={Style["musk"]} onClick={() => {
                                        setLargeItem(item.id)
                                    }}/>
                                </div>
                            )
                        })}
                    </div>

                    <div className={Style["result-item-large"]}>
                        <h2>{Items[largeItem].text}</h2>
                        {Items[largeItem].largeComponent}
                    </div>


                </div>
            </div>

        </div>
    )
}