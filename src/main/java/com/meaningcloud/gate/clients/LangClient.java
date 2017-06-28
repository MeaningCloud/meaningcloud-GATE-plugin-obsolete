/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meaningcloud.gate.clients;

import gate.util.InvalidOffsetException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author ADRIAN
 */
public class LangClient {

	public static Map<String, ArrayList<String>> collectInfo(Element response)
			throws InvalidOffsetException, UnsupportedEncodingException {

		Map<String, ArrayList<String>> features = new HashMap<String, ArrayList<String>>();
		ArrayList<String> langs = new ArrayList<String>();
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> relevances = new ArrayList<String>();

		NodeList nodeL = response.getElementsByTagName("language");
		for (int i = 0; i < nodeL.getLength(); i++) {
			Node lang = nodeL.item(i);
			String value_name = lang.getNodeName();
			if ("language".equals(value_name)) {
				langs.add(new String(lang.getTextContent().getBytes(), "UTF-8"));
			}
		}
		nodeL = response.getElementsByTagName("name");
		for (int i = 0; i < nodeL.getLength(); i++) {
			Node lang = nodeL.item(i);
			String value_name = lang.getNodeName();
			if("name".equals(value_name)) {
				names.add(new String(lang.getTextContent().getBytes(), "UTF-8"));
			}
		}
		nodeL = response.getElementsByTagName("relevance");
		for (int i = 0; i < nodeL.getLength(); i++) {
			Node lang = nodeL.item(i);
			String value_name = lang.getNodeName();
			if ("relevance".equals(value_name)) {
				relevances.add(new String(lang.getTextContent().getBytes(), "UTF-8"));
			}
		}
		features.put("language", langs);
		features.put("name", names);
		features.put("relevance", relevances);
		return features;
	}
}
