package com.wanghgk.crawlsever.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

import java.io.IOException;
import java.util.List;

public class ParamMessageUtil {
    private String zhihuStartUrl = "https://zhihu.com/api/v4/questions/";
    private String feedsUrl = "/feeds?cursor=&include=data%5B%2A%5D.is_normal%2Cadmin_closed_comment%2Creward_info%2Cis_collapsed%2Cannotation_action%2Cannotation_detail%2Ccollapse_reason%2Cis_sticky%2Ccollapsed_by%2Csuggest_edit%2Ccomment_count%2Ccan_comment%2Ccontent%2Ceditable_content%2Cattachment%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Ccreated_time%2Cupdated_time%2Creview_info%2Crelevant_info%2Cquestion%2Cexcerpt%2Cis_labeled%2Cpaid_info%2Cpaid_info_content%2Creaction_instruction%2Crelationship.is_authorized%2Cis_author%2Cvoting%2Cis_thanked%2Cis_nothelp%3Bdata%5B%2A%5D.author.follower_count%2Cvip_info%2Cbadge%5B%2A%5D.topics%3Bdata%5B%2A%5D.settings.table_of_content.enabled&limit=5&offset=2&order=default&platform=desktop&session_id=&ws_qiangzhisafe=1";
    private boolean isEnd;
    private String questionId;
    private String next;


    public ParamMessageUtil(String questionId) {
        this.questionId = questionId;
        isEnd = false;
        next = zhihuStartUrl + questionId + feedsUrl;
    }

    public List<JSONObject> getNext() {
        String httpResult = apacheHttpClient(next);
        JSONObject jsonObject = JSON.parseObject(httpResult);

//        System.out.println(jsonObject);
        List answers = JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), String.class);

        //更新循环条件
        isEnd = jsonObject.getJSONObject("paging").getBooleanValue("is_end");
        next = jsonObject.getJSONObject("paging").getString("next");


        //休眠，防止被封
        try {
            Thread.sleep(GetevenNum(1, 1.5) * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return answers;
    }

    public boolean getIsEnd() {
        return isEnd;
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
