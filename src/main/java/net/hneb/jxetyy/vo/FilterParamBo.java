package net.hneb.jxetyy.vo;

import java.util.Map;

/**
 * 
 * @author ebadmin
 *
 */
public class FilterParamBo {

	@SuppressWarnings("rawtypes")
	private Map param;

	private int firstRow;
	private int maxRow;

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public int getMaxRow() {
		return maxRow;
	}

	public void setMaxRow(int maxRow) {
		this.maxRow = maxRow;
	}

	@SuppressWarnings("rawtypes")
	public Map getParam() {
		return param;
	}

	@SuppressWarnings("rawtypes")
	public void setParam(Map param) {
		this.param = param;
	}

}