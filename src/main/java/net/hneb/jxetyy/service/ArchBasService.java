package net.hneb.jxetyy.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import net.hneb.jxetyy.common.mapper.SearchFilters;
import net.hneb.jxetyy.entity.Children;

/**
 * @author zhangshuai
 */
public interface ArchBasService {
    /**
     * @return
     */
    JSONObject getBasicByPhoneNo(String phoneNo);

    String saveChild(JSONObject basicJson, String parentId);

    /**
     * 用姓名、性别、出生日期查询该孩子
     * @return
     */
    Children selectBy3Ele(String childNme, String childSex, String birthday);

    PageInfo<Children> findPage(SearchFilters filters, PageInfo pageInfo);
}
