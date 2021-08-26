package net.hneb.jxetyy.service;

import com.alibaba.fastjson.JSONObject;
import net.hneb.jxetyy.entity.Children;

/**
 * @author zhangshuai
 */
public interface ArchBasService {
    /**
     * @return
     */
    public JSONObject getBasicByPhoneNo(String phoneNo);

    String saveChild(JSONObject basicJson, String parentId);

    /**
     * 用姓名、性别、出生日期查询该孩子
     * @return
     */
    public Children selectBy3Ele(String childNme, String childSex, String birthday);
}
