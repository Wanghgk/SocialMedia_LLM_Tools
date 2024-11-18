package com.wanghgk.crawlsever.pojo;


import lombok.Data;

import java.math.BigInteger;

@Data
public class UpdateQuestionRes {
    private BigInteger id;
    private Integer count;
    private Integer updateCount;
    private Integer createCount;
}
