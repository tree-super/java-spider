package net.hneb.jxetyy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.hneb.jxetyy.service.ReportService;
import net.hneb.jxetyy.utils.HnebRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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

    @RequestMapping(value = {"/report/0809" })
    public String report0809(@RequestParam(name = "pkId") String pkId, Model model) {
        return "report/0809-report";
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

}
