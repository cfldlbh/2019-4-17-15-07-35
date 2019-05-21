package com.lbh.cfld.springbootdemo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/** 
 * 统一请求返回结果model
 * @author tobber
 * @version 2017年11月21日
 */

@ApiModel(value="ResponseResult",description="统一请求返回结果")
public class ResponseResult<T> {
	
	@ApiModelProperty(value = "true:成功、false：失败", required = true)
	private boolean success;
	
	@ApiModelProperty(value = "提示信息", required = true)
    private String message;
	
	@ApiModelProperty(value = "返回对象", required = true)
    private T data;

    /* 不提供直接设置errorCode的接口，只能通过setErrorInfo方法设置错误信息 */
	@ApiModelProperty(value = "错误码", required = false)
    private String errorCode;

    public static <T> ResponseResult<T> newInstance() {
        return new ResponseResult<T>();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // 设置错误信息
    public void setErrorInfo(ResponseErrorEnum responseErrorEnum) {
        this.errorCode = responseErrorEnum.getCode();
        this.message = responseErrorEnum.getMessage();
    }

	public ResponseResult(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}

	public ResponseResult() {
		super();
	}

	/**
	 * @return the data
	 */
	public T getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(T data) {
		this.data = data;
	}
	
}
