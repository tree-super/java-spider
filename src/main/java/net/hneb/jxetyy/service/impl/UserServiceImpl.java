package net.hneb.jxetyy.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.hneb.jxetyy.dao.UserDao;
import net.hneb.jxetyy.entity.User;
import net.hneb.jxetyy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public List<User> selectByPhone(String phone) {
        User example = new User();
        example.setCPhoneNo(phone);
        return userDao.select(example);
    }

    /**
     * 保存家长数据，返回主键
     *
     * @param phoneNo 手机号码
     * @return CUserId 家长主键
     */
    public String saveParent(String phoneNo) {

        // 用手机号码查询该家长是否存在
        List<User> exists = selectByPhone(phoneNo);
        if (null != exists && exists.size() > 0) return exists.get(0).getCUserId();
        User newParent = new User();
        newParent.setLoginCode(phoneNo);
        newParent.setLoginAlias(phoneNo);
        newParent.setCPhoneNo(phoneNo);
        newParent.setCUserNme(phoneNo);
        newParent.setCPassword("*");
        newParent.setCRole("0");
        newParent.setUserStatus(2);
        newParent.setCDataSrc("3");
        newParent.setCSubscribeMrk("0");
        newParent.setCId2(UUID.randomUUID().toString());

        newParent.setCUserId(UUID.randomUUID().toString());
        // 设置公共属性
        newParent.setTCrtTm(new Date());
        newParent.setTUpdTm(new Date());
        // 保存数据
        userDao.insertSelective(newParent);

        // 返回主键
        return newParent.getCUserId();
    }

}
