package net.hneb.jxetyy.vo;

import java.util.List;

/**
 * 
 * @author ebadmin
 *
 */
public class DataResultBo {

	// 查询结果集
	@SuppressWarnings("rawtypes")
	private List data;

	// 记录总数
	private int total = 0;

	@SuppressWarnings("rawtypes")
	public List getData() {
		return data;
	}

	@SuppressWarnings("rawtypes")
	public void setData(List data) {
		this.data = data;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
