package net.hneb.jxetyy.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.hneb.jxetyy.common.mapper.SearchFilters;
import net.hneb.jxetyy.dao.BasNormDao;
import net.hneb.jxetyy.dao.LbpcReportDao;
import net.hneb.jxetyy.entity.BasNorm;
import net.hneb.jxetyy.entity.LbpcQuest;
import net.hneb.jxetyy.entity.LbpcReport;
import net.hneb.jxetyy.service.ReportService;
import net.hneb.jxetyy.utils.DateUtil;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    private static HashMap<Double, Double> bfwMap = new HashMap<>(61);
    static {
        bfwMap.put(-3.0, 0.13);
        bfwMap.put(-2.9, 0.19);
        bfwMap.put(-2.8, 0.26);
        bfwMap.put(-2.7, 0.35);
        bfwMap.put(-2.6, 0.47);
        bfwMap.put(-2.5, 0.62);
        bfwMap.put(-2.4, 0.82);
        bfwMap.put(-2.3, 1.07);
        bfwMap.put(-2.2, 1.39);
        bfwMap.put(-2.1, 1.79);
        bfwMap.put(-2.0, 2.28);
        bfwMap.put(-1.9, 2.87);
        bfwMap.put(-1.8, 3.59);
        bfwMap.put(-1.7, 4.46);
        bfwMap.put(-1.6, 5.48);
        bfwMap.put(-1.5, 6.68);
        bfwMap.put(-1.4, 8.08);
        bfwMap.put(-1.3, 9.68);
        bfwMap.put(-1.2, 11.51);
        bfwMap.put(-1.1, 13.57);
        bfwMap.put(-1.0, 15.87);
        bfwMap.put(-0.9, 18.41);
        bfwMap.put(-0.8, 21.19);
        bfwMap.put(-0.7, 24.20);
        bfwMap.put(-0.6, 27.43);
        bfwMap.put(-0.5, 30.85);
        bfwMap.put(-0.4, 34.46);
        bfwMap.put(-0.3, 38.21);
        bfwMap.put(-0.2, 42.07);
        bfwMap.put(-0.1, 46.06);
        bfwMap.put(0.0, 50.00);
        bfwMap.put(0.1, 53.98);
        bfwMap.put(0.2, 57.93);
        bfwMap.put(0.3, 61.79);
        bfwMap.put(0.4, 65.54);
        bfwMap.put(0.5, 69.15);
        bfwMap.put(0.6, 72.57);
        bfwMap.put(0.7, 75.80);
        bfwMap.put(0.8, 78.81);
        bfwMap.put(0.9, 81.59);
        bfwMap.put(1.0, 84.13);
        bfwMap.put(1.1, 86.43);
        bfwMap.put(1.2, 88.49);
        bfwMap.put(1.3, 90.32);
        bfwMap.put(1.4, 91.92);
        bfwMap.put(1.5, 93.32);
        bfwMap.put(1.6, 94.52);
        bfwMap.put(1.7, 95.54);
        bfwMap.put(1.8, 96.41);
        bfwMap.put(1.9, 97.13);
        bfwMap.put(2.0, 97.72);
        bfwMap.put(2.1, 98.21);
        bfwMap.put(2.2, 98.62);
        bfwMap.put(2.3, 98.93);
        bfwMap.put(2.4, 99.18);
        bfwMap.put(2.5, 99.38);
        bfwMap.put(2.6, 99.53);
        bfwMap.put(2.7, 99.65);
        bfwMap.put(2.8, 99.74);
        bfwMap.put(2.9, 99.81);
        bfwMap.put(3.0, 99.87);
    }

    @Value("${net.hneb.deptname}")
    private String deptname;

    @Autowired
    private LbpcReportDao lbpcReportDao;
    @Autowired
    private BasNormDao basNormDao;

    @Override
    public List<LbpcReport> select(SearchFilters filters) {
        return lbpcReportDao.list(filters.toParamMap());
    }

    /**
     * 保存
     */
    @Override
    public String saveReport(LbpcReport lbpcReport) {
        lbpcReport.setCPkId(UUID.randomUUID().toString());
        lbpcReport.setCOrgPkId(lbpcReport.getCPkId());
        lbpcReport.setCDptId("sys");
        lbpcReport.setCOfficeId("sys");
        lbpcReport.setCUserId("sys");
        lbpcReport.setNSeqNum(1);
        lbpcReport.setTTestTm(new Date());
        // 保存数据
        lbpcReportDao.insertSelective(lbpcReport);

        // 返回主键
        return lbpcReport.getCPkId();
    }



    /**
     * 创建最大报告，将LbpcQuestVO的数据全部复制到LbpcReport中
     *
     * @param quest
     * @param basicJson
     * @return
     * @throws Exception
     */
    @Override
    public LbpcReport createFullReport(LbpcQuest quest, JSONObject basicJson) {
        LbpcReport report = new LbpcReport();

        try {
            PropertyUtils.copyProperties(report, quest);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        report.setCChildNme(basicJson.getString("CChildNme"));
        report.setCChildSex(basicJson.getString("CChildSex"));
        report.setTBirthday(basicJson.getString("TBirthday"));
        report.setCPhoneNo(basicJson.getString("CPhoneNo"));
        report.setCEffMrk("1");
        return report;
    }


    /**
     * 根据 id 查找
     */
    public LbpcReport findById(String id){
//        LbpcReport report = new LbpcReport();
//        report.setCPkId(id);
//        return lbpcReportDao.selectOne(report);
        return lbpcReportDao.selectByPrimaryKey(id);
//        if(reports == null || reports.isEmpty()) return null;

//        return lbpcReportDao.findOne(id);
    }

    /**
     * 根据pkId获取量表测评报告
     *
     * @param pkId
     * @return
     * @throws Exception
     */
    public JSONObject getLbpcReport(String pkId) {

        LbpcReport report = findById(pkId);

        JSONObject reportJson = (JSONObject) JSONObject.toJSON(report);
        JSONObject basicJson = new JSONObject();
        basicJson.put("CLbId", report.getCLbId());
        basicJson.put("CPkId", report.getCPkId());
        basicJson.put("TTestTm", DateUtil.getDateStr(report.getTTestTm(), "yyyy-MM-dd"));
        basicJson.put("CUserId", report.getCUserId());
        basicJson.put("CParentId", report.getCParentId());
        basicJson.put("CChildId", report.getCChildId());
        basicJson.put("CMenzhenNo", report.getCMenzhenNo());
        basicJson.put("CIllHistory", report.getCIllHistory());
        basicJson.put("CDoctor", report.getCDoctor());
        basicJson.put("CExecutor", report.getCExecutor());
        basicJson.put("NConceiveWeek", report.getNConceiveWeek());
        basicJson.put("NConceiveDay", report.getNConceiveDay());
        basicJson.put("CChildRel", report.getCChildRel());
        basicJson.put("CSchool", report.getCSchool());
        basicJson.put("CClazz", report.getCClazz());
        basicJson.put("CGrade", report.getCGrade());

        basicJson.put("CChildNme", report.getCChildNme());
//        basicJson.put("CChildSex", report.getCChildSex());
        basicJson.put("TBirthday", report.getTBirthday());
        basicJson.put("TPreBirth", report.getTPreBirth());
        basicJson.put("NBirthHeight", report.getNBirthHeight());
        basicJson.put("NBirthWeight", report.getNBirthWeight());
        basicJson.put("NBirthHead", report.getNBirthHead());
        basicJson.put("NBirthChest", report.getNBirthChest());
        basicJson.put("NBirthSheight", report.getNBirthSheight());

        basicJson.put("CHospitalNme", deptname);
        basicJson.put("CPhoneNo", report.getCPhoneNo());
        basicJson.put("CInfoOffer", report.getCInfoOffer());

        basicJson.put("CAge", getAge(report));
        basicJson.put("CJzAge", getJzAge(report));// 矫正年龄

        reportJson.put("TTestTm", DateUtil.getDateStr(report.getTTestTm(), "yyyy-MM-dd"));

        for (int i = 1; i <= 5; i++) {
            try {
                basicJson.put("CExt" + i, BeanUtilsBean.getInstance().getProperty(report, "CExt" + i));
            } catch (Exception e) {
                log.error("获取 report ext 属性出错,", e);
                e.printStackTrace();
            }
        }

        JSONObject result = new JSONObject();
        result.put("basic", basicJson);
        result.put("formData", reportJson);

        JSONObject extraData = new JSONObject();
        result.put("extraData", extraData);

        return result;
    }

    @Override
    public LbpcReport create(LbpcQuest quest, JSONObject basicJson, JSONObject custData) {
        LbpcReport report = createFullReport(quest, basicJson);

        //type 1视觉 2听觉 3视听混合
        String type = report.getCExt3();
        if("0".equals(type)){
            type="v";
        }else if ("1".equals(type)){
            type="a";
        }
        else if ("2".equals(type)){
            type="va";
        }
        //Z分
        double cwZ,lbZ , rtZ;
        double cwBfw,lbBfw,rtBfw;
        //T分
        double cwT,lbT,rtT;
        //错误、漏报总数
        int cwSum = 0;
        int lbSum = 0;
        //结论
        String result1 = "", result2 = "";
        //平均反应时间
        int reactionTime  = 0 ;
        //年龄
        Integer nYear = report.getNYear();
        String cExt1 = report.getCExt1();
        String cExt2 = report.getCExt2();
        //计算总数
        JSONArray jsonArray = JSON.parseArray(cExt1);
        for(int i = 0;i<jsonArray.size();i++){
            cwSum+=jsonArray.getJSONObject(i).getInteger("cw");
            lbSum+=jsonArray.getJSONObject(i).getInteger("lb");
        }
        //计算反应时间
        if("".equals(cExt2)||cExt2==null){
            cExt2 ="1500";
        }
        reactionTime = Double.valueOf(cExt2).intValue();

        //计算Z分
        cwZ = saveDecimal(calculateZ(nYear+"","cw",type,cwSum));
        lbZ = saveDecimal(calculateZ(nYear+"","lb",type,lbSum));
        rtZ = saveDecimal(calculateZ(nYear+"","fy",type,reactionTime));

        //计算百分位
        if (cwZ > 3) {
            cwBfw = 99.87;
        } else if (cwZ < -3) {
            cwBfw = 0.13;
        } else {
            cwBfw = bfwMap.get(cwZ);
        }
        if (lbZ > 3) {
            lbBfw = 99.87;
        } else if (lbZ < -3) {
            lbBfw = 0.13;
        } else {
            lbBfw = bfwMap.get(lbZ);
        }
        if (rtZ > 3) {
            rtBfw = 99.87;
        } else if (rtZ < -3) {
            rtBfw = 0.13;
        } else {
            rtBfw = bfwMap.get(rtZ);
        }

        //计算T分 由Z分计算T分：公式：T=50+10 ＊Z
        cwT = 50 + 10 * cwZ;
        lbT = 50 + 10 * lbZ;
        rtT = 50 + 10 * rtZ;

        //计算结论
        if (cwZ < 1.5 && lbZ < 1.5) {
            result1 = "无";
            result2 = "";
        } else if ((cwZ >= 1.5 && cwZ < 2) || (lbZ >= 1.5 && lbZ < 2)) {
            result1 = "可疑";
            result2 = "";
        } else if ((cwZ >= 2 && cwZ < 2.3) || (lbZ >= 2 && lbZ < 2.3)) {
            result1 = "有";
            result2 = "轻度";
        } else if ((cwZ >= 2.3 && cwZ < 3) || (lbZ >= 2.3 && lbZ < 3)) {
            result1 = "有";
            result2 = "中度";
        } else if (cwZ >= 3 || lbZ >= 3) {
            result1 = "有";
            result2 = "重度";
        }

        //存入参数
        report.setCAns1(cwBfw+"");
        report.setCAns2(lbBfw+"");
        report.setCAns3(rtBfw+"");
        report.setCAns4(cwT+"");
        report.setCAns5(lbT+"");
        report.setCAns6(rtT+"");
        report.setCAns7(result1);
        report.setCAns8(result2);
        report.setCAns9(cwSum+"");
        report.setCAns10(lbSum+"");
        report.setCAns11(reactionTime+"");

        report.setCEffMrk("1");
        report.setTCrtTm(new Date());
        report.setTUpdTm(new Date());

        return report;
    }

    private String getAge(LbpcReport report) {
        String value = "";
        Integer NYear = report.getNYear();
        Integer NMonth = report.getNMonth();
        Integer NDay = report.getNDay();
        if (NYear != null && NYear != 0) {
            value += NYear + "岁";
        }
        if (NMonth != null && NMonth != 0) {
            value += NMonth + "月";
        }
        if (NDay != null) {// 0天是可以出现的
            value += NDay + "天";
        }
        return value;
    }

    /**
     * 获取矫正年龄
     *
     * @param report
     * @return
     */
    private String getJzAge(LbpcReport report) {
        String value = "";
        Integer NYear = report.getNYear1();
        Integer NMonth = report.getNMonth1();
        Integer NDay = report.getNDay1();
        if (NYear != null && NYear != 0) {
            value += NYear + "岁";
        }
        if (NMonth != null && NMonth != 0) {
            value += NMonth + "月";
        }
        if (NDay != null) {// 0天是可以出现的
            value += NDay + "天";
        }
        return value;
    }

    public double saveDecimal(double d){
        BigDecimal bd = new BigDecimal(d);

        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);

        return bd.doubleValue();
    }

    public  double calculateZ (String nYear,String lx,String type,int zs){
        //计算Z分
        BasNorm example = new BasNorm();
        example.setCLbId("0809");
        example.setCStr1(nYear);
        example.setCStr2(lx);
        example.setCStr5(type);
        List<BasNorm> bas = basNormDao.select(example);

        //Z分：Z=（X-均数）/SD
        //X:为测试所得的错误总数、漏报总数、平均反应时间
        //均数：请见附见的CPT文件中的每个测试项目相应年龄组的X
        //SD：请见附见的CPT文件中的每个测试项目相应年龄组的SD
        double x = bas.get(0).getCStr3();
        double sd = bas.get(0).getCStr4();
        return  (zs-x)/sd;

    }

}
