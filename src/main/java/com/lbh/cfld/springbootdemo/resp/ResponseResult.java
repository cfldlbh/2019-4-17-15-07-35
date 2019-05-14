package com.lbh.cfld.springbootdemo.resp;


/** 
 * 统一请求返回结果model
 * @author tobber
 * @version 2017年11月21日
 */


public class ResponseResult<T> {

	private boolean success;

    private String message;

    private T data;

    /* 不提供直接设置errorCode的接口，只能通过setErrorInfo方法设置错误信息 */
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
