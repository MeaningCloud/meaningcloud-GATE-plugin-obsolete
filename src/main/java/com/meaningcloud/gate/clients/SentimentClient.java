package com.meaningcloud.gate.clients;

import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;

import com.meaningcloud.gate.param.SentimentBean;
import com.meaningcloud.gate.param.Serialized_resp;

public class SentimentClient {

	public Serialized_resp getResponse(String response/*
													 * ,AnnotationSet outputAnn,
													 * gate.Document doc
													 */)
			throws UnsupportedEncodingException {
		SentimentBean.Sent_data jsonObject = new Gson().fromJson(response,
				SentimentBean.Sent_data.class);
		Serialized_resp sr = jsonObject.serialize();
		return sr;
	}

	public String getString(String response) {
		SentimentBean.Sent_data jsonObject = new Gson().fromJson(response,
				SentimentBean.Sent_data.class);
		return jsonObject.toString();
	}

	public SentimentBean.Sent_data getData(String response) {
		SentimentBean.Sent_data jsonObject = new Gson().fromJson(response,
				SentimentBean.Sent_data.class);
		return jsonObject;
	}

}
