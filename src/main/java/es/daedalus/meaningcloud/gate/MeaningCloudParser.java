/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.daedalus.meaningcloud.gate;

/**
 *
 * @author ADRIAN
 */

import gate.Annotation;
import gate.AnnotationSet;
import gate.DocumentContent;
import gate.Factory;
import gate.ProcessingResource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.InvalidOffsetException;
import gate.util.Out;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.daedalus.meaningcloud.gate.clients.ParserClient;
import es.daedalus.meaningcloud.gate.clients.ParserClient.Annot;
import es.daedalus.meaningcloud.gate.param.ASutil;
import es.daedalus.meaningcloud.gate.param.DisambiguationLevel;
import es.daedalus.meaningcloud.gate.param.Serialized_resp;
import es.daedalus.meaningcloud.gate.param.TokenBean;

/**
 * This class is the implementation of the resource MeaningCloud Parser.
 */
@CreoleResource(name = "MeaningCloud Lemmatization, PoS and Parsing", comment = "MeaningCloud Lemmatization, PoS and Parsing", helpURL = "http://api.meaningcloud.com/parser-info",icon = "/MeaningCloud.png")
public class MeaningCloudParser extends AbstractLanguageAnalyser implements
		ProcessingResource {

	private String inputASname, outputASname, apiURL, key, lang, ud,
			outputType;
	private Boolean unknownWords, relaxedTypography, debug;
	private List<String> annotationTypes = new ArrayList<String>(); // list of
																	// input
																	// annotations
																	// from
																	// which
																	// string
																	// content
																	// will be
																	// submitted
	private String dictionary, mode;
	private DisambiguationLevel disambiguationLevel;
	private static final int RETRY = 5;

	public String textTransform(boolean bool) {
		String ret = bool ? "y" : "n";
		return ret;
	}

	public String translateDM(DisambiguationLevel dm) {
		String ret_dm = "";
		if (dm.equals(DisambiguationLevel.no_disambiguation))
			ret_dm = "0";
		else if (dm.equals(DisambiguationLevel.morphsyntactic_disambiguation))
			ret_dm = "1";
		else if (dm.equals(DisambiguationLevel.basic_disambiguation))
			ret_dm = "2";
		else if (dm.equals(DisambiguationLevel.light_disambiguation))
			ret_dm = "3";
		else if (dm.equals(DisambiguationLevel.strong_disambiguation))
			ret_dm = "4";
		else if (dm.equals(DisambiguationLevel.full_disambiguation))
			ret_dm = "5";
		else {
			ret_dm = "4";
		}

		return ret_dm;
	}

	@Override
	public void execute() throws ExecutionException {
		if (document == null)
			throw new ExecutionException("No document provided");

		AnnotationSet inputAnnSet = document.getAnnotations(inputASname);
		AnnotationSet outputAnnSet = document.getAnnotations(outputASname);
		String text = "";
		String type = "";

		DocumentContent content = document.getContent();

		if (inputAnnSet.isEmpty()) {
			text += content.toString();
			// type = "_document";
			boolean apiOK = false;
			int times = 0;
			while (times < RETRY && !apiOK) {
				times++;
				try {
					apiOK = processWithMeaningCloud(text, type, null,
							outputAnnSet);
					if (debug)
						Out.println("Nr of retry: " + times + ". Text: " + text);
				} catch (InvalidOffsetException ex) {
					Logger.getLogger(MeaningCloudParser.class.getName()).log(
							Level.SEVERE, null, ex);
				} catch (XPathExpressionException ex) {
					Logger.getLogger(MeaningCloudParser.class.getName()).log(
							Level.SEVERE, null, ex);
				} catch (UnsupportedEncodingException ex) {
					Logger.getLogger(MeaningCloudParser.class.getName()).log(
							Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(MeaningCloudParser.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
			times = 0;
		} else {
			if (annotationTypes.size() == 0) {
				text += content.toString();
				// type = "_document";
				boolean apiOK = false;
				int times = 0;
				while (times < RETRY && !apiOK) {
					times++;
					try {
						apiOK = processWithMeaningCloud(text, type, null,
								outputAnnSet);
						if (debug)
							Out.println("Nr of retry: " + times + ". Text: "
									+ text);
					} catch (InvalidOffsetException ex) {
						Logger.getLogger(MeaningCloudParser.class.getName())
								.log(Level.SEVERE, null, ex);
					} catch (XPathExpressionException ex) {
						Logger.getLogger(MeaningCloudParser.class.getName())
								.log(Level.SEVERE, null, ex);
					} catch (UnsupportedEncodingException ex) {
						Logger.getLogger(MeaningCloudParser.class.getName())
								.log(Level.SEVERE, null, ex);
					} catch (IOException ex) {
						Logger.getLogger(MeaningCloudParser.class.getName())
								.log(Level.SEVERE, null, ex);
					}
				}
				times = 0;
			} else {
				for (String inputAnnExpr : annotationTypes) {
					AnnotationSet filteredAS = ASutil.getFilteredAS(
							inputAnnSet, inputAnnExpr);
					Iterator<Annotation> itr = gate.Utils.inDocumentOrder(
							filteredAS).iterator();
					while (itr.hasNext()) {
						Annotation ann = itr.next();
						try {
							text = content.getContent(
									ann.getStartNode().getOffset(),
									ann.getEndNode().getOffset()).toString();
						} catch (InvalidOffsetException ex) {
							Logger.getLogger(MeaningCloudParser.class.getName())
									.log(Level.SEVERE, null, ex);
						}

						boolean apiOK = false;
						int times = 0;
						while (times < RETRY && !apiOK) {
							times++;
							try {
								apiOK = processWithMeaningCloud(text, type, ann,
										outputAnnSet);
								if (debug)
									Out.println("Nr of retry: " + times
											+ ". Text: " + text);
							} catch (InvalidOffsetException ex) {
								Logger.getLogger(
										MeaningCloudParser.class.getName()).log(
										Level.SEVERE, null, ex);
							} catch (XPathExpressionException ex) {
								Logger.getLogger(
										MeaningCloudParser.class.getName()).log(
										Level.SEVERE, null, ex);
							} catch (UnsupportedEncodingException ex) {
								Logger.getLogger(
										MeaningCloudParser.class.getName()).log(
										Level.SEVERE, null, ex);
							} catch (IOException ex) {
								Logger.getLogger(
										MeaningCloudParser.class.getName()).log(
										Level.SEVERE, null, ex);
							}
						}
						times = 0;
					}
				}
			}
		}
	}

	public boolean processWithMeaningCloud(String text, String type,
			Annotation inputAnn, AnnotationSet outputAnnSet)
			throws InvalidOffsetException, XPathExpressionException,
			MalformedURLException, UnsupportedEncodingException, IOException {

		String api = this.getapiURL();
		String key = this.getkey();
		String txt = text;

		if (!txt.isEmpty() && !txt.equals("0")) {
			Post post;
			post = new Post(api);
			if (key != null && !key.isEmpty())
				post.addParameter("key", key);
			else {
				Logger.getLogger(MeaningCloudTopics.class.getName()).severe(
						"Key is empty");
				return false;
			}
			post.addParameter("txt", txt);
			if (this.getlang() != null && !this.getlang().isEmpty())
				post.addParameter("lang", this.getlang());
			else {
				Logger.getLogger(MeaningCloudTopics.class.getName()).severe(
						"Lang is empty");
				return false;
			}
			post.addParameter("src", "gate_1.0_parser");
			post.addParameter("of", "json");
			if (this.getunknownWords() != null)
				post.addParameter("uw", textTransform(this.getunknownWords()));
			if (this.getrelaxedTypography() != null)
				post.addParameter("rt",
						textTransform(this.getrelaxedTypography()));
			post.addParameter("tt", "");
			post.addParameter("st", "y");
			if (this.getdictionary() != null)
				post.addParameter("dic", this.getdictionary());
			if (this.getud() != null)
				post.addParameter("ud", this.getud());
			if (this.getDisambiguationLevel() != null)
				post.addParameter("dm",
						this.translateDM(this.getDisambiguationLevel()));
			if (this.getmode() != null) {
				if (this.getmode().equals("sa") || this.getmode().equals("ma")) {
					post.addParameter("mode", this.getmode());
				} else {
					Logger.getLogger(MeaningCloudTopics.class.getName()).info(
							"not a valid mode");
					return false;
				}
			}
			post.addParameter("verbose", "y");

			if (debug)
				Logger.getLogger(MeaningCloudTopics.class.getName()).info(
						"" + post.params + "");

			byte[] response = post.getResponse().getBytes("UTF-8");
			String resp = new String(response, "UTF-8");

			ParserClient sc = new ParserClient();
			Serialized_resp data = null;
			Long inip = 0L, endp = 0L;
			try {
				data = sc.getResponse(resp);
				if (data != null) {
					for (Serialized_resp.Annot a : data.annot_list) {
						inip = a.inip;
						endp = a.endp;
						if (inputAnn != null)
							outputAnnSet.add(
									inputAnn.getStartNode().getOffset()
											+ a.inip,
									inputAnn.getStartNode().getOffset()
											+ a.endp,
									(!this.getoutputType().isEmpty() ? this
											.getoutputType() : "Token"),
									a.fm.isEmpty() ? Factory.newFeatureMap()
											: a.fm);
						else
							outputAnnSet.add(
									a.inip,
									a.endp,
									(!this.getoutputType().isEmpty() ? this
											.getoutputType() : "Token"),
									a.fm.isEmpty() ? Factory.newFeatureMap()
											: a.fm);
					}
				}
			} catch (Exception e) {
				Logger.getLogger(MeaningCloudTopics.class.getName()).severe(
						e.toString());
				if (inputAnn != null)
					System.err
							.println((inputAnn.getStartNode().getOffset() + inip)
									+ "-"
									+ (inputAnn.getStartNode().getOffset() + endp));
				else
					System.err.println(inip + "-" + endp);
			}
		}
		return true;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "false", comment = "This feature adds a stage to all the possible modes where, much like a spellchecker, the engine tries to find a suitable analysis to the unknown words resulted from the initial analysis assignment. It is specially useful to decrease the impact typos have in text analyses.\n"
			+ "\n" + "y: enabled\n" + "n: disabled (default)")
	public void setunknownWords(Boolean uw) {
		this.unknownWords = uw;
	}

	public Boolean getunknownWords() {
		return unknownWords;
	}

	@RunTime
	@Optional
	@CreoleParameter(comment = "User Defined Dictionary")
	public void setud(String userD) {
		this.ud = userD;
	}

	public String getud() {
		return ud;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "This parameter indicates how reliable the text to analyze is (as far as spelling, typography, etc. are concerned), and influences how strict the engine will be when it comes to take these factors into account in the analysis.\n"
			+ "\n" + "y: enabled (default)\n" + "n: disabled")
	public void setrelaxedTypography(Boolean rt) {
		this.relaxedTypography = rt;
	}

	public Boolean getrelaxedTypography() {
		return relaxedTypography;
	}

	@RunTime
	@Optional
	@CreoleParameter(comment = "Input Annotation Set")
	public void setinputASname(String inputASname) {
		this.inputASname = inputASname;
	}

	public String getinputASname() {
		return inputASname;
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

	@RunTime
	@Optional
	@CreoleParameter(comment = "Output Annotation Set", defaultValue = "MeaningCloud")
	public void setoutputASname(String outputASname) {
		this.outputASname = outputASname;
	}

	public String getoutputASname() {
		return outputASname;
	}

	@RunTime
	@CreoleParameter(comment = "URL of the API", defaultValue = "http://api.meaningcloud.com/parser-1.2")
	public void setapiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	public String getapiURL() {
		return apiURL;
	}

	@RunTime
	@CreoleParameter(comment = "License key")
	public void setkey(String key) {
		this.key = key;
	}

	public String getkey() {
		return key;
	}

	@RunTime
	@CreoleParameter(defaultValue = "en", comment = "Language in which we want to perform the analysis")
	public void setlang(String lang) {
		this.lang = lang;
	}

	public String getlang() {
		return this.lang;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "chetsdp", comment = "Dictionaries to be used")
	public void setdictionary(String dic) {
		this.dictionary = dic;
	}

	public String getdictionary() {
		return dictionary;
	}

	@RunTime
	@CreoleParameter(defaultValue = "strong_disambiguation", comment = "Disambiguation level applied.")
	public void setDisambiguationLevel(DisambiguationLevel ds) {
		this.disambiguationLevel = ds;
	}

	public DisambiguationLevel getDisambiguationLevel() {
		return disambiguationLevel;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "false", comment = "Debug variable for the GATE plugin")
	public void setdebug(Boolean verb) {
		this.debug = verb;
	}

	public Boolean getdebug() {
		return debug;
	}

	@RunTime
	@CreoleParameter(defaultValue = "Token", comment = "Name of the output Type where annotations are stored")
	public void setoutputType(String otype) {
		this.outputType = otype;
	}

	public String getoutputType() {
		return this.outputType;
	}

	@RunTime
	@CreoleParameter(defaultValue = "sa", comment = "Mode of annotation. Available ma and sa")
	public void setmode(String mode) {
		this.mode = mode;
	}

	public String getmode() {
		return this.mode;
	}

} // class PoSTaggingMeaningCloud
