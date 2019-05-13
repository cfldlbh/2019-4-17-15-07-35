package com.lbh.cfld.springbootdemo;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;



public class ZlDama {

	/**
	 * 图片打码
	 * @param type 1 打码、2 报错
	 * @param imgPath 图片路径
	 * @param yzm_id 打码返回的ID
	 * @return
	 */
	public static String imgDama (int type, String imgPath, String yzm_id) {
    	String BOUNDARY = "---------------------------68163001211748"; //boundary就是request头和上传文件内容的分隔符
    	String str="http://v1-http-api.jsdama.com/api.php?mod=php&act=upload";
    	if (type==2) {
    		str="http://v1-http-api.jsdama.com/api.php?mod=php&act=error";
    	}
        Map<String, String> paramMap = getParamMap(yzm_id);
        try {
            URL url=new URL(str);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "multipart/form-data; boundary="+BOUNDARY);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            
            OutputStream out = new DataOutputStream(connection.getOutputStream());
			// 普通参数
			if (paramMap != null) {
				StringBuffer strBuf = new StringBuffer();
				Iterator<Entry<String, String>> iter = paramMap.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String,String> entry = iter.next();
					String inputName = entry.getKey();
					String inputValue = entry.getValue();
					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\""
							+ inputName + "\"\r\n\r\n");
					strBuf.append(inputValue);
				}
				out.write(strBuf.toString().getBytes());
			}
			
			// 图片文件
			if (imgPath != null) {
				File file = new File(imgPath);
				String filename = file.getName();
				String contentType = "image/jpeg";//这里看情况设置
				StringBuffer strBuf = new StringBuffer();
				strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
				strBuf.append("Content-Disposition: form-data; name=\""
						+ "upload" + "\"; filename=\"" + filename+ "\"\r\n");
				strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
				out.write(strBuf.toString().getBytes());
				DataInputStream in = new DataInputStream(
						new FileInputStream(file));
				int bytes = 0;
				byte[] bufferOut = new byte[1024];
				while ((bytes = in.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				in.close();
			}
			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();
            
            //读取URLConnection的响应
            InputStream in = connection.getInputStream();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			while (true) {
				int rc = in.read(buf);
				if (rc <= 0) {
					break;
				} else {
					bout.write(buf, 0, rc);
				}
			}
			in.close();
			//结果输出
			System.out.println(new String(bout.toByteArray()));
			return new String(bout.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	
	/**
	 * 参数信息
	 * @return
	 */
	private static Map<String, String> getParamMap(String yzm_id) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("user_name", "hljslp");
		paramMap.put("user_pw", "HLJslp123");
		paramMap.put("yzm_minlen", "4");
		paramMap.put("yzm_maxlen", "4");
		paramMap.put("yzmtype_mark", "1038");
		paramMap.put("zztool_token", "hljslp");
		if (yzm_id !=null) {
			paramMap.put("yzm_id", yzm_id);
		}
		return paramMap;
	}
}
