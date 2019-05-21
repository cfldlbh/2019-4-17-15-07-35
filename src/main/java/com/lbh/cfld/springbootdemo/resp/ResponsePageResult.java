package com.lbh.cfld.springbootdemo.resp;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/** 
 * 统一请求返回结果model
 * @author tobber
 * @version 2017年11月21日
 */

@ApiModel(value="ResponsePageResult",description="统一请求返回结果")
public class ResponsePageResult<T> {
	
	@ApiModelProperty(value = "true:成功、false：失败", required = true)
	private boolean success;
	
	@ApiModelProperty(value = "提示信息", required = true)
    private String message;
	
	@ApiModelProperty(value = "数据列表", required = true)
    private List<T> listData;
	
	@ApiModelProperty(value = "分页数据", required = true)
    private PageModel pageModel;

    public static <T> ResponsePageResult<T> newInstance() {
        return new ResponsePageResult<T>();
    }

	public ResponsePageResult(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	
	public ResponsePageResult() {
		super();
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the listData
	 */
	public List<T> getListData() {
		return listData;
	}

	/**
	 * @param listData the listData to set
	 */
	public void setListData(List<T> listData) {
		this.listData = listData;
	}

	/**
	 * @return the pageModel
	 */
	public PageModel getPageModel() {
		return pageModel;
	}

	/**
	 * @param pageModel the pageModel to set
	 */
	public void setPageModel(PageModel pageModel) {
		this.pageModel = pageModel;
	}

}
