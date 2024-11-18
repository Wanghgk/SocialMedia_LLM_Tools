package com.wanghgk.crawlsever.service;

import com.wanghgk.crawlsever.pojo.BinaryRes;
import com.wanghgk.crawlsever.pojo.ClassifyRes;
import com.wanghgk.crawlsever.pojo.UpdateQuestionRes;

import java.util.List;

public interface ZhihuService {
    void runSupport(String questionId, String questionKeyWord);

    List<BinaryRes> getBinaryResultList(String questionId);

    List<ClassifyRes> getClassifyTotalRes(String questionId);

    boolean getIsEnd(String questionId);

    void shutDownNow(String questionId);

    UpdateQuestionRes saveAnswers(String questionId, String content, String information, String labels);

    List<UpdateQuestionRes> updateAnswers();

    void runClassify(String questionId, String questionKeyWord, List<String> opinions);
}
