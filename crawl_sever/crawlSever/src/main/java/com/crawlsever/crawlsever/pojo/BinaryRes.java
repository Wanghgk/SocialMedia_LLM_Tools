package com.crawlsever.crawlsever.pojo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BinaryRes {
    private String result;
    private Integer supports;
    private Integer thanks;
    private Integer createTime;

    public BinaryRes(String result, Integer supports, Integer thanks, Integer createTime) {
        this.result = result;
        this.supports = supports;
        this.thanks = thanks;
        this.createTime = createTime;
    }
}
