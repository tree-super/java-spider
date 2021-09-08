package net.hneb.jxetyy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Date;

@Slf4j
public class HttpUtil {
    //Http协议Post请求
    public static String post (String url,String json) {
        String now = DateUtil.getDateStr(new Date());
        log.info("http url:{}, json:{}, time:{}", url, json, now);
        try {
            //初始HttpClient
            CloseableHttpClient httpClient = HttpClients.createDefault();
            //创建Post对象
            HttpPost httpPost = new HttpPost(url);
            //设置Content-Type
            httpPost.setHeader("Content-Type", "application/json");
            //写入JSON数据
            httpPost.setEntity(new StringEntity(json));
            //发起请求，获取response对象
            CloseableHttpResponse response = httpClient.execute(httpPost);
            //获取请求码
            //response.getStatusLine().getStatusCode();
            //获取返回数据实体对象
            HttpEntity entity = response.getEntity();
            //转为字符串
            String result = EntityUtils.toString(entity, "UTF-8");
            log.info("http url:{}, json:{}, time:{}", url, json, now);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            log.error("http, time:{} 请求错误,{} ", now, e);
        }
        return null;
    }
}
