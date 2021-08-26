package net.hneb.jxetyy.dao;

import net.hneb.jxetyy.entity.User;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author zhangshuai
 */
public interface UserDao extends Mapper<User> {
    List<User> list(Map<String, Object> params);
}
