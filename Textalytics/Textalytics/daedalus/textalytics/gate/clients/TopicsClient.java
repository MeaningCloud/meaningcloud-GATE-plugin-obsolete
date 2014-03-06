
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daedalus.textalytics.gate.clients;

import gate.util.InvalidOffsetException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import daedalus.textalytics.gate.param.TopicsBean;


/**
 *
 * @author ADRIAN
 */
public class TopicsClient {
        
public static class Recursive{//Required in order to perform recursive calls to the collect info method
                                  //It is important to traverse the whole tree and get the intra-sentence offsets
                                  //Inter-sentence offsets are kept in the core, not in the client functions
    public List<Annot> hack_ann_list = new ArrayList<Annot>();
    public List<TopicsBean.Entity> hack_entity_list = new ArrayList<TopicsBean.Entity>();
}

public static class AnnotationOffsets{
    public Long startOff=0L,endOff=0L;
}

public static List<Annot> transform(List<TopicsBean.Entity> entity_list,String nameNode, String ud) throws UnsupportedEncodingException {
        List<Annot> annotations = new ArrayList<Annot>();
        Iterator<TopicsBean.Entity> it = entity_list.iterator();     
        int count = 0;
        while(it.hasNext()){
            List<AnnotationOffsets> annOffList = new ArrayList<AnnotationOffsets>();
            annOffList.clear();
            TopicsBean.Entity ent = (TopicsBean.Entity) it.next();
            Iterator<TopicsBean.Entity.variant> it2 = ent.variant_list.iterator();
            gate.FeatureMap fm= gate.Factory.newFeatureMap();
            fm.clear();
            count = 0;
            while(it2.hasNext()){
                AnnotationOffsets annot = new AnnotationOffsets();
                TopicsBean.Entity.variant var = (TopicsBean.Entity.variant) it2.next();                
                if(!var.inip.isEmpty() && !var.endp.isEmpty()){
                    annot.startOff = Long.parseLong(var.inip,10);
                    annot.endOff = Long.parseLong(var.endp,10)+1;
                    count++;
                    annOffList.add(annot);
                }

                if(!ent.form.isEmpty())
                    fm.put("Form",ent.form);
                if(!ent.dictionary.isEmpty())
                     fm.put("Dictionary",new String(ent.dictionary.getBytes(),"UTF-8"));
                if(!ent.id.isEmpty())
                    fm.put("ID",ent.id);
                if(!ent.relevance.isEmpty())
                    fm.put("Relevance",ent.relevance);
                if(ent.sementity_list.size()>0){
	                String sementity_class="",sementity_confidence="",sementity_fiction="",sementity_id="",sementity_type="";   
    	            for(int i_it = 0;i_it<ent.sementity_list.size();i_it++){
    	            	if(i_it>0){
    	            		sementity_class+=";";
    	            		sementity_confidence+=";";
    	            		sementity_fiction+=";";
    	            		sementity_id+=";";
    	            		sementity_type+=";";
    	            	}
        	            if(!ent.sementity_list.get(i_it).sementity_class.isEmpty())
        	            	sementity_class+=ent.sementity_list.get(i_it).sementity_class;
                	    if(!ent.sementity_list.get(i_it).sementity_confidence.isEmpty())
                	    	sementity_confidence+=ent.sementity_list.get(i_it).sementity_confidence;
   	                 	if(!ent.sementity_list.get(i_it).sementity_fiction.isEmpty())
   	                     	sementity_fiction+=ent.sementity_list.get(i_it).sementity_fiction;
    	                if(!ent.sementity_list.get(i_it).sementity_id.isEmpty())
        	                sementity_id+=ent.sementity_list.get(i_it).sementity_id;
            	        if(!ent.sementity_list.get(i_it).sementity_type.isEmpty()){
							sementity_type+=ent.sementity_list.get(i_it).sementity_type;
                	        String semtype = ent.sementity_list.get(i_it).sementity_type;
                	    }
                	}
                	if(!sementity_type.isEmpty()){
                		fm.put("sementity_class",sementity_class);
                		fm.put("sementity_confidence",sementity_confidence);
                		fm.put("sementity_fiction",sementity_fiction);  
                		fm.put("sementity_id",sementity_id);
    	            	fm.put("sementity_type",sementity_type);
    	            }
                }
                if(ent.semld_list.size()>0){
                String semld="",sourceDic="";
                for(int i_it=0;i_it<ent.semld_list.size();i_it++){
                	if(i_it>0){
                		semld+=";";
                		sourceDic+=";";
                	}
                    if(!ent.semld_list.get(i_it).semld.isEmpty()){
                        if(!ent.semld_list.get(i_it).sourceDic.isEmpty()){
                            //fm.put("semld_"+ent.semld_list.get(i_it).sourceDic,ent.semld_list.get(i_it).semld);
                            sourceDic+=ent.semld_list.get(i_it).sourceDic;
                        }
                        //fm.put("semld_"+(i_it+1),ent.semld_list.get(i_it).semld);
                        semld+=ent.semld_list.get(i_it).semld;
                    }
                }
                if(!semld.isEmpty()){
                    fm.put("semld_source",sourceDic);
                	fm.put("semld_value",semld);
                }
                }
                if(ent.standard_list.size()>0){
                	String standard_value="",standard_key="";
                	for(int i_it=0;i_it<ent.standard_list.size();i_it++){
                		if(i_it>0){
                			standard_value+=";";
                			standard_key+=";";
                		}
                		if(!ent.standard_list.get(i_it).value.isEmpty())
                			standard_value+=ent.standard_list.get(i_it).value;
                		if(!ent.standard_list.get(i_it).name.isEmpty())
                			standard_key+=ent.standard_list.get(i_it).name;
                	}
                	if(!standard_value.isEmpty()){
                		fm.put("standard_key",standard_key);
                		fm.put("standard_value",standard_value);
                	}
                }
                if(ent.semgeo_list.size()>0){
                	for(int i_it=0;i_it<ent.semgeo_list.size();i_it++){
                    	int size = ent.semgeo_list.get(i_it).semgeo_list.size();
                    	for(int j_it = 0;j_it<size;j_it++){
                        	if(!ent.semgeo_list.get(i_it).semgeo_list.get(j_it).form.isEmpty())
                            	fm.put(ent.semgeo_list.get(i_it).semgeo_list.get(j_it).name+/*"_"+(j_it+1)+*/"_form",ent.semgeo_list.get(i_it).semgeo_list.get(j_it).form);
	                        if(!ent.semgeo_list.get(i_it).semgeo_list.get(j_it).id.isEmpty())
    	                        fm.put(ent.semgeo_list.get(i_it).semgeo_list.get(j_it).name+/*"_"+(j_it+1)+*/"_id",ent.semgeo_list.get(i_it).semgeo_list.get(j_it).id);
        	                int size_std = ent.semgeo_list.get(i_it).semgeo_list.get(j_it).stdArray.size();
            	            for(int k_it = 0;k_it<size_std;k_it++){
                	            if(!ent.semgeo_list.get(i_it).semgeo_list.get(j_it).stdArray.get(k_it).name.isEmpty())
                    	            fm.put(ent.semgeo_list.get(i_it).semgeo_list.get(j_it).name+"_"+(j_it+1)+"_std."+ent.semgeo_list.get(i_it).semgeo_list.get(j_it).stdArray.get(k_it).name,ent.semgeo_list.get(i_it).semgeo_list.get(j_it).stdArray.get(k_it).value);
                        	}
                    	}
               		}
                }
                if(ent.semtheme_list.size()>0){
                	String semtheme_id="",semtheme_type="";
                	for(int i_it=0;i_it<ent.semtheme_list.size();i_it++){
                		if(i_it>0){
                			semtheme_id+=";";
                			semtheme_type+=";";
                		}
                		if(!ent.semtheme_list.get(i_it).id.isEmpty())
                			//fm.put("semtheme_id_"+(i_it+1),ent.semtheme_list.get(i_it).id);
                			semtheme_id+=ent.semtheme_list.get(i_it).id;
                		if(!ent.semtheme_list.get(i_it).type.isEmpty())
                			//fm.put("semtheme_type_"+(i_it+1),ent.semtheme_list.get(i_it).type);
                			semtheme_type+=ent.semtheme_list.get(i_it).type;
                	}
                	fm.put("semtheme_id", semtheme_id);
                	fm.put("semtheme_type", semtheme_type);
                }
                if(ent.variant_list.size()>0){
                	String variant="";
                	for(int i_it=0;i_it<ent.variant_list.size();i_it++){
                		if(i_it>0){
                			variant+=";";
                		}
                		//if(!ent.variant_list.get(i_it).form.isEmpty())
                			variant+=ent.variant_list.get(i_it).form;
                	}
                	fm.put("variants", variant);
                }
         
            }
            Annot annotat = new Annot();
            for(int k_it = 0;k_it<count;k_it++){
                annotat = new Annot();
                annotat.startOff = annOffList.get(k_it).startOff;
                annotat.endOff = annOffList.get(k_it).endOff;
                annotat.Name = nameNode;
                annotat.fm = fm;
                annotations.add(annotat); 
            }

        }
        return annotations;
    }
 
public static class Annot{

