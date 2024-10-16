package com.crawlsever.crawlsever.service.impl;

import com.crawlsever.crawlsever.pojo.BinaryRes;
import com.crawlsever.crawlsever.service.ZhihuService;
import com.crawlsever.crawlsever.utils.ProcessMessageUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZhihuServiceImpl implements ZhihuService {

    private Map<String,ProcessMessageUtil> processMessageUtilMap = new HashMap<>();

    @Async
    @Override
    public void runSupport(String questionId, String questionKeyWord) {

        if(processMessageUtilMap.containsKey(questionId)){

        }else {
            processMessageUtilMap.put(questionId,new ProcessMessageUtil(questionId, questionKeyWord));
        }
        ProcessMessageUtil processMessageUtil = processMessageUtilMap.get(questionId);

        System.out.println(questionId+"号模型即将开始");
        processMessageUtil.binaryJudge();

        try {
            Thread.sleep(1000*20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        processMessageUtilMap.remove(questionId);
    }

    @Override
    public List<BinaryRes> getResultList(String questionId) {
        return processMessageUtilMap.get(questionId).getResList();
    }

    @Override
    public boolean getIsEnd(String questionId) {
        return processMessageUtilMap.get(questionId).getIsEnd();
    }

    @Override
    public void shutDownNow(String questionId) {
        processMessageUtilMap.get(questionId).shutDownNow();
        try {
            Thread.sleep(1000*20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        processMessageUtilMap.remove(questionId);
    }

//    class flushMap extends Thread {
//
//        @Override
//        public void run() {
//            sharedMap.put(sharedqQestionId, sharedProcessMessageUtil.getResList());
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
}