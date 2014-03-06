/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daedalus.textalytics.gate;

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

import daedalus.textalytics.gate.clients.StilusClient;
import daedalus.textalytics.gate.param.ASutil;



/** 
 * This class is the implementation of the resource POSTAGGINGTEXTALYTICS.
 */
@CreoleResource(name = "Textalytics Spell, Grammar and Style Proofreading",
                comment = "Textalytics Spell, Grammar and Style Proofreading",
                helpURL="http://textalytics.com/core/stilus-info")
public class TextalyticsProofreading  extends AbstractLanguageAnalyser
  implements ProcessingResource {

     private String inputASname, outputASname, apiURL="", key="", lang="en",manyErrors="2", dictionary="";
     private Boolean prefixed=true,quotesOrItalics=true,too_longSent=true,
             properNouns=true,tautologyAndLanMisuse=true,spacing=true,
             openingClosing=true,punctuation=true,foreign=true,confusion=true,
             percentage=true,consonantRed=true,debug=false;
     private List<String> inputASTypes = new ArrayList<String>();
     
    public String textTransform(boolean bool){
    String ret = bool ? "y" : "n";
    return ret;    
    } 
           
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
          process(text,type,null,outputAnnSet);
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
                  process(text,type,ann,outputAnnSet);
              }              
          }else{
              if(debug)Out.println("inputASTypes size: "+inputASTypes.size());
              for (String inputAnnExpr : inputASTypes) {
                  if(debug)Out.println("inputAnnExpr: "+inputAnnExpr);
                  AnnotationSet filteredAS = ASutil.getFilteredAS(inputAnnSet,inputAnnExpr);
                  if(debug)Out.println("FilteredAS: "+gate.Utils.cleanStringFor(document, filteredAS));
                  Iterator<Annotation> itr = gate.Utils.inDocumentOrder(filteredAS).iterator();
                  while(itr.hasNext()){
                      Annotation ann = (Annotation) itr.next();
                      //type = "_"+ann.getType();
                      try {
                          text = content.getContent(ann.getStartNode().getOffset(), ann.getEndNode().getOffset()).toString();
                      } catch (InvalidOffsetException ex) {
                          Logger.getLogger(TextalyticsClass.class.getName()).log(Level.SEVERE, null, ex);
                      }
                      process(text,type,ann,outputAnnSet);
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
      
      if(!text.isEmpty() && !text.equals("0")){
         
      try {
          StilusClient c = new StilusClient();
          String api = this.getapiURL();
          String key = this.getkey();
          String txt = text;
          String langg = "en";
           
          try{
             if(!this.getlang().isEmpty())
                 langg = this.getlang();
          }catch(Exception e){}
          
          
          String dic = "";
          if(this.getdictionary().isEmpty()){
              String lang = getlang();
              if ("es".equals(lang)) {
                dic = "chetsdpqr";
              }
              else if ("en".equals(lang)) {
                dic = "chetsdpCA";
              }
              else if ("fr".equals(lang)) {
                dic = "chetsdpF";
              }
              else if ("it".equals(lang)) {
                dic = "chetsdpoa";
              }              
          }
          
          Post post;
          post = new Post (api);
          post.addParameter("key", key);
          post.addParameter("txt", txt);
          post.addParameter("lang", langg);
          post.addParameter("dic",dic);
          post.addParameter("of", "xml");
          post.addParameter("pp",textTransform(this.getprefixed()));
          post.addParameter("aqoi",textTransform(this.getquotesOrItalics()));
          post.addParameter("tls",textTransform(this.gettoo_longSent()));
          post.addParameter("dpn",textTransform(this.getproperNouns()));
          post.addParameter("red",textTransform(this.gettautologyAndLanMisuse()));
          post.addParameter("spa",textTransform(this.getspacing()));
          post.addParameter("comppunc",textTransform(this.getopeningClosing()));
          post.addParameter("corrpunc", textTransform(this.getpunctuation()));
          post.addParameter("alw",textTransform(this.getforeign()));
          post.addParameter("wct",textTransform(this.getconfusion()));
          post.addParameter("wps", textTransform(this.getpercentage()));
          post.addParameter("stme", this.getmanyErrors());
          post.addParameter("wrc", textTransform(this.getconsonantRed()));
          
          byte[] response = post.getResponse().getBytes("UTF-8");
          String resp = new String(response,"UTF-8");
          if(debug)Out.println("Response:"+resp);
          
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
                          List<StilusClient.result> result_list = StilusClient.collectInfo(response_node,"result",outputAnnSet);
                          setDocFeatures(result_list,type, inputAnn, outputAnnSet);
                      }catch (Exception ex){
                         Logger.getLogger(TextalyticsProofreading.class.getName()).log(Level.SEVERE, null, ex);
                      }
                  }
              } catch (Exception e) {
                  System.err.println(e.toString());
                  System.out.println("Not found");
              }
          } catch (ParserConfigurationException ex) {
              Logger.getLogger(TextalyticsProofreading.class.getName()).log(Level.SEVERE, null, ex);
          }  catch (SAXException ex) {
              Logger.getLogger(TextalyticsProofreading.class.getName()).log(Level.SEVERE, null, ex);
          } catch (IOException ex) {
              Logger.getLogger(TextalyticsProofreading.class.getName()).log(Level.SEVERE, null, ex);
          }
      } catch (MalformedURLException ex) {
          Logger.getLogger(TextalyticsProofreading.class.getName()).log(Level.SEVERE, null, ex);
      } catch (UnsupportedEncodingException ex) {
          Logger.getLogger(TextalyticsProofreading.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
          Logger.getLogger(TextalyticsProofreading.class.getName()).log(Level.SEVERE, null, ex);
      }
      }
   }
   
   public void setDocFeatures(List<StilusClient.result> result_list,String type, Annotation inputAnn,AnnotationSet outputAnnSet) throws InvalidOffsetException{

       Iterator<StilusClient.result> it = result_list.iterator();
       if(debug)Out.println("resultList: "+result_list.size());
    try{
        
        int res_count = 0;
    while(it.hasNext()){
        gate.FeatureMap fm= gate.Factory.newFeatureMap();
        res_count++;
        StilusClient.result r = it.next();
        if(!r.text.isEmpty())
            fm.put("text", r.text);
        if(!r.type.isEmpty())
            fm.put("type",r.type);
        if(!r.level.isEmpty())
            fm.put("level",r.level);
        if(!r.rule.isEmpty())
            fm.put("rule", r.rule);
        if(!r.msg.isEmpty())
            fm.put("msg", r.msg);
        if(r.sug_list.size() > 0){
            Iterator<StilusClient.result.suggestion> it2 = r.sug_list.iterator();
            int sug_count = 0;
            String sug_form="",sug_confidence="";
            while(it2.hasNext()){
                StilusClient.result.suggestion s = it2.next();
            	if(sug_count == 0){
            		sug_form += s.form;
            		sug_confidence += s.confidence;
            	}else{
            		sug_form+=";"+s.form;
            		sug_confidence += ";"+s.confidence;
            	}
                sug_count++;           
            }
            fm.put("sug_form", sug_form);
            fm.put("sug_confidence", sug_confidence);
        }
        if((!r.inip.isEmpty())&&(!r.endp.isEmpty())){
            if(inputAnn != null){
                outputAnnSet.add((inputAnn.getStartNode().getOffset()+Long.parseLong(r.inip, 10)), (inputAnn.getStartNode().getOffset()+(Long.parseLong(r.endp, 10)+1)), "proofreading"+type, fm);
            }else{
                outputAnnSet.add(Long.parseLong(r.inip, 10), (Long.parseLong(r.endp, 10)+1), "proofreading"+type, fm);
            }
        }
    }

    }catch (Exception ex){
        Logger.getLogger(TextalyticsProofreading.class.getName()).log(Level.SEVERE, null, ex);
}
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
   @CreoleParameter(comment = "URL Of the API to query", defaultValue="http://textalytics.com/core/stilus-1.1") 
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
    @CreoleParameter(comment = "It specifies the language in which the text must be analyzed. The current supported values are the following:\n" +
"  \n" +
"en: English  \n" +
"es: Spanish  \n" +
"it: Italian  \n" +
"fr: French  ")
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
    @CreoleParameter(comment = "Describes how it will behave when it finds sentences with many errors\n" +
"\n" +
"0: Check for all\n" +
"1: Group and ignore\n" +
"2: Group and warn (default)")
    public void setmanyErrors(String me){
        this.manyErrors = me;
    }
    public String getmanyErrors(){
        return this.manyErrors;
    }
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "This parameter will specify the list of active dictionaries that will be used in the topic extraction")    
    public void setdictionary(String dic){
        this.dictionary = dic;
    }
    public String getdictionary(){
        return this.dictionary;
    }
    
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Smart detection of prefixed words\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")    
   
    public void setprefixed(Boolean p){
        this.prefixed = p;
    }
    public Boolean getprefixed(){
        return this.prefixed;
    }    
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Smart handling of words written in italics or with quotation marks\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")    
    public void setquotesOrItalics(Boolean q){
        this.quotesOrItalics = q;
    } 
    public Boolean getquotesOrItalics(){
        return this.quotesOrItalics;
    }

    @RunTime
    @Optional
    @CreoleParameter(comment = "Warn of too-long sentences\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")    
    public void settoo_longSent(Boolean tl){
        this.too_longSent = tl;
    } 
    public Boolean gettoo_longSent(){
        return this.too_longSent;
    }
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Smart detection of proper nouns\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")    

    public void setproperNouns(Boolean pn){
        this.properNouns = pn;
    } 
    public Boolean getproperNouns(){
        return this.properNouns;
    }
        
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Check tautology and language misuse\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")    
    public void settautologyAndLanMisuse(Boolean tt){
        this.tautologyAndLanMisuse = tt;
    } 
    public Boolean gettautologyAndLanMisuse(){
        return this.tautologyAndLanMisuse;
    }
    
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Check spacing\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")    
    public void setspacing(Boolean ss){
        this.spacing = ss;
    } 
    public Boolean getspacing(){
        return this.spacing;
    }    
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Verify the opening and closing of pairs of signs\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")    
    public void setopeningClosing(Boolean oc){
        this.openingClosing = oc;
    } 
    public Boolean getopeningClosing(){
        return this.openingClosing;
    }
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Check punctuation marks\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")    
    
    public void setpunctuation(Boolean p){
        this.punctuation = p;
    } 
    public Boolean getpunctuation(){
        return this.punctuation;
    }    
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Warn of foreign words to be avoided\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")    
    public void setforeign(Boolean f){
        this.foreign = f;
    } 
    public Boolean getforeign(){
        return this.foreign;
    }
 
   
    @RunTime
    @Optional
    @CreoleParameter(comment = "Warn of confusion between terms\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")    

    public void setconfusion(Boolean c){
        this.confusion = c;
    } 
    public Boolean getconfusion(){
        return this.confusion;
    }
    
    @RunTime
    @Optional
    @CreoleParameter(comment = "Warn of percentage signs (%) not spaced (Spanish only)\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")    
    
    public void setpercentage(Boolean p){
        this.percentage = p;
    }   
    public Boolean getpercentage(){
        return this.percentage;
    }
    
        @RunTime
    @Optional
    @CreoleParameter(comment = "Warn of group consonant reduction (Spanish only)\n" +
"\n" +
"y: enabled (default)\n" +
"n: disabled")    
    
    public void setconsonantRed(Boolean p){
        this.consonantRed = p;
    }   
    public Boolean getconsonantRed(){
        return this.consonantRed;
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
    public void setinputASTypes(List<String> iat)
    {
	this.inputASTypes = iat;
    }
    
    public List<String> getinputASTypes()
    {
	return inputASTypes;
    }  
  
} // class PoSTaggingTextalytics

