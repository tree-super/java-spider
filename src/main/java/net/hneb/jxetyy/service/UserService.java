package net.hneb.jxetyy.service;

import net.hneb.jxetyy.entity.User;

import java.util.List;

public interface UserService {

    /**
     * 通过手机号查询
     * @param phone
     * @return
     */
    public List<User> selectByPhone(String phone);

    String saveParent(String cPhoneNo);
}
