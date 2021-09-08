package net.hneb.jxetyy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.hneb.jxetyy.common.mapper.SearchFilters;
import net.hneb.jxetyy.common.mapper.SearchOp;
import net.hneb.jxetyy.entity.LbpcNo;
import net.hneb.jxetyy.entity.LbpcQuest;
import net.hneb.jxetyy.entity.LbpcReport;
import net.hneb.jxetyy.service.LbNoService;
import net.hneb.jxetyy.service.ReportService;
import net.hneb.jxetyy.utils.HnebRequestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zhangshuai
 * @create 2021/08/9 22:50
 */
@Controller
@Slf4j
public class LbController {
    @Autowired
    private LbNoService lbNoService;
    @Autowired
    private ReportService reportService;

    @RequestMapping(value = {"/lb/0809" })
    public String lb0809() {
        return "lb/0809";
    }

    @RequestMapping(value = {"/cpt" })
    public String cpt(@RequestParam(name = "order",required = false) String order,
                      @RequestParam(name = "name",required = false) String name,
                      @RequestParam(name = "sex",required = false) String sex,
                      @RequestParam(name = "bithday",required = false) String bithday,
                      @RequestParam(name = "phone",required = false) String phone,
                      @RequestParam(name = "executor",required = false) String executor,
                      @RequestParam(name = "doctor",required = false) String doctor,
                      @RequestParam(name = "patientNo",required = false) String patientNo,
                      @RequestParam(name = "escort",required = false) String escort,
                      @RequestParam(name = "relation",required = false) String relation,
                      @RequestParam(name = "history",required = false) String history,
                      Model model) {
        if(StringUtils.isNotBlank(order)){
            List<LbpcReport> reports = reportService.select(new SearchFilters("C_ANS_15", SearchOp.EQ, order));
            if(reports == null || reports.isEmpty()){model.addAttribute("order", order);}
            else if(reports.stream().anyMatch(r -> "1".equals(r.getCEffMrk()))){
                model.addAttribute("error", "该订单已经存在报告，如需要测试请先作废之前的报告！");
            }
        } else {model.addAttribute("error", "缺少基本信息:order，无法进行测试，请关闭页面重新进入！");}
        if(StringUtils.isNotBlank(name))model.addAttribute("name", name);
        else model.addAttribute("error", "缺少基本信息:name，无法进行测试，请关闭页面重新进入！");
        if(StringUtils.isNotBlank(sex))model.addAttribute("sex", sex);
        else model.addAttribute("error", "缺少基本信息:sex，无法进行测试，请关闭页面重新进入！");
        if(StringUtils.isNotBlank(bithday))model.addAttribute("bithday", bithday);
        else model.addAttribute("error", "缺少基本信息:bithday，无法进行测试，请关闭页面重新进入！");
        if(StringUtils.isNotBlank(phone))model.addAttribute("phone", phone);
        else model.addAttribute("error", "缺少基本信息:phone，无法进行测试，请关闭页面重新进入！");
        if(StringUtils.isNotBlank(executor))model.addAttribute("executor", executor);
        else model.addAttribute("error", "缺少基本信息:executor，无法进行测试，请关闭页面重新进入！");
        if(StringUtils.isNotBlank(doctor))model.addAttribute("doctor", doctor);
        else model.addAttribute("error", "缺少基本信息:doctor，无法进行测试，请关闭页面重新进入！");
        if(StringUtils.isNotBlank(patientNo))model.addAttribute("patientNo", patientNo);
        if(StringUtils.isNotBlank(escort))model.addAttribute("escort", escort);
        if(StringUtils.isNotBlank(relation))model.addAttribute("relation", relation);
        if(StringUtils.isNotBlank(order))model.addAttribute("history", history);
        return "lb/0809";
    }

    @RequestMapping(value = {"/help/0809" })
    public String help0809() {
        return "lb/0809-help";
    }

    @RequestMapping(value = {"/lb/getLbBasInfo" })
    @ResponseBody
    public JSONObject getLbBasInfo(HttpServletRequest servletRequest) {

        JSONObject custDataReq = HnebRequestUtils.getRequestCustData(servletRequest);
        String lbId = custDataReq.getString("lbId");

        JSONObject custDataRes = new JSONObject();
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("doctors_", PcDataUtils.getDoctors());
//        map.put("executors_", PcDataUtils.getExecutors());
//        map.put("hospitals_", PcDataUtils.getHospitals());
//        cd.put("tipsinfo", map);
        if(StringUtils.isBlank(lbId)){
            return HnebRequestUtils.getResponseData(custDataRes, new JSONArray(), true);
        }
        LbpcNo lb = lbNoService.selectByLbId(lbId);
        JSONObject lb0809 = new JSONObject();
        lb0809.put("name", lb.getCLbNme());
        lb0809.put("age", lb.getCAge());
        lb0809.put("range", JSON.parseObject(lb.getCAgeJson()));
        lb0809.put("introduce", lb.getCIntroduce());
        lb0809.put("guide", lb.getCGuide());
        JSONObject lbMsg = new JSONObject();
        lbMsg.put("0809", lb0809);
        custDataRes.put("lbMsg_", lbMsg);
        custDataRes.put("doctors_", new JSONObject());
        custDataRes.put("executors_", new JSONObject());
        custDataRes.put("hospitals_", new JSONObject());

        return HnebRequestUtils.getResponseData(custDataRes, new JSONArray(), true);
    }

