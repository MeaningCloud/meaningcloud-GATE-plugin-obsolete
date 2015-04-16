/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.daedalus.meaningcloud.gate;

import gate.Annotation;
import gate.AnnotationSet;
import gate.DocumentContent;
import gate.ProcessingResource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.InvalidOffsetException;
import gate.util.Out;

import java.io.ByteArrayInputStream;
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

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.daedalus.meaningcloud.gate.clients.StilusClient;
import es.daedalus.meaningcloud.gate.param.ASutil;

/**
 * This class is the implementation of the resource MeaningCloud proofreading.
 */
@CreoleResource(name = "MeaningCloud Spell, Grammar and Style Proofreading", comment = "MeaningCloud Spell, Grammar and Style Proofreading", helpURL = "http://www.meaningcloud.com/developer/text-proofreading/doc/1.2", icon = "/MeaningCloud.png")
public class MeaningCloudProofreading extends AbstractLanguageAnalyser implements
		ProcessingResource {

	private String inputASname, outputASname, apiURL, key, lang, manyErrors,
			dictionary;
	private Boolean prefixed, quotesOrItalics, too_longSent, properNouns,
			tautologyAndLanMisuse, spacing, openingClosing, punctuation,
			foreign, confusion, percentage, consonantRed, debug;
	private List<String> annotationTypes = new ArrayList<String>();

	public String textTransform(boolean bool) {
		String ret = bool ? "y" : "n";
		return ret;
	}

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
			process(text, type, null, outputAnnSet);
		} else {
			if (annotationTypes.size() == 0) {
				text += content.toString();
				process(text, type, null, outputAnnSet);
				/*
				 * Iterator<Annotation> inputIt =
				 * gate.Utils.inDocumentOrder(inputAnnSet).iterator();
				 * 
				 * while(inputIt.hasNext()){ Annotation ann = inputIt.next();
				 * try { text =
				 * content.getContent(ann.getStartNode().getOffset(),
				 * ann.getEndNode().getOffset()).toString(); } catch
				 * (InvalidOffsetException ex) {
				 * Logger.getLogger(MeaningCloudClass
				 * .class.getName()).log(Level.SEVERE, null, ex); }
				 * process(text,type,ann,outputAnnSet); }
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
						Annotation ann = (Annotation) itr.next();
						try {
							text = content.getContent(
									ann.getStartNode().getOffset(),
									ann.getEndNode().getOffset()).toString();
						} catch (InvalidOffsetException ex) {
							Logger.getLogger(MeaningCloudClass.class.getName())
									.log(Level.SEVERE, null, ex);
						}
						process(text, type, ann, outputAnnSet);
					}
				}
			}
		}
	}

	public void process(String text, String type, Annotation inputAnn,
			AnnotationSet outputAnnSet) {
		if (inputAnn != null) {
			if (debug)
				Out.println("Processing: " + inputAnn.getType());
		} else {
			if (debug)
				Out.println("Processing the whole document");
		}

		if (!text.isEmpty() && !text.equals("0")) {

			try {
				StilusClient c = new StilusClient();
				String api = this.getapiURL();
				String key = this.getkey();
				String txt = text;

				Post post;
				post = new Post(api);
				post.addParameter("src", "gate_1.0");
				if (key != null && !key.isEmpty())
					post.addParameter("key", key);
				else {
					Logger.getLogger(MeaningCloudTopics.class.getName()).severe(
							"Key is not set");
					return;
				}
				post.addParameter("txt", txt);
				if (this.getlang() != null && !this.getlang().isEmpty())
					post.addParameter("lang", this.getlang());
				else {
					Logger.getLogger(MeaningCloudTopics.class.getName()).severe(
							"Lang is not set");
					return;
				}
				if (this.getdictionary() != null)
					post.addParameter("dic", this.getdictionary());
				else {
					Logger.getLogger(MeaningCloudTopics.class.getName()).severe(
							"Dictionary is unset");
					return;
				}
				post.addParameter("of", "xml");
				post.addParameter("pp", textTransform(this.getprefixed()));
				post.addParameter("aqoi",
						textTransform(this.getquotesOrItalics()));
				post.addParameter("tls", textTransform(this.gettoo_longSent()));
				post.addParameter("dpn", textTransform(this.getproperNouns()));
				post.addParameter("red",
						textTransform(this.gettautologyAndLanMisuse()));
				post.addParameter("spa", textTransform(this.getspacing()));
				post.addParameter("comppunc",
						textTransform(this.getopeningClosing()));
				post.addParameter("corrpunc",
						textTransform(this.getpunctuation()));
				post.addParameter("alw", textTransform(this.getforeign()));
				post.addParameter("wct", textTransform(this.getconfusion()));
				post.addParameter("wps", textTransform(this.getpercentage()));
				post.addParameter("stme", this.getmanyErrors());
				post.addParameter("wrc", textTransform(this.getconsonantRed()));

				byte[] response = post.getResponse().getBytes("UTF-8");
				String resp = new String(response, "UTF-8");
				if (debug)
					Out.println("Response:" + resp);

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
											+ code.getTextContent() + ". "
											+ post.params.toString());
						} else {
							try {
								List<StilusClient.result> result_list = StilusClient
										.collectInfo(response_node, "result",
												outputAnnSet);
								setDocFeatures(result_list, type, inputAnn,
										outputAnnSet);
							} catch (Exception ex) {
								Logger.getLogger(
										MeaningCloudProofreading.class.getName())
										.log(Level.SEVERE, null, ex);
							}
						}
					} catch (Exception e) {
						System.err.println(e.toString());
						System.out.println("Not found");
					}
				} catch (ParserConfigurationException ex) {
					Logger.getLogger(MeaningCloudProofreading.class.getName())
							.log(Level.SEVERE, null, ex);
				} catch (SAXException ex) {
					Logger.getLogger(MeaningCloudProofreading.class.getName())
							.log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(MeaningCloudProofreading.class.getName())
							.log(Level.SEVERE, null, ex);
				}
			} catch (MalformedURLException ex) {
				Logger.getLogger(MeaningCloudProofreading.class.getName()).log(
						Level.SEVERE, null, ex);
			} catch (UnsupportedEncodingException ex) {
				Logger.getLogger(MeaningCloudProofreading.class.getName()).log(
						Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(MeaningCloudProofreading.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
	}

	public void setDocFeatures(List<StilusClient.result> result_list,
			String type, Annotation inputAnn, AnnotationSet outputAnnSet)
			throws InvalidOffsetException {
		if (result_list.size() > 0) {
			Iterator<StilusClient.result> it = result_list.iterator();
			if (debug)
				Out.println("resultList: " + result_list.size());
			try {
				while (it.hasNext()) {
					StilusClient.result r = it.next();
					gate.FeatureMap fm = gate.Factory.newFeatureMap();
					if ((!r.inip.isEmpty()) && (!r.endp.isEmpty())) {
						ArrayList<String> suglist_form = new ArrayList<String>();
						ArrayList<String> suglist_conf = new ArrayList<String>();
						fm.put("text", (r.text != null ? r.text : ""));
						fm.put("type", (r.type != null ? r.type : ""));
						fm.put("level", (r.level != null ? r.level : ""));
						fm.put("rule", (r.rule != null ? r.rule : ""));
						fm.put("msg", (r.msg != null ? r.msg : ""));
						if (r.sug_list.size() > 0) {
							Iterator<StilusClient.result.suggestion> it2 = r.sug_list
									.iterator();
							while (it2.hasNext()) {
								StilusClient.result.suggestion s = it2.next();
								suglist_form.add(s.form);
								suglist_conf.add(s.confidence);
							}
						}
						if (!suglist_form.isEmpty())
							fm.put("suggestion_list_form", suglist_form);
						if (!suglist_conf.isEmpty())
							fm.put("suggestion_list_confidence", suglist_conf);

						if (inputAnn != null) {
							outputAnnSet
									.add((inputAnn.getStartNode().getOffset() + Long
											.parseLong(r.inip, 10)), (inputAnn
											.getStartNode().getOffset() + (Long
											.parseLong(r.endp, 10) + 1)),
											"proofreading" + type, fm);
						} else {
							outputAnnSet.add(Long.parseLong(r.inip, 10),
									(Long.parseLong(r.endp, 10) + 1),
									"proofreading" + type, fm);
						}
					}
				}

			} catch (Exception ex) {
				Logger.getLogger(MeaningCloudProofreading.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
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
	@CreoleParameter(comment = "Output Annotation Set", defaultValue = "MeaningCloud")
	public void setoutputASname(String outputASname) {
		this.outputASname = outputASname;
	}

	public String getoutputASname() {
		return outputASname;
	}

	@RunTime
	@CreoleParameter(comment = "URL Of the API to query", defaultValue = "http://api.meaningcloud.com/stilus-1.2")
	public void setapiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	public String getapiURL() {
		return apiURL;
	}

	@RunTime
	@CreoleParameter(comment = "License Key")
	public void setkey(String key) {
		this.key = key;
	}

	public String getkey() {
		return key;
	}

	@RunTime
	@CreoleParameter(defaultValue = "en", comment = "It specifies the language in which the text must be analyzed. The current supported values are the following:\n"
			+ "  \n"
			+ "en: English  \n"
			+ "es: Spanish  \n"
			+ "it: Italian  \n" + "fr: French  ")
	public void setlang(String lang) {
		this.lang = lang;
	}

	public String getlang() {
		return this.lang;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "2", comment = "Describes how it will behave when it finds sentences with many errors\n"
			+ "\n"
			+ "0: Check for all\n"
			+ "1: Group and ignore\n"
			+ "2: Group and warn (default)")
	public void setmanyErrors(String me) {
		this.manyErrors = me;
	}

	public String getmanyErrors() {
		return this.manyErrors;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "chetsdp", comment = "This parameter will specify the list of active dictionaries that will be used in the topic extraction")
	public void setdictionary(String dic) {
		this.dictionary = dic;
	}

	public String getdictionary() {
		return this.dictionary;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "Smart detection of prefixed words\n"
			+ "\n" + "y: enabled (default)\n" + "n: disabled")
	public void setprefixed(Boolean p) {
		this.prefixed = p;
	}

	public Boolean getprefixed() {
		return this.prefixed;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "Smart handling of words written in italics or with quotation marks\n"
			+ "\n" + "y: enabled (default)\n" + "n: disabled")
	public void setquotesOrItalics(Boolean q) {
		this.quotesOrItalics = q;
	}

	public Boolean getquotesOrItalics() {
		return this.quotesOrItalics;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "Warn of too-long sentences\n"
			+ "\n" + "y: enabled (default)\n" + "n: disabled")
	public void settoo_longSent(Boolean tl) {
		this.too_longSent = tl;
	}

	public Boolean gettoo_longSent() {
		return this.too_longSent;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "Smart detection of proper nouns\n"
			+ "\n" + "y: enabled (default)\n" + "n: disabled")
	public void setproperNouns(Boolean pn) {
		this.properNouns = pn;
	}

	public Boolean getproperNouns() {
		return this.properNouns;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "Check tautology and language misuse\n"
			+ "\n" + "y: enabled (default)\n" + "n: disabled")
	public void settautologyAndLanMisuse(Boolean tt) {
		this.tautologyAndLanMisuse = tt;
	}

	public Boolean gettautologyAndLanMisuse() {
		return this.tautologyAndLanMisuse;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "Check spacing\n" + "\n"
			+ "y: enabled (default)\n" + "n: disabled")
	public void setspacing(Boolean ss) {
		this.spacing = ss;
	}

	public Boolean getspacing() {
		return this.spacing;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "Verify the opening and closing of pairs of signs\n"
			+ "\n" + "y: enabled (default)\n" + "n: disabled")
	public void setopeningClosing(Boolean oc) {
		this.openingClosing = oc;
	}

	public Boolean getopeningClosing() {
		return this.openingClosing;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "Check punctuation marks\n"
			+ "\n" + "y: enabled (default)\n" + "n: disabled")
	public void setpunctuation(Boolean p) {
		this.punctuation = p;
	}

	public Boolean getpunctuation() {
		return this.punctuation;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "Warn of foreign words to be avoided\n"
			+ "\n" + "y: enabled (default)\n" + "n: disabled")
	public void setforeign(Boolean f) {
		this.foreign = f;
	}

	public Boolean getforeign() {
		return this.foreign;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "Warn of confusion between terms\n"
			+ "\n" + "y: enabled (default)\n" + "n: disabled")
	public void setconfusion(Boolean c) {
		this.confusion = c;
	}

	public Boolean getconfusion() {
		return this.confusion;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "Warn of percentage signs (%) not spaced (Spanish only)\n"
			+ "\n" + "y: enabled (default)\n" + "n: disabled")
	public void setpercentage(Boolean p) {
		this.percentage = p;
	}

	public Boolean getpercentage() {
		return this.percentage;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "true", comment = "Warn of group consonant reduction (Spanish only)\n"
			+ "\n" + "y: enabled (default)\n" + "n: disabled")
	public void setconsonantRed(Boolean p) {
		this.consonantRed = p;
	}

	public Boolean getconsonantRed() {
		return this.consonantRed;
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

} // class MeaningCloudProofreading

