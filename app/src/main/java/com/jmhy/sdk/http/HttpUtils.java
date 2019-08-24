package com.jmhy.sdk.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;


import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;

import org.apache.http.util.EntityUtils;

/**
 * Http公用方法
 * 
 */
public class HttpUtils {
	public void htt(){
	//	HttpClient.this.
	}

	// 压缩的最小数据
	public static long DEFAULT_SYNC_MIN_GZIP_BYTES = 256;

	/**
	 * Retrieves the minimum size for compressing data. Shorter data will not be
	 * compressed.
	 * 
	 * @return
	 */
	public static long getMinGzipSize() {
		return DEFAULT_SYNC_MIN_GZIP_BYTES;
	}

	/**
	 * Gets the input stream from a response entity. If the entity is gzipped
	 * then this will get a stream over the uncompressed data.
	 * 
	 * @param entity
	 *            the entity whose content should be read
	 * @return the input stream to read from
	 * @throws IOException
	 */
	public static InputStream getUnzippedContent(HttpEntity entity)
			throws IOException {

		InputStream responseStream = entity.getContent();

		if (responseStream == null) {
			return responseStream;
		}
		Header header = entity.getContentEncoding();
		if (header == null) {
			return responseStream;
		}
		String contentEncoding = header.getValue();
		if (contentEncoding == null) {
			return responseStream;
		}
		if (contentEncoding.contains("gzip")) {
			responseStream = new GZIPInputStream(responseStream);
		}
		return responseStream;
	}

	/**
	 * 解析Http String Entity
	 * 
	 * @param response
	 * @return 返回的消息string
	 */
	public static String getStringResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		try {
			if (entity == null) {
				return null;
			}

			return EntityUtils.toString(entity);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 解析HTTP InputStream Entity
	 * 
	 * @param response
	 * @return 返回的消息InputStream
	 */
	public static InputStream getInputStreamResponse(HttpResponse response) {

		HttpEntity entity = response.getEntity();
		try {
			if (entity == null) {
				return null;
			}
			
			return getUnzippedContent(entity);
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;

	}
}
