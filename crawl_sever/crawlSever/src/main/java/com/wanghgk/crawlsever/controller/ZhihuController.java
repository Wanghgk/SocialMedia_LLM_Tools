package com.wanghgk.crawlsever.controller;

import com.alibaba.fastjson.JSONObject;
import com.wanghgk.crawlsever.pojo.*;
import com.wanghgk.crawlsever.service.ZhihuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/anazhihu")
public class ZhihuController {

    @Autowired
    private ZhihuService zhihuService;

    @PostMapping("/support")
    public String support(@RequestBody String data) {
        JSONObject dataObj = JSONObject.parseObject(data);
        String questionId = dataObj.getString("questionId");
        String questionKeyWord = dataObj.getString("questionKeyWord");
        System.out.println(questionId);
        System.out.println(questionKeyWord);
        zhihuService.runSupport(questionId, questionKeyWord);

        return "运行完成";
    }

    @PostMapping("/classify")
    public String classify(@RequestBody String data) {
        JSONObject dataObj = JSONObject.parseObject(data);
        String questionId = dataObj.getString("questionId");
        String questionKeyWord = dataObj.getString("questionKeyWord");
        List<String> opinions = dataObj.getJSONArray("opinions").toJavaList(String.class);

        zhihuService.runClassify(questionId,questionKeyWord,opinions);

        return "运行完成";
    }

    @PostMapping("/binaryResult")
    public BinaryTotalRes binaryResultRequest(@RequestBody String data) {
        JSONObject dataObj = JSONObject.parseObject(data);
        System.out.println(dataObj);
        String questionId = dataObj.getString("questionId");
        List<BinaryRes> resList = zhihuService.getBinaryResultList(questionId);
        BinaryTotalRes binaryTotalRes = new BinaryTotalRes();
        binaryTotalRes.setBinaryResList(resList);
        boolean isEnd = zhihuService.getIsEnd(questionId);
        binaryTotalRes.setEnd(isEnd);

        return binaryTotalRes;
    }

    @PostMapping("/classifyResult")
    public ClassifyTotalRes classifyResultRequest(@RequestBody String data) {
        JSONObject dataObj = JSONObject.parseObject(data);
        String questionId = dataObj.getString("questionId");
        List<ClassifyRes> resList = zhihuService.getClassifyTotalRes(questionId);
        boolean isEnd = zhihuService.getIsEnd(questionId);
        ClassifyTotalRes classifyTotalRes = new ClassifyTotalRes(isEnd,resList);

        return classifyTotalRes;
    }

    @PostMapping("/shutDownNow")
    public String shutdownNow(@RequestBody String data) {
        JSONObject dataObj = JSONObject.parseObject(data);
        String questionId = dataObj.getString("questionId");
        System.out.println("开始停止"+questionId+"号问题");
        zhihuService.shutDownNow(questionId);
        System.out.println("已经停止"+questionId+"号问题");

        return "已停止";
    }

    @PostMapping("/save")
    public UpdateQuestionRes save(@RequestBody String data) {
        JSONObject dataObj = JSONObject.parseObject(data);
        String questionId = dataObj.getString("questionId");
        String content = dataObj.getString("content");
        String information = dataObj.getString("information");
        String labels = dataObj.getString("labels");
        System.out.println("开始保存"+questionId+"号问题");
        UpdateQuestionRes updateQuestionRes = zhihuService.saveAnswers(questionId, content, information, labels);
        System.out.println("已经停止"+questionId+"号问题");

        return updateQuestionRes;
    }

    @PostMapping("/update")
    public List<UpdateQuestionRes> update() {
        System.out.println("开始更新");
        List<UpdateQuestionRes> updateQuestionResList = zhihuService.updateAnswers();

        return updateQuestionResList;
    }
}
