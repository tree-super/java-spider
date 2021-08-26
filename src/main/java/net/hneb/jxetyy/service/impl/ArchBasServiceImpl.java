package net.hneb.jxetyy.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.hneb.jxetyy.common.mapper.SearchFilters;
import net.hneb.jxetyy.common.mapper.SearchOp;
import net.hneb.jxetyy.dao.ChildrenDao;
import net.hneb.jxetyy.entity.Children;
import net.hneb.jxetyy.entity.User;
import net.hneb.jxetyy.service.ArchBasService;
import net.hneb.jxetyy.service.UserService;
import net.hneb.jxetyy.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * @author zhangshuai
 */
@Slf4j
@Service
public class ArchBasServiceImpl implements ArchBasService {

    @Autowired
    private UserService userService;
    @Autowired
    private ChildrenDao childrenDao;

    public JSONObject getBasicByPhoneNo(String phoneNo) {
        JSONObject result = new JSONObject();
        result.put("hasParent", false);// 是否有家长数据 UserVO
        result.put("hasChildren", false);// 是否有孩子数据
        List<User> parents = userService.selectByPhone(phoneNo);
        if (null == parents || parents.size() < 1) return result;

        result.put("hasParent", true);
        JSONObject parentJson = new JSONObject();
        parentJson.put("CParentId", parents.get(0).getCUserId());
        result.put("parentInfo", parentJson);

        SearchFilters sf = new SearchFilters("C_USER_ID", SearchOp.EQ, parents.get(0).getCUserId());
        List<Children> childList = childrenDao.list(sf.toParamMap());
        if (childList == null || childList.size() < 1) {
            result.put("hasChildren", false);
            return result;
        }
        result.put("hasChildren", true);
        JSONArray childJsonArray = new JSONArray();
        for (Children child : childList) {
            JSONObject childJson = new JSONObject();
            childJson.put("CChildId", child.getCChildId());// 孩子id
            childJson.put("CChildNme", child.getCChildNme());// 姓名
            childJson.put("CChildSex", child.getCChildSex());// 性别
            childJson.put("TBirthday", DateUtil.getDateStr(child.getTBirthday(), "yyyy-MM-dd"));// 出生年月

            childJson.put("TPreBirth", DateUtil.getDateStr(child.getTPreBirth(), "yyyy-MM-dd"));// 预产期
            childJson.put("NBirthHeight", child.getNBirthHeight());// 出生时身高
            childJson.put("NBirthWeight", child.getNBirthWeight());// 出生时体重
            childJson.put("NBirthChest", child.getNBirthChest());// 出生时胸围
            childJson.put("NBirthHead", child.getNBirthHead());// 出生时头围
            childJson.put("NBirthSheight", child.getNBirthSheight());// 出生时坐高
            childJson.put("NConceiveWeek", child.getNConceiveWeek());// 孕周
            childJson.put("NConceiveDay", child.getNConceiveDay());// 孕天
            String illString = "";
            if (child.getCIll() != null) {
                for (String ill : child.getCIll().split(" ")) {
                    if (!("".equals(ill.trim()))) {
                        illString = illString + "$~$" + ill.trim();
                    }
                }
            }
            childJson.put("IllList", illString);
            childJsonArray.add(childJson);
        }
        result.put("childrenInfo", childJsonArray);
        return result;
    }

