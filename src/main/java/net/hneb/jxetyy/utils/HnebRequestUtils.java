package net.hneb.jxetyy.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.hneb.jxetyy.vo.FilterParamBo;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangshuai
 */
@Slf4j
public class HnebRequestUtils {
    static {
        TypeUtils.compatibleWithJavaBean = true;
    }

    public static JSONObject getRequestCustData(HttpServletRequest servletRequest) {
        JSONObject json = getRequestJson(servletRequest);
        return json.getJSONObject("custData_");
    }

    public static JSONObject getRequestJson(HttpServletRequest servletRequest) {
        String RequestJsonData_ = servletRequest.getParameter("RequestJsonData_");
        //		System.out.println(RequestJsonData_);
        String requestJsonData = null;
        try {
            requestJsonData = URLDecoder.decode(URLDecoder.decode(RequestJsonData_, "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("unicode编码出错, ", e);
        }
        return JSON.parseObject(requestJsonData);
    }

    public static JSONObject getRequestBizData(HttpServletRequest servletRequest) {
        JSONObject json = getRequestJson(servletRequest);
        JSONArray bizDataJsonArray = json.getJSONArray("bizData_");
        JSONObject bizJsonData = new JSONObject();
        JSONObject filterJsonData = new JSONObject();
        for (int i = 0; i < bizDataJsonArray.size(); i++) {
            JSONObject bizData = (JSONObject) bizDataJsonArray.get(i);
            String formId = (String) bizData.get("formId_");
            JSONArray dataArray = bizData.getJSONArray("data_");
            bizJsonData.put(formId, dataArray);

            if (bizData.get("filter_") != null) {
                JSONObject filter = bizData.getJSONObject("filter_");
                FilterParamBo filterBo = new FilterParamBo();
                int pageNum = Integer.parseInt(filter.getString("PageNum_"));
                int pageSize = Integer.parseInt(filter.getString("PageSize_"));
                filterBo.setFirstRow((pageNum - 1) * pageSize);
//                filterBo.setMaxRow(pageNum*pageSize);
                filterBo.setMaxRow(pageSize);
//                filter.remove("PageNum_");
//                filter.remove("PageSize_");
                Map t = new HashMap();
                for (Object key : filter.keySet()) {
                    if (!"".equals(filter.get(key))) {
                        t.put(key, filter.get(key).toString());
                    }
                }
                filterBo.setParam(t);
                filterJsonData.put(formId, filterBo);
            }
        }
        bizJsonData.put("filter", filterJsonData);
        return bizJsonData;
    }

    public static void putBizList(JSONArray bizJsonDataRes, String dzId, PageInfo pageInfo) {
        JSONObject data = new JSONObject();
        data.put("total", pageInfo.getTotal());
        if(pageInfo.getList() != null && pageInfo.getList().size() > 0)
            data.put("rows", JSON.toJSON(pageInfo.getList()));
        else
            data.put("rows", pageInfo.getList());
        JSONObject data2 = new JSONObject();
        data2.put("formId_", dzId);
        data2.put("data_", data);
        bizJsonDataRes.add(data2);
    }

    public static JSONObject getBizData(String dzId, JSONObject json) {
        JSONObject data = new JSONObject();
        JSONObject jsonArray[] = { json };
        data.put("total", 1);
        data.put("rows", jsonArray);
        JSONObject data2 = new JSONObject();
        data2.put("formId_", dzId);
        data2.put("data_", data);
        return data2;
    }


    public static JSONObject getResponseData(JSONObject custJsonData, JSONArray bizJsonData, boolean responseStatus) {
        JSONObject res = new JSONObject();
        res.put("CustJsonData_", custJsonData);
        res.put("BizJsonData_", bizJsonData);
        res.put("ResponseStatus_", responseStatus);
        return res;
    }
}
