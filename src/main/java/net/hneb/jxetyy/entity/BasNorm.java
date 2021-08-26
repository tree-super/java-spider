package net.hneb.jxetyy.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BasNorm
 */
@Data
@Table(name = "t_bas_norm")
public class BasNorm implements java.io.Serializable {

	private static final long serialVersionUID = 8311942326845060111L;

	// Fields

	//C_PK_ID
	//C_STR1
	@Id
	@Column(name="C_PK_ID")
	private String cPKID;
	private String cStr1;
	//C_STR2
	private String cStr2;
	//C_STR3
	private Double cStr3;
	//C_STR4
	private Double cStr4;
	//C_STR5
	private String cStr5;
	//C_LB_ID
	private String cLbId;

}