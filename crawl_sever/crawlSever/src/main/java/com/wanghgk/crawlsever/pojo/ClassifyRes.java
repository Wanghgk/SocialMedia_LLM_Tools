package com.wanghgk.crawlsever.pojo;

import lombok.Data;

@Data
public class ClassifyRes {
    private String result;
    private Integer supports;
    private Integer thanks;
    private Integer createTime;

    public ClassifyRes(String result, Integer supports, Integer thanks, Integer createTime) {
        this.result = result;
        this.supports = supports;
        this.thanks = thanks;
        this.createTime = createTime;
    }
}
