/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meaningcloud.gate;

/**
 *
 * @author ADRIAN
 */

import com.meaningcloud.gate.clients.SentimentClient;
import com.meaningcloud.gate.param.ASutil;
import com.meaningcloud.gate.param.Serialized_resp;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPathExpressionException;

/**
 * This class is the implementation of the resource MeaningCloud Sentiment Analysis.
 */
@CreoleResource(name = "MeaningCloud Sentiment Analysis", comment = "MeaningCloud Sentiment Analysis", helpURL = "http://www.meaningcloud.com/developer/sentiment-analysis/doc/2.1", icon = "/MeaningCloud.png")
public class MeaningCloudSentiment extends AbstractLanguageAnalyser implements
		ProcessingResource {
	private String inputASname, outputASname, apiURL, key, lang, model, verbose, egp, rt, uw, dm, sdg, cont, cs, ud;
	private boolean debug;
	private List<String> annotationTypes = new ArrayList<String>(); // list of
																	// input
																	// annotations
																	// from
																	// which
																	// string
																	// content
																	// will be
																	// submitted
	private static final int RETRY = 5;

	public String textTransform(boolean bool) {
		String ret = bool ? "y" : "n";
		return ret;
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
				try {
					apiOK = processWithMeaningCloud(text, type, null,
							outputAnnSet);
					if (debug)
						Out.println("Nr of retry: " + times + ". Text: " + text);
					times++;
				} catch (InvalidOffsetException ex) {
					Logger.getLogger(MeaningCloudSentiment.class.getName()).log(
							Level.SEVERE, null, ex);
				} catch (XPathExpressionException ex) {
					Logger.getLogger(MeaningCloudSentiment.class.getName()).log(
							Level.SEVERE, null, ex);
				} catch (UnsupportedEncodingException ex) {
					Logger.getLogger(MeaningCloudSentiment.class.getName()).log(
							Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(MeaningCloudSentiment.class.getName()).log(
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
					try {
						apiOK = processWithMeaningCloud(text, type, null,
								outputAnnSet);
						if (debug)
							Out.println("Nr of retry: " + times + ". Text: "
									+ text);
						times++;
					} catch (InvalidOffsetException ex) {
						Logger.getLogger(MeaningCloudSentiment.class.getName())
								.log(Level.SEVERE, null, ex);
					} catch (XPathExpressionException ex) {
						Logger.getLogger(MeaningCloudSentiment.class.getName())
								.log(Level.SEVERE, null, ex);
					} catch (UnsupportedEncodingException ex) {
						Logger.getLogger(MeaningCloudSentiment.class.getName())
								.log(Level.SEVERE, null, ex);
					} catch (IOException ex) {
						Logger.getLogger(MeaningCloudSentiment.class.getName())
								.log(Level.SEVERE, null, ex);
					}
				}
				times = 0;
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
							Logger.getLogger(
									MeaningCloudSentiment.class.getName()).log(
									Level.SEVERE, null, ex);
						}
						// type = "_"+ann.getType();
						boolean apiOK = false;
						int times = 0;
						while (times < RETRY && !apiOK) {
							try {
								apiOK = processWithMeaningCloud(text, type, ann,
										outputAnnSet);
								if (debug)
									Out.println("Nr of retry: " + times
											+ ". Text: " + text);
								times++;
							} catch (InvalidOffsetException ex) {
								Logger.getLogger(
										MeaningCloudSentiment.class.getName())
										.log(Level.SEVERE, null, ex);
							} catch (XPathExpressionException ex) {
								Logger.getLogger(
										MeaningCloudSentiment.class.getName())
										.log(Level.SEVERE, null, ex);
							} catch (UnsupportedEncodingException ex) {
								Logger.getLogger(
										MeaningCloudSentiment.class.getName())
										.log(Level.SEVERE, null, ex);
							} catch (IOException ex) {
								Logger.getLogger(
										MeaningCloudSentiment.class.getName())
										.log(Level.SEVERE, null, ex);
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
		if (inputAnn != null) {
			if (debug)
				Out.println("Processing: " + inputAnn.getType());
		} else {
			if (debug)
				Out.println("Processing the whole document");
		}

		// ParserClient c = new ParserClient();

		String api = this.getapiURL();
		String key = this.getkey();
		String txt = text;

		if (!txt.isEmpty() && !txt.equals("0")) {

			if (debug)
				Out.println("Text: " + txt);

			Post post;
			post = new Post(api);
			post.addParameter("src", "gate_2.4");
			if (this.getkey() != null && !this.getkey().isEmpty())
				post.addParameter("key", key);
			else {
				Logger.getLogger(MeaningCloudSentiment.class.getName()).severe(
						"Key is unset");
				return false;
			}
			post.addParameter("txt", txt);
                        if (this.getlang() != null && !this.getlang().isEmpty())
                                post.addParameter("lang", this.getlang());
                        else {
                                Logger.getLogger(MeaningCloudSentiment.class.getName()).severe(
                                                "Lang is unset");
                                return false;
                        }
			if (this.getmodel() != null && !this.getmodel().isEmpty())
				post.addParameter("model", this.getmodel());
			else {
				Logger.getLogger(MeaningCloudSentiment.class.getName()).severe(
						"Model is unset");
				return false;
			}
			post.addParameter("of", "json");
			if(this.getVerbose() != null && !this.getVerbose().isEmpty())
				post.addParameter("verbose", this.getVerbose());
			if(this.getEgp() != null && !this.getEgp().isEmpty())
				post.addParameter("egp", this.getEgp());
			if(this.getRt() != null && !this.getRt().isEmpty())
				post.addParameter("rt", this.getRt());
			if(this.getUw() != null && !this.getUw().isEmpty())
				post.addParameter("uw", this.getUw());
			if(this.getDm() != null && !this.getDm().isEmpty())
				post.addParameter("dm", this.getDm());
			if(this.getSdg() != null && !this.getSdg().isEmpty())
				post.addParameter("sdg", this.getSdg());
			if(this.getCont() != null && !this.getCont().isEmpty())
				post.addParameter("cont", this.getCont());
			if(this.getCs() != null && !this.getCs().isEmpty())
				post.addParameter("cs", this.getCs());
			if(this.getUd() != null && !this.getUd().isEmpty())
				post.addParameter("ud", this.getUd());

			if (debug)
				Logger.getLogger(MeaningCloudSentiment.class.getName()).info(
						"" + post.params + "");

			byte[] response = post.getResponse().getBytes("UTF-8");
			String resp = new String(response, "UTF-8");
			if (debug)
				Out.println("Response:" + resp);

			SentimentClient sClient = new SentimentClient();
			Serialized_resp sr = sClient.getResponse(resp);
			if (sr.s.code == 0) {
				if (sr.annot_list.size() > 0) {
					for (Serialized_resp.Annot at : sr.annot_list) {
						if (inputAnn != null) {// Inter-sentence offsets are
												// added here to the
												// intra-sentence offsets
												// returned by the API
							outputAnnSet.add(inputAnn.getStartNode()
									.getOffset() + at.inip, inputAnn
									.getStartNode().getOffset() + at.endp,
									"sentiment" + type + "_segment", at.fm);
						} else {
							outputAnnSet.add(at.inip, at.endp, "sentiment"
									+ type + "_segment", at.fm);
						}
					}
				} else {
					Logger.getLogger(MeaningCloudSentiment.class.getName())
							.info("According to the MeaningCloud model you chose, the text you have processed does not contain any sentiment or opinion (no keywords relevant for sentiment analysis have been found).");
				}
			} else {
				Logger.getLogger(MeaningCloudSentiment.class.getName()).severe(
						"API Error" + sr.s.toString() + "\n"
								+ post.params.toString());
			}
		}
		return true;
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
			+ "Type,  \n"
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
	@CreoleParameter(comment = "URL of the API", defaultValue = "https://api.meaningcloud.com/sentiment-2.1")
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
        @CreoleParameter(comment = "Language")
        public void setlang(String lang) {
                this.lang = lang;
        }

        public String getlang() {
                return lang;
        }

        @RunTime
	@CreoleParameter(defaultValue = "general", comment = "Sentiment model chosen.\n"
			+ "The current available models are the following:\n"
			+ "\tgeneral: Generic domain\n")
	public void setmodel(String model) {
		this.model = model;
	}

	public String getmodel() {
		return model;
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


	public String getVerbose() {
		return verbose;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "n", comment = "Verbose mode")
	public void setVerbose(String verbose) {
		this.verbose = verbose;
	}


	public String getEgp() {
		return egp;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "n", comment = "Expand global polarity")
	public void setEgp(String egp) {
		this.egp = egp;
	}

	public String getRt() {
		return rt;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "n", comment = "Relaxed typography")
	public void setRt(String rt) {
		this.rt = rt;
	}

	public String getUw() {
		return uw;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "n", comment = "Deal with unknown words")
	public void setUw(String uw) {
		this.uw = uw;
	}

	public String getDm() {
		return dm;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "s", comment = "Disambiguation level")
	public void setDm(String dm) {
		this.dm = dm;
	}

	public String getSdg() {
		return sdg;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "l", comment = "Semantic disambiguation grouping")
	public void setSdg(String sdg) {
		this.sdg = sdg;
	}

	public String getCont() {
		return cont;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "", comment = "Disambiguation context")
	public void setCont(String cont) {
		this.cont = cont;
	}

	public String getCs() {
		return cs;
	}
	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "n", comment = "Case sensitive concepts")
	public void setCs(String cs) {
		this.cs = cs;
	}

	public String getUd() {
		return ud;
	}

	@RunTime
	@Optional
	@CreoleParameter(defaultValue = "", comment = "User dictionary")
	public void setUd(String ud) {
		this.ud = ud;
	}

} // class MeaningCloudSentiment
