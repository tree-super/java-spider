package net.hneb.jxetyy.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import net.hneb.jxetyy.common.mapper.SearchFilters;
import net.hneb.jxetyy.entity.LbpcQuest;
import net.hneb.jxetyy.entity.LbpcReport;

import java.util.List;

public interface ReportService {

    /**
     */
    public List<LbpcReport> select(SearchFilters filters);

    /**
     * 根据关键字分页查询
     */
    PageInfo<LbpcReport> findPage(SearchFilters filters, PageInfo pageInfo);

    String saveReport(LbpcReport lbpcReport);

    LbpcReport createFullReport(LbpcQuest quest, JSONObject basicJson);

    LbpcReport findById(String id);

    JSONObject getLbpcReport(String pkId);

    LbpcReport create(LbpcQuest quest, JSONObject basicJson, JSONObject custData);


}
