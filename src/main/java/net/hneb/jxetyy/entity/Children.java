package net.hneb.jxetyy.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Children
 */
@Data
@Table(name = "t_children")
public class Children implements java.io.Serializable {

	private static final long serialVersionUID = 8045769393234674108L;

	// Fields

	@Id
	private String CChildId;
	private String CUserId;
	private String CChildNme;
	private String CChildSex;
	private String TBirthday;
	private String CPrebirthMrk;
	private String TPreBirth;
	private Integer NConceiveWeek;
	private Integer NConceiveDay;
	private Float NBirthHeight;
	private Float NBirthWeight;
	private Float NBirthHead;
	private Float NBirthChest;
	private Float NBirthSheight;
	private String CBirthStsMrk;
	private String CIdNo;
	private String CPhone;
	private String CProvince;
	private String CCity;
	private String CCrtId;
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date TCrtTm;
	private String CUpdId;
	private Date TUpdTm;
	private String CHeadImg;
	private String CIll;
	private String CLishou;

	private String CEducation;

	/** default constructor */
	public Children() {
	}

	/** minimal constructor */
	public Children(String cUserId, String cChildNme, String cChildSex, String tBirthday) {
		this.CUserId = cUserId;
		this.CChildNme = cChildNme;
		this.CChildSex = cChildSex;
		this.TBirthday = tBirthday;
	}

	/** full constructor */
	public Children(String cUserId, String cChildNme, String cChildSex, String tBirthday, String CPrebirthMrk,
                    String TPreBirth, Integer NConceiveWeek, Integer NConceiveDay, Float NBirthHeight, Float NBirthWeight,
                    Float NBirthHead, Float NBirthChest, Float NBirthSheight, String CBirthStsMrk, String CIdNo, String CPhone,
                    String CProvince, String CCity, String cCrtId, Date tCrtTm, String cUpdId, Date tUpdTm, String CHeadImg,
                    String CIll) {
		this.CUserId = cUserId;
		this.CChildNme = cChildNme;
		this.CChildSex = cChildSex;
		this.TBirthday = tBirthday;
		this.CPrebirthMrk = CPrebirthMrk;
		this.TPreBirth = TPreBirth;
		this.NConceiveWeek = NConceiveWeek;
		this.NConceiveDay = NConceiveDay;
		this.NBirthHeight = NBirthHeight;
		this.NBirthWeight = NBirthWeight;
		this.NBirthHead = NBirthHead;
		this.NBirthChest = NBirthChest;
		this.NBirthSheight = NBirthSheight;
		this.CBirthStsMrk = CBirthStsMrk;
		this.CIdNo = CIdNo;
		this.CPhone = CPhone;
		this.CProvince = CProvince;
		this.CCity = CCity;
		this.CCrtId = cCrtId;
		this.TCrtTm = tCrtTm;
		this.CUpdId = cUpdId;
		this.TUpdTm = tUpdTm;
		this.CHeadImg = CHeadImg;
		this.CIll = CIll;
	}

}