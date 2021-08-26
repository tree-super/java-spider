package net.hneb.jxetyy.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * t_lbpc_quest
 */
@Data
@Table(name = "t_lbpc_quest")
public class LbpcQuest implements java.io.Serializable {

	private static final long serialVersionUID = -2217374227080425015L;

	// Fields
	@Id
	private String CPkId;
	private String COrgPkId;
	private String CPkgId;
	private String CSvcPkgId;
	private Integer NSeqNum;
	private String CLbId;
	private String CChildId;
	private String CParentId;
	private String CChildNme;
	private String CChildSex;
	private Date TBirthday;
	private String CPhoneNo;
	private Integer NYear;
	private Integer NMonth;
	private Integer NDay;
	private Integer NConceiveWeek;
	private Integer NConceiveDay;
	private Date TPreBirth;
	private Integer NYear1;
	private Integer NMonth1;
	private Integer NDay1;
	private Float NBirthHeight;
	private Float NBirthWeight;
	private Float NBirthChest;
	private Float NBirthSheight;
	private Float NBirthHead;
	private String CDptId;
	private String COfficeId;
	private String CDoctor;
	private String CExecutor;
	private String CInfoOffer;
	private String CUserId;
	private Date TTestTm;
	private String CMenzhenNo;
	private String CChildRel;
	private String CIllHistory;
	private String CState;
	private String CEffMrk;
	private String CFormId;

	private String CSchool;
	private String CClazz;
	private String CGrade;

	private Date TCrtTm;
	private String CCrtId;
	private Date TUpdTm;
	private String CUpdId;
	private String CAns1;
	private String CAns2;
	private String CAns3;
	private String CAns4;
	private String CAns5;
	private String CAns6;
	private String CAns7;
	private String CAns8;
	private String CAns9;
	private String CAns10;
	private String CAns11;
	private String CAns12;
	private String CAns13;
	private String CAns14;
	private String CAns15;
	private String CAns16;
	private String CAns17;
	private String CAns18;
	private String CAns19;
	private String CAns20;
	private String CAns21;
	private String CAns22;
	private String CAns23;
	private String CAns24;
	private String CAns25;
	private String CAns26;
	private String CAns27;
	private String CAns28;
	private String CAns29;
	private String CAns30;
	private String CAns31;
	private String CAns32;
	private String CAns33;
	private String CAns34;
	private String CAns35;
	private String CAns36;
	private String CAns37;
	private String CAns38;
	private String CAns39;
	private String CAns40;
	private String CAns41;
	private String CAns42;
	private String CAns43;
	private String CAns44;
	private String CAns45;
	private String CAns46;
	private String CAns47;
	private String CAns48;
	private String CAns49;
	private String CAns50;
	private String CAns51;
	private String CAns52;
	private String CAns53;
	private String CAns54;
	private String CAns55;
	private String CAns56;
	private String CAns57;
	private String CAns58;
	private String CAns59;
	private String CAns60;
	private String CAns61;
	private String CAns62;
	private String CAns63;
	private String CAns64;
	private String CAns65;
	private String CAns66;
	private String CAns67;
	private String CAns68;
	private String CAns69;
	private String CAns70;
	private String CAns71;
	private String CAns72;
	private String CAns73;
	private String CAns74;
	private String CAns75;
	private String CAns76;
	private String CAns77;
	private String CAns78;
	private String CAns79;
	private String CAns80;
	private String CAns81;
	private String CAns82;
	private String CAns83;
	private String CAns84;
	private String CAns85;
	private String CAns86;
	private String CAns87;
	private String CAns88;
	private String CAns89;
	private String CAns90;
	private String CAns91;
	private String CAns92;
	private String CAns93;
	private String CAns94;
	private String CAns95;
	private String CAns96;
	private String CAns97;
	private String CAns98;
	private String CAns99;
	private String CAns100;
	private String CExt1;
	private String CExt2;
	private String CExt3;
	private String CExt4;
	private String CExt5;
	private String CExt6;
	private String CExt7;
	private String CExt8;
	private String CExt9;
	private String CExt10;
	private String CExt11;
	private String CExt12;
	private String CExt13;
	private String CExt14;
	private String CExt15;
	private String CExt16;
	private String CExt17;
	private String CExt18;
	private String CExt19;
	private String CExt20;
	private String CExt21;
	private String CExt22;
	private String CExt23;
	private String CExt24;
	private String CExt25;
	private String CExt26;
	private String CExt27;
	private String CExt28;
	private String CExt29;
	private String CExt30;

	// Constructors

	/**
	 * default constructor
	 */
	public LbpcQuest() {
	}

	/**
	 * minimal constructor
	 */
	public LbpcQuest(String cOrgPkId, Integer nSeqNum, String cLbId, String cChildId, String cParentId,
                     String cChildNme, String cChildSex, Date tBirthday, String cPhoneNo, String cDptId, String cOfficeId,
                     String cUserId, String cState, String cEffMrk) {
		this.COrgPkId = cOrgPkId;
		this.NSeqNum = nSeqNum;
		this.CLbId = cLbId;
		this.CChildId = cChildId;
		this.CParentId = cParentId;
		this.CChildNme = cChildNme;
		this.CChildSex = cChildSex;
		this.TBirthday = tBirthday;
		this.CPhoneNo = cPhoneNo;
		this.CDptId = cDptId;
		this.COfficeId = cOfficeId;
		this.CUserId = cUserId;
		this.CState = cState;
		this.CEffMrk = cEffMrk;
	}

