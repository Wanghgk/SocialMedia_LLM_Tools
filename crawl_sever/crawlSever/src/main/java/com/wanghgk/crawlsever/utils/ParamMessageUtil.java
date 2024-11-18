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

        System.out.println(jsonObject);
        List answers = JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), String.class);

        //更新循环条件
        isEnd = jsonObject.getJSONObject("paging").getBooleanValue("is_end");
        next = jsonObject.getJSONObject("paging").getString("next");


        //休眠，防止被封
        try {
            Thread.sleep(GetevenNum(0.5, 2.5) * 1000L);
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
        httpGet.setHeader("Cookie", "_xsrf=hvjXKKMRuGyNrdkOCbK6aTF4Ux5CnurX; _zap=ecd5ef5e-9242-4167-afa5-977598c908c4; d_c0=AUBSBfxaAxmPTipx4U9Uom-5RNUPMx2W_pQ=|1722501907; q_c1=84647a4152cf41b39da6941dbeb61a89|1726392997000|1726392997000; __utma=51854390.531860950.1728830548.1728830548.1728830548.1; __utmz=51854390.1728830548.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmv=51854390.100-1|2=registration_date=20190624=1^3=entry_date=20190624=1; gdxidpyhxdE=qxdBwOZmm8LwM0wN6OpYl3BU7PpPiJpj09W%5CzanE8txlkYOc775eztCuDeB1xfTvVaxjEP1g4RuKN4%5CGlB%2Bpt%2FhTvCoAW8%2BTfwjER%2Bv8LZv2ArM6yz3GdM%5C98Srol4E7%2B4NJ%2BpYCXRMnVW3iVNeqaKC4COj6p3CMjqZnwciae43QXh9c%3A1731746827562; captcha_session_v2=2|1:0|10:1731745959|18:captcha_session_v2|88:LzhnQnc4c0VGK1BOK3RscEhTeGNZMkpOTUxxdDhRb25zMmJKUERGL3RRQ2lyNlEzS1BMenU4dmlEMWs2ZGxLaQ==|4c78a70e6fe8e432caf1d6d15d42d5b2d8f4f413c755c5c996b7484b8e13165b; __snaker__id=X3Xmz44dp1f1NUYN; captcha_ticket_v2=2|1:0|10:1731745980|17:captcha_ticket_v2|728:eyJ2YWxpZGF0ZSI6IkNOMzFfM05SUGQ0MXk5NGYuOUs5RldaYXFrcnB5MDZMQmJELjJqeERqSVA0cWY1cnVyRDF4VUp3dypSTXJ0RHNUeXZOcGtCVXhBTUoxT3czYU5oWUJ5d2RIaSpDTVdrZGlXYk14S0QyYUJydmNscyprWCpENXdtUE1ZYlkwVG12OEFBXzRCV1ZjNVExaFBEdnBlKjJKUnR3UzRYdDEzZUx1WEQzVzlIR1BBYVBDSVhCaHFJc2c4QkVBYW5uaGl5OV9mZ3pxNmpMdWNtMWdwbDRQKm5TQkJpdmhvUUFacGlaaVRYcGJwSHZ0NFpKeDV5SVNrbXFlQXN5OVdVb0U2SGJodEE5ZGJkWksqNkNES0dncUZSKmE2T0hUeVlETEg0b0RyKkRTZVNVbm1PZlF1TWFpX2hwbUtlNGdDU2V3cmtCOEhwaGlVYzR6Z0IuWG12YUFIWlNSWUYzRW05d3Vxc0VLOU5maEgqKnpxanRFR2NtUS40bG8xQW5oZEpGZTA1VkN3bWliaWczcG1qUSpfZ1Y1cF9YS0NXampxMzZFMTZfT05MTTZlSlRTSnpyaEdrSlZsYUtxcmpSaTU1X0R1ajhRYlY4SmZpR29XRlBFVUpyQWFLSE5JOXdvZW1zTjZtZWZUdGk1YjNldk9aRXJxTGpkX3R0ZlM4cFRDYTNWKkdkQ2hCMk5MX3JxWk03N192X2lfMSJ9|54ef31c8cf2e0e6cd686bdb3340e461da92fc331a3975fd0f2edb8f9eedb6d37; z_c0=2|1:0|10:1731745996|4:z_c0|92:Mi4xd3A3NVBnQUFBQUFCUUZJRl9Gb0RHU1lBQUFCZ0FsVk56S1lsYUFDd20tQzJ0MlgzMDNmYm9xelVxeVFTSXZwOTB3|e94089da583dc2262569723d303ce8ede470f502e2b78a2b797af4921d4e5e43; __zse_ck=003_bIDFm=gJK4dTlzWihQvYOGiYba8WJ/wXh4t1M3aPkO+p29pnYF0xY1Fy1W/TJl+Eke+Wq8LjNwtDG+t0gCVDM9QdlIPdzAnLuUCnqQhw+YXV; tst=r; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1731639539,1731696264,1731743917,1731757579; HMACCOUNT=20131A97553A2F3D; SESSIONID=mii1zeJmXzBqEdjkPsJAUaiJ2HmTuDus7gYmGnWXw5d; JOID=VVocAk6kMo6L9wpRdqIlEr8FVlVhzFDvvo9qEBP-Tcfytmw-Fqa3uurwBFJ60Lhh5JVLMjNbmWWKv0Z9oZqAMyU=; osd=U1ocBEmiMo6N8AxRdqQiFL8FUFJnzFDpuYlqEBX5S8fysGs4FqaxvezwBFR91rhh4pJNMjNdnmOKv0B6p5qANSI=; Hm_lpvt_98beee57fd2ef70ccdd5ca52b9740c49=1731757584; BEC=ec64a27f4feb1b29e8161db426d61998");

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
