/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daedalus.textalytics.gate.clients;

import gate.util.InvalidOffsetException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

import daedalus.textalytics.gate.TextalyticsParser;
import daedalus.textalytics.gate.param.SentimentBean;
import daedalus.textalytics.gate.param.Serialized_resp;
import daedalus.textalytics.gate.param.TokenBean;


/**
 *
 * @author ADRIAN
 */
public class ParserClient {
    
public static class Annot{ //Implementation of an Annotation Object without ID. It will be assigned to the
                           //actual AnnotationSet in the core component. It contains intra-sentence offsets
    public Long startOff=0L,endOff=0L;
    public String Name = "";
    public gate.FeatureMap fm= gate.Factory.newFeatureMap();  
}
    


public static class Recursive{ //Required in order to perform recursive calls to the collect info method
                               //It is important to traverse the whole tree and get the intra-sentence offsets
                               //Inter-sentence offsets are kept in the core, not in the client functions
   public List<Annot> outAS = new ArrayList<Annot>();
   public List<TokenBean.Token_xml> tokenList = new ArrayList<TokenBean.Token_xml>(); 
}

public static Recursive collectInfo(Element response_node, String nameNode, String dompos,String fatherID) throws InvalidOffsetException, UnsupportedEncodingException, XPathExpressionException {
   
    //gate.AnnotationSet a = original;
    List<Annot> outAS = new ArrayList<Annot>();
    List<TokenBean.Token_xml> tokenList = new ArrayList<TokenBean.Token_xml>();
              
     //NodeList nodeL = response.getElementsByTagName(nameNode);
    XPath xpath = XPathFactory.newInstance().newXPath();
    XPathExpression expr1 = xpath.compile(dompos);
    NodeList nodeL = (NodeList)expr1.evaluate(response_node, XPathConstants.NODESET);
    //System.out.println("tokens: "+nodeL.getLength());
      for(int i=0; i<nodeL.getLength(); i++) {
         
    	  TokenBean.Token_xml token = new TokenBean.Token_xml();
        token.fatherID = fatherID;
        Node node = nodeL.item(i);
        NodeList data_node = node.getChildNodes();
        

        for(int j=0; j<data_node.getLength(); j++){         
          
          Node n = data_node.item(j); 
          String name = n.getNodeName();
          if(name.equals("form")) 
            token.form = new String(n.getTextContent().getBytes(),"UTF-8");
          else if(name.equals("type"))
            token.type = new String(n.getTextContent().getBytes(),"UTF-8");
          else if(name.equals("normalized_form"))
            token.normalized_form = new String(n.getTextContent().getBytes(),"UTF-8");
          else if(name.equals("bold"))
            token.bold = new String(n.getTextContent().getBytes(),"UTF-8");
          else if(name.equals("italic"))
            token.italic = new String(n.getTextContent().getBytes(),"UTF-8");
          else if(name.equals("underscore"))
            token.underscore = new String(n.getTextContent().getBytes(),"UTF-8");
          else if(name.equals("title"))
            token.title = new String(n.getTextContent().getBytes(),"UTF-8");
          else if(name.equals("separation"))
            token.separation = new String(n.getTextContent().getBytes(),"UTF-8");
          else if(name.equals("quote_level"))
            token.quote_level = new String(n.getTextContent().getBytes(),"UTF-8");
          else if(name.equals("head"))
            token.head =new String(n.getTextContent().getBytes(),"UTF-8");
          else if(name.equals("syntactic_tree_relation_list")){
            try{
                NodeList syntTreeRel = n.getChildNodes();
                for(int s_it=0;s_it<syntTreeRel.getLength();s_it++){
                	TokenBean.Token_xml.SyntTreeRelation s = new TokenBean.Token_xml.SyntTreeRelation();
                    Node synt = syntTreeRel.item(s_it);
                    NodeList dataList = synt.getChildNodes();
                    for(int t_it=0;t_it<dataList.getLength();t_it++){
                        Node data = dataList.item(t_it);
                        String dataName = data.getNodeName();
                        if(dataName.equals("id")){
                            s.id = new String(data.getTextContent().getBytes(),"UTF-8");
                        }
                        else if(dataName.equals("type")){
                            s.type = new String(data.getTextContent().getBytes(),"UTF-8");
                        }
                    }
                    token.syntTreeRelationList.add(s);
                }
            }catch(Exception e){
                System.err.println("Exception in syntTreeRelations: " + e.toString());
            }
            
          }
          else if(name.equals("analysis_list")){
          try{
              NodeList analyses = n.getChildNodes();
              for(int a_it=0;a_it<analyses.getLength();a_it++){
                  Node analysis = analyses.item(a_it);
                  NodeList analysisList = analysis.getChildNodes();
                  TokenBean.Token_xml.Analysis ana = new TokenBean.Token_xml.Analysis();
                  for(int b_it = 0;b_it<analysisList.getLength();b_it++){
                      Node data = analysisList.item(b_it);
                      String dataName = data.getNodeName();
                      if(dataName.equals("origin"))
                          ana.origin = new String(data.getTextContent().getBytes(),"UTF-8");
                      else if(dataName.equals("variety_dictionary"))
                          ana.varietyDictionary = new String(data.getTextContent().getBytes(),"UTF-8");
                      else if(dataName.equals("thematic_dictionary"))
                          ana.thematicDictionary = new String(data.getTextContent().getBytes(),"UTF-8");
                      else if(dataName.equals("tag"))
                          ana.tag = new String(data.getTextContent().getBytes(),"UTF-8");
                      else if(dataName.equals("lemma"))
                          ana.lemma = new String(data.getTextContent().getBytes(),"UTF-8");
                      else if(dataName.equals("original_form"))
                          ana.originalForm = new String(data.getTextContent().getBytes(),"UTF-8");
                      else if(dataName.equals("tag_info"))
                          ana.tagInfo = new String(data.getTextContent().getBytes(),"UTF-8");
                      else if(dataName.equals("variety_dictionary_info"))
                          ana.varietyDictionary = new String(data.getTextContent().getBytes(),"UTF-8");
                      else if(dataName.equals("thematic_dictionary_info"))
                          ana.thematicDictionary = new String(data.getTextContent().getBytes(),"UTF-8");
                      else if(dataName.equals("remission"))
                          ana.remission = new String(data.getTextContent().getBytes(),"UTF-8");
                      else if(dataName.equals("check_info")){
                          NodeList checks = data.getChildNodes();
                          for(int c_it=0;c_it<checks.getLength();c_it++){
                              Node check = checks.item(c_it);
                              String checkName = check.getNodeName();
                              if(checkName.equals("tag"))
                                  ana.checkInf.tag = new String(check.getTextContent().getBytes(),"UTF-8");
                              else if(checkName.equals("check_extra_info"))
                                  ana.checkInf.checkExtraInfo = new String(check.getTextContent().getBytes(),"UTF-8");
                              else if(checkName.equals("form_list")){
                                  NodeList forms = check.getChildNodes();
                                  for(int d_it = 0;d_it<forms.getLength();d_it++){
                                      Node forms_form = forms.item(d_it);
                                      String formName = forms_form.getNodeName();
                                      if(formName.equals("form")){
                                          ana.checkInf.forms.add(new String(forms_form.getTextContent().getBytes(),"UTF-8"));                                      
                                      }                                  
                                  }                              
                              }
                          }
                      }
                      else if(dataName.equals("sense_id_list")){
                          NodeList senseIds = data.getChildNodes();
                          for(int c_it=0;c_it<senseIds.getLength();c_it++){
                              Node senseId = senseIds.item(c_it);
                              String senseIdName = senseId.getNodeName();
                              if(senseIdName.equals("sense_id"))
                                  ana.senseIds.add(new String(senseId.getTextContent().getBytes(),"UTF-8")); 
                            }
                      }
                  }  
                  token.analysisList.add(ana);
              }
          
          }catch(Exception e){
              System.err.println("Exception in analyses: " + e.toString());
          }          
          }
          else if(name.equals("sense_list")){
              NodeList senses = n.getChildNodes();              
              for(int c_it=0;c_it<senses.getLength();c_it++){
                  Node sensess = senses.item(c_it);
                  NodeList senseList = sensess.getChildNodes();
                  TokenBean.Token_xml.Senses s = new TokenBean.Token_xml.Senses();
                  for(int d_it=0;d_it<senseList.getLength();d_it++){
                      Node sense = senseList.item(d_it);
                      String senseName = sense.getNodeName();
                      if(senseName.equals("id"))
                          s.id = sense.getTextContent();
                      else if(senseName.equals("info"))
                          s.info = sense.getTextContent();
                  }
                  token.senseList.add(s);
              }
          }
          else if(name.equals("id"))
              token.id = n.getTextContent();
          else if(name.equals("inip")){
              String inip_value = n.getTextContent();
             if(token.type.equals("sentence")){
                 Long min = Long.MAX_VALUE;
                 //Long max = Long.MIN_VALUE;
                     //NodeList subTokenList  = n.getChildNodes();
                     NodeList subTokenList = data_node;
                     for(int subT_it = 0;subT_it<subTokenList.getLength();subT_it++){
                         Node subToken  = subTokenList.item(subT_it);
                         String subt = subToken.getNodeName();
                         //Out.println("Child nodes of token sentence: "+subt);
                         if(subt.equals("token_list")){
                             NodeList Tokens = subToken.getChildNodes();
                             for(int T_it = 0;T_it<Tokens.getLength();T_it++){
                                 Node T2 = Tokens.item(T_it);
                                 //Out.println("Token??"+T2.getNodeName());
                                 NodeList tk = T2.getChildNodes();
                                 for(int T_jt = 0;T_jt<tk.getLength();T_jt++){
                                     Node T = tk.item(T_jt);
                                     String t_i_val  = T.getNodeName();
                                     //Out.println("inip??"+t_i_val);
                                     if(t_i_val.equals("inip")){
                                         Long inip_val = Long.parseLong(T.getTextContent(),10);
                                         if(inip_val < min){
                                             min = inip_val;
                                             //Out.println("new inip:"+min);
                                     }
                                 }
                             }
                             }
                         
                         }
                     }                 
                 token.inip = min;
             }else{
                 token.inip = Long.parseLong(inip_value,10);
             } 
          }
          else if(name.equals("endp")){
              String inip_value = n.getTextContent();
             if(token.type.equals("sentence")){
                 //Out.println("SENTENCE!");
                 Long max = Long.MIN_VALUE;
                     //NodeList subTokenList  = n.getChildNodes();
                     NodeList subTokenList = data_node;
                     for(int subT_it = 0;subT_it<subTokenList.getLength();subT_it++){
                         Node subToken  = subTokenList.item(subT_it);
                         String subt = subToken.getNodeName();
                         if(subt.equals("token_list")){
                             NodeList Tokens = subToken.getChildNodes();
                             for(int T_it = 0;T_it<Tokens.getLength();T_it++){
                                 Node T2 = Tokens.item(T_it);
                                 NodeList tk = T2.getChildNodes();
                                 for(int T_jt = 0;T_jt<tk.getLength();T_jt++){
                                     Node T = tk.item(T_jt);
                                     String t_i_val  = T.getNodeName();
                                 if(t_i_val.equals("endp")){
                                     Long inip_val = Long.parseLong(T.getTextContent(),10);
                                     if(inip_val > max){
                                         max = inip_val;
                                         //Out.println("new endp:"+max);
                                     }
                                 }
                             }
                             }
                         }
                     }                 
                 token.endp = max+1;
             }else{
                 token.endp = Long.parseLong(inip_value,10)+1;
             } 
          }
          else if(name.equals("token_list")){
                Recursive h2 = collectInfo((Element)n, nameNode,"./token", token.id);
                if(h2.tokenList.size() > 0){
                    tokenList.addAll(h2.tokenList);
                }          
        }  
        }//For each element
        tokenList.add(token);        
          }
      outAS = transform(tokenList,nameNode);
      
      Recursive h = new Recursive();
      h.tokenList = tokenList;
      h.outAS = outAS;
      
     return h;
}

public static List<Annot> transform(List<TokenBean.Token_xml> tokenList,String nameNode) throws UnsupportedEncodingException {
    List<Annot> outputAS = new ArrayList<Annot>();       
    for(TokenBean.Token_xml t : tokenList){
        
        Annot a = new Annot();
        try{
             gate.FeatureMap fm= gate.Factory.newFeatureMap();
             //if(!t.fatherID != null)
             	fm.put("fatherID", t.fatherID);
             if(!t.form.toString().isEmpty())
            	 fm.put("form",t.form.toString());
             if(!t.type.isEmpty()){
            	 fm.put("type", t.type);
             }else{
            	 fm.put("type","elemental");
             }
             if(!t.normalized_form.isEmpty())
            	 fm.put("normalizedForm", t.normalized_form);
             if(!t.id.isEmpty())
            	 fm.put("id",t.id);
             if(!t.bold.isEmpty())
            	 fm.put("bold",t.bold);
             if(!t.italic.isEmpty())
            	 fm.put("italic",t.italic);
             if(!t.underscore.isEmpty())
            	 fm.put("underscore",t.underscore);
             if(!t.title.isEmpty())
            	 fm.put("title",t.title);
             if(!t.separation.isEmpty())
            	 fm.put("separation", t.separation);
             if(!t.quote_level.isEmpty())
            	 fm.put("quoteLevel", t.quote_level);
             if(!t.head.isEmpty())
            	 fm.put("head", t.head);
             if(t.syntTreeRelationList.size()>0){
            	 String syntTreeRelation_id_string = "[";
            	 String syntTreeRelation_type_string = "[";
            	 for(int k_it= 0;k_it<t.syntTreeRelationList.size();k_it++){
            		 if(k_it==0){
            			 syntTreeRelation_id_string +=  t.syntTreeRelationList.get(k_it).id;
            			 syntTreeRelation_type_string += t.syntTreeRelationList.get(k_it).type;
            		 }else{
            			 syntTreeRelation_id_string +=  ","+t.syntTreeRelationList.get(k_it).id;
            			 syntTreeRelation_type_string += ","+t.syntTreeRelationList.get(k_it).type;           			 
            		 }
            		 fm.put("syntTreeRelation_"+(k_it+1)+"_id",t.syntTreeRelationList.get(k_it).id);
                     fm.put("syntTreeRelation_"+(k_it+1)+"_type",t.syntTreeRelationList.get(k_it).type);
                 }
            	 syntTreeRelation_id_string += "]";
            	 syntTreeRelation_type_string += "]";
            	 fm.put("syntTreeRelation_id_string", syntTreeRelation_id_string);
            	 fm.put("syntTreeRelation_type_string", syntTreeRelation_type_string);
             }
             if(t.analysisList.size()>0){
            	 String analysis_origin_string = "[";
            	 String analysis_variety_dictionary_string = "[";
            	 String analysis_thematic_dictionary_string = "[";
            	 String analysis_tag_string = "[";
            	 String analysis_lemma_string = "[";
            	 String analysis_original_form_string = "[";
            	 String analysis_tag_info_string = "[";
            	 String analysis_variety_dictionary_info_string = "[";
            	 String analysis_thematic_dictionary_info_string = "[";
            	 
                 for(int k_it=0;k_it<t.analysisList.size();k_it++){
                	 if(k_it==0){
                		 analysis_origin_string = ((!t.analysisList.get(k_it).origin.isEmpty()) ? t.analysisList.get(k_it).origin : "");
                    	 analysis_variety_dictionary_string = ((!t.analysisList.get(k_it).varietyDictionary.isEmpty()) ? t.analysisList.get(k_it).varietyDictionary : "");
                    	 analysis_thematic_dictionary_string = ((!t.analysisList.get(k_it).thematicDictionary.isEmpty()) ? t.analysisList.get(k_it).thematicDictionary : "");
                    	 analysis_tag_string = ((!t.analysisList.get(k_it).tag.isEmpty()) ? t.analysisList.get(k_it).tag : "");
                    	 analysis_lemma_string = ((!t.analysisList.get(k_it).lemma.isEmpty()) ? t.analysisList.get(k_it).lemma : "");
                    	 analysis_original_form_string = ((!t.analysisList.get(k_it).originalForm.isEmpty()) ? t.analysisList.get(k_it).originalForm : "");
                    	 analysis_tag_info_string = ((!t.analysisList.get(k_it).tagInfo.isEmpty()) ? t.analysisList.get(k_it).tagInfo : "");
                    	 analysis_variety_dictionary_info_string = ((!t.analysisList.get(k_it).varietyDictionaryInfo.isEmpty()) ? t.analysisList.get(k_it).varietyDictionaryInfo : "");
                    	 analysis_thematic_dictionary_info_string = ((!t.analysisList.get(k_it).thematicDictionaryInfo.isEmpty()) ? t.analysisList.get(k_it).thematicDictionaryInfo : "");
            		 }else{
            			 analysis_origin_string = ((!t.analysisList.get(k_it).origin.isEmpty()) ? ","+t.analysisList.get(k_it).origin : ",");
                    	 analysis_variety_dictionary_string = ((!t.analysisList.get(k_it).varietyDictionary.isEmpty()) ? ","+t.analysisList.get(k_it).varietyDictionary : ",");
                    	 analysis_thematic_dictionary_string = ((!t.analysisList.get(k_it).thematicDictionary.isEmpty()) ? ","+t.analysisList.get(k_it).thematicDictionary : ",");
                    	 analysis_tag_string = ((!t.analysisList.get(k_it).tag.isEmpty()) ? ","+t.analysisList.get(k_it).tag : ",");
                    	 analysis_lemma_string = ((!t.analysisList.get(k_it).lemma.isEmpty()) ? ","+t.analysisList.get(k_it).lemma : ",");
                    	 analysis_original_form_string = ((!t.analysisList.get(k_it).originalForm.isEmpty()) ? ","+t.analysisList.get(k_it).originalForm : ",");
                    	 analysis_tag_info_string = ((!t.analysisList.get(k_it).tagInfo.isEmpty()) ? ","+t.analysisList.get(k_it).tagInfo : ",");
                    	 analysis_variety_dictionary_info_string = ((!t.analysisList.get(k_it).varietyDictionaryInfo.isEmpty()) ? ","+t.analysisList.get(k_it).varietyDictionaryInfo : ",");
                    	 analysis_thematic_dictionary_info_string = ((!t.analysisList.get(k_it).thematicDictionaryInfo.isEmpty()) ? ","+t.analysisList.get(k_it).thematicDictionaryInfo : ",");        			 
            		 }
                	 
                	 if(!t.analysisList.get(k_it).origin.isEmpty())
                		 fm.put("analysis_"+(k_it+1)+"_origin",t.analysisList.get(k_it).origin);
                     if(!t.analysisList.get(k_it).varietyDictionary.isEmpty())
                    	 fm.put("analysis_"+(k_it+1)+"_varietyDictionary",t.analysisList.get(k_it).varietyDictionary);
                     if(!t.analysisList.get(k_it).thematicDictionary.isEmpty())
                    	 fm.put("analysis_"+(k_it+1)+"_thematicDictionary",t.analysisList.get(k_it).thematicDictionary);
                     if(!t.analysisList.get(k_it).tag.isEmpty())
                    	 fm.put("analysis_"+(k_it+1)+"_tag",t.analysisList.get(k_it).tag);
                     if(!t.analysisList.get(k_it).lemma.isEmpty())
                    	 fm.put("analysis_"+(k_it+1)+"_lemma",t.analysisList.get(k_it).lemma);
                     if(!t.analysisList.get(k_it).originalForm.isEmpty())
                    	 fm.put("analysis_"+(k_it+1)+"_originalForm",t.analysisList.get(k_it).originalForm);
                     if(!t.analysisList.get(k_it).tagInfo.isEmpty())
                    	 fm.put("analysis_"+(k_it+1)+"_tagInfo",t.analysisList.get(k_it).tagInfo);
                     if(!t.analysisList.get(k_it).varietyDictionaryInfo.isEmpty())
                    	 fm.put("analysis_"+(k_it+1)+"_varietyDictionaryInfo",t.analysisList.get(k_it).varietyDictionaryInfo);
                     if(!t.analysisList.get(k_it).thematicDictionaryInfo.isEmpty())
                    	 fm.put("analysis_"+(k_it+1)+"_thematicDictionaryInfo",t.analysisList.get(k_it).thematicDictionaryInfo);
                     if(!t.analysisList.get(k_it).remission.isEmpty())
                    	 fm.put("analysis_"+(k_it+1)+"_remission",t.analysisList.get(k_it).remission);
                     if(!t.analysisList.get(k_it).checkInf.toString().isEmpty()){
                    	 String analysis_checkInfo_tag_str="[";
                    	 String analysis_checkExtraInfo_str="[";
                    	 String analysis_checkInfo_form="[";
                    	 
                    	 if(!t.analysisList.get(k_it).checkInf.tag.isEmpty())
                    		 fm.put("analysis_"+(k_it+1)+"_checkInfo_tag",t.analysisList.get(k_it).checkInf.tag);
                         if(!t.analysisList.get(k_it).checkInf.checkExtraInfo.isEmpty())
                             fm.put("analysis_"+(k_it+1)+"_checkInfo_checkExtraInfo",t.analysisList.get(k_it).checkInf.checkExtraInfo);
                         if(t.analysisList.get(k_it).checkInf.forms.size()>0){                  
                             for(int l_it = 0;l_it<t.analysisList.get(k_it).checkInf.forms.size();l_it++){
                            	 fm.put("analysis_"+(k_it+1)+"_checkInfo_form_"+(l_it+1),t.analysisList.get(k_it).checkInf.forms.get(l_it));
                             }
                         }
                     }
                     if(t.analysisList.get(k_it).senseIds.size()>0){
                    	 for(int l_it = 0;l_it<t.analysisList.get(k_it).senseIds.size();l_it++){
                    		 fm.put("analysis_"+(k_it+1)+"_senseId"+(l_it+1),t.analysisList.get(k_it).senseIds.get(l_it));
                         }
                     }
                }
           }
           if(t.senseList.size()>0){
        	   for(int k_it=0;k_it<t.senseList.size();k_it++){
        		   fm.put("senses_"+(k_it+1)+"_id",t.senseList.get(k_it).id);
                   fm.put("senses_"+(k_it+1)+"_info",t.senseList.get(k_it).info);
               }                          
           }
           a.fm= fm;
           a.Name = nameNode;
           a.startOff = t.inip;
           a.endOff=t.endp;
           
       }catch (Exception ex){
    	   Logger.getLogger(ParserClient.class.getName()).log(Level.SEVERE, null, ex);
       }  
       outputAS.add(a);
    }
    return outputAS;
}


public Serialized_resp getResponse(String response) throws UnsupportedEncodingException{
	TokenBean jsonObject = new Gson().fromJson(response, TokenBean.class);
	Serialized_resp sr = jsonObject.serialize();
	return sr;
}

public TokenBean getData(String response){
	TokenBean jsonObject = new Gson().fromJson(response, TokenBean.class );
	return jsonObject;
}

}