	/**
	 * full constructor
	 */
	public LbpcQuest(String cOrgPkId, Integer nSeqNum, String cLbId, String cChildId, String cParentId,
                     String cChildNme, String cChildSex, Date tBirthday, String cPhoneNo, Integer NYear, Integer NMonth,
                     Integer NDay, Integer NConceiveWeek, Integer NConceiveDay, Date TPreBirth, Integer NYear1, Integer NMonth1,
                     Integer NDay1, Float NBirthHeight, Float NBirthWeight, Float NBirthChest, Float NBirthSheight,
                     Float NBirthHead, String cDptId, String cOfficeId, String CDoctor, String CExecutor, String CInfoOffer,
                     String cUserId, Date TTestTm, String CMenzhenNo, String cChildRel, String CIllHistory, String cState,
                     String cEffMrk, String CFormId, String CSchool, String CClazz, String CGrade, Date tCrtTm, String cCrtId,
                     Date tUpdTm, String cUpdId, String CAns1, String CAns2, String CAns3, String CAns4, String CAns5,
                     String CAns6, String CAns7, String CAns8, String CAns9, String CAns10, String CAns11, String CAns12,
                     String CAns13, String CAns14, String CAns15, String CAns16, String CAns17, String CAns18, String CAns19,
                     String CAns20, String CAns21, String CAns22, String CAns23, String CAns24, String CAns25, String CAns26,
                     String CAns27, String CAns28, String CAns29, String CAns30, String CAns31, String CAns32, String CAns33,
                     String CAns34, String CAns35, String CAns36, String CAns37, String CAns38, String CAns39, String CAns40,
                     String CAns41, String CAns42, String CAns43, String CAns44, String CAns45, String CAns46, String CAns47,
                     String CAns48, String CAns49, String CAns50, String CAns51, String CAns52, String CAns53, String CAns54,
                     String CAns55, String CAns56, String CAns57, String CAns58, String CAns59, String CAns60, String CAns61,
                     String CAns62, String CAns63, String CAns64, String CAns65, String CAns66, String CAns67, String CAns68,
                     String CAns69, String CAns70, String CAns71, String CAns72, String CAns73, String CAns74, String CAns75,
                     String CAns76, String CAns77, String CAns78, String CAns79, String CAns80, String CAns81, String CAns82,
                     String CAns83, String CAns84, String CAns85, String CAns86, String CAns87, String CAns88, String CAns89,
                     String CAns90, String CAns91, String CAns92, String CAns93, String CAns94, String CAns95, String CAns96,
                     String CAns97, String CAns98, String CAns99, String CAns100, String CExt1, String CExt2, String CExt3,
                     String CExt4, String CExt5, String CExt6, String CExt7, String CExt8, String CExt9, String CExt10,
                     String CExt11, String CExt12, String CExt13, String CExt14, String CExt15, String CExt16, String CExt17,
                     String CExt18, String CExt19, String CExt20, String CExt21, String CExt22, String CExt23, String CExt24,
                     String CExt25, String CExt26, String CExt27, String CExt28, String CExt29, String CExt30) {
		this.COrgPkId = cOrgPkId;
		this.NSeqNum = nSeqNum;
		this.CLbId = cLbId;
		this.CChildId = cChildId;
		this.CParentId = cParentId;
		this.CChildNme = cChildNme;
		this.CChildSex = cChildSex;
		this.TBirthday = tBirthday;
		this.CPhoneNo = cPhoneNo;
		this.NYear = NYear;
		this.NMonth = NMonth;
		this.NDay = NDay;
		this.NConceiveWeek = NConceiveWeek;
		this.NConceiveDay = NConceiveDay;
		this.TPreBirth = TPreBirth;
		this.NYear1 = NYear1;
		this.NMonth1 = NMonth1;
		this.NDay1 = NDay1;
		this.NBirthHeight = NBirthHeight;
		this.NBirthWeight = NBirthWeight;
		this.NBirthChest = NBirthChest;
		this.NBirthSheight = NBirthSheight;
		this.NBirthHead = NBirthHead;
		this.CDptId = cDptId;
		this.COfficeId = cOfficeId;
		this.CDoctor = CDoctor;
		this.CExecutor = CExecutor;
		this.CInfoOffer = CInfoOffer;
		this.CUserId = cUserId;
		this.TTestTm = TTestTm;
		this.CMenzhenNo = CMenzhenNo;
		this.CChildRel = cChildRel;
		this.CIllHistory = CIllHistory;
		this.CState = cState;
		this.CEffMrk = cEffMrk;
		this.CFormId = CFormId;

		this.CSchool = CSchool;
		this.CClazz = CClazz;
		this.CGrade = CGrade;

		this.TCrtTm = tCrtTm;
		this.CCrtId = cCrtId;
		this.TUpdTm = tUpdTm;
		this.CUpdId = cUpdId;
		this.CAns1 = CAns1;
		this.CAns2 = CAns2;
		this.CAns3 = CAns3;
		this.CAns4 = CAns4;
		this.CAns5 = CAns5;
		this.CAns6 = CAns6;
		this.CAns7 = CAns7;
		this.CAns8 = CAns8;
		this.CAns9 = CAns9;
		this.CAns10 = CAns10;
		this.CAns11 = CAns11;
		this.CAns12 = CAns12;
		this.CAns13 = CAns13;
		this.CAns14 = CAns14;
		this.CAns15 = CAns15;
		this.CAns16 = CAns16;
		this.CAns17 = CAns17;
		this.CAns18 = CAns18;
		this.CAns19 = CAns19;
		this.CAns20 = CAns20;
		this.CAns21 = CAns21;
		this.CAns22 = CAns22;
		this.CAns23 = CAns23;
		this.CAns24 = CAns24;
		this.CAns25 = CAns25;
		this.CAns26 = CAns26;
		this.CAns27 = CAns27;
		this.CAns28 = CAns28;
		this.CAns29 = CAns29;
		this.CAns30 = CAns30;
		this.CAns31 = CAns31;
		this.CAns32 = CAns32;
		this.CAns33 = CAns33;
		this.CAns34 = CAns34;
		this.CAns35 = CAns35;
		this.CAns36 = CAns36;
		this.CAns37 = CAns37;
		this.CAns38 = CAns38;
		this.CAns39 = CAns39;
		this.CAns40 = CAns40;
		this.CAns41 = CAns41;
		this.CAns42 = CAns42;
		this.CAns43 = CAns43;
		this.CAns44 = CAns44;
		this.CAns45 = CAns45;
		this.CAns46 = CAns46;
		this.CAns47 = CAns47;
		this.CAns48 = CAns48;
		this.CAns49 = CAns49;
		this.CAns50 = CAns50;
		this.CAns51 = CAns51;
		this.CAns52 = CAns52;
		this.CAns53 = CAns53;
		this.CAns54 = CAns54;
		this.CAns55 = CAns55;
		this.CAns56 = CAns56;
		this.CAns57 = CAns57;
		this.CAns58 = CAns58;
		this.CAns59 = CAns59;
		this.CAns60 = CAns60;
		this.CAns61 = CAns61;
		this.CAns62 = CAns62;
		this.CAns63 = CAns63;
		this.CAns64 = CAns64;
		this.CAns65 = CAns65;
		this.CAns66 = CAns66;
		this.CAns67 = CAns67;
		this.CAns68 = CAns68;
		this.CAns69 = CAns69;
		this.CAns70 = CAns70;
		this.CAns71 = CAns71;
		this.CAns72 = CAns72;
		this.CAns73 = CAns73;
		this.CAns74 = CAns74;
		this.CAns75 = CAns75;
		this.CAns76 = CAns76;
		this.CAns77 = CAns77;
		this.CAns78 = CAns78;
		this.CAns79 = CAns79;
		this.CAns80 = CAns80;
		this.CAns81 = CAns81;
		this.CAns82 = CAns82;
		this.CAns83 = CAns83;
		this.CAns84 = CAns84;
		this.CAns85 = CAns85;
		this.CAns86 = CAns86;
		this.CAns87 = CAns87;
		this.CAns88 = CAns88;
		this.CAns89 = CAns89;
		this.CAns90 = CAns90;
		this.CAns91 = CAns91;
		this.CAns92 = CAns92;
		this.CAns93 = CAns93;
		this.CAns94 = CAns94;
		this.CAns95 = CAns95;
		this.CAns96 = CAns96;
		this.CAns97 = CAns97;
		this.CAns98 = CAns98;
		this.CAns99 = CAns99;
		this.CAns100 = CAns100;
		this.CExt1 = CExt1;
		this.CExt2 = CExt2;
		this.CExt3 = CExt3;
		this.CExt4 = CExt4;
		this.CExt5 = CExt5;
		this.CExt6 = CExt6;
		this.CExt7 = CExt7;
		this.CExt8 = CExt8;
		this.CExt9 = CExt9;
		this.CExt10 = CExt10;
		this.CExt11 = CExt11;
		this.CExt12 = CExt12;
		this.CExt13 = CExt13;
		this.CExt14 = CExt14;
		this.CExt15 = CExt15;
		this.CExt16 = CExt16;
		this.CExt17 = CExt17;
		this.CExt18 = CExt18;
		this.CExt19 = CExt19;
		this.CExt20 = CExt20;
		this.CExt21 = CExt21;
		this.CExt22 = CExt22;
		this.CExt23 = CExt23;
		this.CExt24 = CExt24;
		this.CExt25 = CExt25;
		this.CExt26 = CExt26;
		this.CExt27 = CExt27;
		this.CExt28 = CExt28;
		this.CExt29 = CExt29;
		this.CExt30 = CExt30;
	}

}