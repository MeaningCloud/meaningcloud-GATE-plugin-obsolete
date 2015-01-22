/*
 * Sentiment Analysis 1.0 sample client for Java
 * (c) Daedalus
 */
package es.daedalus.meaningcloud.gate;

//import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
//import java.net.URLEncoder;
//import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

public class PostApache {
	private URL url;
	List<NameValuePair> params;
	int TIMEOUT_VALUE = 60000;// 0;// 120000;

	public PostApache(String api) throws MalformedURLException {
		url = new URL(api);
		params = new ArrayList();
	}

	public void addParameter(String name, String value)
			throws UnsupportedEncodingException {
		/*
		 * codificamos cada uno de los valores if (params.length()>0) params +=
		 * "&" + URLEncoder.encode(name, "UTF-8") + "=" +
		 * URLEncoder.encode(value, "UTF-8"); else params +=
		 * URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value,
		 * "UTF-8");
		 */
		params.add(new BasicNameValuePair(name, new String(value.getBytes(),
				"UTF-8")));
	}

	public String getResponse() throws IOException, URISyntaxException {
		String response = "";

		// HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url.toURI());
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(TIMEOUT_VALUE / 2)
				.setConnectionRequestTimeout(TIMEOUT_VALUE)
				.setSocketTimeout(4 * TIMEOUT_VALUE).build();
		CloseableHttpClient httpclient = HttpClientBuilder.create()
				.setDefaultRequestConfig(config).build();
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}
		/*
		 * Execute the HTTP Request
		 */
		try {
			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity respEntity = httpresponse.getEntity();

			if (respEntity != null) {
				// EntityUtils to get the response content
				response += EntityUtils.toString(respEntity);
			}
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		httpclient.close();
		return response;
	}
}
