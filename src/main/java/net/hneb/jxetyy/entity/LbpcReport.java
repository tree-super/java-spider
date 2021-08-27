package net.hneb.jxetyy.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.util.TypeUtils;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * LbpcReport
 */
@Data
@Table(name = "t_lbpc_report")
public class LbpcReport implements java.io.Serializable {

	private static final long serialVersionUID = 3007568492686203507L;

	@Id
	@Column(name="C_PK_ID")
//	@JSONField(name="CPkId")
	private String CPkId;
	@Column(name="C_ORG_PK_ID")
	private String COrgPkId;
	@Column(name="C_PKG_ID")
	private String CPkgId;
	@Column(name="C_SVC_PKG_ID")
	private String CSvcPkgId;
	@Column(name="N_SEQ_NUM")
	private Integer NSeqNum;
	@Column(name="C_LB_ID")
	private String CLbId;
	@Column(name="C_CHILD_ID")
	private String CChildId;
	@Column(name="C_PARENT_ID")
	private String CParentId;
	@Column(name="C_CHILD_NME")
	private String CChildNme;
	@Column(name="C_CHILD_SEX")
	private String CChildSex;
	@Column(name="T_BIRTHDAY")
	private String TBirthday;
	@Column(name="C_PHONE_NO")
	private String CPhoneNo;
	@Column(name="N_YEAR")
	private Integer NYear;
	@Column(name="N_MONTH")
	private Integer NMonth;
	@Column(name="N_DAY")
	private Integer NDay;
	@Column(name="N_CONCEIVE_WEEK")
	private Integer NConceiveWeek;
	@Column(name="N_CONCEIVE_DAY")
	private Integer NConceiveDay;
	@Column(name="T_PRE_BIRTH")
	private String TPreBirth;
	@Column(name="N_YEAR1")
	private Integer NYear1;
	@Column(name="N_MONTH1")
	private Integer NMonth1;
	@Column(name="N_DAY1")
	private Integer NDay1;
	@Column(name="N_BIRTH_HEIGHT")
	private Float NBirthHeight;
	@Column(name="N_BIRTH_WEIGHT")
	private Float NBirthWeight;
	@Column(name="N_BIRTH_CHEST")
	private Float NBirthChest;
	@Column(name="N_BIRTH_SHEIGHT")
	private Float NBirthSheight;
	@Column(name="N_BIRTH_HEAD")
	private Float NBirthHead;
	@Column(name="C_DPT_ID")
	private String CDptId;
	@Column(name="C_OFFICE_ID")
	private String COfficeId;
	@Column(name="C_EXECUTOR")
	private String CExecutor;
	@Column(name="C_DOCTOR")
	private String CDoctor;
	@Column(name="C_INFO_OFFER")
	private String CInfoOffer;
	@Column(name="C_USER_ID")
	private String CUserId;
	@Column(name="T_TEST_TM")
	private Date TTestTm;
	@Column(name="C_MENZHEN_NO")
	private String CMenzhenNo;
	@Column(name="C_CHILD_REL")
	private String CChildRel;
	@Column(name="C_ILL_HISTORY")
	private String CIllHistory;
	@Column(name="C_EFF_MRK")
	private String CEffMrk;
	@Column(name="C_SCHOOL")
	private String CSchool;
	@Column(name="C_CLAZZ")
	private String CClazz;
	@Column(name="C_GRADE")
	private String CGrade;
	@Column(name="T_CRT_TM")
	private Date TCrtTm;
	@Column(name="C_CRT_ID")
	private String CCrtId;
	@Column(name="T_UPD_TM")
	private Date TUpdTm;
	@Column(name="C_UPD_ID")
	private String CUpdId;
	@Column(name="C_ANS_1")
	private String CAns1;
	@Column(name="C_ANS_2")
	private String CAns2;
	@Column(name="C_ANS_3")
	private String CAns3;
	@Column(name="C_ANS_4")
	private String CAns4;
	@Column(name="C_ANS_5")
	private String CAns5;
	@Column(name="C_ANS_6")
	private String CAns6;
	@Column(name="C_ANS_7")
	private String CAns7;
	@Column(name="C_ANS_8")
	private String CAns8;
	@Column(name="C_ANS_9")
	private String CAns9;
	@Column(name="C_ANS_10")
	private String CAns10;
	@Column(name="C_ANS_11")
	private String CAns11;
	@Column(name="C_ANS_12")
	private String CAns12;
	@Column(name="C_ANS_13")
	private String CAns13;
	@Column(name="C_ANS_14")
	private String CAns14;
	@Column(name="C_ANS_15")
	private String CAns15;
	@Column(name="C_EXT1")
	private String CExt1;
	@Column(name="C_EXT2")
	private String CExt2;
	@Column(name="C_EXT3")
	private String CExt3;
	@Column(name="C_EXT4")
	private String CExt4;
	@Column(name="C_EXT5")
	private String CExt5;


}