package daedalus.textalytics.gate.clients;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import daedalus.textalytics.gate.param.SentimentBean;
import daedalus.textalytics.gate.param.Serialized_resp;

import com.google.gson.Gson;
import com.sun.xml.internal.txw2.Document;




public class SentimentClient {
	
	public Serialized_resp getResponse(String response/*,AnnotationSet outputAnn, gate.Document doc*/) throws UnsupportedEncodingException{
		SentimentBean.Sent_data jsonObject = new Gson().fromJson( response, SentimentBean.Sent_data.class );
		Serialized_resp sr = jsonObject.serialize();
		return sr;
	}
	
	public String getString(String response){
		SentimentBean.Sent_data jsonObject = new Gson().fromJson( response, SentimentBean.Sent_data.class );
		return jsonObject.toString();
	}
	
	public SentimentBean.Sent_data getData(String response){
		SentimentBean.Sent_data jsonObject = new Gson().fromJson(response, SentimentBean.Sent_data.class );
		return jsonObject;
	}

}