    @RequestMapping(value = {"/callback" })
    @ResponseBody
    public JSONObject callback(JSONObject json) {
        log.info("回写的订单信息, {}", json.toJSONString());
        JSONObject res = new JSONObject();
        res.put("result", "好");
        return res;
    }

    @RequestMapping(value = {"lb/submit"})
    @ResponseBody
    public JSONObject submit(HttpServletRequest request){
        JSONObject custData = HnebRequestUtils.getRequestCustData(request);
        JSONObject  bizData = HnebRequestUtils.getRequestBizData(request);

        JSONObject custDataRes = new JSONObject();
        if(bizData.getJSONArray("basic")== null
                || bizData.getJSONArray("basic").isEmpty()){
            return HnebRequestUtils.getResponseData(custDataRes, new JSONArray(), true);
        }
        JSONObject basicMap = bizData.getJSONArray("basic").getJSONObject(0);
        JSONObject questJson = new JSONObject();

        questJson.putAll(basicMap);
        LbpcQuest quest = mapperValue(questJson);

        JSONObject bmResult = lbNoService.submit(quest, basicMap,custData);
        return HnebRequestUtils.getResponseData(bmResult, new JSONArray(), true);
    }


    private LbpcQuest mapperValue(JSONObject questJson){
        LbpcQuest quest = new LbpcQuest();
        if(StringUtils.isNotBlank(questJson.getString("CLbId")))
            quest.setCLbId(questJson.getString("CLbId"));
        if(StringUtils.isNotBlank(questJson.getString("CChildNme")))
            quest.setCChildNme(questJson.getString("CChildNme"));
        if(StringUtils.isNotBlank(questJson.getString("CExt1")))
            quest.setCExt1(questJson.getString("CExt1"));
//        if(StringUtils.isNotBlank(questJson.getString("hiddenNum")))
//            quest.set(questJson.getString("hiddenNum"));
        if(StringUtils.isNotBlank(questJson.getString("CExt3")))
            quest.setCExt3(questJson.getString("CExt3"));
        if(StringUtils.isNotBlank(questJson.getString("CExt2")))
            quest.setCExt2(questJson.getString("CExt2"));
        if(StringUtils.isNotBlank(questJson.getString("CParentId")))
            quest.setCParentId(questJson.getString("CParentId"));
        if(StringUtils.isNotBlank(questJson.getString("NDay")))
            quest.setNDay(questJson.getInteger("NDay"));
        if(StringUtils.isNotBlank(questJson.getString("CPkId")))
            quest.setCPkId(questJson.getString("CPkId"));
        if(StringUtils.isNotBlank(questJson.getString("CDoctor")))
            quest.setCDoctor(questJson.getString("CDoctor"));
        if(StringUtils.isNotBlank(questJson.getString("NMonth")))
            quest.setNMonth(questJson.getInteger("NMonth"));
        if(StringUtils.isNotBlank(questJson.getString("NSeqNum")))
            quest.setNSeqNum(questJson.getInteger("NSeqNum"));
        if(StringUtils.isNotBlank(questJson.getString("NYear")))
            quest.setNYear(questJson.getInteger("NYear"));
        if(StringUtils.isNotBlank(questJson.getString("CChildSex")))
            quest.setCChildSex(questJson.getString("CChildSex"));
        if(StringUtils.isNotBlank(questJson.getString("CMenzhenNo")))
            quest.setCMenzhenNo(questJson.getString("CMenzhenNo"));
        if(StringUtils.isNotBlank(questJson.getString("COrgPkId")))
            quest.setCOrgPkId(questJson.getString("COrgPkId"));
        if(StringUtils.isNotBlank(questJson.getString("CFormId")))
            quest.setCFormId(questJson.getString("CFormId"));
        if(StringUtils.isNotBlank(questJson.getString("CPhoneNo")))
            quest.setCPhoneNo(questJson.getString("CPhoneNo"));
        if(StringUtils.isNotBlank(questJson.getString("CInfoOffer")))
            quest.setCInfoOffer(questJson.getString("CInfoOffer"));
        if(StringUtils.isNotBlank(questJson.getString("CPkgId")))
            quest.setCPkgId(questJson.getString("CPkgId"));
        if(StringUtils.isNotBlank(questJson.getString("CIllHistory")))
            quest.setCIllHistory(questJson.getString("CIllHistory"));
        if(StringUtils.isNotBlank(questJson.getString("CChildRel")))
            quest.setCChildRel(questJson.getString("CChildRel"));
        if(StringUtils.isNotBlank(questJson.getString("CChildId")))
            quest.setCChildId(questJson.getString("CChildId"));
        if(StringUtils.isNotBlank(questJson.getString("CUserId")))
            quest.setCUserId(questJson.getString("CUserId"));
        if(StringUtils.isNotBlank(questJson.getString("TBirthday")))
            quest.setTBirthday(questJson.getString("TBirthday"));
        if(StringUtils.isNotBlank(questJson.getString("CExecutor")))
            quest.setCExecutor(questJson.getString("CExecutor"));
        return quest;
    }

}
