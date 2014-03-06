package daedalus.textalytics.gate.param;

import gate.Factory;
import gate.FeatureMap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import daedalus.textalytics.gate.param.Serialized_resp.Annot;


public class TokenBean {
	public Serialized_resp.Status status;
	public Token[] token_list = null;
	
	public static class Token_xml{ 
	    public String fatherID="";
	    public String type = "";
	    public String form = "";
	    public String normalized_form = "";
	    public String id="";
	    public Long inip=0L;
	    public Long endp =0L;
	    public String bold = "";
	    public String italic = "";
	    public String underscore = "";
	    public String title="";
	    public String separation = "";
	    public String quote_level="";
	    public String head="";
	    public ArrayList<SyntTreeRelation> syntTreeRelationList = new ArrayList<SyntTreeRelation>();
	    public ArrayList<Analysis> analysisList = new ArrayList<Analysis>();
	    //public ArrayList<TopicsBean.Entity> topicsList = new ArrayList<TopicsBean.Entity>();
	    public ArrayList<Senses> senseList = new ArrayList<Senses>(); 
	    //public ArrayList<Token> tokenList = new ArrayList<Token>();
	    
	public static class SyntTreeRelation{
		public String id="",type="";
	}
	public static class Analysis{
		public String origin="",varietyDictionary="",thematicDictionary="",tag="",lemma="",originalForm="",
	            tagInfo="",varietyDictionaryInfo="",thematicDictionaryInfo="",remission="";
		public CheckInfo checkInf = new CheckInfo();
	    public static class CheckInfo{
	    	public String tag="", checkExtraInfo="";
	    	public ArrayList<String> forms = new ArrayList<String>();
	    }
	   
	    public ArrayList<String> senseIds = new ArrayList<String>();
	    
	}
	public static class Senses{
		public String id="",info="";
	}
	
	}
	
	public static class Token{ 
	    public String type;
	    public String form;
	    public String normalized_form;
	    public String id;
	    public Long inip;
	    public Long endp;
	    public String bold;
	    public String italic;
	    public String underscore;
	    public String title;
	    public String separation;
	    public String quote_level;
	    public String head;
	    public SyntTreeRelation[] syntactic_tree_relation_list = null;
	    public Analysis[] analysis_list = null;
	    public TopicsBean topic_list = null;
	    public Senses[] sense_list = null; 
	    public Token[] token_list = null;
	    
	    @Override public String toString(){
	    	String ret =  "\nToken [\n\t";
	    	/*if(form!=null)
	    		ret += "form=" + form;
	    	if(type!=null)
	    		ret+=", type=" + type;
	    	if(normalized_form!=null)
	    		ret+= ", normalized="+ normalized_form;
	    	if(bold!=null)
	    		ret+= ", bold=" + bold;
	    	if(italic!=null)
	    		ret+= ", italic=" + italic;
	    	if(underscore!=null)
	    		ret+= ", underscore=" + underscore;
	    	if(title!=null)
	    		ret+= ", title=" + title;
	    	if(separation!=null)
	    		ret+= ", separation=" + separation;
	    	if(quote_level!=null)
	    		ret+=", quote_level=" + quote_level;
	    	if(head!=null)
	    		ret+= ", head=" + head;
	    	if(syntactic_tree_relation_list!=null)
	    		for(SyntTreeRelation s : syntactic_tree_relation_list)
	    			ret += s.toString();
	    	if(analysis_list!=null)
	    		for(Analysis a : analysis_list)
	    			ret += a.toString();
	    	if(sense_list!=null)
	    		for(Senses s : sense_list)
	    			ret += s.toString();
	    	if(token_list!=null)
	    		for(Token t : token_list)
	    			ret += t.toString();
	    	if(topic_list!=null)
    			ret += "Topics ["+topic_list.toString()+"]";
			ret+="\n\t]";*/
			return ret;
	    }
	    
	    public static class SyntTreeRelation{
		public String id,type;
		public String toString(){
			String ret = "\nSyntactic_tree_relation [\n\t";
			if(id!=null)
				ret+="id="+id;
			if(type!=null)
				ret+=", type="+type;
			ret+="\n\t]";
			return ret;
		}
	}
	    public static class Analysis{
		public String origin,variety_dictionary,thematic_dictionary,tag,lemma,original_form,
		tag_info,variety_dictionary_info,thematic_dictionary_info,remission;
		public CheckInfo check_info = null;
		public SenseID[] sense_id_list = null;
		
	    public static class CheckInfo{
	    	public String tag, check_extra_info;
	    	public Form[] form_list = null;
	    	
