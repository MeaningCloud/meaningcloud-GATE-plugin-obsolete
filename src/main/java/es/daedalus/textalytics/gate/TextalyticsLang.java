package es.daedalus.textalytics.gate;

import gate.Annotation;
import gate.AnnotationSet;
import gate.DocumentContent;
import gate.Factory;
import gate.FeatureMap;
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

import es.daedalus.textalytics.gate.clients.LangClient;
import es.daedalus.textalytics.gate.param.ASutil;

/**
 * This class is the implementation of the resource TEXTALYTICSLANG.
 */
@CreoleResource(name = "Textalytics Language Identification", comment = "Textalytics Language Identification", helpURL = "http://textalytics.com/core/lang-info")
public class TextalyticsLang extends AbstractLanguageAnalyser implements
  ProcessingResource {
  private String apiURL, key;

  private String inputASname, outputASname;

  private List<String> annotationTypes = new ArrayList<String>();

  private boolean debug;

  public void execute() throws ExecutionException {

    if(document == null) 
      throw new ExecutionException("No Document Provided");
    
    if (key == null || key.trim().isEmpty())
      throw new ExecutionException("No API Key Provided");
    
    if (apiURL == null || apiURL.trim().isEmpty())
      throw new ExecutionException("Service URL Has Not Been Set");

    AnnotationSet inputAnnSet = document.getAnnotations(inputASname);
    
    String text = "";

    DocumentContent content = document.getContent();

    if(inputAnnSet.isEmpty()) {
      text += content.toString();
      process(text, null);
    } else {
      if(annotationTypes.size() == 0) {
    	  text += content.toString();
          process(text, null);
        /*Iterator<Annotation> inputIt =
          gate.Utils.inDocumentOrder(inputAnnSet).iterator();

        while(inputIt.hasNext()) {
          Annotation ann = inputIt.next();
          try {
            text =
              content.getContent(ann.getStartNode().getOffset(),
                ann.getEndNode().getOffset()).toString();
          } catch(InvalidOffsetException ex) {
            Logger.getLogger(TextalyticsClass.class.getName()).log(
              Level.SEVERE, null, ex);
          }
          process(text, ann);
        }*/
      } else {
        if(debug) Out.println("annotationTypes size: " + annotationTypes.size());
        for(String inputAnnExpr : annotationTypes) {
          if(debug) Out.println("inputAnnExpr: " + inputAnnExpr);
          AnnotationSet filteredAS =
            ASutil.getFilteredAS(inputAnnSet, inputAnnExpr);
          if(debug)
            Out.println("FilteredAS: " +
              gate.Utils.cleanStringFor(document, filteredAS));
          Iterator<Annotation> itr =
            gate.Utils.inDocumentOrder(filteredAS).iterator();
          while(itr.hasNext()) {
            Annotation ann = itr.next();
            try {
              text =
                content.getContent(ann.getStartNode().getOffset(),
                  ann.getEndNode().getOffset()).toString();
            } catch(InvalidOffsetException ex) {
              Logger.getLogger(TextalyticsClass.class.getName()).log(
                Level.SEVERE, null, ex);
            }
            process(text, ann);
          }
        }
      }
    }
  }

  public void process(String text, Annotation inputAnn) {
    String type = "";
	  
    if(inputAnn != null) {
      type = inputAnn.getType();
      if(debug) Out.println("Processing: " + inputAnn.getType());
    } else {
      if(debug) Out.println("Processing the whole document");
    }

    //LangClient c = new LangClient();

    String api = this.getApiURL();
    String key = this.getKey();
    String txt = text;

    if(!txt.isEmpty() && !txt.equals("0")) {
      if(debug) Out.println("Text: " + txt);

      Post post;
      try {
        post = new Post(api);
        if(key!=null && !key.isEmpty())
        	post.addParameter("key", key);
        else{
        	Logger.getLogger(TextalyticsTopics.class.getName()).severe("Key is not set");
        	return;
        }
				post.addParameter("src","gate_1.0_lang");
        post.addParameter("txt", txt);
        post.addParameter("of", "xml");

        byte[] response = post.getResponse().getBytes("UTF-8");
        String resp = new String(response, "UTF-8");
        if(debug) Out.println("Response:" + resp);

        // Show topics
        DocumentBuilderFactory docBuilderFactory =
          DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
          docBuilder = docBuilderFactory.newDocumentBuilder();
          org.w3c.dom.Document doc =
            docBuilder.parse(new ByteArrayInputStream(response));
          doc.getDocumentElement().normalize();
          Element response_node = doc.getDocumentElement();
          try {
            NodeList statusL = response_node.getElementsByTagName("status");
            org.w3c.dom.Node status = statusL.item(0);
            NamedNodeMap attributes = status.getAttributes();
            org.w3c.dom.Node code = attributes.item(0);
            if(!code.getTextContent().equals("0")) {
            	Logger.getLogger(TextalyticsTopics.class.getName()).severe("API Error: "+code.getTextContent()+""+post.params.toString());
            } else {
              try {
                List<String> updated = LangClient.collectInfo(response_node);
                setDocFeatures(updated, type, inputAnn);
              } catch(Exception e) {
                Logger.getLogger(TextalyticsClass.class.getName()).log(
                  Level.SEVERE, null, e);
              }
            }
          } catch(Exception e) {
            Logger.getLogger(TextalyticsClass.class.getName()).log(
              Level.SEVERE, null, e);
            Logger.getLogger(TextalyticsClass.class.getName()).severe(
              "Not found");
          }

        } catch(ParserConfigurationException ex) {
          Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE,
            null, ex);
        } catch(SAXException ex) {
          Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE,
            null, ex);
        } catch(IOException ex) {
          Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE,
            null, ex);
        }

      } catch(UnsupportedEncodingException ex) {
        Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE,
          null, ex);
      } catch(IOException ex) {
        Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE,
          null, ex);
      }
    }
  }

  public void setDocFeatures(List<String> lang_list, String type,
                             Annotation inputAnn)
    throws InvalidOffsetException, UnsupportedEncodingException {
	  if(lang_list.size()>0){
		  Iterator<String> it = lang_list.iterator();
		  FeatureMap fm = Factory.newFeatureMap();
		  List<String> lang = new ArrayList<String>();
		  while(it.hasNext()) {
			  lang.add(new String(it.next().getBytes(),"utf-8"));
		  }
		  fm.put("lang", lang);
		  if(inputAnn != null) {
			  Logger.getLogger(TextalyticsClass.class.getName()).info("The text you have processed is written in "+fm.get("lang")+". The annotation was created as a new Feature of your inputAS");
			  FeatureMap fm2 = inputAnn.getFeatures();
			  fm2.putAll(fm);
		  } else {
			  Logger.getLogger(TextalyticsClass.class.getName()).info("The text you have processed is written in "+fm.get("lang")+". The annotation was created as a Document Feature");
			  FeatureMap fm2 = document.getFeatures();
			  fm2.putAll(fm);
		  }
	  }
  }

  @RunTime
  @CreoleParameter(comment = "URL Of the API to query", defaultValue = "http://textalytics.com/core/lang-1.1")
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
  @CreoleParameter(comment = "AnnotationSet with the input content")
  public void setInputASName(String t) {
    this.inputASname = t;
  }

  public String getInputASName() {
    return inputASname;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "Output Annotation Set", defaultValue = "Textalytics")
  public void setOutputASName(String t) {
    this.outputASname = t;
  }

  public String getOutputASName() {
    return outputASname;
  }

  @RunTime
  @Optional
  @CreoleParameter(defaultValue = "false",comment = "Debug variable for the GATE plugin")
  public void setDebug(Boolean verb) {
    this.debug = verb;
  }

  public Boolean getDebug() {
    return debug;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "Filter content by this expression. It allows format: \n"
    + "Type.FeatureName  \n" + "or  \n" + "Type.FeatureName==FeatureValue  \n")
  public void setannotationTypes(List<String> iat) {
    this.annotationTypes = iat;
  }

  public List<String> getannotationTypes() {
    return annotationTypes;
  }
}
