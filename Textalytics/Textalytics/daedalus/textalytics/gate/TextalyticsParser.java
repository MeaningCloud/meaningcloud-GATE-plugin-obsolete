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

import daedalus.textalytics.gate.clients.ParserClient;
import daedalus.textalytics.gate.clients.ParserClient.Annot;
import daedalus.textalytics.gate.param.ASutil;
import daedalus.textalytics.gate.param.DisambiguationLevel;
import daedalus.textalytics.gate.param.Serialized_resp;
import daedalus.textalytics.gate.param.TokenBean;

/** 
 * This class is the implementation of the resource POSTAGGINGTEXTALYTICS.
 */
@CreoleResource(name = "Textalytics Lemmatization, PoS and Parsing",
                comment = "Textalytics Lemmatization, PoS and Parsing",
                helpURL="http://textalytics.com/core/parser-info")
public class TextalyticsParser  extends AbstractLanguageAnalyser
  implements ProcessingResource {
    
     private String inputASname, outputASname, apiURL, key, lang="",ud="";
     private Boolean unknownWords=false,relaxedTypography=true,debug=false;     
     private List<String> inputASTypes = new ArrayList<String>(); // list of input annotations from which string content will be submitted     
     private String dictionary="";
     private DisambiguationLevel disambiguationLevel;
     private static final int RETRY = 10; 
     
    public String textTransform(boolean bool){
    String ret = bool ? "y" : "n";
    return ret;    
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
            
      if(inputAnnSet.isEmpty()){
          text+=content.toString();
          //type = "_document";
           boolean apiOK = false;
           int times = 0;
           while(times<RETRY && !apiOK ){
        	   times++;
               try {
                   apiOK = processWithTextalytics(text,type,null,outputAnnSet);
                   if(debug)Out.println("Nr of retry: "+times+". Text: "+text);
               } catch (InvalidOffsetException ex) {
                   Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
               } catch (XPathExpressionException ex) {
                   Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
               } catch (UnsupportedEncodingException ex) {
                   Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
               } catch (IOException ex) {
                   Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
           times = 0;
      }else{
          if (inputASTypes.size()==0) {
              Iterator<Annotation> inputIt = gate.Utils.inDocumentOrder(inputAnnSet).iterator();
              
              while(inputIt.hasNext()){
                  Annotation ann = inputIt.next();
                  try {
                      text = content.getContent(ann.getStartNode().getOffset(), ann.getEndNode().getOffset()).toString();
                  } catch (InvalidOffsetException ex) {
                      Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
                  }
                  //type = "_"+ann.getType();
                  boolean apiOK = false;
                  int times = 0;
                  while(times<RETRY && !apiOK ){
                	  times++;
                      try {
                          apiOK = processWithTextalytics(text,type,ann,outputAnnSet);
                          if(debug)Out.println("Nr of retry: "+times+". Text: "+text);
                          //times++;
                      } catch (InvalidOffsetException ex) {
                          Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
                      } catch (XPathExpressionException ex) {
                          Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
                      } catch (UnsupportedEncodingException ex) {
                          Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
                      } catch (IOException ex) {
                          Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
                      }
                  }
                  times = 0;
              }              
          }else{
              //if(debug)Out.println("inputASTypes size: "+inputASTypes.size());
              for (String inputAnnExpr : inputASTypes) {
                  //if(debug)Out.println("inputAnnExpr: "+inputAnnExpr);
                  AnnotationSet filteredAS = ASutil.getFilteredAS(inputAnnSet,inputAnnExpr);
                  //if(debug)Out.println("FilteredAS: "+gate.Utils.cleanStringFor(document, filteredAS));
                  Iterator<Annotation> itr = gate.Utils.inDocumentOrder(filteredAS).iterator();
                  while(itr.hasNext()){
                      Annotation ann = itr.next();
                  try {
                      text = content.getContent(ann.getStartNode().getOffset(), ann.getEndNode().getOffset()).toString();
                  } catch (InvalidOffsetException ex) {
                      Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
                  }                      
                      //type = "_"+ann.getType();
                  boolean apiOK = false;
                  int times = 0;
                  while(times<RETRY && !apiOK ){
                	  times++;
                      try {
                          apiOK = processWithTextalytics(text,type,ann,outputAnnSet);
                          if(debug)Out.println("Nr of retry: "+times+". Text: "+text);
                      } catch (InvalidOffsetException ex) {
                          Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
                      } catch (XPathExpressionException ex) {
                          Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
                      } catch (UnsupportedEncodingException ex) {
                          Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
                      } catch (IOException ex) {
                          Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
                      }
                  }
                  times = 0;
                  }
              }
          }      
      } 
    }

 public boolean processWithTextalytics(String text,String type,Annotation inputAnn,AnnotationSet outputAnnSet) throws InvalidOffsetException, XPathExpressionException, MalformedURLException, UnsupportedEncodingException, IOException{
       
   String api = this.getapiURL();
   String key = this.getkey();
   String txt = text;
       
   if(!txt.isEmpty() && !txt.equals("0")){
            
       String lang ="";
       try{
           if(!this.getlang().isEmpty())
               lang = this.getlang();
       }catch(Exception e){
           Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, e);
       }
       
       String dic = "";
       try{
           if(!this.getdictionary().isEmpty())
               dic = this.getdictionary();
       }catch(Exception e){
           Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, e);
       }
       
       Boolean uw = false;
       try{
           if(!this.getunknownWords().toString().isEmpty())
               uw = this.getunknownWords();
       }catch(Exception e){
           Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, e);
       }
      Boolean rt = true;
      try{
          if(!this.getrelaxedTypography().toString().isEmpty())
              rt = this.getrelaxedTypography();
      }catch(Exception e){
          Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, e);
      }    
      
      String userD = "";
      try{
          if(!this.getud().isEmpty())
              userD = this.getud();
      }catch(Exception e){
          Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, e);
      }
             
      String dm = translateDM(this.getDisambiguationLevel());
      
      
      Post post;
      post = new Post (api);
        post.addParameter("key", key);
        post.addParameter("txt", txt);
        post.addParameter("lang", lang);
        post.addParameter("of", "json");
        post.addParameter("uw",textTransform(uw));
        post.addParameter("rt",textTransform(rt));
        post.addParameter("tt","a");
	    post.addParameter("st","y");
        if(dic != "" && dic != null)
            post.addParameter("dic", dic);
        if(!(userD.isEmpty() || userD== "")){
            post.addParameter("ud",userD);
        }
        if(!(dm.isEmpty() || dm.equals(""))){
            post.addParameter("dm", dm);
        }
        post.addParameter("mode","sa");
        post.addParameter("verbose","y");
        
        if(debug)Logger.getLogger(TextalyticsTopics.class.getName()).info(""+post.params+"");
        
        byte[] response = post.getResponse().getBytes("UTF-8");
        String resp = new String(response,"UTF-8");
        
        ParserClient sc = new ParserClient();
        Serialized_resp data = null;
        Long inip=0L,endp=0L;
        try{
        	data = sc.getResponse(resp);
        	if(data!=null){
        	for(Serialized_resp.Annot a : data.annot_list){
        		inip = a.inip;
        		endp = a.endp;
        		if(inputAnn!=null)
        			outputAnnSet.add(inputAnn.getStartNode().getOffset()+a.inip,inputAnn.getStartNode().getOffset()+a.endp,"token",a.fm.isEmpty() ? Factory.newFeatureMap() : a.fm);
        		else
        			outputAnnSet.add(a.inip,a.endp,"token",a.fm.isEmpty() ? Factory.newFeatureMap() : a.fm);
        	}
        	}
        }catch(Exception e){
        	Logger.getLogger(TextalyticsTopics.class.getName()).severe(e.toString());
        	if(inputAnn!=null)
        		System.out.println((inputAnn.getStartNode().getOffset()+inip)+"-"+(inputAnn.getStartNode().getOffset()+endp));
        	else
        		System.out.println(inip+"-"+endp);
        }		
        //if(debug)Out.println("Response:"+resp);
        
        /*DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.parse(new ByteArrayInputStream(response));
            doc.getDocumentElement().normalize();
            Element response_node = doc.getDocumentElement();
            NodeList statusL = response_node.getElementsByTagName("status");
            org.w3c.dom.Node status = statusL.item(0);
            NamedNodeMap attributes = status.getAttributes();
            org.w3c.dom.Node code = attributes.item(0);
            if(!code.getTextContent().equals("0")) {
                 Logger.getLogger(TextalyticsParser.class.getName()).severe("API Error: "+code.getTextContent()+""+post.params.toString());
                return false;
            } else {
                 if(debug)Logger.getLogger(TextalyticsParser.class.getName()).info("Analyzing: "+document.getName()+", Sentence: "+txt);
                ParserClient.Recursive h = new ParserClient.Recursive();
                List<ParserClient.Annot> annotations = new ArrayList<Annot>();
                h = ParserClient.collectInfo(response_node,"token","/response/token_list/token","0");
                annotations = h.outAS;
                for (ParserClient.Annot at : annotations) {
                    if(inputAnn != null){//Inter-sentence offsets are added here to the intra-sentence offsets returned by the API
                        outputAnnSet.add(inputAnn.getStartNode().getOffset()+at.startOff, inputAnn.getStartNode().getOffset()+at.endOff, "parser"+type+"_"+at.Name, at.fm);
                    }else{
                        outputAnnSet.add(at.startOff, at.endOff, "parser"+type+"_"+at.Name, at.fm);                        
                    }
                }
            }
            
        } catch (   ParserConfigurationException  ex) {
            Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (   IOException  ex) {
          Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (   SAXException  ex) {
          Logger.getLogger(TextalyticsParser.class.getName()).log(Level.SEVERE, null, ex);
        }
         
       return true;
       */
   }
   return true;
   }

    @RunTime
    @Optional
    @CreoleParameter(comment = "This feature adds a stage to all the possible modes where, much like a spellchecker, the engine tries to find a suitable analysis to the unknown words resulted from the initial analysis assignment. It is specially useful to decrease the impact typos have in text analyses.\n" +
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
    @CreoleParameter(comment = "User Defined Dictionary")
    public void setud(String userD){
        this.ud = userD;
    }
    public String getud(){
        return ud;
    }
    
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "This parameter indicates how reliable the text to analyze is (as far as spelling, typography, etc. are concerned), and influences how strict the engine will be when it comes to take these factors into account in the analysis.\n" +
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
    @CreoleParameter(comment = "Input Annotation Set")
    public void setinputASname(String inputASname)
    {
	this.inputASname = inputASname;
    }
    public String getinputASname()
    {
	return inputASname;
    }

   
    @RunTime
    @Optional
    @CreoleParameter(comment = "Filter content by this expression. It allows format: \n"+
            "Type.FeatureName  \n"+
            "or  \n"+
            "Type.FeatureName==FeatureValue  \n")
    public void setinputASTypes(List<String> iat)
    {
	this.inputASTypes = iat;
    }
    public List<String> getinputASTypes()
    {
	return inputASTypes;
    }


    @RunTime
    @Optional
    @CreoleParameter(comment = "Output Annotation Set", defaultValue="Textalytics") 
    public void setoutputASname(String outputASname)
    {
        this.outputASname = outputASname;
    }
    public String getoutputASname()
    {
	return outputASname;
    }

    @RunTime
    @CreoleParameter(comment = "URL of the API", defaultValue="http://textalytics.com/core/parser-1.2") 
    public void setapiURL(String apiURL)
    {
	this.apiURL = apiURL;
    }
     public String getapiURL()
    {
	return apiURL;
    }   
    

    @RunTime
    @CreoleParameter(comment = "License key") 
    public void setkey(String key)
    {
	this.key = key;
    }
    public String getkey()
    {
	return key;
    }   
    

    @RunTime
    @CreoleParameter(comment = "Language in which we want to perform the analysis") 
    public void setlang(String lang)
    {
	this.lang = lang;
    }
    public String getlang()
    {
	return lang;
    }

    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Dictionaries to be used") 
    public void setdictionary(String dic)
    {
	this.dictionary = dic;
    }
    public String getdictionary()
    {
	return dictionary;
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
      
    @RunTime
    @Optional
    @CreoleParameter(comment = "Debug variable for the GATE plugin")
    public void setdebug(Boolean verb){
        this.debug = verb;
    }    
    public Boolean getdebug(){
        return debug;
    }   
      
} // class PoSTaggingTextalytics
