package com.wanghgk.crawlsever.pojo;

import java.util.List;

public class ClassifyTotalRes {
    private boolean isEnd;
    private List<ClassifyRes> classifyResList;

    public ClassifyTotalRes(boolean isEnd, List<ClassifyRes> classifyResList) {
        this.isEnd = isEnd;
        this.classifyResList = classifyResList;
    }
}
