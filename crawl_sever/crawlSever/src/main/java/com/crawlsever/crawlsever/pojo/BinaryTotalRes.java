package com.crawlsever.crawlsever.pojo;

import lombok.Data;

import java.util.List;

@Data
public class BinaryTotalRes {
    private boolean isEnd;
    private List<BinaryRes> binaryResList;
}
