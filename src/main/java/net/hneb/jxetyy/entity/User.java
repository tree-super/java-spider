package net.hneb.jxetyy.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * User 实体
 * 
 * @author ebadmin
 */
@Data
@Table(name = "t_user")
public class User implements java.io.Serializable {

	private static final long serialVersionUID = 88891180822340999L;

	// Fields
	@Id
	private String CUserId;
	private String tenantId; // 租户标识
	private String loginCode; // 登录编码
	private String loginAlias; // 登录别名
	private String email; // 电子邮箱
	private String CPhoneNo;
	private String CUserNme;
	private String CPassword;
	private String CRole;
	private Integer userStatus; // 用户状态（1-未激活，2-已激活，3-已锁定，4-已禁用，5-已注销）
	private String CDataSrc;
	private String CWxId;
	private String CUnionId;
	private String CId2;
	private String CUserIconUrl;
	private String CSign;
	private String CDptId;
	private String CCrtId;
	private Date TCrtTm;
	private String CUpdId;
	private Date TUpdTm;
	private Integer NFansNum;
	private Integer NZanNum;
	private String CWxQrcodeUrl;
	private String CWxQrcodeUrl2;
	private String CSubscribeMrk;
	private String CPro;
	private String CCity;
	private String CZone;
	private Boolean deleted; // 逻辑删除标识（0-未删除，1-已删除）

	// Constructors

	/** default constructor */
	public User() {
	}

	/** minimal constructor */
	public User(String cUserNme, String cPhoneNo, String CPassword, String CRole, String CDataSrc,
                String CSubscribeMrk) {
		this.CUserNme = cUserNme;
		this.CPhoneNo = cPhoneNo;
		this.CPassword = CPassword;
		this.CRole = CRole;
		this.CDataSrc = CDataSrc;
		this.CSubscribeMrk = CSubscribeMrk;
		this.deleted = false;
	}

	/** full constructor */
	public User(String cUserId, String tenantId, String loginCode, String loginAlias, String email, String cPhoneNo,
                String cUserNme, String cPassword, String cRole, Integer userStatus, String cDataSrc, String cWxId,
                String cUnionId, String cId2, String cUserIconUrl, String cSign, String cDptId, String cCrtId, Date tCrtTm,
                String cUpdId, Date tUpdTm, Integer nFansNum, Integer nZanNum, String cWxQrcodeUrl, String cWxQrcodeUrl2,
                String cSubscribeMrk, String cPro, String cCity, String cZone, Boolean deleted) {
		super();
		this.CUserId = cUserId;
		this.setTenantId(tenantId);
		this.loginCode = loginCode;
		this.loginAlias = loginAlias;
		this.email = email;
		this.CPhoneNo = cPhoneNo;
		this.CUserNme = cUserNme;
		this.CPassword = cPassword;
		this.CRole = cRole;
		this.userStatus = userStatus;
		this.CDataSrc = cDataSrc;
		this.CWxId = cWxId;
		this.CUnionId = cUnionId;
		this.CId2 = cId2;
		this.CUserIconUrl = cUserIconUrl;
		this.CSign = cSign;
		this.CDptId = cDptId;
		this.CCrtId = cCrtId;
		this.TCrtTm = tCrtTm;
		this.CUpdId = cUpdId;
		this.TUpdTm = tUpdTm;
		this.NFansNum = nFansNum;
		this.NZanNum = nZanNum;
		this.CWxQrcodeUrl = cWxQrcodeUrl;
		this.CWxQrcodeUrl2 = cWxQrcodeUrl2;
		this.CSubscribeMrk = cSubscribeMrk;
		this.CPro = cPro;
		this.CCity = cCity;
		this.CZone = cZone;
		this.deleted = deleted;
	}
}