	    	public class Form{
	    		String form;
	    		public String toString(){
	    			return form;
	    		}
	    	}
	    	
	    	public String toString(){
	    		String ret = "\nCheckInfo [\n\t";
	    		if(tag!=null)
	    			ret+="tag="+tag;
	    		if(check_extra_info!=null)
	    			ret+=", check_extra_info"+check_extra_info;
	    		if(form_list!=null){
	    			ret+="Form_list [\n\t";
	    			for(Form f : form_list)
	    				ret+=f.form;
	    			ret+="\n\t]";
	    		}
	    		ret+="\n\t]";
	    		return ret;
	    	}
	    	
	    	public FeatureMap annotate(FeatureMap fm) throws UnsupportedEncodingException{
	    		if(this.tag!=null)
				if(fm.containsKey("Analysis_checkinfo_tag")){
					String tmp = (String) fm.get("Analysis_checkinfo_tag");
					fm.put("Analysis_checkinfo_tag", tmp+";"+(new String(tag.getBytes(),"utf-8")));
				}else{
					fm.put("Analysis_checkinfo_tag", (new String(tag.getBytes(),"utf-8")));
				}
	    		if(this.check_extra_info!=null)
				if(fm.containsKey("Analysis_checkinfo_check_extra_info")){
					String tmp = (String) fm.get("Analysis_checkinfo_check_extra_info");
					fm.put("Analysis_checkinfo_check_extra_info", tmp+";"+(new String(check_extra_info.getBytes(),"utf-8")));
				}else{
					fm.put("Analysis_checkinfo_check_extra_info",(new String(check_extra_info.getBytes(),"utf-8")));
				}
				
	    		if(this.form_list!=null){
				String form  = "";
				if(fm.containsKey("Analysis_checkinfo_forms")){
					int cnt = 0;
					String tmp = fm.get("Analysis_checkinfo_forms").toString();
					for(Form s : form_list){
						if(cnt==0){
							form += s.form;
						}else{
							form+="|"+s.form;
						}
						cnt++;	
					}
					fm.put("Analysis_checkinfo_forms", tmp+";"+form);
				}else{
					int cnt = 0;
					for(Form s : form_list){
						if(cnt==0){
							form += s.form;
						}else{
							form+="|"+s.form;
						}
						cnt++;	
					}
					fm.put("Analysis_checkinfo_forms", form);
				}
	    		}
	    		    		
				return fm;
			}
	    }
	    public static class SenseID{
	    	public String sense_id;
	    	public String toString(){
	    		String ret = "";
	    		if(sense_id != null)
	    			ret+="\nSense [\n\tid="+sense_id.toString()+"\n\t]";
	    		return ret;
	    	}
	    	public FeatureMap annotate(FeatureMap fm){
	    		if(fm.containsKey("Analysis_senseIDs")){
					String tmp = (String) fm.get("Analysis_senseID_form");
					fm.put("Analysis_senseID_form", tmp+";"+sense_id);
				}else{
					fm.put("Analysis_senseID_form", sense_id);
				}
	    		return fm;
	    	}
	    }
	    
	    public String toString(){
	    	String ret = "\nAnalysis [\n\t";
	    	if(origin!=null)
	    		ret+="origin="+origin;
	    	if(variety_dictionary!=null)
	    		ret+=", variety_dictionary="+variety_dictionary;
	    	if(thematic_dictionary!=null)
	    		ret+=", thematic_dictionary="+thematic_dictionary;
	    	if(tag!=null)
	    		ret+=", tag="+tag;
	    	if(lemma!=null)
	    		ret+=", lemma="+lemma;
	    	if(original_form!=null)
	    		ret+=", original_form="+original_form;
	    	if(tag_info!=null)
	    		ret+=", tag_info="+tag_info;
	    	if(variety_dictionary_info!=null)
	    		ret+=", variety_dictionary_info="+variety_dictionary_info;
	    	if(thematic_dictionary_info!=null)
	    		ret+=", thematic_dictionary_info="+thematic_dictionary_info;
	    	if(remission!=null)
	    		ret+=", remission="+remission;
	    	if(check_info!=null)
	    		ret += "\nCheckInfo [\n\t"+check_info.toString()+"]";
	    	if(sense_id_list!=null){
	    		ret+="\nSense_id_list [\n\t";
	    		for(SenseID s: sense_id_list)
	    			ret+=s.toString();
	    		ret+="\n\t]";
	    	}
	    	ret+="\n\t]";
	    	return ret;
	    }
	    
