package com.crawlsever.crawlsever.service;

import com.crawlsever.crawlsever.pojo.BinaryRes;

import java.util.List;
import java.util.Map;

public interface ZhihuService {
    void runSupport(String questionId, String questionKeyWord);

    List<BinaryRes> getResultList(String questionId);

    boolean getIsEnd(String questionId);

    void shutDownNow(String questionId);
}
