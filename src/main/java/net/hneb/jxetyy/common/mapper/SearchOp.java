package net.hneb.jxetyy.common.mapper;

public enum SearchOp  {
	EQ("="),
	GT(">"),
	GE(">="),
	LT("<"),
	LE("<="),
	NEQ("!="),
	IN("in"),
	NIN("notin"),
	NL("isnull"),
	NNL("!isnull"),
	LKAFTER("like"),
	LK("like");
	
	private String value;
	SearchOp(String i)
	{
		this.value=i;
	}
	
	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