    public Long startOff=0L,endOff=0L;
    public String Name = "";
    public gate.FeatureMap fm= gate.Factory.newFeatureMap();
    
}



public static Recursive collectInfo(Element response, String nameNode, String ud) throws InvalidOffsetException, UnsupportedEncodingException{
    

    List<Annot> annotations = new ArrayList<Annot>(); 
    Recursive rec = new Recursive();
    
     NodeList nodeL = response.getElementsByTagName(nameNode);
     if(nodeL.getLength()>0){
     List<TopicsBean.Entity> entity_list = new ArrayList<TopicsBean.Entity>(nodeL.getLength()); 
      for(int i=0; i<nodeL.getLength(); i++) {
    	  TopicsBean.Entity ent = new TopicsBean.Entity();
        Node node = nodeL.item(i);
        NodeList data_node = node.getChildNodes();            
        for(int j=0; j<data_node.getLength(); j++){               
          Node n = data_node.item(j); 
          String name = n.getNodeName();
          String value = new String(n.getTextContent().getBytes(),"UTF-8");
            if ("form".equals(name)) {
              ent.form = new String(n.getTextContent().getBytes(),"UTF-8");
            }
            else if ("subentity_list".equals(name)) {
              try{
                  NodeList subentities = n.getChildNodes();
                  Recursive subannotations = collectInfo((Element)subentities,"subentity",ud);
                  Iterator<TopicsBean.Entity> suba_it = subannotations.hack_entity_list.iterator();
                  while(suba_it.hasNext()){
                      entity_list.add((TopicsBean.Entity)suba_it.next());
                  }
              }catch(InvalidOffsetException e){
                System.err.println("Exception in subentities: " + e.toString());
              }catch(UnsupportedEncodingException e){
                System.err.println("Exception in subentities: " + e.toString());
              }
            }
            else if ("dictionary".equals(name)) {
              ent.dictionary = n.getTextContent();
            }
            else if ("id".equals(name)) {
              ent.id = new String(n.getTextContent().getBytes(),"UTF-8");
            }
            else if ("relevance".equals(name)) {
              ent.relevance = new String(n.getTextContent().getBytes(),"UTF-8");
            }
            else if ("sementity".equals(name)) {
             NodeList typeLi = n.getChildNodes();
             TopicsBean.Entity.sementity sem = new TopicsBean.Entity.sementity();
             for(int li_it=0;li_it<typeLi.getLength();li_it++){
				Node sem_node = typeLi.item(li_it);
				String name_aux = sem_node.getNodeName();
				switch (name_aux) {
					case "type":
						sem.sementity_type = new String(sem_node.getTextContent().getBytes(),"UTF-8");
						break;
					case "class":
						sem.sementity_class = new String(sem_node.getTextContent().getBytes(),"UTF-8");
                        break;
                    case "fiction":
                        sem.sementity_fiction = new String(sem_node.getTextContent().getBytes(),"UTF-8");
                        break;
                    case "confidence":
                        sem.sementity_confidence = new String(sem_node.getTextContent().getBytes(),"UTF-8");
                        break;
                    case "id":
                        sem.sementity_id = new String(sem_node.getTextContent().getBytes(),"UTF-8");
                        break;
                }
              }
              ent.sementity_list.add(sem);
            }
            else if ("semgeo_list".equals(name)) {
              NodeList semgeoLi = n.getChildNodes();
              for(int semgeo_li=0;semgeo_li<semgeoLi.getLength();semgeo_li++){
            	  TopicsBean.Entity.semgeo s = new TopicsBean.Entity.semgeo();
                  Node semge = (Node) semgeoLi.item(semgeo_li);
                  NodeList aux = semge.getChildNodes();
                  for(int aux_it=0;aux_it<aux.getLength();aux_it++){
                      Node aux2 = (Node) aux.item(aux_it);
                      TopicsBean.Entity.semgeo.element e = new TopicsBean.Entity.semgeo.element();
                      NodeList aux5 = aux2.getChildNodes();
                      e.name = new String(aux2.getNodeName().getBytes(),"UTF-8");
                      for(int aux6_it=0;aux6_it<aux5.getLength();aux6_it++){
                          Node aux7 = (Node) aux5.item(aux6_it);
                          if(aux7.getNodeName().equals("form"))
                             e.form = new String(aux7.getTextContent().getBytes(),"UTF-8");
                          else if(aux7.getNodeName().equals("id"))
                             e.id  = new String(aux7.getTextContent().getBytes(),"UTF-8");
                          else if(aux7.getNodeName().contains("std")){
                        	  TopicsBean.Entity.semgeo.element.std std_local = new TopicsBean.Entity.semgeo.element.std();
                             std_local.name = new String(aux7.getNodeName().getBytes(),"UTF-8");
                             std_local.value =  new String(aux7.getTextContent().getBytes(),"UTF-8");
                             e.stdArray.add(std_local); 
                          }
                       }
                      s.semgeo_list.add(e);
                      }
              ent.semgeo_list.add(s);
              }
            }
            else if ("semld_list".equals(name)) {
              NodeList semldLi = n.getChildNodes();
              Map<String,String> dictionaries = new HashMap<String, String>();
              int count = 0;
              for(int semld_li = 0;semld_li<semldLi.getLength();semld_li++){
                  Node aux = semldLi.item(semld_li);
                  String name_aux = aux.getNodeName();
                  TopicsBean.Entity.semld s = new TopicsBean.Entity.semld();
                  if(name_aux.equals("semld")){
                      NodeList dics = null;
                      if((aux.hasChildNodes()) && (aux.getFirstChild().getNodeName()!="#cdata-section")){
                          dics = aux.getChildNodes();
                          for(int dics_it=0;dics_it<dics.getLength();dics_it++){
                              Node dic = dics.item(dics_it);
                              s.semld = new String(dic.getTextContent().getBytes(),"UTF-8");
                              if(dictionaries.containsKey(new String(dic.getNodeName().getBytes(),"UTF-8"))){
                                  String dict = new String(dic.getNodeName().getBytes(),"UTF-8");
                                  dict+="_"+count;
                                  s.sourceDic = dict;
                                  count++;
                              }else{
                                  dictionaries.put(new String(dic.getNodeName().getBytes(),"UTF-8"), new String(dic.getNodeName().getBytes(),"UTF-8"));
                                  s.sourceDic = new String(dic.getNodeName().getBytes(),"UTF-8");
                              }
                              ent.semld_list.add(s);
                          }                                
                      }else{
                      s.semld = new String(aux.getTextContent().getBytes(),"UTF-8");
                      ent.semld_list.add(s);
                      }
                  }
              }
            }
            else if ("semrefer_list".equals(name)) {
              NodeList semreferLi = n.getChildNodes();
              for(int semrefer_li = 0;semrefer_li<semreferLi.getLength();semrefer_li++){
              //TODO:!!!!                  
              }
            }
            else if ("semtheme_list".equals(name)) {
              NodeList semthemeLi = n.getChildNodes();
              for(int semtheme_li = 0;semtheme_li<semthemeLi.getLength();semtheme_li++){
                  Node semthem = (Node) semthemeLi.item(semtheme_li);
                  NodeList aux = semthem.getChildNodes();
                  TopicsBean.Entity.semtheme s = new TopicsBean.Entity.semtheme();
                  for(int aux_it=0;aux_it<aux.getLength();aux_it++){
                      Node aux2 = (Node) aux.item(aux_it);                  
                      String aux_name = aux2.getNodeName();
                      if ("id".equals(aux_name)) {
                        s.id = new String(aux2.getTextContent().getBytes(),"UTF-8");
                      }
                      else if ("type".equals(aux_name)) {
                        s.type = new String(aux2.getTextContent().getBytes(),"UTF-8");
                      }
                  }                
              ent.semtheme_list.add(s);
              }
            }
            else if ("standard_list".equals(name)) {
              NodeList standardLi = n.getChildNodes();
              for(int std_li = 0;std_li<standardLi.getLength();std_li++){
                  Node std = (Node) standardLi.item(std_li);
                  TopicsBean.Entity.standard std_local = new TopicsBean.Entity.standard();
                  std_local.name = new String(std.getFirstChild().getNodeName().getBytes(),"utf-8");
                  std_local.value = new String(std.getTextContent().getBytes(),"UTF-8");
                  ent.standard_list.add(std_local);
              }
            }
            else if ("variant_list".equals(name)) {
              NodeList variantLi = n.getChildNodes();
              for(int semgeo_li=0;semgeo_li<variantLi.getLength();semgeo_li++){
            	  TopicsBean.Entity.variant v = new TopicsBean.Entity.variant();
                  Node var = (Node) variantLi.item(semgeo_li);
                  NodeList aux = var.getChildNodes();
                  for(int aux_it=0;aux_it<aux.getLength();aux_it++){
                      Node aux2 = (Node) aux.item(aux_it);
                      String aux_name = aux2.getNodeName();
                      if ("form".equals(aux_name)) {
                        v.form = new String(aux2.getTextContent().getBytes(),"UTF-8");
                        //v.form = aux2.getTextContent();
                      }
                      else if ("inip".equals(aux_name)) {
                        v.inip =  new String(aux2.getTextContent().getBytes(),"UTF-8");
                      }
                      else if ("endp".equals(aux_name)) {
                        v.endp =  new String(aux2.getTextContent().getBytes(),"UTF-8");
                      }
                  }
                  ent.variant_list.add(v);
                }
            }
        }
        entity_list.add(ent); 
      }

      annotations = transform(entity_list,nameNode,ud);
        rec.hack_ann_list = annotations;
        rec.hack_entity_list = entity_list;

     }  

     return rec;
}

public static List<Annot> collectShort(Element response, String nameNode)throws InvalidOffsetException{
     List<Annot> annotations = new ArrayList<Annot>();
              
     NodeList nodeL = response.getElementsByTagName(nameNode);
      for(int i=0; i<nodeL.getLength(); i++) {
          Long startOffset = 0L,endOffset =0L;
        Node node = nodeL.item(i);
        NodeList data_node = node.getChildNodes();
        String form = "";
        String normalizedForm = "";
        String actualTime = "";
        String precision = "";
        String amount = "";
        String numericValue = "";
        String currency = "";
        String who = "";
        String who_lemma = "";//ToDo: list
        String verb = "";
        String verb_lemma = "";//ToDo: list
        String type = "";
        String subject_form = "";
        String subject_lemmas = "";
        String subject_sensesID = "";
        String subject_semantic_lemma = "";
        String verb_form = "";
        String verb_lemmas = "";
        String verb_sensesID = "";
        String verb_semanticLemma = "";
        String complement_form = "";//ToDo: complement_list!!!
        String complement_type = "";
        String degree = "";
                 
        boolean flag_inip=false,flag_endp = false;
        for(int j=0; j<data_node.getLength(); j++){
          Node n = data_node.item(j); 
          String name = n.getNodeName();
          if ("form".equals(name)) {
              form = n.getTextContent();
          }
          else if ("normalized_form".equals(name)) {
            normalizedForm = n.getTextContent();
          }
          else if ("atual_time".equals(name)) {
            actualTime = n.getTextContent();
          }
          else if ("precision".equals(name)) {
            precision = n.getTextContent();
          }
          else if ("amount".equals(name)) {
            amount = n.getTextContent();
          }
          else if ("numeric_value".equals(name)) {
            numericValue = n.getTextContent();
          }
          else if ("currency".equals(name)) {
            currency = n.getTextContent();
          }
          else if ("who".equals(name)) {
            who = n.getTextContent();
          }
          else if ("who-lemma".equals(name)) {
            who_lemma = n.getTextContent();
          }
          else if ("verb-lemma".equals(name)) {
            verb_lemma = n.getTextContent();
          }
          else if ("type".equals(name)) {
            type = n.getTextContent();
          }
          else if ("inip".equals(name)) {
            String inip_value = n.getTextContent();
            if(inip_value!=null){
                startOffset = Long.parseLong(inip_value,10);
                flag_inip = true;
            }else{
                System.err.println("error inip");
            }
          }
          else if ("endp".equals(name)) {
            String endp_value = n.getTextContent();
            if(endp_value!=null){
                endOffset = Long.parseLong(endp_value,10);
                flag_endp = true;
            }else{
                System.err.println("error endp");
            }
  
          }
          
        if(((startOffset+endOffset) != 0) && (flag_inip&flag_endp)){//If the entity was present in the text
            try{
                gate.FeatureMap fm= gate.Factory.newFeatureMap();
                if(!form.isEmpty())
                    fm.put("form",form);
                if(!normalizedForm.isEmpty())
                    fm.put("normalizedForm",normalizedForm);
                if(!actualTime.isEmpty())
                    fm.put("actualTime",actualTime);
                if(!precision.isEmpty())
                    fm.put("precision", precision);
                if(!amount.isEmpty())
                    fm.put("amount",amount);
                if(!numericValue.isEmpty())
                    fm.put("numericValue",numericValue);
                if(!currency.isEmpty())
                    fm.put("currency",currency);
                if(!who.isEmpty())
                    fm.put("who",who);
                if(!who_lemma.isEmpty())
                    fm.put("who_lemma",who_lemma);
                if(!verb.isEmpty())
                    fm.put("verb",verb);
                if(!verb_lemma.isEmpty())
                    fm.put("verb_lemma",verb_lemma);
                if(!type.isEmpty())
                    fm.put("type",type);
                if(!subject_form.isEmpty())
                    fm.put("subject.form",subject_form);
                if(!subject_lemmas.isEmpty())
                    fm.put("subject.lemmas",subject_lemmas);
                if(!subject_sensesID.isEmpty())
                    fm.put("subject.sensesID",subject_sensesID);
                if(!subject_semantic_lemma.isEmpty())
                    fm.put("subject.semantic.lemma",subject_semantic_lemma);
                if(!verb_form.isEmpty())                    
                    fm.put("verb.form",verb_form);
                if(!verb_lemmas.isEmpty())
                    fm.put("verb.lemmas",verb_lemmas);
                if(!verb_sensesID.isEmpty())
                    fm.put("verb.sensesID",verb_sensesID);
                if(!verb_semanticLemma.isEmpty())
                    fm.put("verb.semanticLemma",verb_semanticLemma);
                if(!complement_form.isEmpty())
                    fm.put("complement.form",complement_form);
                if(!complement_type.isEmpty())
                    fm.put("complement.type",complement_type);
                if(!degree.isEmpty())
                    fm.put("degree",degree);
                Annot annot = new Annot();
                annot.startOff = startOffset;
                annot.endOff = endOffset+1;
                annot.Name = nameNode;
                annot.fm = fm;
                annotations.add(annot);
                flag_inip=false;flag_endp = false;
            }catch (Exception e){
                System.err.println(e.toString());
            }
        }
        }
      }
      return annotations;
}

}