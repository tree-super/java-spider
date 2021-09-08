package net.hneb.jxetyy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.DeserializationFeature;
import lombok.extern.slf4j.Slf4j;
import net.hneb.jxetyy.dao.LbpcNoDao;
import net.hneb.jxetyy.entity.LbpcNo;
import net.hneb.jxetyy.entity.LbpcQuest;
import net.hneb.jxetyy.entity.LbpcReport;
import net.hneb.jxetyy.service.ArchBasService;
import net.hneb.jxetyy.service.LbNoService;
import net.hneb.jxetyy.service.ReportService;
import net.hneb.jxetyy.service.UserService;
import net.hneb.jxetyy.utils.AgeUtils;
import net.hneb.jxetyy.utils.DateUtil;
import net.hneb.jxetyy.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;


/**
 * @author zhangshuai
 */
@Slf4j
@Service
public class LbpcNoServiceImpl implements LbNoService {

    @Value("${net.hneb.his.order}")
    private String his;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LbpcNoDao lbpcNoDao;
    @Autowired
    private UserService userService;
    @Autowired
    private ArchBasService archBasService;
    @Autowired
    private ReportService reportService;

    @Override
    public LbpcNo selectByLbId(String lbId) {
        LbpcNo example = new LbpcNo();
        example.setCLbId(lbId);
        return lbpcNoDao.selectOne(example);
    }


    /**
     * 提交量表并生成报告
     *
     * @param quest
     * @param basicJson
     * @throws Exception
     */
    @Override
    @Transactional
    public JSONObject submit(LbpcQuest quest, JSONObject basicJson, JSONObject custData){

        String parentId = basicJson.getString("CParentId");
        String childId = basicJson.getString("CChildId");

        if (StringUtils.isBlank(parentId)) {
            // 保存家长数据 手机号码
            parentId = userService.saveParent(basicJson.getString("CPhoneNo"));
        }

        // 更新孩子数据
        childId = archBasService.saveChild(basicJson, parentId);

        quest.setCParentId(parentId);
        quest.setCChildId(childId);

        // 设置孩子测评时的年龄
        Map<String, Integer> ageMap = AgeUtils
                .calcAge(DateUtil.strToDate(basicJson.getString("TBirthday"), "yyyy-MM-dd"), new Date());
        quest.setNYear(ageMap.get("YEAR"));
        quest.setNMonth(ageMap.get("MONTH"));
        quest.setNDay(ageMap.get("DAY"));

        if (basicJson.get("TPreBirth") != null && !("".equals(basicJson.get("TPreBirth")))) {
            Map<String, Integer> ageMap1 = AgeUtils
                    .calcAge(DateUtil.strToDate(basicJson.getString("TPreBirth"), "yyyy-MM-dd"), new Date());
            quest.setNYear1(ageMap1.get("YEAR"));
            quest.setNMonth1(ageMap1.get("MONTH"));
            quest.setNDay1(ageMap1.get("DAY"));
        }

        // 保存测评原始数据
        //this.pcService.saveOrUpdateQuest(quest, "1", custData);// 提交状态
        // 生成测评报告
        LbpcReport report = reportService.create(quest, basicJson, custData);
        // 保存测评报告
        reportService.saveReport(report);
        log.info("生成报告:{}", report.getCPkId());
        //回写报告 id 到 his接口,记得在作废报告也要调用回写报告接口
        if(StringUtils.isNotBlank(report.getCAns15()))syncOrder(report);

        JSONObject result = new JSONObject();
        result.put("flag", true);
        result.put("CPkId", report.getCPkId());
        return result;
    }


    public void syncOrder(LbpcReport report){
        log.info("回写报告,report:{},order:{}", report.getCPkId(), report.getCAns15());
        JSONObject req = new JSONObject();
        req.put("order", report.getCAns15());//订单 id
        req.put("report", report.getCPkId());//报告 id
        req.put("state", report.getCEffMrk());//"1"正常，"2"作废
        req.put("testTime", DateUtil.getDateStr(report.getTTestTm()));//测试时间
        req.put("result", report.getCAns7());// 结论
        req.put("level", report.getCAns8());// 症状程度
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String json = req.toJSONString();
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String now = DateUtil.getDateStr(new Date());
        log.info("http url:{}, json:{}, time:{}", his, json, now);
        ResponseEntity<String> postForEntity = restTemplate.postForEntity(his, request, String.class);
        String result = postForEntity.getBody();
        log.info("http url:{}, result:{}, time:{}", result, now);
//        HttpUtil.post(his, req.toJSONString());
    }
}
