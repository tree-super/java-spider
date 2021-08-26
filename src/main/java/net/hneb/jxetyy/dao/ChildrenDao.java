package net.hneb.jxetyy.dao;

import net.hneb.jxetyy.entity.Children;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author zhangshuai
 */
public interface ChildrenDao extends Mapper<Children> {
    List<Children> list(Map<String, Object> params);
}
