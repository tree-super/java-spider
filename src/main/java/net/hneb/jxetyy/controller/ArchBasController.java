package net.hneb.jxetyy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.hneb.jxetyy.service.ArchBasService;
import net.hneb.jxetyy.utils.HnebRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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

}
