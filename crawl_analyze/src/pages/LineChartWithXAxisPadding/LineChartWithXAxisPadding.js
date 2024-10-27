import React, { PureComponent } from 'react';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ReferenceLine,
    ResponsiveContainer,
} from 'recharts';

export default class LineChartWithXAxisPadding extends PureComponent {

    constructor(props) {
        super(props);
        this.state = {
            data: [],
        };
    }

    componentDidMount() {
        this.groupAndFillMonths(this.props.list);
        console.log(this.state);
    }

    componentDidUpdate(prevProps) {
        // 只有在 props 变化时重新分组
        if (prevProps.list !== this.props.list) {
            this.groupAndFillMonths(this.props.list);
            console.log("折线图更新");
            console.log(this.state);
        }
    }

    groupByMonth(data) {
        return data.reduce((acc, item) => {
            const timestamp = item.createTime * 1000; // 将 createTime 转换为毫秒
            const date = new Date(timestamp);
            const year = date.getFullYear();
            const month = date.getMonth() + 1; // getMonth() returns 0-11, so add 1

            // 创建按年月组合的键（例如 "2024-10"）
            const key = `${year}-${String(month).padStart(2, '0')}`;

            // 如果键不存在于累加器中，初始化它
            if (!acc[key]) {
                acc[key] = [];
            }

            // 将项推入适当的月份组
            acc[key].push(item);

            return acc;
        }, {});
    }

    getDateRange(data) {
        const timestamps = data.map((item) => item.createTime * 1000);
        const earliestTimestamp = Math.min(...timestamps);
        const latestTimestamp = Math.max(...timestamps);
        const earliest = new Date(earliestTimestamp);
        const latest = new Date(latestTimestamp);

        return { earliest, latest };
    }

    generateFullMonthRange(earliest, latest) {
        const fullRange = {};
        let current = new Date(earliest);

        while (current <= latest) {
            const year = current.getFullYear();
            const month = current.getMonth() + 1; // getMonth() returns 0-11, so add 1
            const key = `${year}-${String(month).padStart(2, '0')}`;

            fullRange[key] = []; // 初始化每个月为空数组
            current.setMonth(current.getMonth() + 1); // 跳到下一个月
        }

        return fullRange;
    }

    groupAndFillMonths(data) {
        const grouped = this.groupByMonth(data);
        const { earliest, latest } = this.getDateRange(data);
        const fullRange = this.generateFullMonthRange(earliest, latest);

        // 合并分组后的数据与完整的月份范围
        let listData = []
        const totalStamp = {...fullRange, ...grouped};
        console.log(totalStamp);
        for(const key in totalStamp) {
            let agree = 0
            let disagree = 0
            let notMind = 0
            let error = 0
            for(const item of totalStamp[key]) {
                if(item.result === "支持") {
                    agree += this.props.getPlus(item.supports,item.thanks);
                }else if(item.result === "反对") {
                    disagree += this.props.getPlus(item.supports,item.thanks)
                }else if(item.result === "无法判断") {
                    notMind += this.props.getPlus(item.supports,item.thanks)
                }else if(item.result === "模型失效") {
                    error += this.props.getPlus(item.supports,item.thanks)
                }
            }
            listData[listData.length] = {
                "name": key,
                "agree": agree,
                "disagree": disagree,
                "notMind": notMind
            } ;
        }
        this.setState({data: listData})
    }

    render() {
        const {data} = this.state

        return (
            <ResponsiveContainer width="100%" height="100%">
                <LineChart width={500} height={300} data={data}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="name" padding={{ left: 30, right: 30 }} />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Line type="monotone" dataKey="agree" stroke="#8884d8" activeDot={{ r: 8 }} />
                    <Line type="monotone" dataKey="disagree" stroke="#82ca9d" />
                    <Line type="monotone" dataKey="notMind" stroke="#ff7300" />
                </LineChart>
            </ResponsiveContainer>
        );
    }
}
