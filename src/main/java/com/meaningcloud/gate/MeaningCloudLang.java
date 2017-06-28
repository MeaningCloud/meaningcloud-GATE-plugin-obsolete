package com.meaningcloud.gate;

import com.meaningcloud.gate.clients.LangClient;
import com.meaningcloud.gate.param.ASutil;
import gate.*;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.InvalidOffsetException;
import gate.util.Out;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the implementation of the resource api.meaningcloud.com/lang.
 */
@CreoleResource(name = "MeaningCloud Language Identification", comment = "MeaningCloud Language Identification", helpURL = "http://www.meaningcloud.com/developer/language-identification/doc/1.1", icon = "/MeaningCloud.png")
public class MeaningCloudLang extends AbstractLanguageAnalyser implements
		ProcessingResource {
	private String apiURL, key, selection, threshold;

	private String inputASname, outputASname;

	private List<String> annotationTypes = new ArrayList<String>();

	private boolean debug;

	public void execute() throws ExecutionException {

		if (document == null)
			throw new ExecutionException("No Document Provided");

		if (key == null || key.trim().isEmpty())
			throw new ExecutionException("No API Key Provided");

		if (apiURL == null || apiURL.trim().isEmpty())
			throw new ExecutionException("Service URL Has Not Been Set");

		AnnotationSet inputAnnSet = document.getAnnotations(inputASname);

		String text = "";

		DocumentContent content = document.getContent();

		if (inputAnnSet.isEmpty()) {
			text += content.toString();
			process(text, null);
		} else {
			if (annotationTypes.size() == 0) {
				text += content.toString();
				process(text, null);
				/*
				 * Iterator<Annotation> inputIt =
				 * gate.Utils.inDocumentOrder(inputAnnSet).iterator();
				 *
				 * while(inputIt.hasNext()) { Annotation ann = inputIt.next();
				 * try { text =
				 * content.getContent(ann.getStartNode().getOffset(),
				 * ann.getEndNode().getOffset()).toString(); }
				 * catch(InvalidOffsetException ex) {
				 * Logger.getLogger(MeaningCloudClass.class.getName()).log(
				 * Level.SEVERE, null, ex); } process(text, ann); }
				 */
			} else {
				if (debug)
					Out.println("annotationTypes size: "
							+ annotationTypes.size());
				for (String inputAnnExpr : annotationTypes) {
					if (debug)
						Out.println("inputAnnExpr: " + inputAnnExpr);
					AnnotationSet filteredAS = ASutil.getFilteredAS(
							inputAnnSet, inputAnnExpr);
					if (debug)
						Out.println("FilteredAS: "
								+ gate.Utils.cleanStringFor(document,
										filteredAS));
					Iterator<Annotation> itr = gate.Utils.inDocumentOrder(
							filteredAS).iterator();
					while (itr.hasNext()) {
						Annotation ann = itr.next();
						try {
							text = content.getContent(
									ann.getStartNode().getOffset(),
									ann.getEndNode().getOffset()).toString();
						} catch (InvalidOffsetException ex) {
							Logger.getLogger(MeaningCloudClass.class.getName())
									.log(Level.SEVERE, null, ex);
						}
						process(text, ann);
					}
				}
			}
		}
	}

	public void process(String text, Annotation inputAnn) {
		String type = "";

		if (inputAnn != null) {
			type = inputAnn.getType();
			if (debug)
				Out.println("Processing: " + inputAnn.getType());
		} else {
			if (debug)
				Out.println("Processing the whole document");
		}

		// LangClient c = new LangClient();

		String api = this.getApiURL();
		String key = this.getKey();
		String txt = text;

		if (!txt.isEmpty() && !txt.equals("0")) {
			if (debug)
				Out.println("Text: " + txt);

			Post post;
			try {
				post = new Post(api);
				if (key != null && !key.isEmpty())
					post.addParameter("key", key);
				else {
					Logger.getLogger(MeaningCloudTopics.class.getName()).severe(
							"Key is not set");
					return;
				}
				post.addParameter("src", "gate_2.4");
				post.addParameter("txt", txt);
				post.addParameter("of", "xml");
				post.addParameter("selection", selection);
				post.addParameter("threshold", threshold);

				byte[] response = post.getResponse().getBytes("UTF-8");
				String resp = new String(response, "UTF-8");
				if (debug)
					Out.println("Response:" + resp);

				// Show topics
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder;
				try {
					docBuilder = docBuilderFactory.newDocumentBuilder();
					org.w3c.dom.Document doc = docBuilder
							.parse(new ByteArrayInputStream(response));
					doc.getDocumentElement().normalize();
					Element response_node = doc.getDocumentElement();
					try {
						NodeList statusL = response_node
								.getElementsByTagName("status");
						org.w3c.dom.Node status = statusL.item(0);
						NamedNodeMap attributes = status.getAttributes();
						org.w3c.dom.Node code = attributes.item(0);
						if (!code.getTextContent().equals("0")) {
							Logger.getLogger(MeaningCloudTopics.class.getName())
									.severe("API Error: "
											+ code.getTextContent() + ""
											+ post.params.toString());
						} else {
							try {
								Map<String, ArrayList<String>> updated = LangClient
										.collectInfo(response_node);
								setDocFeatures(updated, type, inputAnn);
							} catch (Exception e) {
								Logger.getLogger(
										MeaningCloudClass.class.getName()).log(
										Level.SEVERE, null, e);
							}
						}
					} catch (Exception e) {
						Logger.getLogger(MeaningCloudClass.class.getName()).log(
								Level.SEVERE, null, e);
						Logger.getLogger(MeaningCloudClass.class.getName())
								.severe("Not found");
					}

				} catch (ParserConfigurationException ex) {
					Logger.getLogger(MeaningCloudClass.class.getName()).log(
							Level.SEVERE, null, ex);
				} catch (SAXException ex) {
					Logger.getLogger(MeaningCloudClass.class.getName()).log(
							Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(MeaningCloudClass.class.getName()).log(
							Level.SEVERE, null, ex);
				}

			} catch (UnsupportedEncodingException ex) {
				Logger.getLogger(MeaningCloudClass.class.getName()).log(
						Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(MeaningCloudClass.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
	}

	public void setDocFeatures(Map<String, ArrayList<String>> lang_list, String type,
			Annotation inputAnn) throws InvalidOffsetException,
			UnsupportedEncodingException {
		FeatureMap fm = Factory.newFeatureMap();
//		List<String> langs = lang_list.get("language");
//		if (langs.size() > 0) {
//			Iterator<String> it = langs.iterator();
//			List<String> lang = new ArrayList<String>();
//			while (it.hasNext()) {
//				lang.add(new String(it.next().getBytes(), "utf-8"));
//			}
//			fm.put("language", lang);
//		}
		List<String> languages = lang_list.get("language");
		if(languages.size() > 0) {
			Iterator<String> it = languages.iterator();
			List<String> language = new ArrayList<String>();
			while(it.hasNext()) {
				language.add(new String(it.next().getBytes(), "utf-8"));
			}
			fm.put("language", language);
		}
		List<String> names = lang_list.get("name");
		if(names.size() > 0) {
			Iterator<String> it = names.iterator();
			List<String> name = new ArrayList<String>();
			while(it.hasNext()) {
				name.add(new String(it.next().getBytes(), "utf-8"));
			}
			fm.put("name", name);
		}
		List<String> relevances = lang_list.get("relevance");
		if(relevances.size() > 0) {
			Iterator<String> it = relevances.iterator();
			List<String> relevance = new ArrayList<String>();
			while(it.hasNext()) {
				relevance.add(new String(it.next().getBytes(), "utf-8"));
			}
			fm.put("relevance", relevance);
		}
		if (inputAnn != null) {
			Logger.getLogger(MeaningCloudClass.class.getName())
					.info("The text you have processed is written in "
							+ fm.get("language")
							+ ". The annotation was created as a new Feature of your inputAS");
			FeatureMap fm2 = inputAnn.getFeatures();
			fm2.putAll(fm);
		} else {
			Logger.getLogger(MeaningCloudClass.class.getName())
					.info("The text you have processed is written in "
							+ fm.get("language")
							+ ". The annotation was created as a Document Feature");
			FeatureMap fm2 = document.getFeatures();
			fm2.putAll(fm);
		}
	}

	@RunTime
	@CreoleParameter(comment = "URL Of the API to query", defaultValue = "https://api.meaningcloud.com/lang-2.0")
	public void setApiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	public String getApiURL() {
		return apiURL;
	}

	@RunTime
	@CreoleParameter(comment = "License Key")
	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	@RunTime
	@Optional
	@CreoleParameter(comment = "List of expected languages, separated by |", defaultValue = "")
	public void setSelection(String selection) { this.selection = selection; }

	public String getSelection() { return selection; }

	@RunTime
	@Optional
	@CreoleParameter(comment = "Language detection threshold as a percentage of similarity with respect to the top result",
			defaultValue = "100")
	public void setThreshold(String threshold) { this.threshold = threshold; }

	public String getThreshold() { return threshold; }

	@RunTime
	@Optional
	@CreoleParameter(comment = "AnnotationSet with the input content")
	public void setInputASName(String t) {
		this.inputASname = t;
	}

	public String getInputASName() {
		return inputASname;
	}

	@RunTime
	@Optional
	@CreoleParameter(comment = "Output Annotation Set", defaultValue = "MeaningCloud")
	public void setOutputASName(String t) {
		this.outputASname = t;
	}

	public String getOutputASName() {
		return outputASname;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "false", comment = "Debug variable for the GATE plugin")
	public void setDebug(Boolean verb) {
		this.debug = verb;
	}

	public Boolean getDebug() {
		return debug;
	}

	@RunTime
	@Optional
	@CreoleParameter(comment = "Filter content by this expression. It allows format: \n"
			+ "Type.FeatureName  \n"
			+ "or  \n"
			+ "Type.FeatureName==FeatureValue  \n")
	public void setannotationTypes(List<String> iat) {
		this.annotationTypes = iat;
	}

	public List<String> getannotationTypes() {
		return annotationTypes;
	}
}
