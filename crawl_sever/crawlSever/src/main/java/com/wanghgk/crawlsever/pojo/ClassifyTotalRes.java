package com.wanghgk.crawlsever.pojo;

import lombok.Data;

import java.util.List;

@Data
public class ClassifyTotalRes {
    private boolean isEnd;
    private List<ClassifyRes> classifyResList;

    public ClassifyTotalRes(boolean isEnd, List<ClassifyRes> classifyResList) {
        this.isEnd = isEnd;
        this.classifyResList = classifyResList;
    }
}
