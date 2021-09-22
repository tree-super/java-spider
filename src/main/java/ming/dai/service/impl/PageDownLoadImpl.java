package ming.dai.service.impl;

import lombok.extern.slf4j.Slf4j;
import ming.dai.service.PageDownLoad;
import ming.dai.util.HttpClientDownPage;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class PageDownLoadImpl implements PageDownLoad {
    @Value("${ming.dai.wh}")
    private String wh;
    @Value("${ming.dai.gjzs}")
    private String gjzs;
    @Value("${ming.dai.wgjs}")
    private String wgjs;
    @Value("${ming.dai.kx}")
    private String kx;

    @Override
    public HashMap<String, String> downWH() {
        String content = HttpClientDownPage.sendGet(wh);
        //log.info("wh, {}", content);
        //通过Jsoup进行页面解析
        Document document = Jsoup.parse(content);
        HashMap<String, String> wh = new HashMap<>();
        //根据网页标签解析源码
        Element usdElement = document.getElementById("USD");
        wh.put("USD",usdElement.child(1).text());
        wh.put("USDRate",usdElement.child(3).text());
        //根据网页标签解析源码
        Element gpElement = document.getElementById("GBPUSD");//英镑美元
        wh.put("GBPUSD",gpElement.child(1).text());
        wh.put("GBPUSDRate",gpElement.child(3).text());
        Element cnyElement = document.getElementById("USDCNY");//美元在岸人民币
        wh.put("USDCNY",cnyElement.child(1).text());
        wh.put("USDCNYRate",cnyElement.child(3).text());
        Element audElement = document.getElementById("AUDUSD");//澳元美元
        wh.put("AUDUSD",audElement.child(1).text());
        wh.put("AUDUSDRate",audElement.child(3).text());
        Element jpyElement = document.getElementById("USDJPY");//美元日元
        wh.put("USDJPY",jpyElement.child(1).text());
        wh.put("USDJPYRate",jpyElement.child(3).text());
        Element eurElement = document.getElementById("EURUSD");
        wh.put("EURUSD",eurElement.child(1).text());
        wh.put("EURUSDRate",eurElement.child(3).text());
        return wh;
    }

    @Override
    public HashMap<String, String> downGJZS() {
        String content = HttpClientDownPage.sendGet(gjzs);
        //log.info("gjzs, {}", content);
        //通过Jsoup进行页面解析
        Document document = Jsoup.parse(content);
        HashMap<String, String> gjzs = new HashMap<>();
        //根据网页标签解析源码
        Element element1A0001 = document.getElementById("1A0001");//上证指数
        gjzs.put("A0001",element1A0001.child(1).text());
        gjzs.put("A0001Rate",element1A0001.child(3).text());
        Element elementNASDAQ = document.getElementById("NASDAQ");//纳斯达克指数
        gjzs.put("NASDAQ",elementNASDAQ.child(1).text());
        gjzs.put("NASDAQRate",elementNASDAQ.child(3).text());
        Element elementDAX = document.getElementById("DAX");//德国DAX30
        gjzs.put("DAX",elementDAX.child(1).text());
        gjzs.put("DAXRate",elementDAX.child(3).text());
        Element elementNIKKI = document.getElementById("NIKKI");//日经225
        gjzs.put("NIKKI",elementNIKKI.child(1).text());
        gjzs.put("NIKKIRate",elementNIKKI.child(3).text());
        return gjzs;
    }

    @Override
    public HashMap<String, String> downWGJS() {
        String content = HttpClientDownPage.sendGet(wgjs);
        //log.info("wgjs, {}", content);
        //通过Jsoup进行页面解析
        Document document = Jsoup.parse(content);
        HashMap<String, String> wgjs = new HashMap<>();
        //根据网页标签解析源码
        Element usdElement = document.getElementById("XAU");
        wgjs.put("XAU",usdElement.child(1).text());
        wgjs.put("XAURate",usdElement.child(3).text());
        //根据网页标签解析源码
        Element gpElement = document.getElementById("XAG");//英镑美元
        wgjs.put("XAG",gpElement.child(1).text());
        wgjs.put("XAGRate",gpElement.child(3).text());
        return wgjs;
    }

    @Override
    public List<String> downKX() {
        String content = HttpClientDownPage.sendGet(kx);
        //log.info("kx, {}", content);
        //通过Jsoup进行页面解析
        Document document = Jsoup.parse(content);
        List<String> kx = new ArrayList<>();
        //根据网页标签解析源码
        Elements newsElements = document.select(".body_zb_li");
        for(Element news : newsElements){
            Elements newSpan = news.getElementsByTag("span");
            if(newSpan==null || newSpan.isEmpty())continue;
            String message = newSpan.get(0).text();
            if(StringUtils.isNotBlank(message)
                    && !message.contains("<br/>")
                    && message.length() < 100)
                kx.add(message);
            if(kx.size() > 4) break;
        }
        return kx;
    }
}
