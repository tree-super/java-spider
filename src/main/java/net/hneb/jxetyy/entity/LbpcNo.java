package net.hneb.jxetyy.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * LbpcNoVO 实体类。<br>
 * @author ebadmin
 */
@Data
@Table(name = "t_lbpc_no")
public class LbpcNo implements java.io.Serializable {

	private static final long serialVersionUID = 8311942326845060299L;

	// Fields
	@Id
	private String CLbId;
	private String CKindNo;
	private String CLbNme;
	private String CAge;
	private String CAgeJson;
	private String CIntroduce;
	private String CGuide;
	private Integer NShowNum;
	private String CVipMrk;
	private String CShowMrk;
	private String CCrtId;
	private Date TCrtTm;
	private String CUpdId;
	private Date TUpdTm;

	// Constructors

	/** default constructor */
	public LbpcNo() {
	}

	/** minimal constructor */
	public LbpcNo(String cLbId, String CKindNo, String CLbNme, String CAge, String CShowMrk) {
		this.CLbId = cLbId;
		this.CKindNo = CKindNo;
		this.CLbNme = CLbNme;
		this.CAge = CAge;
		this.CShowMrk = CShowMrk;
	}

	/** full constructor */
	public LbpcNo(String cLbId, String CKindNo, String CLbNme, String CAge, String CAgeJson, String CIntroduce,
				  String CGuide, Integer NShowNum, String CVipMrk, String CShowMrk, String cCrtId, Date tCrtTm, String cUpdId,
				  Date tUpdTm) {
		this.CLbId = cLbId;
		this.CKindNo = CKindNo;
		this.CLbNme = CLbNme;
		this.CAge = CAge;
		this.CAgeJson = CAgeJson;
		this.CIntroduce = CIntroduce;
		this.CGuide = CGuide;
		this.NShowNum = NShowNum;
		this.CVipMrk = CVipMrk;
		this.CShowMrk = CShowMrk;
		this.CCrtId = cCrtId;
		this.TCrtTm = tCrtTm;
		this.CUpdId = cUpdId;
		this.TUpdTm = tUpdTm;
	}
}