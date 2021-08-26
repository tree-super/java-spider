package net.hneb.jxetyy.service;

import com.alibaba.fastjson.JSONObject;
import net.hneb.jxetyy.common.mapper.SearchFilters;
import net.hneb.jxetyy.entity.LbpcQuest;
import net.hneb.jxetyy.entity.LbpcReport;

import java.util.List;

public interface ReportService {

    /**
     */
    public List<LbpcReport> select(SearchFilters filters);

    String saveReport(LbpcReport lbpcReport);

    LbpcReport createFullReport(LbpcQuest quest, JSONObject basicJson);

    LbpcReport findById(String id);

    JSONObject getLbpcReport(String pkId);

    LbpcReport create(LbpcQuest quest, JSONObject basicJson, JSONObject custData);


}
