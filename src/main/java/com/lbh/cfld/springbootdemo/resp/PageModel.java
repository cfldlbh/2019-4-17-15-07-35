package com.lbh.cfld.springbootdemo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/** 
 * @author tobber
 * @version 2017年11月22日
 */
@ApiModel(value="PageModel",description="分页参数实体bean")
public class PageModel {
	
	@ApiModelProperty(value="当前页",required=true)
	private int page;
	
	@ApiModelProperty(value="每页显示条数",required=true)
	private int pageSize;
	
	@ApiModelProperty(value="总页数",required=true)
	private int pageTotal;
	
	@ApiModelProperty(value="总条数",required=true)
	private int rowCount;

	/**
	 * 获取分页参数
	 * @param page 当前页
	 * @param pageSize 每页显示条数
	 * @param rowCount 总条数
	 * @return
	 */
	public static PageModel getPageModel(int page, int pageSize, int rowCount){
		PageModel pageModel = new PageModel(page,pageSize,rowCount);
		if(rowCount%pageSize==0){
			pageModel.setPageTotal(rowCount/pageSize);
		}else{
			pageModel.setPageTotal((rowCount/pageSize)+1);
		}
		return pageModel;
	}
	
	public PageModel(int page, int pageSize, int rowCount) {
		super();
		this.page = page;
		this.pageSize = pageSize;
		this.rowCount = rowCount;
	}
	
	public PageModel() {
		super();
	}

	/**
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * @param page the page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the pageTotal
	 */
	public int getPageTotal() {
		return pageTotal;
	}

	/**
	 * @param pageTotal the pageTotal to set
	 */
	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}

	/**
	 * @return the rowCount
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * @param rowCount the rowCount to set
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	
}
