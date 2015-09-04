package com.jamtu.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 公用列表实体类
 * 
 * @created 2014-05-13
 * @author lijq
 */
@SuppressWarnings("serial")
public class CommonList<T> extends Entity implements PageList<T> {

	private int pageSize;
	private int count;

	private List<T> list = new ArrayList<T>();

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int getCount() {
		return count;
	}

	public int getPageSize() {
		return pageSize;
	}

	@Override
	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
}
