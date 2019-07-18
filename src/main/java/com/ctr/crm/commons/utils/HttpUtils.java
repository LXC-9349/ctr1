package com.ctr.crm.commons.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.Map.Entry;

public class HttpUtils {

	private static final Log logger = LogFactory.getLog("exception");
	private CloseableHttpClient client = null;
	private static final int SOCKET_TIMEOUT = 3000;
	private static final int CONNECT_TIMEOUT = 3000;
	
	private HttpUtils() {
		try {
			client = HttpClients.createDefault();
		} catch (Exception e) {
			logger.error("init SSLClient failed", e);
		}
	}
	
	public static HttpUtils getInstance(){
		return new HttpUtils();
	}
	
	public String doPost(String url, String params){
		return doPost(url, params, CONNECT_TIMEOUT, SOCKET_TIMEOUT);
	}
	
	public String doPost(String url, Map<String, String> params){
		return doPost(url, params, CONNECT_TIMEOUT, SOCKET_TIMEOUT);
	}

	/**
	 * post请求
	 *
	 * @param url
	 *            功能和操作
	 * @param body
	 *            要post的数据
	 * @return
	 * @throws IOException
	 */
	public String post(String url, String body) {
		System.out.println("body:" + System.lineSeparator() + body);

		String result = "";
		try {
			OutputStreamWriter out = null;
			BufferedReader in = null;
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();

			// 设置连接参数
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(20000);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// 提交数据
			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(body);
			out.flush();

			// 读取返回数据
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = "";
			boolean firstLine = true; // 读第一行不加换行符
			while ((line = in.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
				} else {
					result += System.lineSeparator();
				}
				result += line;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String doPost(String url, String param, int connectTimeOut,
			int socketTimeOut) {

		CloseableHttpResponse response = null;
		String responseContent = null;
		try {
			HttpPost httppost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(connectTimeOut)
					.setSocketTimeout(socketTimeOut).build();// 设置请求和传输超时时间
			httppost.setConfig(requestConfig);

			StringEntity myEntity = new StringEntity(param, "UTF-8");
			StringBuffer out = new StringBuffer();
			InputStream is = myEntity.getContent();
			byte[] b = new byte[4096];
			for (int n; (n = is.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}
			System.out.println(out.toString());
			httppost.setEntity(myEntity);

			// 执行请求
			response = client.execute(httppost);
			// 返回数据
			HttpEntity entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			logger.info("HttpUtil.doPost[url:" + url + ",params:" + param
					+ "] exception. reason:" + e.getMessage());
			return null;
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return responseContent;
	}
	
	public String doPost(String url, Map<String, String> params, int connectTimeOut, int socketTimeOut) {
		// 参数校验
		if(url == null || "".equals(url)) {
			throw new IllegalArgumentException("The url can't be empty!");
		}
		HttpPost httpPost = null;
		CloseableHttpResponse response = null;
		// 组织请求参数
		try {
			logger.info("httpclient do post. url="+url);
			httpPost = new HttpPost(url);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			if(params != null && params.size() > 0) {
				Set<Entry<String, String>> sets = params.entrySet();
				Iterator<Entry<String, String>> it = sets.iterator();
				while(it.hasNext()) {
					Entry<String, String> entry = it.next();
					NameValuePair nameValue = new BasicNameValuePair(entry.getKey(), entry.getValue());
					paramsList.add(nameValue);
				}
			}
			UrlEncodedFormEntity urlEntity = new UrlEncodedFormEntity(paramsList, Consts.UTF_8);
			httpPost.setEntity(urlEntity);
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(connectTimeOut)
					.setSocketTimeout(socketTimeOut).build();//设置请求和传输超时时间
			httpPost.setConfig(requestConfig);
			
			// 执行请求
			response = client.execute(httpPost); 
			// 返回数据
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity, "UTF-8");
			}
		}catch(Exception e){
			logger.info("HttpUtil.doPost[url:"+url+",params:"+params.toString()+"] exception. reason:"+ e.getMessage());
			return null;
		}finally {
			try {
				if(response != null) 
					response.close();
			} catch (IOException e) {
			}
		}
		return null;
	}
	
	public String doGet(String url){
		HttpGet httpGet= null;
		CloseableHttpResponse response = null;
		try {
			httpGet = new HttpGet(url);
			//设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(CONNECT_TIMEOUT)
					.setSocketTimeout(SOCKET_TIMEOUT).build();
			httpGet.setConfig(requestConfig);
			//执行请求
			response = client.execute(httpGet);
			// 返回数据
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity, "UTF-8");
			}
		} catch (Exception e) {
			logger.info("HttpUtil.doGet[url:"+url+"] exception. reason:"+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if(response != null) 
					response.close();
			} catch (IOException e) {
			}
		}
		return null;
	}
	
}
