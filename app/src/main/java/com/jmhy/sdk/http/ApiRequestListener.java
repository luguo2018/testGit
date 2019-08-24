package com.jmhy.sdk.http;

import android.os.Parcelable;

/**
 * http request callback
 * 
 * 
 * 
 */
public interface ApiRequestListener  {

	/**
	 * The callback when request success
	 * 
	 * 
	 * @param obj
	 *            The http response
	 */
	void onSuccess(Object obj);

	/**
	 * The callback when request error
	 * 
	 * @param statusCode
	 *            error code
	 */
	void onError(int statusCode);
}
