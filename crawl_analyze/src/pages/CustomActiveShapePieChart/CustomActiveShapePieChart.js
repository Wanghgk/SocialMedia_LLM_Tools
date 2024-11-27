import React, { PureComponent } from 'react';
import { PieChart, Pie, Sector, ResponsiveContainer } from 'recharts';



const renderActiveShape = (props) => {
    const RADIAN = Math.PI / 180;
    const { cx, cy, midAngle, innerRadius, outerRadius, startAngle, endAngle, fill, payload, percent, value } = props;
    const sin = Math.sin(-RADIAN * midAngle);
    const cos = Math.cos(-RADIAN * midAngle);
    const sx = cx + (outerRadius + 10) * cos;
    const sy = cy + (outerRadius + 10) * sin;
    const mx = cx + (outerRadius + 30) * cos;
    const my = cy + (outerRadius + 30) * sin;
    const ex = mx + (cos >= 0 ? 1 : -1) * 22;
    const ey = my;
    const textAnchor = cos >= 0 ? 'start' : 'end';

    return (
        <g>
            <text x={cx} y={cy} dy={8} textAnchor="middle" fill={"#000"} style={{display:"block",width:"30px",height:"100px",whiteSpace:"nowrap",overflow:"hidden",textOverflow:"ellipsis"}} >
                {payload.name}
            </text>
            <Sector
                cx={cx}
                cy={cy}
                innerRadius={innerRadius}
                outerRadius={outerRadius}
                startAngle={startAngle}
                endAngle={endAngle}
                fill={fill}
            />
            <Sector
                cx={cx}
                cy={cy}
                startAngle={startAngle}
                endAngle={endAngle}
                innerRadius={outerRadius + 6}
                outerRadius={outerRadius + 10}
                fill={fill}
            />
            <path d={`M${sx},${sy}L${mx},${my}L${ex},${ey}`} stroke={fill} fill="none" />
            <circle cx={ex} cy={ey} r={2} fill={fill} stroke="none" />
            <text x={ex + (cos >= 0 ? 1 : -1) * 12} y={ey} textAnchor={textAnchor} fill="#333">{`${value}`}</text>
            <text x={ex + (cos >= 0 ? 1 : -1) * 12} y={ey} dy={18} textAnchor={textAnchor} fill="#999">
                {`(Rate ${(percent * 100).toFixed(2)}%)`}
            </text>
        </g>
    );
};

export default class CustomActiveShapePieChart extends PureComponent {

    constructor(props) {
        super(props);
        this.state = {
            data:this.props.resultNames,
            activeIndex: 0,
        };
    }

    componentDidMount() {
        this.listToData(this.props.list)
    }

    componentDidUpdate(prevProps) {
        // 只有在 props 变化时重新分组
        if (prevProps.list !== this.props.list) {
            this.listToData(this.props.list)
        }
    }

    listToData(list) {
        let resultNames = this.props.resultNames;
        list.forEach((item) => {
            resultNames.forEach((resultName,index) => {
                if (item.result === resultName.name) {
                    resultNames[index].value += this.props.getPlus(item.supports, item.thanks)
                }
            })
        })

        this.setState({
            data: resultNames,
        })
    }

    onPieEnter = (_, index) => {
        this.setState({
            activeIndex: index,
        });
    };

    render() {
        const {data} = this.state

        return (
            <ResponsiveContainer width="100%" height="100%">
                <PieChart width={this.props.width} height={this.props.height}>
                    <Pie
                        activeIndex={this.state.activeIndex}
                        activeShape={renderActiveShape}
                        data={data}
                        cx="50%"
                        cy="50%"
                        innerRadius={this.props.width - 20}
                        outerRadius={this.props.width}
                        fill="#787878"
                        dataKey="value"
                        onMouseEnter={this.onPieEnter}
                    />
                </PieChart>
            </ResponsiveContainer>
        );
    }
}
