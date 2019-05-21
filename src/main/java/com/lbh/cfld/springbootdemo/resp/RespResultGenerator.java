package com.lbh.cfld.springbootdemo.resp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/** 
 * 统一的RestController结果生成器
 * @author tobber
 * @version 2017年11月21日
 */
public class RespResultGenerator {

	 private static final Logger LOGGER = LoggerFactory.getLogger(RespResultGenerator.class);

	   
	   /**
	     * 生成响应成功(带正文)的结果
	     *
	     * @param data    结果正文
	     * @param message 成功提示信息
	     * @return ResponseEntity<ResponseResult<T>>
	     */
	   public static <T> ResponseEntity<ResponseResult<T>> genOK(T data,String message){
				   
		   ResponseResult<T> result = ResponseResult.newInstance();
	       result.setSuccess(true);
	       result.setData(data);
	       result.setMessage(message);
	       if (LOGGER.isDebugEnabled()) {
	           LOGGER.debug("--------> result:{}", result.toString());
	       }
	       return new ResponseEntity<ResponseResult<T>>(result, HttpStatus.OK);
	   }
	   
	   /**
	     * 生成分页数据
	     * @param dataList 结果列表
	     * @param pageModel 分页信息
	     * @param message 成功提示信息
	     * @return ResponseEntity<ResponseResult<T>>
	     */
	   public static <T> ResponseEntity<ResponsePageResult<T>> genOK(List<T> dataList,PageModel pageModel,String message){
				   
		   ResponsePageResult<T> result = ResponsePageResult.newInstance();
	       result.setSuccess(true);
	       result.setListData(dataList);
	       result.setMessage(message);
	       result.setPageModel(pageModel);
	       if (LOGGER.isDebugEnabled()) {
	           LOGGER.debug("--------> result:{}", result.toString());
	       }
	       return new ResponseEntity<ResponsePageResult<T>>(result, HttpStatus.OK);
	   }
	   
	   /**
	     * 生成响应成功(不带正文)的结果
	     * @param data    结果正文
	     * @param message 成功提示信息
	     * @return ResponseEntity<ResponseResult<T>>
	     */
	   @SuppressWarnings("rawtypes")
	   public static ResponseEntity<ResponseResult> genOK(String message){
		   
		   ResponseResult result = ResponseResult.newInstance();
	       result.setSuccess(true);
	       result.setMessage(message);
	       if (LOGGER.isDebugEnabled()) {
	           LOGGER.debug("--------> result:{}", result.toString());
	       }
	       return new ResponseEntity<ResponseResult>(result, HttpStatus.OK);
	   }
	   
	   /**
	     * 生成响应失败(带正文)的结果
	     * @param data    结果正文
	     * @param message 失败提示信息
	     * @return ResponseEntity<ResponseResult<T>>
	     */
	   public static <T> ResponseEntity<ResponseResult<T>> genError(T data,String erroMessage){
				   
		   ResponseResult<T> result = ResponseResult.newInstance();
	       result.setSuccess(false);
	       result.setData(data);
	       result.setMessage(erroMessage);
	       if (LOGGER.isDebugEnabled()) {
	           LOGGER.debug("--------> result:{}", result.toString());
	       }
	       return new ResponseEntity<ResponseResult<T>>(result, HttpStatus.OK);
	   }
	   
	   /**
	     * 生成响应失败(不带正文)的结果
	     * @param data    结果正文
	     * @param message 成功提示信息
	     * @return ResponseEntity<ResponseResult<T>>
	     */
	   @SuppressWarnings("rawtypes")
	   public static ResponseEntity<ResponseResult> genError(String erroMessage){
		   
		   ResponseResult result = ResponseResult.newInstance();
	       result.setSuccess(false);
	       result.setMessage(erroMessage);
	       if (LOGGER.isDebugEnabled()) {
	           LOGGER.debug("--------> result:{}", result.toString());
	       }
	       return new ResponseEntity<ResponseResult>(result, HttpStatus.OK);
	   }
	   
	   /**
	     * 生成响应失败(带errorCode)的结果
	     * @param data    结果正文
	     * @param message 成功提示信息
	     * @return ResponseEntity<ResponseResult<T>>
	     */
	   @SuppressWarnings("rawtypes")
	   public static <T> ResponseEntity<ResponseResult> genError(ResponseErrorEnum responseErrorEnum){
		   
		   ResponseResult result = ResponseResult.newInstance();
	       result.setSuccess(false);
	       result.setErrorInfo(responseErrorEnum);
	       if (LOGGER.isDebugEnabled()) {
	           LOGGER.debug("--------> result:{}", result.toString());
	       }
	       return new ResponseEntity<ResponseResult>(result, HttpStatus.OK);
	   }

}
