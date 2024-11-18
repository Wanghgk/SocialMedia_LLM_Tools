package com.wanghgk.crawlsever.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wanghgk.crawlsever.mapper.ZhiHuMapper;
import com.wanghgk.crawlsever.pojo.BinaryRes;
import com.wanghgk.crawlsever.pojo.ClassifyRes;
import com.wanghgk.crawlsever.pojo.UpdateQuestionRes;
import com.wanghgk.crawlsever.service.ZhihuService;
import com.wanghgk.crawlsever.utils.ParamMessageUtil;
import com.wanghgk.crawlsever.utils.ProcessMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZhihuServiceImpl implements ZhihuService {

    private Map<String,ProcessMessageUtil> processMessageUtilMap = new HashMap<>();

    @Autowired
    private ZhiHuMapper zhiHuMapper;

    @Async
    @Override
    public void runSupport(String questionId, String questionKeyWord) {

        if(processMessageUtilMap.containsKey(questionId)){

        }else {
            processMessageUtilMap.put(questionId,new ProcessMessageUtil(questionId, questionKeyWord));
        }
        ProcessMessageUtil processMessageUtil = processMessageUtilMap.get(questionId);

        System.out.println(questionId+"号模型即将开始");
        processMessageUtil.startJudge();

        try {
            Thread.sleep(1000*20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        processMessageUtilMap.remove(questionId);
    }

    @Override
    public void runClassify(String questionId, String questionKeyWord, List<String> opinions) {

        if(processMessageUtilMap.containsKey(questionId)){

        }else {
            processMessageUtilMap.put(questionId,new ProcessMessageUtil(questionId, questionKeyWord, opinions));
        }
        ProcessMessageUtil processMessageUtil = processMessageUtilMap.get(questionId);
    }

    @Override
    public List<ClassifyRes> getClassifyTotalRes(String questionId) {
        return processMessageUtilMap.get(questionId).getClassifyResList();
    }

    @Override
    public List<BinaryRes> getBinaryResultList(String questionId) {
        return processMessageUtilMap.get(questionId).getBinaryResList();
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

    @Override
    public UpdateQuestionRes saveAnswers(String questionId, String content, String information, String labels) {
        BigInteger idInteger = BigInteger.valueOf(Long.parseLong(questionId.trim()));
        String tmpContent = zhiHuMapper.getContentById(idInteger);
        if(tmpContent == null){
            zhiHuMapper.addQuestion(idInteger, content, information, labels);
        }else if(!tmpContent.equals(content)){
            zhiHuMapper.updateQuestion(idInteger, content, information);
        }
        Integer count = 0;
        Integer updateCount = 0;
        Integer createCount = 0;
        ParamMessageUtil paramMessageUtil = new ParamMessageUtil(String.valueOf(idInteger));
        while (!paramMessageUtil.getIsEnd()) {
            List<JSONObject> answers = paramMessageUtil.getNext();
            for (Object answerObject : answers) {
                JSONObject answer = (JSONObject) JSONObject.parseObject((String) answerObject);
                String answerContent = answer.getJSONObject("target").getString("content");
                Integer support = answer.getJSONObject("target").getInteger("voteup_count");
                Integer thanks = answer.getJSONObject("target").getInteger("thanks_count");
                Integer createTime = answer.getJSONObject("target").getInteger("created_time");
                Integer updateTime = answer.getJSONObject("target").getInteger("updated_time");
                BigInteger id = answer.getJSONObject("target").getBigInteger("id");


                Integer tmptime = zhiHuMapper.getUpdateTime(id);

                if(tmptime == null){
                    zhiHuMapper.addAnswer(id, idInteger, answerContent, support, thanks, createTime, updateTime);
                    ++createCount;
                }else if(tmptime < updateTime){
                    zhiHuMapper.updateAnswer(id, idInteger, answerContent, support, thanks, updateTime);
                    ++updateCount;
                }

                ++count;
            }
        }
        zhiHuMapper.updateQuestionUpdateTime(idInteger);
        UpdateQuestionRes updateQuestionRes = new UpdateQuestionRes();
        updateQuestionRes.setId(idInteger);
        updateQuestionRes.setCount(count);
        updateQuestionRes.setUpdateCount(updateCount);
        updateQuestionRes.setCreateCount(createCount);

        return updateQuestionRes;
    }

    @Override
    public List<UpdateQuestionRes> updateAnswers() {
        List<BigInteger> questionIds = zhiHuMapper.getQuestionIds();
        Integer count = 0;
        Integer updateCount = 0;
        Integer createCount = 0;
        List<UpdateQuestionRes> updateQuestionResList = new ArrayList<>();
        for (BigInteger questionId : questionIds) {
            boolean toUpdate = zhiHuMapper.getToUpdateById(questionId);
            System.out.println(toUpdate);
            if(toUpdate){
                try {
                    ParamMessageUtil paramMessageUtil = new ParamMessageUtil(String.valueOf(questionId));
                    while (!paramMessageUtil.getIsEnd()) {
                        List<JSONObject> answers = paramMessageUtil.getNext();
                        for (Object answerObject : answers) {
                            JSONObject answer = (JSONObject) JSONObject.parseObject((String) answerObject);
                            String answerContent = answer.getJSONObject("target").getString("content");
                            Integer support = answer.getJSONObject("target").getInteger("voteup_count");
                            Integer thanks = answer.getJSONObject("target").getInteger("thanks_count");
                            Integer createTime = answer.getJSONObject("target").getInteger("created_time");
                            Integer updateTime = answer.getJSONObject("target").getInteger("updated_time");
                            BigInteger id = answer.getJSONObject("target").getBigInteger("id");


                            Integer tmptime = zhiHuMapper.getUpdateTime(id);

                            if(tmptime == null){
                                zhiHuMapper.addAnswer(id, questionId, answerContent, support, thanks, createTime, updateTime);
                                ++createCount;
                            }else if(tmptime < updateTime){
                                zhiHuMapper.updateAnswer(id, questionId, answerContent, support, thanks, updateTime);
                                ++updateCount;
                            }

                            ++count;
                        }
                    }
                    zhiHuMapper.updateQuestionUpdateTime(questionId);
                }catch (Exception e){
                    System.out.println(questionId+"号问题出现异常。错误信息"+e.getMessage());
                }
            }


            UpdateQuestionRes updateQuestionRes = new UpdateQuestionRes();
            updateQuestionRes.setId(questionId);
            updateQuestionRes.setCount(count);
            updateQuestionRes.setUpdateCount(updateCount);
            updateQuestionRes.setCreateCount(createCount);
            updateQuestionResList.add(updateQuestionRes);
            count=0;
            updateCount=0;
            createCount=0;
        }

        return updateQuestionResList;
    }



//    class flushMap extends Thread {
//
//        @Override
//        public void run() {
//            sharedMap.put(sharedQuestionId, sharedProcessMessageUtil.getResList());
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
}