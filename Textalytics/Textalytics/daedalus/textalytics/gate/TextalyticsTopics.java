/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daedalus.textalytics.gate;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import daedalus.textalytics.gate.clients.TopicsClient;
import daedalus.textalytics.gate.param.ASutil;
import daedalus.textalytics.gate.param.DisambiguationLevel;


/** 
 * This class is the implementation of the resource TOPICSTEXTALYTICS.
 */
@CreoleResource(name = "Textalytics Topics Extraction",
                comment = "Textalytics Topics Extraction",
                helpURL="http://textalytics.com/core/topics-info")
public class TextalyticsTopics  extends AbstractLanguageAnalyser
  implements ProcessingResource, Serializable {

     private String inputASname, outputASname, apiURL="", key, lang="en", topicTypes="a",timeref;
     private List<String> inputASTypes = new ArrayList<String>(); // list of input annotations from which string content will be submitted
     private Boolean unknownWords=false,relaxedTypography=true,subTopics=false, caseSensitive=false,debug=false;
     private String dictionary="chetsdpCA";//, userDictionary="";
     private DisambiguationLevel disambiguationLevel;
     private String context="";
     private List<String> udDictionaries= new ArrayList<String>();
     private static final int RETRY = 5; 
   
     
     public static class Offset{
         Long start,end;
     }
     
     public String translateDM(DisambiguationLevel dm){
         String ret_dm="";
        if(dm.equals(DisambiguationLevel.no_disambiguation))
            ret_dm="0";
        else if(dm.equals(DisambiguationLevel.morphsyntactic_disambiguation))
            ret_dm="1";
        else if(dm.equals(DisambiguationLevel.basic_disambiguation))
            ret_dm="2";
        else if(dm.equals(DisambiguationLevel.light_disambiguation))
            ret_dm="3";
        else if(dm.equals(DisambiguationLevel.strong_disambiguation))
            ret_dm = "4";
        else if(dm.equals(DisambiguationLevel.full_disambiguation))
            ret_dm="5";
        else{
            ret_dm = "4";
        }

        return ret_dm;
     }
     
    public String textTransform(boolean bool){
    String ret = bool ? "y" : "n";
    return ret;    
    } 
    
  @Override  
   public void execute() throws ExecutionException
    {
        if(document == null)
            throw new ExecutionException("No document provided");
        
       
       AnnotationSet inputAnnSet = document.getAnnotations(inputASname);
       AnnotationSet outputAnnSet = document.getAnnotations(outputASname);
       String text = "";
       String type = "";

      DocumentContent content = document.getContent();   
      
      if(getudDictionaries().size()==0){
          List<String> uds = new ArrayList<String>();
          uds.add("");
          setudDictionaries(uds);
      }
            
      if(inputAnnSet.isEmpty()){
          text+=content.toString();
          //type = "_document";
          Iterator<String> udIt = this.getudDictionaries().iterator();
          while(udIt.hasNext()){
              String ud = udIt.next();
              boolean apiOK = false;
              int times = 0;
              while(times<RETRY && !apiOK ){
                  apiOK = processWithTextalytics(text,type,null,outputAnnSet,ud);
                  if(debug)Out.println("Nr of retry: "+times+". Text: "+text);
                  times++;
              }
              times = 0;
          }
      }else{
          if (inputASTypes.size()==0) {
              Iterator<Annotation> inputIt = gate.Utils.inDocumentOrder(inputAnnSet).iterator();
              
              while(inputIt.hasNext()){
                  Annotation ann = inputIt.next();
                  //type = "_"+ann.getType();
                  try {
                      text = content.getContent(ann.getStartNode().getOffset(), ann.getEndNode().getOffset()).toString();    
                  } catch (InvalidOffsetException ex) {
                      Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                  }
                  Iterator<String> udIt = this.getudDictionaries().iterator();
                  while(udIt.hasNext()){
                      String ud = udIt.next();
                      boolean apiOK = false;
                      int times = 0;
                      while(times<RETRY && !apiOK ){
                          apiOK = processWithTextalytics(text,type,ann,outputAnnSet,ud);
                          if(debug)Out.println("Nr of retry: "+times+". Text: "+text);
                          times++;
                      }
                      times = 0;
                  }
              }              
          }else{
              if(debug)Out.println("inputASTypes size: "+inputASTypes.size());
              for (String inputAnnExpr : inputASTypes) {
                  if(debug)Out.println("inputAnnExpr: "+inputAnnExpr);
                  AnnotationSet filteredAS = ASutil.getFilteredAS(inputAnnSet,inputAnnExpr);
                  if(debug){
                	  if(!filteredAS.isEmpty())
                		  Out.println("FilteredAS: "+gate.Utils.cleanStringFor(document, filteredAS));
                  }
                  Iterator<Annotation> itr = gate.Utils.inDocumentOrder(filteredAS).iterator();
                  while(itr.hasNext()){
                      Annotation ann = itr.next();
                      //type = "_"+ann.getType();
                      try {
                          text = content.getContent(ann.getStartNode().getOffset(), ann.getEndNode().getOffset()).toString();
                      } catch (InvalidOffsetException ex) {
                          Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                      }
                      Iterator<String> udIt = this.getudDictionaries().iterator();
                      while(udIt.hasNext()){
                          String ud = udIt.next();
                          boolean apiOK = false;
                          int times = 0;
                          while(times<RETRY && !apiOK ){
                              apiOK = processWithTextalytics(text,type,ann,outputAnnSet,ud);
                              if(debug)Out.println("Nr of retry: "+times+". Text: "+text);
                              times++;
                          }
                          times = 0;
                      }
                  }
              }
          }      
      } 
    }

    public boolean processWithTextalytics(String text,String type,Annotation inputAnn,AnnotationSet outputAnnSet, String ud){
       String api = this.getapiURL();
      
       if(!text.isEmpty() && !text.equals("0")){
       //AnnotationSet outputAnnSet = (outputASname == null || outputASname.trim().length() == 0) ? document.getAnnotations() : document.getAnnotations(outputASname);
       String key = this.getkey();
       String txt = text;
       String lang = this.getlang();
       String tt = this.gettopicTypes();
       String dm = translateDM(this.getDisambiguationLevel());
       
       String dic = "";
       dic = this.getdictionary();//ToDo: lang as enum, and switch default dic if not set by the end-user
       
       Boolean uw  = this.getunknownWords();
       Boolean rt = this.getrelaxedTypography();
       Boolean st = this.getsubTopics();
       Boolean cs = this.getcaseSensitive();
       String timeref = this.timeref == null ? "" : this.gettimeref();
       
       Post post;
      
         try {
            post = new Post (api);
            post.addParameter("key", key);
            post.addParameter("txt", txt);
            post.addParameter("lang", lang);
            post.addParameter("tt",tt);
            post.addParameter("of", "xml");
            post.addParameter("uw",textTransform(uw));
            post.addParameter("rt",textTransform(rt));
            post.addParameter("st",textTransform(st));
            post.addParameter("cs",textTransform(cs));
            post.addParameter("ud", ud);
            post.addParameter("dm", dm);
            if((!this.getcontext().isEmpty())&&(this.getcontext() != ""))
                post.addParameter("cont",this.getcontext());
            if(dic != "" && dic != null)
                post.addParameter("dic", dic);
            if(timeref != "" && timeref != null)
                post.addParameter("timeref", timeref);
            
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            if(debug)Logger.getLogger(TextalyticsTopics.class.getName()).info(""+post.params+"");
            
            byte[] resp = post.getResponse().getBytes("UTF-8");
            String response = new String(resp);

            // Show topics
            
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;
            try {
             docBuilder = docBuilderFactory.newDocumentBuilder();

             org.w3c.dom.Document doc =  docBuilder.parse(new ByteArrayInputStream(resp));
             doc.normalizeDocument();

             Element response_node = doc.getDocumentElement();
             response_node.normalize();
             try {
                 NodeList statusL = response_node.getElementsByTagName("status");
                 org.w3c.dom.Node status = statusL.item(0);
                 NamedNodeMap attributes = status.getAttributes();
                 org.w3c.dom.Node code = attributes.item(0);
                 if(!code.getTextContent().equals("0")) {
                     Out.println("Not found");
                     Logger.getLogger(TextalyticsTopics.class.getName()).severe("API Error: "+code.getTextContent()+""+post.params.toString());
                     return false;
                 } else {   
                     try{
                         TopicsClient.Recursive hack = new TopicsClient.Recursive();
                         List<TopicsClient.Annot> annotations = new ArrayList<TopicsClient.Annot>();
                         hack = TopicsClient.collectInfo(response_node,"entity",ud);
                         annotations = hack.hack_ann_list;
                         //Out.println("Number of annotations: " + annotations.size());
                                for (TopicsClient.Annot at : annotations) {
                                    if(inputAnn != null){
                                        outputAnnSet.add(inputAnn.getStartNode().getOffset()+at.startOff, inputAnn.getStartNode().getOffset()+at.endOff, ""+type+""+at.Name, at.fm);
                                    }else{
                                        outputAnnSet.add(at.startOff, at.endOff, ""+type+""+at.Name, at.fm);
                                    }
                                }
                     }catch (Exception ex){
                          Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                     } 
                     try{
                         TopicsClient.Recursive hack = new TopicsClient.Recursive();
                         List<TopicsClient.Annot> annotations = new ArrayList<TopicsClient.Annot>();
                         //Out.println("inputAnnSet of the concepts "+inputAnnSet.toString());
                         hack = TopicsClient.collectInfo(response_node,"concept",ud);
                         annotations = hack.hack_ann_list;
                         //Out.println("Number of annotations: " + annotations.size());
                                for (TopicsClient.Annot at : annotations) {
                                    if(inputAnn != null){//Inter-annotation offsets are added to intra-annotation offsets.
                                        outputAnnSet.add(inputAnn.getStartNode().getOffset()+at.startOff, inputAnn.getStartNode().getOffset()+at.endOff, ""+type+""+at.Name, at.fm);
                                    }else{
                                        outputAnnSet.add(at.startOff, at.endOff, ""+type+""+at.Name, at.fm);
                                    }
                                }
                     }catch (Exception ex){
                          Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                     }
                     try{
                         List<TopicsClient.Annot> annotations = TopicsClient.collectShort(response_node,"time_expression");;
                         
                         for (TopicsClient.Annot at : annotations) {
                             if(inputAnn != null){
                                        outputAnnSet.add(inputAnn.getStartNode().getOffset()+at.startOff, inputAnn.getStartNode().getOffset()+at.endOff, ""+type+""+at.Name, at.fm);
                             }else{
                                        outputAnnSet.add(at.startOff, at.endOff, ""+type+""+at.Name, at.fm);
                             }
                         }
                     }catch (Exception ex){
                         Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                     }
                     try{
                         List<TopicsClient.Annot> annotations = TopicsClient.collectShort(response_node,"money_expression");
                          
                         for (TopicsClient.Annot at : annotations) {
                             if(inputAnn != null){
                                 outputAnnSet.add(inputAnn.getStartNode().getOffset()+at.startOff, inputAnn.getStartNode().getOffset()+at.endOff, ""+type+""+at.Name, at.fm);
                             }else{
                                 outputAnnSet.add(at.startOff, at.endOff, ""+type+""+at.Name, at.fm);
                             }
                         }
                     }catch (Exception ex){
                          Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                     }                         
                     try{
                         List<TopicsClient.Annot> annotations = TopicsClient.collectShort(response_node,"uri");
                          
                         for (TopicsClient.Annot at : annotations) {
                             if(inputAnn != null){
                                 outputAnnSet.add(inputAnn.getStartNode().getOffset()+at.startOff, inputAnn.getStartNode().getOffset()+at.endOff, ""+type+""+at.Name, at.fm);
                             }else{
                                 outputAnnSet.add(at.startOff, at.endOff, ""+type+""+at.Name, at.fm);
                             }
                                }
                     }catch (Exception ex){
                         Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                     }
                     try{
                         List<TopicsClient.Annot> annotations = TopicsClient.collectShort(response_node,"phone_expression");;
                          
                         for (TopicsClient.Annot at : annotations) {
                            if(inputAnn != null){
                                 outputAnnSet.add(inputAnn.getStartNode().getOffset()+at.startOff, inputAnn.getStartNode().getOffset()+at.endOff, ""+type+""+at.Name, at.fm);
                             }else{
                                 outputAnnSet.add(at.startOff, at.endOff, ""+type+""+at.Name, at.fm);
                             }
                         }
                     }catch (Exception ex){
                          Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                     }                         
                     try{
                         List<TopicsClient.Annot> annotations = TopicsClient.collectShort(response_node,"other_expression");
                         
                         for (TopicsClient.Annot at : annotations) {
                             if(inputAnn != null){
                                 outputAnnSet.add(inputAnn.getStartNode().getOffset()+at.startOff, inputAnn.getStartNode().getOffset()+at.endOff, ""+type+""+at.Name, at.fm);
                             }else{
                                 outputAnnSet.add(at.startOff, at.endOff, ""+type+""+at.Name, at.fm);
                             }
                         }
                     }catch (Exception ex){
                          Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                     }                         
                     try{
                         List<TopicsClient.Annot> annotations = TopicsClient.collectShort(response_node,"quotation");
                         
                         for (TopicsClient.Annot at : annotations) {
                            if(inputAnn != null){
                                 outputAnnSet.add(inputAnn.getStartNode().getOffset()+at.startOff, inputAnn.getStartNode().getOffset()+at.endOff, ""+type+""+at.Name, at.fm);
                             }else{
                                 outputAnnSet.add(at.startOff, at.endOff, ""+type+""+at.Name, at.fm);
                             }
                         }
                     }catch (Exception ex){
                          Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                     }                         
                 }
             } catch (Exception ex) {
                 Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                 System.out.println("Not found");
             } 
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(TextalyticsTopics.class.getName()).log(Level.SEVERE, null, ex);
            }  catch (SAXException ex) {
                Logger.getLogger(TextalyticsTopics.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TextalyticsTopics.class.getName()).log(Level.SEVERE, null, ex);
            } 
         } catch (MalformedURLException ex) {
             Logger.getLogger(TextalyticsTopics.class.getName()).log(Level.SEVERE, null, ex);
         } catch (UnsupportedEncodingException ex) {
             Logger.getLogger(TextalyticsTopics.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
             Logger.getLogger(TextalyticsTopics.class.getName()).log(Level.SEVERE, null, ex);
         }   
         //return true;
       }
       return true;
    }
    
    @Optional
    @RunTime
    @CreoleParameter(comment = "Filter content by this expression. It allows format: \n"+
            "Type.FeatureName  \n"+
            "or  \n"+
            "Type.FeatureName==FeatureValue  \n")
    public void setInputASTypes(List<String> inputASTypes) {
        this.inputASTypes = inputASTypes;
    }

    public List<String> getInputASTypes() {
        return inputASTypes;
    }
       
    @Optional
    @RunTime
    @CreoleParameter(comment = "Name of the User defined Dictionaries to be used")
    public void setudDictionaries(List<String> ud) {
        this.udDictionaries = ud;
    }

    public List<String> getudDictionaries() {
        return udDictionaries;
    }
    
    
    @RunTime
    @CreoleParameter(defaultValue = "strong_disambiguation",
    comment = "Disambiguation level applied.")
    public void setDisambiguationLevel(DisambiguationLevel ds) {
        this.disambiguationLevel = ds;
    }

    public DisambiguationLevel getDisambiguationLevel() {
        return disambiguationLevel;
    }
    
    @Optional
    @RunTime
    @CreoleParameter(comment = "Context prioritization for entity semantic disambiguation.")
    public void setcontext(String c) {
        this.context = c;
    }

    public String getcontext() {
        return context;
    }

    @RunTime
    @Optional
    @CreoleParameter(comment = "This feature adds a stage to the topic extraction in which the engine, much like a spellchecker, tries to find a suitable analysis to the unknown words resulted from the initial analysis assignment. It is specially useful to decrease the impact typos have in text analyses.\n" +
"\n" +
"y: enabled\n" +
"n: disabled (default)")  
    public void setunknownWords(Boolean uw){
        this.unknownWords = uw;
    }
    public Boolean getunknownWords(){
        return unknownWords;
    }
    
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "This paramater indicates how reliable the text (as far as spelling, typography, etc. are concerned) to analyze is, and influences how strict the engine will be when it comes to take these factors into account in the topic extraction.\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")
    public void setrelaxedTypography(Boolean rt){
        this.relaxedTypography = rt;
    }
    public Boolean getrelaxedTypography(){
        return relaxedTypography;
    }
        

    @RunTime
    @Optional
    @CreoleParameter(comment = "Indicates if the concept extraction must be case sensitive.")

    public void setcaseSensitive(Boolean cs){
        this.caseSensitive = cs;
    }
    public Boolean getcaseSensitive(){
        return caseSensitive;
    }

    @RunTime
    @Optional
    @CreoleParameter(comment = "This parameter will indicate if subtopics are to be shown.")
    public void setsubTopics(Boolean st){
        this.subTopics = st;
    }
    public Boolean getsubTopics(){
        return subTopics;
    }

    @RunTime
    @Optional
    @CreoleParameter(comment = "Input Annotation Set")
    public void setinputASname(String inputASname){
	this.inputASname = inputASname;
    }
     public String getinputASname(){
	return inputASname;
    }

    @RunTime
    @Optional
    @CreoleParameter(comment = "Output Annotation Set", defaultValue="Textalytics") 
    public void setoutputASname(String outputASname){
        this.outputASname = outputASname;
    }
    public String getoutputASname(){
	return outputASname;
    }

    @RunTime
    @CreoleParameter(comment = "URL of the API", defaultValue="http://textalytics.com/core/topics-1.2") 
    public void setapiURL(String apiURL){
	this.apiURL = apiURL;
    }
    public String getapiURL(){
	return apiURL;
    }   

    @RunTime
    @CreoleParameter(comment = "License key") 
    public void setkey(String key){
	this.key = key;
    }
    public String getkey(){
	return key;
    }   

    @RunTime
    @CreoleParameter(comment = "It specifies the language in which the text must be analyzed.")
    public void setlang(String lang){
	this.lang = lang;
    }
    public String getlang(){
	return lang;
    }

    @RunTime
    @Optional
    @CreoleParameter(comment = "Topic Types to detect")
    public void settopicTypes(String tt){
	this.topicTypes = tt;
    }
    public String gettopicTypes(){
	return topicTypes;
    }

    @RunTime
    @Optional
    @CreoleParameter(comment = "Time reference")
    public void settimeref(String timeref){
	this.timeref = timeref;
    }
    public String gettimeref(){
	return timeref;
    }

    @RunTime
    @Optional
    @CreoleParameter(comment = "Dictionary")
    public void setdictionary(String dic){
	this.dictionary = dic;
    }
    public String getdictionary(){
	return dictionary;
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
    
} // class TopicsTextalytics
