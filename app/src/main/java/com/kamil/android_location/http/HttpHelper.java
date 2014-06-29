package com.kamil.android_location.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpHelper {

	private final int TIMEOUT_MILLISEC = 10000; // 10s
	
	public boolean postJsonData(String url, String jsonBody) {
	    HttpClient httpclient = new DefaultHttpClient(buildPostParams());
	    HttpPost httpPost = new HttpPost(url);

	    try {
	        httpPost.setEntity(new ByteArrayEntity(jsonBody.getBytes("UTF-8")));
	        httpPost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	        
	        HttpResponse response = httpclient.execute(httpPost);
	        
	        Log.d("HTTP Client", EntityUtils.toString(response.getEntity()));
	        
	        return true;
	    } catch (ClientProtocolException e) {
	        Log.e("HTTP Client", "Error sending request to " + url + ". Client Protocol Error = " + e.getMessage());
	    } catch (IOException e) {
	    	Log.e("HTTP Client", "Error sending request to " + url + ". IOError = " + e.getMessage());
	    } catch (Exception e) {
	    	Log.e("HTTP Client", "Error sending request to " + url + ". Error = " + e.getMessage() + ", Class=" + e.getClass().getName());
	    }
	    
	    return false;
	}
	
	private HttpParams buildPostParams() {
		HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
	    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
	    
	    return httpParams;
	}
}
