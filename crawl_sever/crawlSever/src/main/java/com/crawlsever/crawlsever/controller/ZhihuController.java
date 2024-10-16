package com.crawlsever.crawlsever.controller;

import com.alibaba.fastjson.JSONObject;
import com.crawlsever.crawlsever.pojo.BinaryRes;
import com.crawlsever.crawlsever.pojo.BinaryTotalRes;
import com.crawlsever.crawlsever.service.ZhihuService;
import com.crawlsever.crawlsever.utils.ProcessMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    @PostMapping("/result")
    public BinaryTotalRes resultRequest(@RequestBody String data) {
        JSONObject dataObj = JSONObject.parseObject(data);
        System.out.println(dataObj);
        String questionId = dataObj.getString("questionId");
        List<BinaryRes> resList = zhihuService.getResultList(questionId);
        BinaryTotalRes binaryTotalRes = new BinaryTotalRes();
        binaryTotalRes.setBinaryResList(resList);
        boolean isEnd = zhihuService.getIsEnd(questionId);
        binaryTotalRes.setEnd(isEnd);

        return binaryTotalRes;
    }

    @PostMapping("shutDownNow")
    public String shutdownNow(@RequestBody String data) {
        JSONObject dataObj = JSONObject.parseObject(data);
        String questionId = dataObj.getString("questionId");
        System.out.println("开始停止"+questionId+"号问题");
        zhihuService.shutDownNow(questionId);
        System.out.println("已经停止"+questionId+"号问题");

        return "已停止";
    }
}
