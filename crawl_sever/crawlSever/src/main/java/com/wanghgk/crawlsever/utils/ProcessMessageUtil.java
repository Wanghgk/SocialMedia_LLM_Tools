package com.wanghgk.crawlsever.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanghgk.crawlsever.pojo.BinaryRes;
import com.wanghgk.crawlsever.pojo.ClassifyRes;
import lombok.Getter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProcessMessageUtil {

    private String questionId;

    private String questionKeyWord;

    private int mode;

    private List<String> opinions;

    @Getter
    private List<BinaryRes> binaryResList;

    @Getter
    private List<ClassifyRes> classifyResList;


    private String zhihuStartUrl = "https://zhihu.com/api/v4/questions/";
    private String feedsUrl = "/feeds?cursor=&include=data%5B%2A%5D.is_normal%2Cadmin_closed_comment%2Creward_info%2Cis_collapsed%2Cannotation_action%2Cannotation_detail%2Ccollapse_reason%2Cis_sticky%2Ccollapsed_by%2Csuggest_edit%2Ccomment_count%2Ccan_comment%2Ccontent%2Ceditable_content%2Cattachment%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Ccreated_time%2Cupdated_time%2Creview_info%2Crelevant_info%2Cquestion%2Cexcerpt%2Cis_labeled%2Cpaid_info%2Cpaid_info_content%2Creaction_instruction%2Crelationship.is_authorized%2Cis_author%2Cvoting%2Cis_thanked%2Cis_nothelp%3Bdata%5B%2A%5D.author.follower_count%2Cvip_info%2Cbadge%5B%2A%5D.topics%3Bdata%5B%2A%5D.settings.table_of_content.enabled&limit=5&offset=2&order=default&platform=desktop&session_id=&ws_qiangzhisafe=1";
    private String modelUrl = "http://localhost:11434/api/";

    private Lock lock = new ReentrantLock();
    private boolean isEnd = false;
    private boolean shutDownNow = false;

    public ProcessMessageUtil(String questionId, String questionKeyWord) {
        this.questionId = questionId;
        this.questionKeyWord = questionKeyWord;
        this.binaryResList = new ArrayList<>();
        this.mode = 0;
    }

    public ProcessMessageUtil(String questionId, String questionKeyWord, List<String> opinions) {
        this.questionId = questionId;
        this.questionKeyWord = questionKeyWord;
        this.opinions = opinions;
        this.classifyResList = new ArrayList<>();
        this.mode = 1;
    }

    public boolean getIsEnd() {
        return isEnd;
    }

    public void shutDownNow() {
        shutDownNow = true;
    }

    public void startJudge() {
        isEnd = false;
        String zhihuQuestionId = questionId;

        Integer total = 6;
        String next = zhihuStartUrl + zhihuQuestionId + feedsUrl;
        System.out.println(next);

        ExecutorService es = Executors.newFixedThreadPool(total);

        while (!isEnd && !shutDownNow) {
            String httpResult = apacheHttpClient(next);
            JSONObject jsonObject = JSON.parseObject(httpResult);

            System.out.println(jsonObject);
            List answers = JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), String.class);

            for (Object answerObject : answers) {
                JSONObject answer = (JSONObject) JSONObject.parseObject((String) answerObject);
                Document answerHtml = Jsoup.parse(answer.getJSONObject("target").getString("content").toString());
                Elements answerElements = answerHtml.getElementsByTag("p");
                String answerString = "";
                for (Element answerElement : answerElements) {
                    answerString += answerElement.text() + "\n";
                }

                Future future;
                if(mode == 0) {
                    future = es.submit(new getBinaryModelMessage(modelUrl, answerString, answer.getJSONObject("target").getInteger("voteup_count"), answer.getJSONObject("target").getInteger("thanks_count"), answer.getJSONObject("target").getInteger("created_time")));
                }else{
                    future = es.submit(new getClassifyModelMessage(modelUrl, answerString, answer.getJSONObject("target").getInteger("voteup_count"), answer.getJSONObject("target").getInteger("thanks_count"), answer.getJSONObject("target").getInteger("created_time")));

                }
                try {
                    future.get(5, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
//                    BinaryRes binaryRes = new BinaryRes("模型失效", supports, thanks, time);
//                    resList.add(binaryRes);
                    future.cancel(true);
                }
            }
            //更新循环条件
            isEnd = jsonObject.getJSONObject("paging").getBooleanValue("is_end");
            next = jsonObject.getJSONObject("paging").getString("next");


            //休眠，防止被封
            try {
                Thread.sleep(GetevenNum(1.5, 2.5) * 1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        es.shutdown();

        while (!es.isTerminated() && !shutDownNow) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (shutDownNow) {
            es.shutdownNow();
        }
    }

    class getBinaryModelMessage implements Runnable {

        private String targetModelUrl;

        private String answerString;

        private static Integer supports;

        private static Integer thanks;

        private static Integer time;


        public getBinaryModelMessage(String targetModelUrl, Object answerObject, Integer supports, Integer thanks, Integer time) {

            this.targetModelUrl = targetModelUrl;
            this.answerString = JSON.parseObject(answerObject.toString()).getJSONObject("target").getString("content");
            this.supports = supports;
            this.thanks = thanks;
            this.time = time;
        }

        public getBinaryModelMessage(String targetModelUrl, String answerString, Integer supports, Integer thanks, Integer time) {
            this.targetModelUrl = targetModelUrl;
            this.answerString = answerString;
            this.supports = supports;
            this.thanks = thanks;
            this.time = time;
        }

        @Override
        public void run() {
            String content = answerString;

            JSONObject sendData = new JSONObject();
            sendData.put("stream", false);

            sendData.put("model", "llama3.1");
            sendData.put("messages", new JSONObject[]{
                    getMessageObj("user", "你是一个文章观点判断专家。请你根据文本中重要的词语、句子并在考虑中文的反话、阴阳怪气等反向观点表达方式的前提下，分析下列文本是否支持" + questionKeyWord + "，然后返回“支持”或“反对”，不要返回其他内容。请注意不要用文本的积极或消极情感代替文本的支持或反对观点"),
                    getMessageObj("assistant", "好的，我将根据文本对" + questionKeyWord + "的态度仅返回“支持”或“反对”两个字，若无法判断态度则仅返回“无法判断”四个字。"),
                    getMessageObj("user", content)
            });

            //请求大模型，此处同步阻塞
            String llamaResult = "";

            llamaResult = apacheHttpPostClient(targetModelUrl + "chat", sendData.toJSONString());


            JSONObject llamaResultJsonObject = JSON.parseObject(llamaResult.toString());
            String modelJudent = "";

            modelJudent = llamaResultJsonObject.getJSONObject("message").getString("content");

            binaryJudgementsCount(modelJudent);

        }

        private void binaryJudgementsCount(String modelJudent) {
            lock.lock();

            if (modelJudent.contains("支持")) {
//                judgeMap.compute("支持", (k, tmpCount) -> tmpCount + 1);
                BinaryRes binaryRes = new BinaryRes("支持", supports, thanks, time);
                binaryResList.add(binaryRes);
            } else if (modelJudent.contains("反对")) {
//                judgeMap.compute("反对", (k, tmpCount) -> tmpCount + 1);
                BinaryRes binaryRes = new BinaryRes("反对", supports, thanks, time);
                binaryResList.add(binaryRes);
            } else if (modelJudent.contains("无法判断")) {
//                judgeMap.compute("无法判断", (k, tmpCount) -> tmpCount + 1);
                BinaryRes binaryRes = new BinaryRes("无法判断", supports, thanks, time);
                binaryResList.add(binaryRes);
            } else {
//                judgeMap.compute("模型失效", (k, tmpCount) -> tmpCount + 1);
                BinaryRes binaryRes = new BinaryRes("模型失效", supports, thanks, time);
                binaryResList.add(binaryRes);
            }

            lock.unlock();
        }
    }

    class getClassifyModelMessage implements Runnable {

        private String targetModelUrl;

        private String answerString;

        private static Integer supports;

        private static Integer thanks;

        private static Integer time;


        public getClassifyModelMessage(String targetModelUrl, Object answerObject, Integer supports, Integer thanks, Integer time) {

            this.targetModelUrl = targetModelUrl;
            this.answerString = JSON.parseObject(answerObject.toString()).getJSONObject("target").getString("content");
            this.supports = supports;
            this.thanks = thanks;
            this.time = time;
        }

        public getClassifyModelMessage(String targetModelUrl, String answerString, Integer supports, Integer thanks, Integer time) {
            this.targetModelUrl = targetModelUrl;
            this.answerString = answerString;
            this.supports = supports;
            this.thanks = thanks;
            this.time = time;
        }

        @Override
        public void run() {
            String content = answerString;

            JSONObject sendData = new JSONObject();
            sendData.put("stream", false);

            sendData.put("model", "llama3.1");
            sendData.put("messages", new JSONObject[]{
                    getMessageObj("user", "你是一个专业的文章主题分类工作者，将就<目标问题>在以下<观点列表>中选择与提供的<文本主要内容>最契合的<观点>，并且仅输出该<观点>本身，不输出其余任何信息。"),
                    getMessageObj("assistant", "明白了，我将根据提供的<文本主要内容>就<目标问题>在<观点列表>中选择一条最匹配的一条<观点>，并且仅输出匹配到的<观点>本身，若无匹配到的<观点>则会输出\"未匹配到观点\"，并且不输出任何其他信息。"),
                    getMessageObj("user", "<目标问题>:"+questionKeyWord+",<观点列表>:"+opinions+",<文本主要内容>:"+content)
            });

            //请求大模型，此处同步阻塞
            String llamaResult = "";

            llamaResult = apacheHttpPostClient(targetModelUrl + "chat", sendData.toJSONString());


            JSONObject llamaResultJsonObject = JSON.parseObject(llamaResult.toString());
            String modelJudent = "";

            modelJudent = llamaResultJsonObject.getJSONObject("message").getString("content");

            classifyJudgementsCount(modelJudent);

        }

        private void classifyJudgementsCount(String modelJudent) {
            lock.lock();

            boolean matched = false;
            for(String opinion : opinions) {
                if(modelJudent.contains(opinion)) {
                    matched = true;
                    ClassifyRes classifyRes = new ClassifyRes(opinion, supports, thanks, time);
                    classifyResList.add(classifyRes);
                }
                if (matched) {
                    break;
                }
            }

            lock.unlock();
        }
    }

    private static JSONObject getMessageObj(String role, String content) {
        JSONObject messageObj = new JSONObject();
        messageObj.put("role", role);
        messageObj.put("content", content);

        return messageObj;
    }

    private static String apacheHttpClient(String url) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36 Edg/128.0.0.0");
        httpGet.setHeader("Cookie", "_xsrf=hvjXKKMRuGyNrdkOCbK6aTF4Ux5CnurX; _zap=ecd5ef5e-9242-4167-afa5-977598c908c4; d_c0=AUBSBfxaAxmPTipx4U9Uom-5RNUPMx2W_pQ=|1722501907; q_c1=84647a4152cf41b39da6941dbeb61a89|1726392997000|1726392997000; __utma=51854390.531860950.1728830548.1728830548.1728830548.1; __utmz=51854390.1728830548.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmv=51854390.100-1|2=registration_date=20190624=1^3=entry_date=20190624=1; __snaker__id=X3Xmz44dp1f1NUYN; z_c0=2|1:0|10:1731745996|4:z_c0|92:Mi4xd3A3NVBnQUFBQUFCUUZJRl9Gb0RHU1lBQUFCZ0FsVk56S1lsYUFDd20tQzJ0MlgzMDNmYm9xelVxeVFTSXZwOTB3|e94089da583dc2262569723d303ce8ede470f502e2b78a2b797af4921d4e5e43; gdxidpyhxdE=69gGHn%2BTijipGa50m9Wd%2Br9WENUVO%2Bnjp4GIpwbDYR8qGzkR4CV%2BYNnBQ%2BGdNWnotZ%2FQUpuZ80Ag1VbgsuWGJmp%5C752pD59f3fe2Z9Bl7YX6cJJl5NhI6aMoswYkl%2BwjDmnDyYrbqZWdA5UQe0UErCxn2%5C0EKJPJQfdCA%2Fulqy7%2B0CTr%3A1731860653847; __zse_ck=003_br/eWbcWYNZXmBJ+/ihLVOslX33eS3Omi5hAigALloXM2OxsjXKudyC/B6=Bs18b7anP70RUGdbAkgVKnVYmyLemuBcqHoNU2NcWra+260d6; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1732027820,1732084940,1732155739,1732240252; HMACCOUNT=20131A97553A2F3D; tst=r; SESSIONID=mRGMqhLd639dSMhjrMzrRBTvWEsk3eCfzqdFdCgUpt9; JOID=VVESBUoSKzTf4u7JPBMzpucatcIlcXdylZWIi1kiFgO6tqWvWGzHDbnq7sY6HfSewsJR64DX95qzx8V3iJPPVSs=; osd=V1sXCkoQITHQ4uzDORwzpO0fusIne3J9lZeCjlYiFAm_uaWtUmnIDbvg68k6H_6bzcJT4YXY95i5wsp3ipnKWis=; Hm_lpvt_98beee57fd2ef70ccdd5ca52b9740c49=1732240257; BEC=69a31c4b51f80d1feefe6d6caeac6056");

        HttpResponse httpResponse = null;
        String content = null;

        try {
            httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            content = EntityUtils.toString(httpEntity);
        } catch (Exception e) {

        } finally {

        }

        return content;
    }

    private static String apacheHttpPostClient(String url, String sendData) {
        String body = "";

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36 Edg/128.0.0.0");
        httpPost.setHeader("Connection", "keep-alive");

        //装填参数
        StringEntity s = new StringEntity(sendData, "UTF-8");
        s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                "application/json"));
        //设置参数到请求对象中
        httpPost.setEntity(s);

        int statusCode = 0;

        //循环保证每个答案都有输出

        try {
            //拿到结果，同步阻塞
            HttpResponse httpResponse = httpClient.execute(httpPost);

            //更新状态码
            statusCode = httpResponse.getStatusLine().getStatusCode();
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null && statusCode == 200) {
                body = EntityUtils.toString(httpEntity, "UTF-8");

                EntityUtils.consume(httpEntity);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(GetevenNum(70, 110));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        return body;

    }

    private static int GetevenNum(double num1, double num2) {
        int s = (int) num1 + (int) (Math.random() * (num2 - num1));
        if (s % 2 == 0) {
            return s;
        } else
            return s + 1;
    }
}
