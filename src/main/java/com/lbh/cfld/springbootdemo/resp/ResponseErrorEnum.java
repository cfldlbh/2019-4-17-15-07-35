package com.lbh.cfld.springbootdemo.resp;



import java.util.HashMap;
import java.util.Map;

/** 
 * @author tobber
 * @version 2017年11月21日
 */


public class ResponseErrorEnum {


	private String code;
	

    private String message;
    
    public ResponseErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // 序列化enum
    private Map<String, Object> serialize() {
        Map<String, Object> valueMap = new HashMap<>(2);
        valueMap.put("code", this.code);
        valueMap.put("message", this.message);
        return valueMap;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
