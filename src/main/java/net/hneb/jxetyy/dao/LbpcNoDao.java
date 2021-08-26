package net.hneb.jxetyy.dao;

import net.hneb.jxetyy.entity.LbpcNo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author zhangshuai
 */
public interface LbpcNoDao extends Mapper<LbpcNo> {
    List<LbpcNo> list(Map<String, Object> params);
}
