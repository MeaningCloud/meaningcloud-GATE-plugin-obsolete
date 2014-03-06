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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPathExpressionException;

import daedalus.textalytics.gate.clients.SentimentClient;
import daedalus.textalytics.gate.param.ASutil;
import daedalus.textalytics.gate.param.SentimentBean;
import daedalus.textalytics.gate.param.Serialized_resp;

/** 
 * This class is the implementation of the resource POSTAGGINGTEXTALYTICS.
 */
@CreoleResource(name = "Textalytics Sentiment Analysis",
                comment = "Textalytics Sentiment Analysis",
                helpURL="http://textalytics.com/core/sentiment-info")
public class TextalyticsSentiment  extends AbstractLanguageAnalyser
  implements ProcessingResource { 
     private String inputASname, outputASname, apiURL, key, model="",entities="",concepts="";
     private boolean debug=false;     
     private List<String> inputASTypes = new ArrayList<String>(); // list of input annotations from which string content will be submitted     
     private static final int RETRY = 10; 
     
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
            
      if(inputAnnSet.isEmpty()){
          text+=content.toString();
          //type = "_document";
           boolean apiOK = false;
           int times = 0;
           while(times<RETRY && !apiOK ){
               try {
                   apiOK = processWithTextalytics(text,type,null,outputAnnSet);
                   if(debug)Out.println("Nr of retry: "+times+". Text: "+text);
                   times++;
               } catch (InvalidOffsetException ex) {
                   Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
               } catch (XPathExpressionException ex) {
                   Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
               } catch (UnsupportedEncodingException ex) {
                   Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
               } catch (IOException ex) {
                   Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
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
                      Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
                  }
                  //type = "_"+ann.getType();
                  boolean apiOK = false;
                  int times = 0;
                  while(times<RETRY && !apiOK ){
                      try {
                          apiOK = processWithTextalytics(text,type,ann,outputAnnSet);
                          if(debug)Out.println("Nr of retry: "+times+". Text: "+text);
                          times++;
                      } catch (InvalidOffsetException ex) {
                          Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
                      } catch (XPathExpressionException ex) {
                          Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
                      } catch (UnsupportedEncodingException ex) {
                          Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
                      } catch (IOException ex) {
                          Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
                      }
                  }
                  times = 0;
              }              
          }else{
              if(debug)Out.println("inputASTypes size: "+inputASTypes.size());
              for (String inputAnnExpr : inputASTypes) {
                  if(debug)Out.println("inputAnnExpr: "+inputAnnExpr);
                  AnnotationSet filteredAS = ASutil.getFilteredAS(inputAnnSet,inputAnnExpr);
                  if(debug)Out.println("FilteredAS: "+gate.Utils.cleanStringFor(document, filteredAS));
                  Iterator<Annotation> itr = gate.Utils.inDocumentOrder(filteredAS).iterator();
                  while(itr.hasNext()){
                      Annotation ann = itr.next();
                  try {
                      text = content.getContent(ann.getStartNode().getOffset(), ann.getEndNode().getOffset()).toString();
                  } catch (InvalidOffsetException ex) {
                      Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
                  }                      
                      //type = "_"+ann.getType();
                  boolean apiOK = false;
                  int times = 0;
                  while(times<RETRY && !apiOK ){
                      try {
                          apiOK = processWithTextalytics(text,type,ann,outputAnnSet);
                          if(debug)Out.println("Nr of retry: "+times+". Text: "+text);
                          times++;
                      } catch (InvalidOffsetException ex) {
                          Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
                      } catch (XPathExpressionException ex) {
                          Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
                      } catch (UnsupportedEncodingException ex) {
                          Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
                      } catch (IOException ex) {
                          Logger.getLogger(TextalyticsSentiment.class.getName()).log(Level.SEVERE, null, ex);
                      }
                  }
                  times = 0;
                  }
              }
          }      
      } 
    }

 public boolean processWithTextalytics(String text,String type,Annotation inputAnn,AnnotationSet outputAnnSet) throws InvalidOffsetException, XPathExpressionException, MalformedURLException, UnsupportedEncodingException, IOException{
   if(inputAnn!=null){
          if(debug)Out.println("Processing: "+inputAnn.getType());
      }else{
          if(debug)Out.println("Processing the whole document");
   }
   
   //ParserClient c = new ParserClient();
       
   String api = this.getapiURL();
   String key = this.getkey();
   String txt = text;
       
   if(!txt.isEmpty() && !txt.equals("0")){
       
       if(debug)Out.println("Text: "+txt);

             
      
      
      Post post;
      post = new Post (api);
        post.addParameter("key", key);
        post.addParameter("txt", txt);
        post.addParameter("model", this.getmodel());
        post.addParameter("of", "json");
        if(!this.getentities().isEmpty())
        	post.addParameter("entities",this.getentities());
        if(!this.getConcepts().isEmpty())
        	post.addParameter("concepts",this.getConcepts());
        
        if(debug)Logger.getLogger(TextalyticsSentiment.class.getName()).info(""+post.params+"");
        
        byte[] response = post.getResponse().getBytes("UTF-8");
        String resp = new String(response,"UTF-8");
        if(debug)Out.println("Response:"+resp);
        
        SentimentClient sClient = new SentimentClient();
        Serialized_resp sr = sClient.getResponse(resp);
        if(sr.s.code == 0){
        	document.getFeatures().putAll(sr.doc_fm);
            for (Serialized_resp.Annot at : sr.annot_list) {
            	if(inputAnn != null){//Inter-sentence offsets are added here to the intra-sentence offsets returned by the API
            		outputAnnSet.add(inputAnn.getStartNode().getOffset()+at.inip, inputAnn.getStartNode().getOffset()+at.endp, "sentiment"+type+"_segment", at.fm);
            	}else{
            		outputAnnSet.add(at.inip, at.endp, "sentiment"+type+"_segment", at.fm);
            	}
            }
        }else{
        	Logger.getLogger(TextalyticsSentiment.class.getName()).info("API Error"+sr.s.toString()+"\n"+post.params.toString());
        }        
        }
   return true;
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
    @CreoleParameter(comment = "URL of the API", defaultValue="http://textalytics.com/core/sentiment-1.1") 
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
    @CreoleParameter(comment = "Sentiment model chosen. If no model is specified, the most adequate one will be detected automatically, based on the language of the text.\n"
    		+ "The current available models are the following:\n"
    		+ "\tes-general: Generic domain (Spanish)\n"
    		+ "\ten-general: Generic domain (English)\n"
    		+ "\tfr-general: Generic domain (French)\n") 
    public void setmodel(String model)
    {
	this.model = model;
    }
    public String getmodel()
    {
	return model;
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
    @CreoleParameter(comment = "Entities") 
    public void setEntities(String entities)
    {
	this.entities = entities;
    }
    public String getentities()
    {
	return entities;
    }  
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Concepts") 
    public void setConcepts(String concepts)
    {
	this.concepts = concepts;
    }
    public String getConcepts()
    {
	return concepts;
    }  
    
      
} // class TextalyticsSentiment
