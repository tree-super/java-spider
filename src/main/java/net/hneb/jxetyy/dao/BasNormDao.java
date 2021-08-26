package net.hneb.jxetyy.dao;

import net.hneb.jxetyy.entity.BasNorm;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author zhangshuai
 */
public interface BasNormDao extends Mapper<BasNorm> {
    List<BasNorm> list(Map<String, Object> params);
}
