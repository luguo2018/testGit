package com.jmhy.sdk.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.jmhy.sdk.utils.AESCrypt;
import com.jmhy.sdk.utils.SecurityUtils;

import android.util.Log;

/**
 * 获取API请求内容  的工厂方法
 */
public class ApiRequestFactory {

	public static final boolean DEBUG = true;
	public static final String LOGTAG = "ApiRequestFactory";


	/**
	 * 
	 * 获取Web Api HttpRequest
	 * @param url
	 * @param httpType  
	 * @return
	 */
	public static HttpUriRequest getRequest(String url, String httpType,
			HashMap<String, Object> param, String appKey) {
		// 得到hashmap的key集合，进行排序
		Object[] keyArr = param.keySet().toArray();
		Arrays.sort(keyArr);
		// http get
		if (httpType.equals("get")){
			StringBuilder md5Str = new StringBuilder();
			StringBuilder urlBuilderData = new StringBuilder(url);
		    StringBuilder urlBuilder=new StringBuilder(url);
		    urlBuilderData.append("?");
		    urlBuilder.append("?data=");
			for (Object key : keyArr){
				String value = (String) param.get(key)==null?"":(String) param.get(key);
				// 对特殊字符进行encode
				try {
					urlBuilderData.append(key).append("=")
							.append(URLEncoder.encode(value, "UTF-8"))
							.append("&");
				
						
					
					// md5Str.append(URLEncoder.encode(value, "UTF-8"));
					
				} catch (UnsupportedEncodingException e) {

					e.printStackTrace();
				}
			}

			md5Str.append(appKey);
//			try {
//				urlBuilderData.append("appKey=").append(URLEncoder.encode(appKey, "UTF-8")).append("&");
//			} catch (UnsupportedEncodingException e1) {
//				e1.printStackTrace();
//			}

			String md5ResultString = SecurityUtils.getMD5Str(md5Str.toString());

			urlBuilderData.append("sign=").append(md5ResultString);

			HttpGet httpGet = null;
//            try {
//            	byte[] encryptData=RSAUtils.encryptByPublicKey(urlBuilderData.toString().getBytes("utf-8"), RSAUtils.getPublicKey())  ;
//            	urlBuilder.append(Base64Utils.encode(encryptData));
//            } catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			httpGet = new HttpGet(urlBuilderData.toString());
  //        Log.i("kk", "getqing"+urlBuilderData.toString());
			return httpGet;
		}

		// http post
		if (httpType.equals("post")) {
			Log.d("JiMiSDK", "url = " + url);
			Log.d("JiMiSDK", "method = POST");

			final ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
			StringBuilder md5Str = new StringBuilder();
			StringBuilder urlBuilderData = new StringBuilder();
			for (Object key : keyArr) {
				String value = (String) param.get(key);
					postParams.add(new BasicNameValuePair((String) key,
							value));
				Log.d("JiMiSDK", key + " = " + value);
					//md5Str.append(value);
				//	try {
					/*	if(key.equals("context")){
							md5Str.append(key).append("=")
							.append(value)
							.append("&");
							//Log.i("kk","没encode"+value);
						}else{
							md5Str.append(key).append("=")
							.append(URLEncoder.encode(value, "UTF-8"))
							.append("&");
							//Log.i("kk","有encode"+value);
						}
						
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
			} 
		    md5Str.append("access_token=").append(URLEncoder.encode(param.get("access_token").toString())).
		    append("&").append("time=").append(URLEncoder.encode(param.get("time").toString())).append("&").
		    append("context=").append(param.get("context").toString()).append("&");

			md5Str.append("appkey=").append(appKey);
			Log.d("JiMiSDK", "md5Str.toString().toLowerCase() = " + md5Str.toString().toLowerCase());

			String md5ResultString = SecurityUtils.getMD5Str(md5Str.toString().toLowerCase());
			postParams.add(new BasicNameValuePair("sign", md5ResultString));
	
		    urlBuilderData.append("sign=").append(md5ResultString);
			Log.e("TAG", "md5Str: "+md5Str.toString());
			Log.e("TAG", "md5ResultString: "+md5ResultString);
			Log.d("JiMiSDK", "params: " + urlBuilderData.toString());
		
		    String data = null;
		   
			HttpPost httpPost = new HttpPost(url);

			try {
				httpPost.setEntity( new UrlEncodedFormEntity(postParams,
						HTTP.UTF_8));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			//Log.i("kk", "post params:" + postParams.toString());
			return httpPost;
		}
	
		return null;

	}
}
