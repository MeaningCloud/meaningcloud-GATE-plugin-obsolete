/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daedalus.textalytics.gate;

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

import daedalus.textalytics.gate.clients.ClassClient;
import daedalus.textalytics.gate.param.ASutil;





/** 
 * This class is the implementation of the resource TEXTALYTICSCLASS.
 */
@CreoleResource(name = "Textalytics Text Classification",
                comment = "Textalytics Text Classification",
                helpURL="http://textalytics.com/core/class-1.1-info")
public class TextalyticsClass  extends AbstractLanguageAnalyser
  implements ProcessingResource {
     private String inputASname, outputASname;
     private List<String> annotationTypes = new ArrayList<String>();
     private String apiURL="", key="", title="";
     private Boolean verbose=false,debug=false;
     private String model="",categories="";
     
public static class category{
    public String code="",label="",abs_relevance="",relevance="";
    public static class term {
        public String form="",abs_relevance="";
    }
    public List<term> term_list = new ArrayList<term>();
}
  
     
public String textTransform(boolean bool){
    String ret = bool ? "y" : "n";
    return ret;    
} 
           
           
public void execute() throws ExecutionException
    {
      
        if(document == null)
          throw new ExecutionException("No document provided");
        
       
       AnnotationSet inputAnnSet = document.getAnnotations(inputASname);
       //AnnotationSet outputAnnSet = document.getAnnotations(outputASname);
       String text = "";
       String type = "";

      DocumentContent content = document.getContent();   
            
      if(inputAnnSet.isEmpty()){
          text+=content.toString();
          type = "_document";
          process(text,type,null,inputAnnSet);
      }else{
          if (annotationTypes.size()==0) {
              Iterator<Annotation> inputIt = gate.Utils.inDocumentOrder(inputAnnSet).iterator();
              
              while(inputIt.hasNext()){
                  Annotation ann = inputIt.next();
                  type = "_"+ann.getType();
                  try {
                      text = content.getContent(ann.getStartNode().getOffset(), ann.getEndNode().getOffset()).toString();    
                  } catch (InvalidOffsetException ex) {
                      Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                  }
                  process(text,type,ann,inputAnnSet);
              }              
          }else{
              if(debug)Out.println("annotationTypes size: "+annotationTypes.size());
              for (String inputAnnExpr : annotationTypes) {
                  if(debug)Out.println("inputAnnExpr: "+inputAnnExpr);
                  AnnotationSet filteredAS = ASutil.getFilteredAS(inputAnnSet,inputAnnExpr);
                  if(debug)Out.println("FilteredAS: "+gate.Utils.cleanStringFor(document, filteredAS));
                  Iterator<Annotation> itr = gate.Utils.inDocumentOrder(filteredAS).iterator();
                  while(itr.hasNext()){
                      Annotation ann = itr.next();
                      type = "_"+ann.getType();
                      try {
                          text = content.getContent(ann.getStartNode().getOffset(), ann.getEndNode().getOffset()).toString();
                      } catch (InvalidOffsetException ex) {
                          Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                      }
                      process(text,type,ann,inputAnnSet);
                  }
              }
          }      
      } 
    }
  
  public void process(String text,String type, Annotation inputAnn, AnnotationSet outputAnnSet){
    if(inputAnn!=null){
      if(debug)Out.println("Processing: "+inputAnn.getType());
    }else{
      if(debug)Out.println("Processing the whole document");
    }
              
      
    ClassClient c = new ClassClient();
      
    String api = this.getapiURL();
    String key = this.getkey();
    String txt = text;
    String mod = "";
    String cat = "";
       
    if(!txt.isEmpty() && !txt.equals("0")){   
       if(debug)Out.println("Text: "+txt);
       
       if(!this.getmodel().isEmpty()){
           mod = this.getmodel();
       }
       if(!this.getcategories().isEmpty()){
           cat = this.getcategories();
       }

       Post post;
         try {
            post = new Post (api);
            post.addParameter("key", key);
            post.addParameter("txt", txt);
            post.addParameter("of", "xml");           
            post.addParameter("verbose",textTransform(this.verbose));
            post.addParameter("model", mod);
            if(!this.getcategories().isEmpty())
                post.addParameter("categories",cat);
            if(!this.gettitle().isEmpty())
                post.addParameter("title", this.gettitle());

            
            byte[] response = post.getResponse().getBytes("UTF-8");
            String resp = new String(response,"UTF-8");
            if(debug)Out.println("Response:"+resp);
          
      // Show topics
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder;
         try {
             docBuilder = docBuilderFactory.newDocumentBuilder();
             org.w3c.dom.Document doc = docBuilder.parse(new ByteArrayInputStream(response));
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
            try{
                List<TextalyticsClass.category> updated = ClassClient.collectInfo(response_node);
                setDocFeatures(updated,type, inputAnn/*,outputAnnSet*/);
          }catch (Exception e){
              Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, e);
          }
        }
      } catch (Exception e) {
        Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, e);
        Logger.getLogger(TextalyticsClass.class.getName()).severe("Not found");
      }
             
         } catch (ParserConfigurationException ex) {
             Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
         }  catch (SAXException ex) {
                Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
            }
     
         } catch (UnsupportedEncodingException ex) {
             Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
             Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
  }
  
  public void setDocFeatures(List<TextalyticsClass.category> category_list,String type, Annotation inputAnn/*,AnnotationSet outputAnnSet*/) throws InvalidOffsetException, UnsupportedEncodingException{
      if(category_list.size()>0){
	  Iterator<category> it = category_list.iterator();
      int cat_count = 0;
      FeatureMap fm = Factory.newFeatureMap();
      String category_code = "";
      String category_label = "";
      String category_relevance = "";
      String category_abs_relevance = "";
      String category_term_form = "";
      String category_term_abs_relevance = "";
      while(it.hasNext()){
    	  TextalyticsClass.category cat = it.next();
          if(cat_count == 0){
        	  category_code += new String(cat.code.getBytes(),"utf-8");
        	  category_label += cat.label;//new String(cat.label.getBytes(),"utf-8");
        	  category_relevance += new String(cat.relevance.getBytes(),"utf-8");
        	  category_abs_relevance += new String(cat.abs_relevance.getBytes(),"utf-8");
        	  if(cat.term_list.size()>0){
        		  //System.out.println("terms available"+gate.Utils.cleanStringFor(document, inputAnn));
        		  Iterator<TextalyticsClass.category.term> it2 = cat.term_list.iterator();
        		  int term_count = 0;
              		while(it2.hasNext()){
              			TextalyticsClass.category.term t = it2.next();
              			if(term_count == 0){
              				category_term_form += t.form.isEmpty() ? " " : new String(t.form.getBytes(),"utf-8"); 
              				category_term_abs_relevance += t.abs_relevance.isEmpty() ? " " : new String(t.abs_relevance.getBytes(),"utf-8");
              			}else{
              				category_term_form += "|"+new String(t.form.getBytes(),"utf-8"); 
              				category_term_abs_relevance += "|"+new String(t.abs_relevance.getBytes(),"utf-8");
              			}
              			term_count++;
              		}
        	  }
          }else{
        	  category_code += ";"+new String(cat.code.getBytes(),"utf-8");
        	  category_label += ";"+cat.label;//new String(cat.label.getBytes(),"utf-8");
        	  category_relevance += ";"+new String(cat.relevance.getBytes(),"utf-8");
        	  category_abs_relevance += ";"+new String(cat.abs_relevance.getBytes(),"utf-8");
        	  if(cat.term_list.size()>0){
        		//System.out.println("terms available"+gate.Utils.cleanStringFor(document, inputAnn));
        		Iterator<TextalyticsClass.category.term> it2 = cat.term_list.iterator();
        		int term_count = 0;
        		while(it2.hasNext()){
                  TextalyticsClass.category.term t = it2.next();
                  if(term_count == 0){
                	  category_term_form += ";"+(t.form.isEmpty() ? " " : new String(t.form.getBytes(),"utf-8")); 
                	  category_term_abs_relevance += ";"+(t.abs_relevance.isEmpty() ? " " : new String(t.abs_relevance.getBytes(),"utf-8"));
                  }else{
                	  category_term_form += "|"+new String(t.form.getBytes(),"utf-8"); 
                	  category_term_abs_relevance += "|"+new String(t.abs_relevance.getBytes(),"utf-8");
                  }
                  term_count++;
        		}
        	  }else{
        		  category_term_form += ";";
        		  category_term_abs_relevance += ";";
        	  }
          }          
          cat_count++;
      }
      fm.put("category_code", category_code);
      fm.put("category_label", category_label);
      fm.put("category_relevance", category_relevance);
      fm.put("category_abs_relevance", category_abs_relevance);
      fm.put("category_term_form", category_term_form);
      fm.put("category_term_abs_relevance", category_term_abs_relevance);
      if(inputAnn != null) {  
      	FeatureMap fm2 = inputAnn.getFeatures();
      	fm2.putAll(fm);
      } else {
      	FeatureMap fm2 = document.getFeatures();
      	fm2.putAll(fm);
      }
      }
  } 

   @RunTime
   @CreoleParameter(comment = "URL Of the API to query", defaultValue="http://textalytics.com/core/class-1.1")
   public void setapiURL(String apiURL)
    {
	this.apiURL = apiURL;
    }
   public String getapiURL()
    {
	return apiURL;
    } 
    
    @RunTime
    @CreoleParameter(comment = "License Key")
    public void setkey(String key)
    {
	this.key = key;
    }
    public String getkey()
    {
	return key;
    }
    
    @RunTime
    @CreoleParameter(comment = "Classification model to use. It will define into which categories the text may be classified.")
    public void setmodel(String m)
    {
	this.model = m;
    }
    public String getmodel()
    {
	return model;
    }
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "List of prefixes of categories to which the classification is limited. Each value will be separated by '|'. All the categories that do not start with any of the prefixes specified in the list will not be taken account in the classification.")
    public void setcategories(String categories)
    {
	this.categories = categories;
    }   
    
    public String getcategories()
    {
	return categories;
    }   

    @RunTime
    @Optional
    @CreoleParameter(comment = "Descriptive title of the content")
    public void settitle(String t)
    {
	this.title = t;
    }
    
    public String gettitle()
    {
	return title;
    }   
    
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "AnnotationSet with the input content")
    public void setinputASname(String t)
    {
	this.inputASname = t;
    }
    public String getinputASname()
    {
	return inputASname;
    }   
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Output Annotation Set", defaultValue="Textalytics") 
    public void setoutputASname(String t)
    {
	this.outputASname = t;
    }     
    public String getoutputASname()
    {
	return outputASname;
    }
   
    @RunTime
    @Optional
    @CreoleParameter(comment = "Verbose mode. Shows additional information about the classification.")
    public void setverbose(Boolean verb){
        this.verbose = verb;
    }
    public Boolean getverbose(){
        return verbose;
    }
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Debug variable for the GATE plugin")
    public void setdebug(Boolean verb){
        this.debug = verb;
    }    
    public Boolean getdebug(){
        return debug;
    }
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Filter content by this expression. It allows format: \n"+
            "Type.FeatureName  \n"+
            "or  \n"+
            "Type.FeatureName==FeatureValue  \n")
    public void setannotationTypes(List<String> iat)
    {
	this.annotationTypes = iat;
    }
    
    public List<String> getannotationTypes()
    {
	return annotationTypes;
    }

    
    
} // class TextalyticsClass
