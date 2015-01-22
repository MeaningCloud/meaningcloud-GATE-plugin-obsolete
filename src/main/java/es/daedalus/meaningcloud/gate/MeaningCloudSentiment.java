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

import es.daedalus.meaningcloud.gate.clients.SentimentClient;
import es.daedalus.meaningcloud.gate.param.ASutil;
import es.daedalus.meaningcloud.gate.param.SentimentBean;
import es.daedalus.meaningcloud.gate.param.Serialized_resp;

/**
 * This class is the implementation of the resource MeaningCloud Sentiment Analysis.
 */
@CreoleResource(name = "MeaningCloud Sentiment Analysis", comment = "MeaningCloud Sentiment Analysis", helpURL = "http://api.meaningcloud.com/sentiment-info")
public class MeaningCloudSentiment extends AbstractLanguageAnalyser implements
		ProcessingResource {
	private String inputASname, outputASname, apiURL, key, model, entities,
			concepts;
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
			post.addParameter("src", "gate_1.0");
			if (this.getkey() != null && !this.getkey().isEmpty())
				post.addParameter("key", key);
			else {
				Logger.getLogger(MeaningCloudSentiment.class.getName()).severe(
						"Key is unset");
				return false;
			}
			post.addParameter("txt", txt);
			if (this.getmodel() != null && !this.getmodel().isEmpty())
				post.addParameter("model", this.getmodel());
			else {
				Logger.getLogger(MeaningCloudSentiment.class.getName()).severe(
						"Model is unset");
				return false;
			}
			post.addParameter("of", "json");
			if (this.getentities() != null)
				post.addParameter("entities", this.getentities());
			if (this.getConcepts() != null)
				post.addParameter("concepts", this.getConcepts());

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
	@CreoleParameter(comment = "URL of the API", defaultValue = "http://api.meaningcloud.com/sentiment-1.2")
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
	@CreoleParameter(defaultValue = "en-general", comment = "Sentiment model chosen. If no model is specified, the most adequate one will be detected automatically, based on the language of the text.\n"
			+ "The current available models are the following:\n"
			+ "\tes-general: Generic domain (Spanish)\n"
			+ "\ten-general: Generic domain (English)\n"
			+ "\tfr-general: Generic domain (French)\n")
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

	@RunTime
	@Optional
	@CreoleParameter(comment = "Entities")
	public void setEntities(String entities) {
		this.entities = entities;
	}

	public String getentities() {
		return entities;
	}

	@RunTime
	@Optional
	@CreoleParameter(comment = "Concepts")
	public void setConcepts(String concepts) {
		this.concepts = concepts;
	}

	public String getConcepts() {
		return concepts;
	}

} // class MeaningCloudSentiment