    /**
     * 保存孩子数据，返回主键
     *
     * @param childJson 姓名、性别、出生日期
     * @param parentId 家长主键
     * @return CChildId 孩子主键
     * @throws Exception
     */
    public String saveChild(JSONObject childJson, String parentId) {

        // 提取参数
        String childNme = childJson.getString("CChildNme");
        String childSex = childJson.getString("CChildSex");
        String birthday = childJson.getString("TBirthday");
        // ---------------------------------------------------------------------
        String prebirthMrk = childJson.getString("CPrebirthMrk"); // 是否早产
        String preBirth = childJson.getString("TPreBirth"); // 预产期
        String conceiveWeek = childJson.getString("NConceiveWeek"); // 孕周
        String conceiveDay = childJson.getString("NConceiveDay"); // 孕天
        String birthHeight = childJson.getString("NBirthHeight"); // 出生时身高
        String birthWeight = childJson.getString("NBirthWeight"); // 出生时体重
        String birthHead = childJson.getString("NBirthHead"); // 出身时头围
        String birthChest = childJson.getString("NBirthChest"); // 出生时胸围
        String birthSheight = childJson.getString("NBirthSheight"); // 出生时坐高
        String birthStsMrk = childJson.getString("CBirthStsMrk"); // 出生时健康状况
        String idNo = childJson.getString("CIdNo"); // 身份证号码
        String phone = childJson.getString("CPhone"); // 联系电话
        String province = childJson.getString("CProvince"); // 所在省
        String city = childJson.getString("CCity"); // 所在市
        String headImg = childJson.getString("CHeadImg"); // 孩子头像
        String illHistory = childJson.getString("CIllHistory"); // 历史疾病
        String lishou = childJson.getString("CLishou"); // 利手
        String education = childJson.getString("CEducation"); // 学历

        // 用姓名、性别、出生日期查询该孩子是否存在
        Children child = selectBy3Ele(childNme, childSex, birthday);
        if (null == child) {
            Children newChild = new Children();

            newChild.setCChildNme(childNme);
            newChild.setCChildSex(childSex);
            newChild.setTBirthday(DateUtil.strToDate(birthday, DateUtil.DATE_YYYY_MM_DD));
            newChild.setCUserId(parentId); // 孩子首要联系人，与基础档案C_PARENT_ID含义不同，
            //			说明如下：
            //			平台内t_child孩子表，一个孩子对应一条数据，第一次创建基础档案时，t_children的C_USER_ID和
            //			t_arch_bas的C_PARENT_ID是相同的；第二次如果该孩子的其他家长陪同就诊同一个科室时，提供的手机
            //			号码和第一次建档不一致，t_children不会新增数据，因为是同一个孩子，基础档案也不会新增，但是家长表
            //			会新增，关联关系表会新增，在不同科室就诊新增档案时（如果不存在），t_children不会新增，
            //			t_arch_bas会创建新档案，同时也使用了新的陪同就诊家长的手机号码；所以，当我们要查看一个家长手机号
            //			码下的孩子，要以t_user,t_children和t_dpt_patient_rel进行关联

            child = newChild;

        }

        // 数据组装（新增和更新）
        if (StringUtils.isNotBlank(prebirthMrk))
            child.setCPrebirthMrk(prebirthMrk);
        if (StringUtils.isNotBlank(preBirth))
            child.setTPreBirth(DateUtil.strToDate(preBirth, DateUtil.DATE_YYYY_MM_DD));
        if (StringUtils.isNotBlank(conceiveWeek))
            child.setNConceiveWeek(Integer.parseInt(conceiveWeek));
        if (StringUtils.isNotBlank(conceiveDay))
            child.setNConceiveDay(Integer.parseInt(conceiveDay));
        if (StringUtils.isNotBlank(birthHeight))
            child.setNBirthHeight(Float.parseFloat(birthHeight));
        if (StringUtils.isNotBlank(birthWeight))
            child.setNBirthWeight(Float.parseFloat(birthWeight));
        if (StringUtils.isNotBlank(birthHead))
            child.setNBirthHead(Float.parseFloat(birthHead));
        if (StringUtils.isNotBlank(birthChest))
            child.setNBirthChest(Float.parseFloat(birthChest));
        if (StringUtils.isNotBlank(birthSheight))
            child.setNBirthSheight(Float.parseFloat(birthSheight));
        if (StringUtils.isNotBlank(birthStsMrk))
            child.setCBirthStsMrk(birthStsMrk);
        if (StringUtils.isNotBlank(idNo))
            child.setCIdNo(idNo);
        if (StringUtils.isNotBlank(phone))
            child.setCPhone(phone);
        if (StringUtils.isNotBlank(province))
            child.setCProvince(province);
        if (StringUtils.isNotBlank(city))
            child.setCCity(city);
        if (StringUtils.isNotBlank(headImg))
            child.setCHeadImg(headImg);
        if (StringUtils.isNotBlank(lishou))
            child.setCLishou(lishou);
        if (StringUtils.isNotBlank(education))
            child.setCEducation(education);
        if (StringUtils.isNotBlank(illHistory)){
            String ills[] = illHistory.split(" ");
            for (String key : ills) {
                if (child.getCIll() == null) {
                    child.setCIll(key.trim());
                } else if (child.getCIll().indexOf(key.trim()) == -1) {
                    child.setCIll(child.getCIll() + " " + key.trim());
                }
            }
        }

        child.setTCrtTm(new Date());
        child.setTUpdTm(new Date());
        if(StringUtils.isNotBlank(child.getCChildId())){
            childrenDao.updateByPrimaryKeySelective(child);
            return child.getCChildId();
        }
        child.setCChildId(UUID.randomUUID().toString());
        // 保存数据
        childrenDao.insertSelective(child);
        // 返回主键
        return child.getCChildId();
    }


    public Children selectBy3Ele(String childNme, String childSex, String birthday){
        if(StringUtils.isBlank(childNme)
            || StringUtils.isBlank(childSex)
                || StringUtils.isBlank(birthday)) return null;
//        SearchFilters filters = new SearchFilters("C_CHILD_NME", SearchOp.EQ, childNme);
//        filters.add("C_CHILD_SEX", SearchOp.EQ, childSex);
//        filters.add("T_BIRTHDAY", SearchOp.EQ, DateUtil.StrToDate(birthday, DateUtil.DATE_YYYY_MM_DD));
//        childrenDao.list(filters.toParamMap());
        Children example = new Children();
        example.setCChildNme(childNme);
        example.setCChildSex(childSex);
        example.setTBirthday(DateUtil.strToDate(birthday, DateUtil.DATE_YYYY_MM_DD));

        List<Children> children = childrenDao.select(example);
        if(children !=null && children.size() >0)
            return children.get(0);
        return null;
    }
}
