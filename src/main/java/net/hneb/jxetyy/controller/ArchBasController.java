package net.hneb.jxetyy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.hneb.jxetyy.common.mapper.SearchFilters;
import net.hneb.jxetyy.common.mapper.SearchOp;
import net.hneb.jxetyy.entity.Children;
import net.hneb.jxetyy.service.ArchBasService;
import net.hneb.jxetyy.utils.DateUtil;
import net.hneb.jxetyy.utils.HnebRequestUtils;
import net.hneb.jxetyy.vo.FilterParamBo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class ArchBasController {
    @Autowired
    private ArchBasService archBasService;

    /**
     * 档案页面
     */
    @RequestMapping(value = {"/arch" })
    public String arch(HttpServletRequest servletRequest) {
        return "arch";
    }

    /**
     * 通过手机号获取测评的基本basic节点数据，返回结果数据类型见pcBm内的方法<br>
     * 下拉框展示该手机号对应的孩子基础信息<br>
     * phoneNo-父母标识
     * ArchBasVO
     */
    @RequestMapping(value = {"/arch/getBasicByPhoneNo" })
    @ResponseBody
    public JSONObject getBasicByPhoneNo(HttpServletRequest servletRequest) {
        JSONObject custData = HnebRequestUtils.getRequestCustData(servletRequest);
        String phoneNo = custData.getString("phoneNo");
        JSONObject result = archBasService.getBasicByPhoneNo(phoneNo);
        return HnebRequestUtils.getResponseData(result, new JSONArray(), true);
    }

    @RequestMapping(value = {"/arch/list" })
    @ResponseBody
    public JSONObject listArchs(HttpServletRequest request){
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
            filters.add("C_PHONE", SearchOp.LK, filter.getParam().get("CPhoneNo"));
        if(StringUtils.isNotBlank(filter.getParam().get("birthBgn")))
            filters.add("T_BIRTHDAY", SearchOp.EQ, filter.getParam().get("birthBgn"));
        if(StringUtils.isNotBlank(filter.getParam().get("CMenzhenNo")))
            filters.add("C_ID_NO", SearchOp.LK, filter.getParam().get("CMenzhenNo"));

        PageInfo pageInfo = new PageInfo();
        String pageNumStr = filter.getParam().get("PageNum_");
        String pageSizeStr = filter.getParam().get("PageSize_");
        pageInfo.setPageNum(Integer.parseInt(pageNumStr));
        pageInfo.setPageSize(Integer.parseInt(pageSizeStr));
        PageInfo<Children> childrenPage = archBasService.findPage(filters, pageInfo);
        JSONArray bizJsonDataRes = new JSONArray();
        HnebRequestUtils.putBizList(bizJsonDataRes, "archMgr", childrenPage);

        JSONObject custDataRes = new JSONObject();
        return HnebRequestUtils.getResponseData(custDataRes, bizJsonDataRes, true);
    }

}
