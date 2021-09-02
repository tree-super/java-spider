package net.hneb.jxetyy.dao;

import net.hneb.jxetyy.entity.LbpcReport;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author zhangshuai
 */
public interface LbpcReportDao extends Mapper<LbpcReport> {
    List<LbpcReport> list(Map<String, Object> params);

    LbpcReport findOne(String id);

    int updateState(@Param("id") String id, @Param("mrk") String CEffMrk);
}