	    public FeatureMap annotate(FeatureMap fm) throws UnsupportedEncodingException{
	    	if(origin!=null)
			if(fm.containsKey("Analysis_origin")){
				String tmp = (String) fm.get("Analysis_origin");
				fm.put("Analysis_origin", tmp+";"+new String(origin.getBytes(),"utf-8"));
			}else{
				fm.put("Analysis_origin", new String(origin.getBytes(),"utf-8"));
			}
	    	if(variety_dictionary!=null)
			if(fm.containsKey("Analysis_variety_dictionary")){
				String tmp = (String) fm.get("Analysis_variety_dictionary");
				fm.put("Analysis_variety_dictionary", tmp+";"+new String(variety_dictionary.getBytes(),"utf-8"));
			}else{
				fm.put("Analysis_variety_dictionary", new String(variety_dictionary.getBytes(),"utf-8"));
			}
	    	if(thematic_dictionary!=null)
			if(fm.containsKey("Analysis_thematic_dictionary")){
				String tmp = (String) fm.get("Analysis_thematic_dictionary");
				fm.put("Analysis_thematic_dictionary", tmp+";"+new String(thematic_dictionary.getBytes(),"utf-8"));
			}else{
				fm.put("Analysis_thematic_dictionary", new String(thematic_dictionary.getBytes(),"utf-8"));
			}
	    	if(this.tag!=null)
			if(fm.containsKey("Analysis_tag")){
				String tmp = (String) fm.get("Analysis_tag");
				fm.put("Analysis_tag", tmp+";"+new String(tag.getBytes(),"utf-8"));
			}else{
				fm.put("Analysis_tag", new String(tag.getBytes(),"utf-8"));
			}
	    	if(this.lemma!=null)
			if(fm.containsKey("Analysis_lemma")){
				String tmp = (String) fm.get("Analysis_lemma");
				fm.put("Analysis_lemma", tmp+";"+new String(lemma.getBytes(),"utf-8"));
			}else{
				fm.put("Analysis_lemma", new String(lemma.getBytes(),"utf-8"));
			}
	    	if(this.original_form!=null)
			if(fm.containsKey("Analysis_original_form")){
				String tmp = (String) fm.get("Analysis_original_form");
				fm.put("Analysis_original_form", tmp+";"+new String(original_form.getBytes(),"utf-8"));
			}else{
				fm.put("Analysis_original_form", new String(original_form.getBytes(),"utf-8"));
			}
	    	if(this.tag_info!=null)
			if(fm.containsKey("Analysis_tag_info")){
				String tmp = (String) fm.get("Analysis_tag_info");
				fm.put("Analysis_tag_info", tmp+";"+new String(tag_info.getBytes(),"utf-8"));
			}else{
				fm.put("Analysis_tag_info", new String(tag_info.getBytes(),"utf-8"));
			}
	    	if(this.variety_dictionary_info!=null)
			if(fm.containsKey("Analysis_variety_dictionary_info")){
				String tmp = (String) fm.get("Analysis_variety_dictionary_info");
				fm.put("Analysis_variety_dictionary_info", tmp+";"+new String(variety_dictionary_info.getBytes(),"utf-8"));
			}else{
				fm.put("Analysis_variety_dictionary_info", new String(variety_dictionary_info.getBytes(),"utf-8"));
			}
	    	if(this.thematic_dictionary_info!=null)
			if(fm.containsKey("Analysis_thematic_dictionary_info")){
				String tmp = (String) fm.get("Analysis_thematic_dictionary_info");
				fm.put("Analysis_thematic_dictionary_info", tmp+";"+new String(thematic_dictionary_info.getBytes(),"utf-8"));
			}else{
				fm.put("Analysis_thematic_dictionary_info", new String(thematic_dictionary_info.getBytes(),"utf-8"));
			}
	    	if(this.remission!=null)
			if(fm.containsKey("Analysis_remission")){
				String tmp = (String) fm.get("Analysis_remission");
				fm.put("Analysis_remission", tmp+";"+new String(remission.getBytes(),"utf-8"));
			}else{
				fm.put("Analysis_remission", new String(remission.getBytes(),"utf-8"));
			}
			//System.out.println("Size1: "+fm.size());
			if(this.check_info!=null)
				fm=check_info.annotate(fm);
			if(sense_id_list!=null)
				for(SenseID s : sense_id_list)
					fm=s.annotate(fm);
			//System.out.println("Size2: "+fm.size());
			
			return fm;
		}

	}
	    public static class Senses{
		public String id,info;
		public String toString(){
			String ret = "\nSense [\n\t";
			if(id!=null)
				ret+="id="+id;
			if(info!=null)
				ret+=", info="+info;
			ret+= "\n\t]";			
			return ret;
		}
		public FeatureMap annotate(FeatureMap fm) throws UnsupportedEncodingException{
			if(this.id!=null)
				if(fm.containsKey("Senses_id")){
					String tmp = (String) fm.get("Senses_id");
					fm.put("Senses_id", tmp+";"+new String(this.id.getBytes(),"utf-8"));
				}else{
					fm.put("Senses_id", new String(this.id.getBytes(),"utf-8"));
				}
			if(this.info!=null)
				if(fm.containsKey("Senses_info")){
					String tmp = (String) fm.get("Senses_info");
					fm.put("Senses_info", tmp+";"+new String(this.info.getBytes(),"utf-8"));
				}else{
					fm.put("Senses_info", new String(this.info.getBytes(),"utf-8"));
				}
			return fm;
		}
	}
	    public ArrayList<Annot> annotate(ArrayList<Annot> ann_list) throws UnsupportedEncodingException{
	    	Annot at = new Serialized_resp().new Annot();
	    	if(inip!=null && endp!=null){
	    			at.inip = inip;
	    			at.endp = endp+1;
		
	    			if(type!=null)
	    				at.fm.put("type", new String(type.getBytes(),"UTF-8"));
	    			if(form!=null)
	    				at.fm.put("form", new String(form.getBytes(),"UTF-8"));
	    			if(normalized_form!=null)
	    				at.fm.put("normalized_form", new String(normalized_form.getBytes(),"UTF-8"));
	    			if(id!=null)
	    				at.fm.put("id", new String(id.getBytes(),"utf-8"));
	    			if(bold!=null)
	    				at.fm.put("bold", new String(bold.getBytes(),"utf-8"));
	    			if(italic!=null)
	    				at.fm.put("italic", new String(italic.getBytes(),"utf-8"));
	    			if(underscore!=null)
	    				at.fm.put("underscore", new String(underscore.getBytes(),"utf-8"));
	    			if(title!=null)
	    				at.fm.put("title", new String(title.getBytes(),"utf-8"));
	    			if(separation!=null)
	    				at.fm.put("separation", new String(separation.getBytes(),"utf-8"));
	    			if(quote_level!=null)
	    				at.fm.put("quote_level", new String(quote_level.getBytes(),"utf-8"));
	    			if(head!=null)
	    				at.fm.put("quote_level", new String(head.getBytes(),"utf-8"));
	    			if(syntactic_tree_relation_list!=null){
	    				String syntree_ids="",syntree_types="";
	    				int cnt = 0;
	    				for(Token.SyntTreeRelation s : syntactic_tree_relation_list){
	    					if(cnt==0){
	    						syntree_ids+=s.id;
	    						syntree_types+=s.type;
	    					}else{
	    						syntree_ids+=";"+s.id;
	    						syntree_types+=";"+s.type;
	    					}
	    					cnt++;
	    				}
	    				at.fm.put("syntactic_tree_relation_id", syntree_ids);
	    				at.fm.put("syntactic_tree_relation_type", syntree_types);
	    			}		
	    			if(analysis_list!=null){
	    				for(Analysis a : analysis_list)
	    					at.fm = a.annotate(at.fm);
	    			}
	    			if(sense_list!=null){
	    				for(daedalus.textalytics.gate.param.TokenBean.Token.Senses s : sense_list)
	    					at.fm = s.annotate(at.fm);
	    			}
	    			if(topic_list!=null){
	    				at.fm.put("Topics", topic_list.toString());
	    			}
	    			ann_list.add(at);
	    			if(token_list!=null)
	    				for(Token tok : token_list){
	    					tok.annotate(ann_list);
	    				}
	    	}
	    	return ann_list;
	    }
	    
	}
		
	public String toString(){
		String ret = "Token List [\n\t";
		if(token_list!=null)
			for(Token t : token_list)
				ret+=t.toString();
		ret+="\n\t]";
		return ret;
	}
		
	public Serialized_resp serialize() throws UnsupportedEncodingException{
		Serialized_resp sr = new Serialized_resp();
		FeatureMap doc_fm = Factory.newFeatureMap();
		doc_fm.put("response", this.status);
		sr.doc_fm = doc_fm;	
		//sr.s = this.status;
		for(Token t : token_list){
			ArrayList<Serialized_resp.Annot> at_list = new ArrayList<Serialized_resp.Annot>();
			at_list = t.annotate(at_list);
			sr.annot_list.addAll(at_list);
		}		
		return sr;
	}
	
	}