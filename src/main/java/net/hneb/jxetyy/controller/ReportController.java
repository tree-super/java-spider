package net.hneb.jxetyy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.hneb.jxetyy.common.mapper.SearchFilters;
import net.hneb.jxetyy.common.mapper.SearchOp;
import net.hneb.jxetyy.entity.LbpcReport;
import net.hneb.jxetyy.service.ReportService;
import net.hneb.jxetyy.utils.DateUtil;
import net.hneb.jxetyy.utils.HnebRequestUtils;
import net.hneb.jxetyy.vo.FilterParamBo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangshuai
 * @create 2021/08/9 22:50
 */
@Controller
@Slf4j
public class ReportController {

    @Value("${net.hneb.lbNme}")
    private String lbNme;

    @Autowired
    private ReportService reportService;

    @RequestMapping(value = {"/report/{id}" })
    public String report0809(@RequestParam(name = "id") String pkId, Model model) {
        model.addAttribute("pkid", pkId);
        return "report/0809-report";
    }

    @RequestMapping(value = {"/report/query" })
    public String reportQuery() {
        return "report/query";
    }

    @RequestMapping(value = {"/report/getLbpcReport" })
    @ResponseBody
    public JSONObject reportDetail(HttpServletRequest request){
        JSONObject custDataReq = HnebRequestUtils.getRequestCustData(request);
        String pkId = custDataReq.getString("pkId");
        JSONObject json = reportService.getLbpcReport(pkId);
        JSONArray bizJsonData = new JSONArray();
        bizJsonData.add(HnebRequestUtils.getBizData("basic", (JSONObject) json.get("basic")));

        JSONObject custDataRes = new JSONObject();
        custDataRes.put("formId", ((JSONObject) json.get("basic")).get("CFormId"));
        custDataRes.put("formData", ((JSONObject) json.get("formData")));
        custDataRes.put("hospitalNme", ((JSONObject) json.get("basic")).get("CHospitalNme"));

        String lbId = ((JSONObject) json.get("basic")).getString("CLbId");
        custDataRes.put("lbNme", lbNme);
        custDataRes.put("extraData", json.get("extraData"));
        return HnebRequestUtils.getResponseData(custDataRes, bizJsonData, true);
    }

    @RequestMapping(value = {"/report/disable" })
    @ResponseBody
    public JSONObject reportDisable(HttpServletRequest request){
        JSONObject custDataReq = HnebRequestUtils.getRequestCustData(request);
        String pkId = custDataReq.getString("pkId");
        JSONArray bizJsonDataRes = new JSONArray();
        JSONObject custDataRes = new JSONObject();
        return HnebRequestUtils.getResponseData(custDataRes, bizJsonDataRes, reportService.disableReport(pkId));
    }

    @RequestMapping(value = {"/report/recover" })
    @ResponseBody
    public JSONObject reportRecover(HttpServletRequest request){
        JSONObject custDataReq = HnebRequestUtils.getRequestCustData(request);
        String pkId = custDataReq.getString("pkId");
        JSONArray bizJsonDataRes = new JSONArray();
        JSONObject custDataRes = new JSONObject();
        return HnebRequestUtils.getResponseData(custDataRes, bizJsonDataRes, reportService.recoverReport(pkId));
    }

    @RequestMapping(value = {"/report/list" })
    @ResponseBody
    public JSONObject listReport(HttpServletRequest request){
        JSONObject custDataReq = HnebRequestUtils.getRequestCustData(request);
        JSONObject bizJsonData = HnebRequestUtils.getRequestBizData(request);
        JSONObject filterJsonData = bizJsonData.getJSONObject("filter");
        FilterParamBo filter = (FilterParamBo)filterJsonData.get("tb");

        SearchFilters filters = new SearchFilters();
        if(StringUtils.isNotBlank(filter.getParam().get("CChildNme")))
            filters.add("C_CHILD_NME", SearchOp.LK, filter.getParam().get("CChildNme"));
        if(StringUtils.isNotBlank(filter.getParam().get("CChildSex")))
            filters.add("C_CHILD_SEX", SearchOp.EQ, filter.getParam().get("CChildSex"));
        if(StringUtils.isNotBlank(filter.getParam().get("CPhoneNo")))
            filters.add("C_PHONE_NO", SearchOp.LK, filter.getParam().get("CPhoneNo"));
        if(StringUtils.isNotBlank(filter.getParam().get("birthBgn")))
            filters.add("T_BIRTHDAY", SearchOp.EQ, filter.getParam().get("birthBgn"));
        if(StringUtils.isNotBlank(filter.getParam().get("CMenzhenNo")))
            filters.add("C_MENZHEN_NO", SearchOp.LK, filter.getParam().get("CMenzhenNo"));

        Boolean state0=custDataReq.getBoolean("state0"),
                state1=custDataReq.getBoolean("state1"),
                state2=custDataReq.getBoolean("state2");
        List<String> states = new ArrayList<>();
        if(state0 != null && state0)states.add("0");
        if(state1 != null && state1)states.add("1");
        if(state2 != null && state2)states.add("2");

        if(states.size() > 0)
            filters.add("C_EFF_MRK", SearchOp.IN, states);
        if(StringUtils.isNotBlank(filter.getParam().get("TBgnTm")))
            filters.add("T_TEST_TM", SearchOp.GE,
                    DateUtil.strToDate(filter.getParam().get("TBgnTm"), DateUtil.DATE_YYYY_MM_DD));
        if(StringUtils.isNotBlank(filter.getParam().get("TEndTm"))) {
            Date endDate = DateUtil.strToDate(filter.getParam().get("TEndTm"), DateUtil.DATE_YYYY_MM_DD);
            endDate = DateUtil.calDay(endDate, 1);
            filters.add("T_TEST_TM", SearchOp.LE, endDate);
        }

        PageInfo pageInfo = new PageInfo();
        String pageNumStr = filter.getParam().get("PageNum_");
        String pageSizeStr = filter.getParam().get("PageSize_");
        pageInfo.setPageNum(Integer.parseInt(pageNumStr));
        pageInfo.setPageSize(Integer.parseInt(pageSizeStr));
        PageInfo<LbpcReport> reportPage = reportService.findPage(filters, pageInfo);
        JSONArray bizJsonDataRes = new JSONArray();
        HnebRequestUtils.putBizList(bizJsonDataRes, "query", reportPage);

        JSONObject custDataRes = new JSONObject();
        return HnebRequestUtils.getResponseData(custDataRes, bizJsonDataRes, true);
    }

}
